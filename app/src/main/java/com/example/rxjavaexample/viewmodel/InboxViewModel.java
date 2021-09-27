package com.example.rxjavaexample.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.rxjavaexample.AccountInfo;
import com.example.rxjavaexample.Define;
import com.example.rxjavaexample.DeviceInfo;
import com.example.rxjavaexample.R;
import com.example.rxjavaexample.interfaces.RequestMethod;
import com.example.rxjavaexample.model.InboxRes;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * 인박스 ViewModel
 */
public class InboxViewModel extends BaseViewModel {
    private static final String TAG = InboxViewModel.class.getSimpleName();
    private static final int PAGE_SIZE = 20;
    private int tempPage;

    public static final String INBOX_LIST_GBN_FLAG_00 = "00";   // 인박스 리스트 조회 카테고리 구분 - 전체(default)
    public static final String INBOX_LIST_GBN_FLAG_01 = "01";   // 인박스 리스트 조회 카테고리 구분 - What's New
    public static final String INBOX_LIST_GBN_FLAG_02 = "02";   // 인박스 리스트 조회 카테고리 구분 - 공지사항

    private static final ArrayList<CategoryInfo> CATEGORY_INFO_LIST = new ArrayList<CategoryInfo>();

    public enum eCategoryType {
        ALL,
        WHATS_NEW,
        NOTICE,
    }

    public static class CategoryInfo {
        public eCategoryType enumCategoryType;
        public int categoryTitle;
        public String categoryGbn;

        CategoryInfo(eCategoryType eCategoryType, int categoryTitle, String categoryGbn) {
            this.enumCategoryType = eCategoryType;
            this.categoryTitle = categoryTitle;
            this.categoryGbn = categoryGbn;
        }

        @Override
        public String toString() {
            return "CategoryInfo{" +
                    "enumCategoryType=" + enumCategoryType +
                    ", categoryTitle=" + categoryTitle +
                    ", categoryGbn='" + categoryGbn + '\'' +
                    '}';
        }
    }

    static {
        CATEGORY_INFO_LIST.add(new CategoryInfo(eCategoryType.ALL, R.string.inbox_category_all, INBOX_LIST_GBN_FLAG_00));
        CATEGORY_INFO_LIST.add(new CategoryInfo(eCategoryType.WHATS_NEW, R.string.inbox_category_whats_new, INBOX_LIST_GBN_FLAG_01));
        CATEGORY_INFO_LIST.add(new CategoryInfo(eCategoryType.NOTICE, R.string.inbox_category_notice, INBOX_LIST_GBN_FLAG_02));
    }

    public static ArrayList<CategoryInfo> getCategoryInfoList() {
        return CATEGORY_INFO_LIST;
    }

    private MutableLiveData<InboxRes.AppVo> getInboxMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> getInboxDetailMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> getErrorMutableLiveData = new MutableLiveData<>();

    public InboxViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<InboxRes.AppVo> getInboxMutableLiveData() {
        return getInboxMutableLiveData;
    }

    public MutableLiveData<String> getInboxDetailMutableLiveData() {
        return getInboxDetailMutableLiveData;
    }

    public MutableLiveData<Integer> getErrorMutableLiveData() {
        return getErrorMutableLiveData;
    }

    public void requestInbox(Context context, String gbn, int page) {
        Log.i(TAG, "requestInbox() called. gbn : " + gbn + ", page : " + page);
        tempPage = page;

        HashMap<String, String> params = new HashMap<>();
        params.put(Define.Inbox.GBN, gbn);
        params.put(Define.Inbox.PAGE, String.valueOf(page));
        params.put(Define.Inbox.PAGE_SIZE, String.valueOf(PAGE_SIZE));

        Single<InboxRes> inboxResSingle = RequestMethod.getInstance().requestInboxRes(context, params);
        request(inboxResSingle);
    }

    public void requestInboxDetail(Context context, String url) {
        HashMap<String, String> param = new HashMap<>();
        param.put(Define.Inbox.AGENT, DeviceInfo.getCustomUserAgent(context));

        String agent = DeviceInfo.getCustomUserAgent(context);
        String appVersion = DeviceInfo.getAppVersion(context);
        String jsessionid = AccountInfo.getInstance().getJSessionID(context);

        Single<ResponseBody> inboxDetailSingle = RequestMethod.getInstance().requestInboxDetail(context, url, agent, appVersion, jsessionid, param);
        requestForString(inboxDetailSingle);
    }

    @Override
    void onResponseSuccess(Object response) {
        Log.i(TAG, "onResponseSuccess() called. ");
        if (response instanceof String) {
            getInboxDetailMutableLiveData().setValue(response.toString());
        } else {
            InboxRes inboxRes = (InboxRes) response;
            if (inboxRes == null || inboxRes.data == null) {
                // 응답값이 비어있는 경우 이전 페이지 원복
                getErrorMutableLiveData().setValue(tempPage);
                return;
            }
            getInboxMutableLiveData().setValue(inboxRes.data);
        }
    }

    @Override
    void onResponseFailure(String resultCode, String resultMessage) {
        Log.i(TAG, "onResponseFailure() called. resultCode : " + resultCode + ", resultMessage : " + resultMessage);
        // 인박스 연동 에러
        if (Define.Inbox.SERVER_STATUS_ERROR_CODE_0009.equalsIgnoreCase(resultCode)) {
            // 조회된 데이터가 없는 경우
            int page = tempPage;
            InboxRes.AppVo appVo = new InboxRes.AppVo();
            appVo.page = String.valueOf(page);
            appVo.pageSize = String.valueOf(PAGE_SIZE);
            appVo.list = new ArrayList<>();
            getInboxMutableLiveData().setValue(appVo);
            return;
        }

        // 그 외 이전 페이지 번호 전달하여 원복 진행
        getErrorMutableLiveData.setValue(tempPage);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
