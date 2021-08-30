package com.example.rxjavaexample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rxjavaexample.R;
import com.example.rxjavaexample.databinding.ActivitySearchBinding;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * Copyright ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.| Confidential
 *
 * @ Description : 검색어 키워드를 입력 후 500ms 안에 입력된 것이 없으면 검색을 시작
 * @ Class : SearchActivity
 * @ Created by : limdoyeon
 * @ Created Date : 2021. 07. 28.
 */
public class SearchActivity extends RxAppCompatActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private ActivitySearchBinding binding;
    private DisposableObserver<CharSequence> disposable;

    public static void start(@NonNull AppCompatActivity activity) {
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SearchActivity.this, R.layout.activity_search);

        disposable = getObservable()
                // 빠른시간안에 액티비티 실행을 다시하면 중복 발생 -> debounce() 로 문제해결
                .debounce(5000, TimeUnit.MILLISECONDS)
                .filter(s -> !TextUtils.isEmpty(s))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver());
    }

    private Observable<CharSequence> getObservable() {
        return Observable.create(emitter -> binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emitter.onNext(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }));
    }

    private DisposableObserver<CharSequence> getObserver() {
        return new DisposableObserver<CharSequence>() {
            @Override
            public void onNext(CharSequence charSequence) {
                Log.i(TAG, "Search : " + charSequence.toString());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
}
