package com.example.z3579.naozhong.until;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.example.z3579.naozhong.R;
import com.example.z3579.naozhong.entity.Ring;

import java.util.ArrayList;
import java.util.List;

public class PlayRing {
    private    MediaPlayer mMediaPlayer;
    private    Context context;
    private static  PlayRing playRing;
    private List<Ring> rList ;
    private RingtoneManager ringtoneManager;//查阅资料，在使用RingtoneManager时，通过ringtoneManager.getCursor()，会对其属性mCursor赋值，在使用ringtoneManager.getRingtoneUri(position)使用的是mCursor，所以保存该对象;
    private PlayRing(Context context){
        this.context=context;
        this.ringtoneManager = new RingtoneManager(context); // 铃声管理器
    }

    public static PlayRing getPlayRing(Context context){
        if(playRing==null){
            playRing=new  PlayRing(context);
        }
        return  playRing;
    }

    //播放指定铃声
    public  void startAlarm(Uri uri, Context context) {
        mMediaPlayer = MediaPlayer.create(context, uri);
        mMediaPlayer.start();
        //本来想让闹钟只播放一遍，发现设置setloop不起作用，遂采用监听方式，发现也不起作用，经查阅资料，系统中的闹铃，
        // 提示音音频文件中存在一个android——loop属性，需要删除该属性，否则自动循环，遂放弃。
//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                // 在播放完毕被回调
//                mp.stop();
//                mp.release();
//                mp = null;
//            }
//        });
    }
    //销毁播放
    public  void destoryMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 得到系统闹铃列表
     * @return
     */
    public  List<Ring> get_List_ALARM(){
        if (rList==null){
            rList = new ArrayList<Ring>();
            ringtoneManager.setType(RingtoneManager.TYPE_ALARM);//设置类型，我需要系统的闹钟铃声，所以这里为闹铃类型

            Cursor cursor = ringtoneManager.getCursor(); //获取铃声表,根据表名取值
            int i=0;
            while (cursor != null && cursor.moveToNext()) {//这里我获取了闹铃的名字和ID，也可以使用Ringtone进行快速播放， ringtoneManager.getRingtone()
                Ring ring = new Ring();
                ring.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                ring.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                ring.setUrlStr(cursor.getString(RingtoneManager.URI_COLUMN_INDEX));
                ring.setIndex(i);
                rList.add(ring);
                i++;
            }
        }
        return rList;
    }
    //添加一个新闹钟时得到默认闹铃，默认为list第一个铃声
    public  Ring getDefaultUrl( ){
        get_List_ALARM();

        return rList.get(0);
    }
    /**
     * 通过数据库存储的url，得到上次选中闹铃，
     * @return 默认闹铃
     */
    public  Ring get_CheckUrl(String url){
        get_List_ALARM();//得到列表
       for (Ring r:rList) {
            if(r.getUrlStr().equals(url)){
                return r;
            }
       }
        return null;
    }


}
