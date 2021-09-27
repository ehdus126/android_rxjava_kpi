package com.example.rxjavaexample.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel 의 베이스 클래스
 */
public abstract class BaseViewModel extends AndroidViewModel implements Callback {
    private static final String TAG = BaseViewModel.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    // Retrofit
    public void request(Call<?> call) {
        call.enqueue(this);
    }

    public void request(Single<?> single) {
        disposable.add(single
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<Object>() {
            @Override
            public void onSuccess(Object object) {
                Log.i(TAG, "request onSuccess() called. ");
                onResponseSuccess(object);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "request onError() called. e : " + e.getMessage());
            }
        }));
    }

    public void requestForString(Single<ResponseBody> single) {
        disposable.add(single
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                Log.i(TAG, "requestForString onSuccess() called. ");
                try {
                    onResponseSuccess(responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "requestForString onError() called. e : " + e.getMessage());
            }
        }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.clear();
        }
    }

    abstract void onResponseSuccess(Object response);
    abstract void onResponseFailure(String resultCode, String resultMessage);

    /**
     * Retrofit 응답 성공
     * @param call
     * @param response
     */
    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) {
        if (!response.isSuccessful() && response.body() == null) {
            return;
        }

        Headers headers = response.headers();
        String resultCode = headers.get("resultCode");
        String resultMessage = headers.get("resultMessage");

        if ("0000".equalsIgnoreCase(resultCode)) {
            onResponseSuccess(response.body());
        } else {
            onResponseFailure(resultCode, resultMessage);
        }

    }

    /**
     * Retrofit 응답 실패 (resultCode 가 0000 이 아닌 경우)
     * @param call
     * @param t
     */
    @Override
    public void onFailure(@NonNull Call call, @NonNull Throwable t) {
        Log.i(TAG, "BaseViewModel onFailure() called. t : " + t.getMessage());
    }
}
