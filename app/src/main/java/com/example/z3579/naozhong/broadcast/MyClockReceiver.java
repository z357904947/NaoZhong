package com.example.z3579.naozhong.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * 该类为闹钟广播接收器
 */
public class MyClockReceiver extends BroadcastReceiver {
    private final String LOG_TAG="MyClockReceiver_测试";
    private final String ACTION_BOOT = "com.example.z3579.naozhong.broadcast.CR";
    @Override
    public void onReceive(Context context, Intent intent) {
       String msg = intent.getStringExtra("note");

            Log.d(LOG_TAG,"收到了闹钟提醒："+msg);
       Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
