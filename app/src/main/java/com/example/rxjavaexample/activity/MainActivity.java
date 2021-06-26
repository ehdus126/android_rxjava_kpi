package com.example.rxjavaexample.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rxjavaexample.R;
import com.example.rxjavaexample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        binding.gugudanButton.setOnClickListener(onClickListener);
        binding.queryButton.setOnClickListener(onClickListener);
        binding.heartBeatButton.setOnClickListener(onClickListener);
        binding.electricBillsButton.setOnClickListener(onClickListener);
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