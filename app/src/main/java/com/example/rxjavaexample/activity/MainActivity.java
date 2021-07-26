package com.example.rxjavaexample.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rxjavaexample.R;
import com.example.rxjavaexample.databinding.ActivityMainBinding;

import hu.akarnokd.rxjava2.math.MathFlowable;
import io.reactivex.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        binding.gugudanButton.setOnClickListener(onClickListener);
        binding.queryButton.setOnClickListener(onClickListener);
        binding.heartBeatButton.setOnClickListener(onClickListener);
        binding.electricBillsButton.setOnClickListener(onClickListener);

        /**
         * 수학함수
         */
        Integer[] data = {1, 2, 3, 4};
        // count
        Single<Long> source = Observable.fromArray(data)
                .count();
        source.subscribe(count -> Log.i(TAG, "count : " + count));

        // max
        Flowable.fromArray(data)
                .to(MathFlowable::max)
                .subscribe(max -> Log.i(TAG, "max : " + max));

        // min
        Flowable.fromArray(data)
                .to(MathFlowable::min)
                .subscribe(min -> Log.i(TAG, "min : " + min));

        // sum
        Flowable<Integer> sumFlowable = Flowable.fromArray(data)
                .to(MathFlowable::sumInt);
        sumFlowable.subscribe(sum -> Log.i(TAG, "sum : " + sum));

        // average
        Flowable<Double> averageFlowable = Flowable.fromArray(data)
                .to(MathFlowable::averageDouble);
        averageFlowable.subscribe(average -> Log.i(TAG, "average : " + average));
    }

    private View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.gugudanButton:
                GugudanExampleActivity.start(MainActivity.this);
                break;
            case R.id.queryButton:
                DataQueryExampleActivity.start(MainActivity.this);
                break;
            case R.id.heartBeatButton:
                HeartBeatExampleActivity.start(MainActivity.this);
                break;
            case R.id.electricBillsButton:
                ElectricBillsActivity.start(MainActivity.this);
                break;
            default:
                Log.d(TAG, "onClickListener error.");
                break;
        }
    };
}