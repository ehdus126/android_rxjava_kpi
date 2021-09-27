package com.example.rxjavaexample.net;

import android.content.Context;

import com.example.rxjavaexample.BuildConfig;
import com.example.rxjavaexample.Define;
import com.example.rxjavaexample.interfaces.WebApi;
import com.example.rxjavaexample.net.interceptor.RedirectInterceptor;
import com.example.rxjavaexample.net.interceptor.RequestHeaderInterceptor;
import com.example.rxjavaexample.net.interceptor.SessionInterceptor;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * retrofit 관리 class
 */
public class ApiManager {
    private static ApiManager apiManager;
    private WebApi webApi;
    private Context context;

    public static ApiManager getInstance(Context context) {
        synchronized (ApiManager.class) {
            if (apiManager == null) {
                apiManager = new ApiManager(context);
            }
            return apiManager;
        }
    }

    public ApiManager(Context context) {
        this.context = context;
    }

    public void initialize() {
        webApi = init(Define.Server.WEB_URL, WebApi.class);
    }

    public WebApi getWebApi() {
        return webApi;
    }

    private <T>T init(String baseUrl, Class<T> tClass) {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        Gson gson = new GsonBuilder().setLenient().create();

        OkHttpClient.Builder okHttpClient = new OkHttpClient()
                .newBuilder()
                // Request Header Interceptor
                .addInterceptor(new RequestHeaderInterceptor(context))
                // 307 Error 처리 Redirect Interceptor
                .addInterceptor(new RedirectInterceptor())
                .addInterceptor(new SessionInterceptor(context))
                // 요청을 시작한 후 서버와의 TCP handShake 가 완료되기까지 지속되는 시간
                .connectTimeout(15, TimeUnit.MINUTES)
                // 연결이 설정되면 모든 바이트가 전송되는 속도를 감시(서버로부터 응답시간이 read timeout 을 초과하면 요청실패로 계산) / socket timeout
                .readTimeout(15, TimeUnit.SECONDS)
                // 얼마나 빨리 서버에 바이트를 보낼 수 있는지 확인
                .writeTimeout(15, TimeUnit.SECONDS)
                // OkHttp3 로 바뀌면서 cookieJar 를 사용하여 CookieManager 에게 위임하도록 세션을 유지(연결)
                .cookieJar(new JavaNetCookieJar(cookieManager));

        // Debug 인 경우에만 Logging 적용
        if (BuildConfig.DEBUG) {
            // Http Logging
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

            okHttpClient.addInterceptor(httpLoggingInterceptor)
                    .addNetworkInterceptor(new StethoInterceptor()); // Stetho : 네트워크 통신에 대한 정보를 볼 수 있음 (Method 요청 값, 결과 값 등)
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient.build())
                .build();

        return retrofit.create(tClass);
    }
}
