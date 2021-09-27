package com.example.rxjavaexample.interfaces;

import android.content.Context;

import com.example.rxjavaexample.model.InboxRes;
import com.example.rxjavaexample.net.ApiManager;

import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class RequestMethod {
    public static RequestMethod instance;

    public static RequestMethod getInstance() {
        synchronized (RequestMethod.class) {
            if (instance == null) {
                instance = new RequestMethod();
            }
            return instance;
        }
    }

    public Single<InboxRes> requestInboxRes(Context context, Map<String, String> params) {
        return ApiManager.getInstance(context).getWebApi().inboxSingleRes(params)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ResponseBody> requestInboxDetail(Context context, String url, String agent, String appVersion, String jsessionid, Map<String, String> params) {
        return ApiManager.getInstance(context).getWebApi().inboxDetailSingleRes(url, agent, appVersion, jsessionid, params)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
