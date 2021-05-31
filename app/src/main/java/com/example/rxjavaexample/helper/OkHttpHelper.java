package com.example.rxjavaexample.helper;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.| Confidential
 *
 * @ Description : HTTP GET 명령을 호출 후 결과 리턴
 * @ Class : OkHttpHelper
 * @ Created by : limdoyeon
 * @ Created Date : 2021. 05. 26.
 */
public class OkHttpHelper {
    private static final String TAG = OkHttpHelper.class.getSimpleName();
    private static OkHttpClient httpHelper = new OkHttpClient();

    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = httpHelper.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            Log.i(TAG, "e :" + e.getMessage());
            throw e;
        }
        return null;
    }
}
