package com.example.z3579.naozhong.until;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.z3579.naozhong.entity.Clock;

import java.util.ArrayList;
import java.util.List;

public class ClockDAO {

    private static MySqlLitHelper mySqlLitHelper;
    private static final String LOG_TAG="ClockDAO_测试";
    private static ClockDAO clockDAO=null;
    private ClockDAO(Context context){
        if(mySqlLitHelper==null){
            mySqlLitHelper=new MySqlLitHelper(context);
        }

    }
    public static ClockDAO getInstance(Context context){
        if(clockDAO==null){
            clockDAO = new ClockDAO(context);
        }
        return  clockDAO;
    }
    /**
     *  得到数据库所有闹钟列表。
     * @return 闹钟列表
     */
    public List<Clock> getClockList(){
        List<Clock> list = null;
        SQLiteDatabase db = mySqlLitHelper.getWritableDatabase();
        Cursor cursor =  db.rawQuery("select * from "+MySqlLitHelper.table_name,null);
        if (cursor.getCount()>0){
            list = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()){
                Integer id = cursor.getInt(cursor.getColumnIndex(MySqlLitHelper.table_id));
                String clock_time = cursor.getString(cursor.getColumnIndex(MySqlLitHelper.clock_time));
                String clock_note = cursor.getString(cursor.getColumnIndex(MySqlLitHelper.clock_note));
                String repeat = cursor.getString(cursor.getColumnIndex(MySqlLitHelper.repeat));
                Integer isalert = cursor.getInt(cursor.getColumnIndex(MySqlLitHelper.isalert));
                Integer ison = cursor.getInt(cursor.getColumnIndex(MySqlLitHelper.ison));
                String url= cursor.getString(cursor.getColumnIndex(MySqlLitHelper.clock_url));
                Log.d(LOG_TAG,clock_time+clock_note+repeat+isalert+ison+url);
                Clock clock = new Clock(id,clock_time,clock_note,repeat,isalert,ison,url);
                list.add(clock);
            }
        }
        cursor.close();
        db.close();
        return list;
    }
    /**
     *  得到新增的闹钟。
     * @return 闹钟列表
     */
    public Clock getAddClock(){
        SQLiteDatabase db = mySqlLitHelper.getWritableDatabase();
        Cursor cursor =  db.rawQuery("select * from "+MySqlLitHelper.table_name,null);
        Clock clock=null;
        if (cursor.moveToLast()){
                Integer id = cursor.getInt(cursor.getColumnIndex(MySqlLitHelper.table_id));
                String clock_time = cursor.getString(cursor.getColumnIndex(MySqlLitHelper.clock_time));
                String clock_note = cursor.getString(cursor.getColumnIndex(MySqlLitHelper.clock_note));
                String repeat = cursor.getString(cursor.getColumnIndex(MySqlLitHelper.repeat));
                Integer isalert = cursor.getInt(cursor.getColumnIndex(MySqlLitHelper.isalert));
                Integer ison = cursor.getInt(cursor.getColumnIndex(MySqlLitHelper.ison));
                String url= cursor.getString(cursor.getColumnIndex(MySqlLitHelper.clock_url));
                Log.d(LOG_TAG,clock_time+clock_note+repeat+isalert+ison+url);
                clock = new Clock(id,clock_time,clock_note,repeat,isalert,ison,url);

        }
        cursor.close();
        db.close();
        return clock;
    }

    /**
     * 关闭或者启用闹钟
     */
    public void offOrOn(Integer id,boolean ison){
        Integer f = ison==true?1:0;
        String sql = "update "+MySqlLitHelper.table_name+" set  "+MySqlLitHelper.ison+"= ? where "+MySqlLitHelper.table_id+" = ?";
        SQLiteDatabase db = mySqlLitHelper.getWritableDatabase();
        //执行SQL
        db.execSQL(sql,new String[]{String.valueOf(f),String.valueOf(id)});
        db.close();
    }
    /**
     * 插入数据
     * @param clock 闹钟实体
     */
    public void inserClock(Clock clock){
        //插入一条闹钟数据,默认开启状态
        SQLiteDatabase db=mySqlLitHelper.getWritableDatabase();
        Integer isalert = clock.isIsalert()==true?1:0;
        String sql="insert into " + MySqlLitHelper.table_name+ " ("+MySqlLitHelper.clock_time+", "+MySqlLitHelper.clock_note+", " +
                ""+MySqlLitHelper.repeat+","+MySqlLitHelper.isalert+","+MySqlLitHelper.ison+","+MySqlLitHelper.clock_url+") values (?, ?, ?, ?,?,?)";
        db.execSQL(sql,new String[]{clock.getClock_time_str(),clock.getClock_note(),clock.repeatSaveStr(),String.valueOf(isalert),"1",clock.getClock_ring()});
        db.close();
    }

}
