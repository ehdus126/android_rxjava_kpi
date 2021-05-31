package com.example.rxjavaexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rxjavaexample.databinding.ActivityHeartBeatExampleBinding;
import com.example.rxjavaexample.helper.OkHttpHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

/**
 * Copyright ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.| Confidential
 *
 * @ Description :
 * @ Class : HeartBeatExampleActivity
 * @ Created by : limdoyeon
 * @ Created Date : 2021. 05. 26.
 */
public class HeartBeatExampleActivity extends AppCompatActivity {
    private static final String TAG = HeartBeatExampleActivity.class.getSimpleName();
    private ActivityHeartBeatExampleBinding binding;

    public static void start(@NonNull AppCompatActivity activity) {
        Intent intent = new Intent(activity, HeartBeatExampleActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(HeartBeatExampleActivity.this, R.layout.activity_heart_beat_example);

        // 호출할 때마다 매번 다른 문구들을 무작위로 출력
        // 2초 간격으로 서버에 ping 보내기
        Observable.timer(2, TimeUnit.SECONDS)
                .map(val -> Define.HeartBeatExample.SERVER_URL)
                .map(OkHttpHelper::get) // 서버에 저장된 URL 정보를 얻기 위해 실행
                .repeat()
                .subscribe(res -> Log.i(TAG, "Ping Result : " + res));

        // timer() 함수 한 번 호출하면 종료되는데 계속 반복되는 이유? repeat() 함수 때문에
        // repeat() : 동작이 한 번 끝난 다음 다시 구독하는 방식으로 동작
    }
}
