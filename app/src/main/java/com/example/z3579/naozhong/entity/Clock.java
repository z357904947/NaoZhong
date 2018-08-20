package com.example.z3579.naozhong.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Clock implements Parcelable{
    private Integer id;//ID
    private Date clock_time;//闹钟时间
    private String clock_time_str;//闹钟时间，字符串类型
    private String clock_note="闹钟";//闹钟标签
    private String[] repeat;//该数组放置每周重复的星期
    private String clock_ring;//铃声
    private boolean isalert=false;//稍后提醒，true稍后提醒功能打开，false关闭
    private boolean ison=true;//当前闹钟是否启用，false关闭，true启用,数据库中1代表true，0代表false
    private static Map<String,String> map=new HashMap<String,String>();
    private final static String LOG_TAG="Clock_测试";

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");//用于得到当前年月日格式
    private static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");//用于对年月日时分格式转换（HH24小时）
    //用于显示闹钟重复星期内容
    static {
        map.put("1","周日 ");
        map.put("2","周一 ");
        map.put("3","周二 ");
        map.put("4","周三 ");
        map.put("5","周四 ");
        map.put("6","周五 ");
        map.put("7","周六 ");
    }
    //得到小时
    public int getHour(){

        return  Integer.parseInt(clock_time_str.substring(0,clock_time_str.indexOf(":")));
    }
    //得到分钟
    public int getMinute(){
        return Integer.parseInt(clock_time_str.substring(clock_time_str.indexOf(":")+1));
    }
    protected Clock(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        clock_time_str = in.readString();
        clock_note = in.readString();
        repeat = in.createStringArray();
        clock_ring = in.readString();
        isalert = in.readByte() != 0;
        ison = in.readByte() != 0;
    }

    public static final Creator<Clock> CREATOR = new Creator<Clock>() {
        @Override
        public Clock createFromParcel(Parcel in) {
            return new Clock(in);
        }

        @Override
        public Clock[] newArray(int size) {
            return new Clock[size];
        }
    };

    /**
     * 返回闹钟时间是上午还是下午
     * @return
     */
    public String getap_am(){
        Integer a =Integer.valueOf(clock_time_str.substring(0,2));
        if(a>=12){
            return "下";
        }
        return "上";
    }

    /**
     * 返回12小时制的闹钟时间
     * @return
     */
    public String get12time(){
        Integer a =getHour();
        Integer b = getMinute();
        if(a>12){
            return (a-12)+":"+b;
        }else if(a==0){
            return (a+12)+":"+b;
        }
        return getClock_time_str_Delete0();
    }
    public String getClock_note() {
        return clock_note;
    }

    public Integer getId() {
        return id;
    }

    public boolean isIson() {
        return ison;
    }

    /**
     * 根据闹钟设置时间得到第一次闹钟响应时间，当前为上午9点，如果你设置了一个闹钟为上午7点，
     * 则该闹钟的响应时间为明天的上午7点，如你设置了一个闹钟为上午10点，则第一次响应时间为今天的上午10点
     * @return 闹钟第一次提示时间
     */
    public Long getStartTime(){
        //1、得到当前时间，2给闹钟时间加上当前年月日，3进行比较，若大于当前时间则该时间为第一次响应时间，若小于，则给该时间再加1天
        //得到当前时间
        Calendar calendar = Calendar.getInstance();
        Date dnow=calendar.getTime();
        //得到当前年月日字符串
        String nowDayStr=format.format(dnow);
        Log.d(LOG_TAG,"转换时间，为："+nowDayStr);
        //拼接闹钟时间
        String clockTimeStr=nowDayStr+this.clock_time_str;
        //将拼接后的字符穿转换为时间
        Date date;
        long startTime=0;
        try {
            date=format2.parse(clockTimeStr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            //比较两个时间的大小
            if(calendar.after(c)){ //如果calendar在c之后
                //即闹钟时间小于当前时间，给闹钟时间+1天
                Log.d(LOG_TAG,"闹钟时间小于当前时间，加1天");
                c.add(Calendar.DAY_OF_MONTH,1);
            }
            Log.d(LOG_TAG,"转换后闹钟时间为："+format2.format(c.getTime()));
            startTime =c.getTimeInMillis();//获得闹钟时间的毫秒数

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startTime;

    }
    public  Clock(Integer id, String clock_time, String note, String repeat, Integer isalert,Integer ison,String url){
        this.id=id;
        this.clock_time_str=clock_time;
        this.clock_note=note;
        if(repeat!=null&&repeat.length()>0){
            this.repeat=repeat.split(",");
        }
        if(isalert==1){
            this.isalert=true;
        }else {
            this.isalert=false;
        }
         this.ison=ison==1?true:false;

        this.clock_ring=url;
    }

    public String getClock_time_str() {
        return clock_time_str;
    }

    public String getClock_time_str_Delete0() {
        String a =clock_time_str;
        if(clock_time_str.substring(0,1).equals("0")&&!clock_time_str.substring(1,1).equals(":")){//取掉时间格式开始的0,条件为第一个字符为0，第二个字符不为冒号，防止0：54这种格式出错
            a=clock_time_str.substring(1);
        }
        return a;
    }

    /**
     * 得到数组的拼接字符串
     * @return
     */
    public String repeattoString() {
        StringBuffer str = new StringBuffer("");
        if(repeat!=null){
            for (String s : repeat) {
                str.append(map.get(s));
            }
        }
        return str.toString();
    }
    public String repeatSaveStr(){
        StringBuffer str = new StringBuffer();
        int i=1;
        if(repeat!=null){
            for (String s : repeat) {
                if(i==1){
                    str.append(s);
                }else {
                    str.append(",");
                    str.append(s);
                }
                i++;
            }
        }
        return str.toString();
    }
    public Clock(){

    }

    public void setClock_time_str(String clock_time_str) {
        this.clock_time_str = clock_time_str;
    }

    public void setClock_time(Date clock_time) {
        this.clock_time = clock_time;
    }

    public void setClock_note(String clock_note) {
        this.clock_note = clock_note;
    }

    public void setRepeat(String[] repeat) {
        this.repeat = repeat;
    }
    public void setRepeat(boolean[]  repeat) {
        StringBuffer str = new StringBuffer();
        for (int i =0;i<repeat.length;i++){
            if(repeat[i]){
                if(str.length()==0){
                    str.append(String.valueOf(i+1));
                }else {
                    str.append(","+String.valueOf(i+1));
                }
            }
        }
        Log.d("测试","重复："+str.toString());
        if(str.length()==0){
            this.repeat=null;
        }else {
            this.repeat=str.toString().split(",");
        }

    }

    public void setClock_ring(String clock_ring) {
        this.clock_ring = clock_ring;
    }

    public void setIsalert(boolean isalert) {
        this.isalert = isalert;
    }

    public Date getClock_time() {
        return clock_time;
    }

    /**
     *得到闹钟标签和重复星期字符串
     * @return
     */
    public String getClock_noteandrepeat() {
        String a  =clock_note;
        if(repeat!=null&&repeat.length>0){

            String str = repeattoString();
            a=clock_note+","+str;
            Log.d("测试","重复："+str.toString());
        }

        return a;
    }

    public String[] getRepeat() {
        return repeat;
    }

    public String getClock_ring() {
        return clock_ring;
    }

    public boolean isIsalert() {
        return isalert;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(clock_time_str);
        dest.writeString(clock_note);
        dest.writeStringArray(repeat);
        dest.writeString(clock_ring);
        dest.writeByte((byte) (isalert ? 1 : 0));
        dest.writeByte((byte) (ison ? 1 : 0));
    }
}
