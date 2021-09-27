package com.example.rxjavaexample.net.interceptor;

import android.content.Context;
import android.text.TextUtils;

import com.example.rxjavaexample.AccountInfo;
import com.example.rxjavaexample.Define;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * jSessionId Interceptor
 */
public class SessionInterceptor implements Interceptor {
    private Context context;

    public SessionInterceptor(Context context) {
        this.context = context;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (originalResponse.request().url().toString().contains(Define.Server.WEB_URL)) {
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                for (String header : originalResponse.headers("Set-Cookie")) {
                    if(header.contains("JSESSIONID")) {
                        String jSessionId = null;
                        String[] cookies = header.split("\\;");
                        int pos = -1;
                        String fieldName = "JSESSIONID=";
                        for (; ++pos < cookies.length; ) {
                            String cookie = cookies[pos];
                            if (!TextUtils.isEmpty(cookie)) {
                                int startIndex = cookie.indexOf(fieldName);
                                if (startIndex > -1) {
                                    jSessionId = cookie.substring(startIndex + fieldName.length(), cookie.length());
                                    break;
                                }
                            }
                        }

                        /* jsession id save */
                        if (!TextUtils.isEmpty(jSessionId)) {
                            AccountInfo.getInstance().setJSessionID(context, jSessionId);
                            break;
                        }
                    }
                }
            }
        }

        return originalResponse;
    }
}
