package com.example.rxjavaexample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rxjavaexample.databinding.ActivityDataQueryExampleBinding;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

/**
 * Copyright ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.| Confidential
 *
 * @ Description :
 * @ Class : DataQueryExampleActivity
 * @ Created by : limdoyeon
 * @ Created Date : 2021. 04. 24.
 */
public class DataQueryExampleActivity extends AppCompatActivity {
    private ActivityDataQueryExampleBinding binding;
    private int tvTotal;

    public static void start(@NonNull AppCompatActivity activity) {
        Intent intent = new Intent(activity, DataQueryExampleActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(DataQueryExampleActivity.this, R.layout.activity_data_query_example);

        List<Pair<String, Integer>> sales = new ArrayList<>();

        // Pair.of() : 자료구조 중 하나로 형태가 같거나 다른 값을 2개 넣을 수 있음
        // 함수형 프로그래밍에서는 일종의 자료구조 클래스를 생성하기 보다는 Pair 혹은 Tuple 같은 일반화 된 자료구조를 선호
        sales.add(Pair.of(Define.QueryExample.TV, 2500));
        sales.add(Pair.of(Define.QueryExample.CAMERA, 300));
        sales.add(Pair.of(Define.QueryExample.TV, 3000));
        sales.add(Pair.of(Define.QueryExample.PHONE, 100));

        Maybe<Integer> tvSales = Observable.fromIterable(sales)
                // TV 매출 필터링
                .filter(sale -> "TV".equals(sale.getLeft()))
                // 해당 값을 가져옴
                .map(sale -> sale.getRight())
                // 합계를 구함
                .reduce((sale1, sale2) -> sale1 + sale2);

        tvSales.subscribe(total -> tvTotal = total);
        binding.tvSaleTextView.setText(String.valueOf(tvTotal));
    }
}
