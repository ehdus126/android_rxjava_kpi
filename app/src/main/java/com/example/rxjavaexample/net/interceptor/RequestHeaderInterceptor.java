package com.example.rxjavaexample.net.interceptor;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.rxjavaexample.DeviceInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class RequestHeaderInterceptor implements Interceptor {
    private static final String TAG = RequestHeaderInterceptor.class.getSimpleName();
    private Context context;

    public RequestHeaderInterceptor(Context context) {
        this.context = context;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        String osVersion = Build.VERSION.RELEASE;
        String appVersion = DeviceInfo.getAppVersion(context);

        Request request = chain.request().newBuilder()
                .addHeader("appVersion", appVersion)
                .addHeader("Accept", "application/json")
                .addHeader("osVersion", osVersion)
                .addHeader("osType", "android")
                .addHeader("User-Agent", DeviceInfo.getCustomUserAgent(context))
                .build();
        Log.i(TAG, "header : " + request.headers().toString());
        // headers={appVersion=91.3.0, Accept=application/json, osVersion=9, osType=android, User-Agent=Starbucks_Android/91.5.1.0(Android:9), model=SM-G950N, msgVersion=2}
        return chain.proceed(request);
    }
}
