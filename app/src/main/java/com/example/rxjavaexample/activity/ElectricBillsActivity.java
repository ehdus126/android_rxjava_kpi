package com.example.rxjavaexample.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rxjavaexample.R;
import com.example.rxjavaexample.databinding.ActivityElectricBillsExampleBinding;

import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;

import io.reactivex.rxjava3.core.Observable;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Copyright ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.| Confidential
 *
 * @ Description :
 * @ Class : ElectricBillsActivity
 * @ Created by : limdoyeon
 * @ Created Date : 2021. 06. 26.
 */

/**
 * 전력 전기 요금표
 * 200kWh 이하 사용 : 기본 요금_910원 / 전력량 요금_처음200kWh 까지 93.3원
 * 201kWh ~ 400kWh 사용 : 기본요금_1,600원 / 전력량 요금_다음 200kWh 까지 187.9원
 * 400kWh 초과 : 기본요금_7,300원 / 전력량 요금_400kWh 초과 280.6
 */
public class ElectricBillsActivity extends AppCompatActivity {
    private static final String TAG = ElectricBillsActivity.class.getSimpleName();
    private ActivityElectricBillsExampleBinding binding;

    private static int ELECTRIC_200 = 200;
    private static int ELECTRIC_400 = 400;

    private static int BASIC_PRICE_910 = 910;
    private static int BASIC_PRICE_1600 = 1600;
    private static int BASIC_PRICE_7300 = 7300;

    private static double ELECTRIC_PRICE_200 = 93.3;
    private static double ELECTRIC_PRICE_400 = 187.9;
    private static double ELECTRIC_PRICE_MORE_400 = 280.6;

    private int index = 0;

    public static void start(@NonNull AppCompatActivity activity) {
        Intent intent = new Intent(activity, ElectricBillsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ElectricBillsActivity.this, R.layout.activity_electric_bills_example);

        String[] data = {"100", // 910 + 93.3 * 100 = 10,240원
                "300"   // 1,600 + 187.9 * 300 = 39,050원
        };

        /**
         * 부수효과 적용 됨
         */
        Observable<Integer> basePrice = Observable.fromArray(data)
                .map(Integer::parseInt)
                .map(value -> {
                    if (value <= ELECTRIC_200) {
                        return BASIC_PRICE_910;
                    } else if (value <= ELECTRIC_400) {
                        return BASIC_PRICE_1600;
                    } else {
                        return BASIC_PRICE_7300;
                    }
                });

        Observable<Integer> usagePrice = Observable.fromArray(data)
                .map(Integer::parseInt)
                .map(value -> {
                    double series1 = min(ELECTRIC_200, value) * ELECTRIC_PRICE_200;
                    double series2 = min(ELECTRIC_200, max(value - ELECTRIC_200, 0)) * ELECTRIC_PRICE_400;
                    double series3 = min(0, max(value - ELECTRIC_400, 0)) * ELECTRIC_PRICE_MORE_400;
                    return (int) (series1 + series2 + series3);
                });

        Observable<Integer> source = Observable.zip(
                basePrice, usagePrice, (value1, value2) -> value1 + value2);

        // 결과 출력
        source.map(value -> new DecimalFormat("#,###").format(value))
                .subscribe(value -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < data.length; i++) {
                        stringBuilder.append("Usage : " + data[index] + "kWh => ");
                        stringBuilder.append("Price : " + value + "원 \n");
                    }
                    binding.result.setText(stringBuilder.toString());

                    // 부수 효과
                    // 전력 사용량을 출력하기 위한 멤버변수 index 참조 -> 함수형 프로그래밍 기본 원칙에 어긋남!!!!
                    index++;
                });


        /**
         * 부수효과 없앰 (처리방법)
         * 1. data 를 추가로 넘겨주는 방법
         * 2. zip() 함수는 2개 이상의 Observable 결합 가능
         * 3. Pair 클래스 사용
         */
        Observable<Pair<String, Integer>> observable = Observable.zip(
                basePrice, usagePrice, Observable.fromArray(data),
                (value1, value2, i) -> Pair.of(i, value1 + value2));

        // 결과 출력
        observable.map(value -> Pair.of(value.getLeft(),
                new DecimalFormat("#,###").format(value.getValue())))
                .subscribe(value -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < data.length; i++) {
                        stringBuilder.append("Usage : " + value.getLeft() + "kWh => ");
                        stringBuilder.append("Price : " + value.getRight() + "원 \n");
                    }
                    binding.result2.setText(stringBuilder.toString());

                    // 멤버변수 index 사라짐!!!
                });
    }
}
