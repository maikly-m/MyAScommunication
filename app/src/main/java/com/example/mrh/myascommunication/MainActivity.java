package com.example.mrh.myascommunication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private EditText et_add;
    private Button btn_add;
    private TextView tv_add;
    private Messenger serviceMessager;
    private Messenger mMessenger;
    public static final int ADD = 0x01;
    public static final int MAINACTIVTY_MESSENGER = 0x02;

    private SoftReference<Handler> mHandler = new SoftReference<Handler>(new Handler(){
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
            case AddService.SERVICE_RESULT:
                String obj = (String) msg.obj;
                if (!TextUtils.isEmpty(submit())){
                    String s = submit() + "=" + obj;
                    tv_add.setText(s);
                }
                break;
            default:
                break;
            }
        }
    });

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected (ComponentName name, IBinder service) {
//            Log.d(TAG, "onServiceConnected: MainActivity");
            serviceMessager = new Messenger(service);
            mMessenger = new Messenger(mHandler.get());
            Message msg = new Message();
            msg.what = MAINACTIVTY_MESSENGER;
            msg.replyTo = mMessenger;
            try{
                //拿到服务的Handler，往服务发送MainActivity的Handler
                serviceMessager.send(msg);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected (ComponentName name) {

        }
    };

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Intent intent = new Intent(this, AddService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initView () {
        et_add = (EditText) findViewById(R.id.et_add);
        btn_add = (Button) findViewById(R.id.btn_add);
        tv_add = (TextView) findViewById(R.id.tv_add);

        btn_add.setOnClickListener(this);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()){
        case R.id.btn_add:
            String submit = submit();
            if (!TextUtils.isEmpty(submit)){
                Message msg = new Message();
                msg.what = ADD;
                msg.obj = submit;
                try{
                    serviceMessager.send(msg);
                } catch (RemoteException e){
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    private String submit () {

        String add = et_add.getText().toString().trim();
        if (TextUtils.isEmpty(add)){
            Toast.makeText(this, "x+y", Toast.LENGTH_SHORT).show();
            return null;
        }
        return add;
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
