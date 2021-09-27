package com.example.rxjavaexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;


/**
 * 디바이스 정보 조회
 */
public class DeviceInfo {
    public static final String DEVICE_ID_KEY = "STARBUCKS_APP_UDID_INFO_ID";
    public static final String DEVICE_ID_KEY_NEW = "STARBUCKS_APP_UDID_INFO_ID_NEW";
    public static final String DEVICE_PUSH_KEY = "GCMID";
    public static final String APP_VERSION = "APP_VERSION";
    public static final String USER_AGENT = "USER_AGENT";
    public static final String OS = "2"; //OS 구분자(1=iOS, 2=Android)

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

    public static void setUUID(Context context, String UDID) {
        putString(context, DEVICE_ID_KEY, UDID);
    }

    public static void setNewUUID(Context context, String UDID) {
        putString(context, DEVICE_ID_KEY_NEW, UDID);
    }

    public static String getUUID(Context context) {
        return getString(context, DEVICE_ID_KEY, "");
    }

    public static String getNewUUID(Context context) {
        return getString(context, DEVICE_ID_KEY_NEW, "");
    }

    // 커스텀 유저 에이전트 정보를 반환
    public static String getCustomUserAgent(Context context) {
        String customUserAgent = getString(context, USER_AGENT, null);
        if (customUserAgent == null) {
            customUserAgent = createCustomUserAgent(context);
        }
        return customUserAgent;
    }

    public static String createCustomUserAgent(Context context) {
        String customUserAgent = "Starbucks_Android/" + DeviceInfo
                .getAppVersion(context) + "(Android:" + DeviceInfo.getAndroidVersion() + ")";
        putString(context, USER_AGENT, customUserAgent);
        return customUserAgent;
    }

    // 현재 App 버전을 반환
    public static String getAppVersion(Context context) {
        String version;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return version;
    }

    // Android Release Version 반환
    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getUserAgent(Context context) {
        return Define.Headers.HEADER_AGENT + getCustomUserAgent(context);
    }
}
