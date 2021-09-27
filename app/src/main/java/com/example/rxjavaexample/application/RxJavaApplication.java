package com.example.rxjavaexample.application;

import android.app.Application;

import com.example.rxjavaexample.AccountInfo;
import com.example.rxjavaexample.net.ApiManager;
import com.facebook.stetho.Stetho;


public class RxJavaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ApiManager.getInstance(this).initialize();

        // Stetho 설정
        Stetho.initializeWithDefaults(this);

        AccountInfo.getInstance().initialize();
    }
}
