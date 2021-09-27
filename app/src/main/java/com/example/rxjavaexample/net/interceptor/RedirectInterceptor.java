package com.example.rxjavaexample.net.interceptor;

import com.example.rxjavaexample.Define;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class RedirectInterceptor implements Interceptor {
    private static final String URL_LOCATION = "Location";

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (Define.Server.ERROR_307 == response.code()) {
            if (request.url().toString().startsWith("http")) {
                request = request.newBuilder()
                        .url(response.header(URL_LOCATION))
                        .build();
                response = chain.proceed(request);
            }
        }
        return response;
    }
}
