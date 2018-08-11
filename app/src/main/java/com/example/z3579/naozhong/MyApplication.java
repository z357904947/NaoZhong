package com.example.z3579.naozhong;

import android.app.Application;

public class MyApplication extends Application {
    //当前系统为24小时标记,false代表为24,true代表12小时制
    private boolean falg12or24;//判断当前系统时间制式，存储到全局变量中方便在fragment中使用

    public void setFalg12or24(boolean falg12or24) {
        this.falg12or24 = falg12or24;
    }

    public boolean isFalg12or24() {
        return falg12or24;
    }
}
