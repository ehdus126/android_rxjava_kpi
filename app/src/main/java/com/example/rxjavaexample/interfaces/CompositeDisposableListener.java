package com.example.rxjavaexample.interfaces;

import io.reactivex.disposables.Disposable;

/**
 * Copyright ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.| Confidential
 *
 * @ Description :
 * @ Class : CompositeDisposableInterface
 * @ Created by : limdoyeon
 * @ Created Date : 2021. 08. 02.
 */
public interface CompositeDisposableListener {
    void disposeCompositeDisposable();

    void addCompositeDisposable(Disposable disposable);
}
