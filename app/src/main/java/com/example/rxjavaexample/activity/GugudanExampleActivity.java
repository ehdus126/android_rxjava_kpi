package com.example.rxjavaexample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rxjavaexample.R;
import com.example.rxjavaexample.databinding.ActivityGugudanExampleBinding;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;

/**
 * Copyright ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.| Confidential
 *
 * @ Description : RX JAVA 를 이용한 구구단 예제
 * @ Class : GugudanExampleActivity
 * @ Created by : limdoyeon
 * @ Created Date : 2021. 04. 17.
 */
public class GugudanExampleActivity extends AppCompatActivity {
    private static final String TAG = GugudanExampleActivity.class.getSimpleName();
    private ActivityGugudanExampleBinding binding;
    private int inputNum;

    public static void start(@NonNull AppCompatActivity activity) {
        Intent intent = new Intent(activity, GugudanExampleActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(GugudanExampleActivity.this, R.layout.activity_gugudan_example);

        // Observable 구구단
        binding.playGugudanObservableButton.setOnClickListener(onClickListener);
        // flatMap() 구구단
        binding.playGugudanFlatMapButton.setOnClickListener(onClickListener);
        // 인라인을 이용한 flatMap() 구구단
        binding.playGugudanInlineFlatMapButton.setOnClickListener(onClickListener);
        // resultSelector 를 이용한 flatMap 구구단
        binding.playGugudanResultSelectorButton.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String inputDanNum = binding.inputDanNum.getText().toString();
            if (TextUtils.isEmpty(inputDanNum)) {
                Log.d(TAG, "inputDanNum is null.");
                return;
            }

            inputNum = Integer.parseInt(inputDanNum);

            switch (v.getId()) {
                case R.id.playGugudanObservableButton:
                    playGugudanWithObservable();
                    break;
                case R.id.playGugudanFlatMapButton:
                    playGugudanWithFlatMap();
                    break;
                case R.id.playGugudanInlineFlatMapButton:
                    playGugudanWithInlineFlatMap();
                    break;
                case R.id.playGugudanResultSelectorButton:
                    playGugudanWithResultSelector();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Observable 를 이용한 구구단 실행
     */
    private void playGugudanWithObservable() {
        Observable<Integer> source= Observable.range(1, 9);
        source.subscribe(row -> Log.i(TAG, inputNum + " * " + row + " = " + inputNum * row));
    }

    /**
     * FlatMap 을 이용한 구구단 실행 (flatMap() : 결과가 Observable 로 나옴)
     *  제네릭 함수 인터페이스
     *  1. Predicate<T> : t 값을 받아서 참 & 거짓을 반환
     *  2. Consumer<T> : t 값을 받아서 처리 (반환값 없음)
     *  3. Function<T, R> : t 값을 받아서 r 값을 반환
     */
    private void playGugudanWithFlatMap() {
        // 1 개의 입력을 받아 9 개의 결과 출력 -> Function<T, R> 이용
        // 여러개의 값 출력해야 하기 때문에 String 이 아닌 Observable<String> 이용
        // 함수 구현(Integer 값을 받아서 String 형태로 반환)
        Function<Integer, Observable<String>> gugudan = num ->
                Observable.range(1, 9).map(row -> num + " * " + row + " = " + inputNum * row);

        Observable<String> source = Observable.just(inputNum).flatMap(gugudan);
        source.subscribe(text -> Log.i(TAG, text));
    }

    /**
     * 인라인을 이용, gugudan 함수를 FlatMap() 함수 내부에 넣어 구구단 실행
     */
    private void playGugudanWithInlineFlatMap() {
        Observable<String> source = Observable.just(inputNum).flatMap(num -> Observable.range(1, 9)
                .map(row -> num + " * " + row + " = " + inputNum * row));
        source.subscribe(text -> Log.i(TAG, text));
    }

    /**
     * resultSelector 를 이용한 구구단 실행
     */
    private void playGugudanWithResultSelector() {
        // BitFunction : 입력인자 2개의 값을 받음
        Observable<String> source = Observable.just(inputNum).flatMap(gugu -> Observable.range(1, 9),
                (gugu, i) -> gugu + " * " + i + " = " + gugu * i);
        source.subscribe(text -> Log.i(TAG, text));
    }
}
