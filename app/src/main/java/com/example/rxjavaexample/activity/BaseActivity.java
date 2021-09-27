package com.example.rxjavaexample.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.rxjavaexample.interfaces.CompositeDisposableListener;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Copyright ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.| Confidential
 *
 * @ Description :
 * @ Class : BaseActivity
 * @ Created by : limdoyeon
 * @ Created Date : 2021. 08. 02.
 */
public class BaseActivity extends RxAppCompatActivity implements CompositeDisposableListener {
    public static final String EXTRA_ACTIVITY_INFO = "EXTRA_ACTIVITY_INFO";

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 생명주기에 따라 Disposable 구독 취소
        if (compositeDisposable != null) {
            disposeCompositeDisposable();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void disposeCompositeDisposable() {
        compositeDisposable.dispose();
    }

    @Override
    public void addCompositeDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }
}
