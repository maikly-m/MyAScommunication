package com.example.mrh.myascommunication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.lang.ref.SoftReference;

/**
 * Created by MR.H on 2016/8/23 0023.
 */
public class AddService extends Service{

    private static final String TAG = "AddService";
    private Messenger mMessenger;
    private String mObj;
    private Messenger mainActivtyMessenger;
    public static final int SERVICE_RESULT = 0X01;
    private SoftReference<Handler> mHandler = new SoftReference<Handler>(new Handler(){
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
            case MainActivity.ADD:
                mObj = (String) msg.obj;
                String[] split = mObj.split("\\+");
                String arg1 = split[0];
                String arg2 = split[1];
                String result = String.valueOf(Integer.parseInt(arg1) + Integer.parseInt(arg2));

                Message msg1 = new Message();
                msg1.what = SERVICE_RESULT;
                msg1.obj = result;
//                Log.d(TAG, "handleMessage: "+result);
                try{
                    mainActivtyMessenger.send(msg1);
                } catch (RemoteException e){
                    e.printStackTrace();
                }
                break;
            case MainActivity.MAINACTIVTY_MESSENGER:

                mainActivtyMessenger = msg.replyTo;
                break;
            }
        }
    });

    @Nullable
    @Override
    public IBinder onBind (Intent intent) {
        mMessenger = new Messenger(mHandler.get());
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate () {
        super.onCreate();

    }

}
