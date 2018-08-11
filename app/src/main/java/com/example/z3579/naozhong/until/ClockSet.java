package com.example.z3579.naozhong.until;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.z3579.naozhong.entity.Clock;

/**
 *  闹钟操作功能类
 */
public class ClockSet {
    private Context context;
    private long  intervalTime=6*1000;//重复闹钟的间隔时长,默认为10分钟
    private final static String KEY_NOTE="note";
    private final static String KEY_ID="id";
    private static ClockSet clockSet=null;

    private final static String LOG_TAG="ClockSet_测试";
    /**
     * 方便日后扩展，提供修改间隔时长的构造函数
     * @param context
     * @param intervalTime 重复闹钟的间隔时长
     */
    public ClockSet(Context context,long  intervalTime){
        this.context=context;
        this.intervalTime=intervalTime;
    }
    private ClockSet(Context context){
        this.context=context;
    }
    public static ClockSet getInstance(Context context){
        if(clockSet==null) {
            clockSet=new ClockSet(context);
        }
        return clockSet;
    }
    /**
     *  发送定时闹钟广播
     * @param clock 闹钟对象
     */
    public void sendClockBroadCast(Clock clock){
        Log.d(LOG_TAG,"执行sendClockBroadCast");
        Intent intent = new Intent("com.example.z3579.naozhong.broadcast.CR");//设置广播接收器的action
        //高版本必须加setComponent，不然广播发不过去。
        // 参数1是app的包名，切记不是广播接收器的包名，参数2是广播接收器的路径
        intent.setComponent(new ComponentName("com.example.z3579.naozhong",
                "com.example.z3579.naozhong.broadcast.MyClockReceiver"));
        intent.putExtra(KEY_NOTE,clock.getClock_note());//设置通知信息为闹钟标签。
        intent.putExtra(KEY_ID,clock.getId());//ID。
        //第二个参数设置为闹钟的ID，适应闹钟修改，覆盖ID相同的
        PendingIntent pd= PendingIntent.getBroadcast(this.context,clock.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);//配置PendingIntent
        //获取alarmManager对象
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //setRepeating在api<19以下可以精确触发，但API 19(Android4.4)开始，AlarmManager的机制是非准确激发的，
        // 操作系统会偏移(shift)闹钟来最小化唤醒和电池消耗。不过AlarManager新增了如下两个方法来支持精确激发。
        // setExact(int type long triggerAtMillis,PendingIntent operation) 设置闹钟闹钟将在精确的时间被激发。
        //setWindow(int type,long windowStartMillis,long windowLengthMillis,PendingIntent operation)设置闹钟将在精确的时间段内被激发。
        //所以API19以后无法使用setInexactRepeating()和setRepeating()，也就是无法设置重复闹钟，唯一解决的方式，也只有启动闹钟的时候再设置一次闹钟，也就变相地实现了重复闹钟了。
        //在6.0及以上手机又出问题了，手机在进入休眠状态一段时间后AlarmManager不工作了，发现6.0中谷歌对低电耗模式和应用待机模式(6.0开始引入)进行了优化，
        // 提供了对应API解决：setExactAndAllowWhileIdle(int type, long triggerAtMillis, PendingIntent operation)
        //我当前的应用最小版本支持到21所以得使用setExact,和setExactAndAllowWhileIdle去解决高版本问题
        //版本适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0及以上
            Log.d(LOG_TAG,"当前android版本6.0以上");
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    clock.getStartTime(), pd);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4及以上
            Log.d(LOG_TAG,"当前android版本4.4到5.9之间");
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, clock.getStartTime(),
                    pd);
        } else {
            Log.d(LOG_TAG,"当前android版本4.3及以下");
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,clock.getStartTime(),intervalTime,pd);
        }
        //在数据库中修改该闹钟开启状态为1
        ClockDAO.getInstance(context).offOrOn(clock.getId(),true);
    }

    /**
     * 移除指定闹钟定时任务
     * @param clock
     */
    public void  moveClock(Clock clock){
        Intent intent = new Intent("com.example.z3579.naozhong.broadcast.CR");//设置广播接收器的action
        //高版本必须加setComponent，不然广播发不过去。
        // 参数1是app的包名，切记不是广播接收器的包名，参数2是广播接收器的路径
        intent.setComponent(new ComponentName("com.example.z3579.naozhong",
                "com.example.z3579.naozhong.broadcast.MyClockReceiver"));
        //第二个参数设置为闹钟的ID，适应闹钟修改，覆盖ID相同的
        PendingIntent pd= PendingIntent.getBroadcast(this.context,clock.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);//配置PendingIntent
        //获取alarmManager对象
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //取消定时任务
        alarmManager.cancel(pd);
        Log.d("测试","移除了提醒");
        //在数据库中修改该闹钟开启状态为0
        ClockDAO.getInstance(context).offOrOn(clock.getId(),false);
    }

    /**
     *  在通知中调用重复设置方法。
     */
    public void getUpAlarmManagerWorkOnReceiver(String note,Integer id) {
       //高版本重复设置闹钟达到低版本中setRepeating相同效果
        Log.d(LOG_TAG,"执行sendClockBroadCast");
        Intent intent = new Intent("com.example.z3579.naozhong.broadcast.CR");//设置广播接收器的action
        //高版本必须加setComponent，不然广播发不过去。
        // 参数1是app的包名，切记不是广播接收器的包名，参数2是广播接收器的路径
        intent.setComponent(new ComponentName("com.example.z3579.naozhong",
                "com.example.z3579.naozhong.broadcast.MyClockReceiver"));
        intent.putExtra(KEY_NOTE,note);//设置通知信息为闹钟标签。
        intent.putExtra(KEY_ID,id);//设置通知信息为闹钟标签。
        //第二个参数设置为闹钟的ID，适应闹钟修改，覆盖ID相同的
        PendingIntent pd= PendingIntent.getBroadcast(this.context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);//配置PendingIntent
        //获取alarmManager对象
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0及以上
             alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                   System.currentTimeMillis() + intervalTime, pd);
       } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4及以上
             alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                   + intervalTime, pd);
       }
   }


}
