package com.example.rxjavaexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 계정 정보
 */
public class AccountInfo {
    private static final String TAG = AccountInfo.class.getSimpleName();
    private static AccountInfo accountInfo;

    private static final String JSESSION_ID = "JSessionId";


    public static AccountInfo getInstance() {
        synchronized (AccountInfo.class) {
            if (accountInfo == null) {
                accountInfo = new AccountInfo();
            }
        }
        return accountInfo;
    }

    public void initialize() {
        Log.i(TAG, "initialize() called");
    }

    // jsessionId 상태 저장
    public void setJSessionID(Context ctx, String jsessionId) {
        putString(ctx, JSESSION_ID, jsessionId);
    }

    // jsessionId 저장 상태 반환
    public String getJSessionID(Context context) {
        return getString(context, JSESSION_ID, "");
    }

    public static synchronized void putString(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
        // editor.commit();
        // commit() : 처리결과를 boolean 타입으로 반환, 반환값이 필요없다면 apply 로 처리하는게 성능적으로 좋음(apply API9 부터 지원)
    }

    public static synchronized String getString(Context context, String key, String def) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, def);
    }
}
