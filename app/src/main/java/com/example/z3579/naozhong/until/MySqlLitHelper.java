package com.example.z3579.naozhong.until;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySqlLitHelper extends SQLiteOpenHelper {
    private final static  String LOG_TAG="MySqlLitHelper";
    public final static  String table_name="clock_table";//表名
    public final static  String table_id="clock_id";//主键
    public final static  String clock_time="clock_time";//存储时间，格式为HH:MM
    public final static  String clock_note="clock_note";//闹钟标签
    public final static  String repeat="repeat";//重复的星期，格式为1，2，3，4，5，6，7（依次代表星期日-星期六 重复）
    public final static  String isalert="isalert";//代表是否稍后提醒，1=true代表是，0=false代表否。
    public final static  String ison="ison";//代表是否启用，1=true代表是，0=false代表否。
    public final static  String clock_url="clock_url";//闹钟铃声url
    public MySqlLitHelper(Context context) {
        super(context, "naozhong.db", null, 1);
    }
    //创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        String str ="CREATE TABLE "+MySqlLitHelper.table_name+"("+MySqlLitHelper.table_id+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                ""+MySqlLitHelper.clock_time+" text,"+MySqlLitHelper.clock_note+" VARCHAR(10),"+MySqlLitHelper.repeat+" VARCHAR(20),"
                +MySqlLitHelper.isalert+" INTEGER ,"+MySqlLitHelper.ison+" INTEGER ,"+MySqlLitHelper.clock_url+" text)";
        Log.d(LOG_TAG,str);
        db.execSQL(str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
