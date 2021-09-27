package com.example.rxjavaexample.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.rxjavaexample.DeviceInfo;
import com.example.rxjavaexample.R;
import com.example.rxjavaexample.databinding.ActivityInboxDetailWebBinding;
import com.example.rxjavaexample.viewmodel.InboxViewModel;
import com.facebook.stetho.common.LogUtil;

import java.io.Serializable;

/**
 * Inbox 상세 웹뷰
 */
public class InboxDetailWebActivity extends BaseActivity {
    private static final String TAG = InboxDetailWebActivity.class.getSimpleName();
    public static final String MIME_TYPE_TEXT_HTML = "text/html";
    public static final String ENCODING_UTF_8 = "UTF-8";

    private ActivityInboxDetailWebBinding binding;
    private ArgInfo argInfo;
    private InboxViewModel viewModel;

    public static class ArgInfo implements Serializable {
        public String url;
        public String gbn;
    }

    public static void start(AppCompatActivity activity, ArgInfo argInfo) {
        Intent intent = new Intent(activity, InboxDetailWebActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_INFO, argInfo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(InboxDetailWebActivity.this, R.layout.activity_inbox_detail_web);

        viewModel = new ViewModelProvider(InboxDetailWebActivity.this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(InboxViewModel.class);
        argInfo = (ArgInfo) getIntent().getSerializableExtra(EXTRA_ACTIVITY_INFO);
        if (argInfo != null) {
            Log.d(TAG, "argInfo url : " + argInfo.url + ", gbn : " + argInfo.gbn);
        }

        init();
        setLayout();
        setObserve();

        viewModel.requestInboxDetail(getApplicationContext(), argInfo.url);
    }

    private void setObserve() {
        viewModel.getInboxDetailMutableLiveData().observe(this, response -> {
            if (isFinishing()) {
                return;
            }
            binding.webView.loadDataWithBaseURL(argInfo.url, response, MIME_TYPE_TEXT_HTML, ENCODING_UTF_8, null);
        });
    }

    private void setLayout() {
        // 웹뷰 타이틀 설정
        if (InboxViewModel.INBOX_LIST_GBN_FLAG_02.equals(argInfo.gbn)) {
            binding.toolbarText.setText(getString(R.string.other_inbox_title_notice));
        } else if (InboxViewModel.INBOX_LIST_GBN_FLAG_01.equals(argInfo.gbn) || TextUtils.isEmpty(argInfo.gbn)) {
            // gbn 값 없이 진입 시에는 What's New 표시
            binding.toolbarText.setText(getString(R.string.other_inbox_title_event));
        }
    }

    // WebView Setting
    private void init() {
        Log.i(TAG, "init() called.");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptFileSchemeCookies(true);
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);

        /* zoom control enable */
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        /* UserAgent 정보를 커스텀 하여 셋팅한다 */
        String customAgentString = DeviceInfo.getCustomUserAgent(this);
        LogUtil.d(TAG, "init() customAgentString : " + customAgentString);
        webSettings.setUserAgentString(customAgentString);

        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(true);

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        cookieManager.setAcceptThirdPartyCookies(binding.webView, true);   // true 설정시 오류 발생하지 않음
    }
}
