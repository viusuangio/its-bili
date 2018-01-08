package com.vicoyu.app;

import android.app.Application;

import cc.duduhuo.applicationtoast.AppToast;

/**
 * Created by Viusuangio on 2018/1/5.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppToast.init(this);
    }
}
