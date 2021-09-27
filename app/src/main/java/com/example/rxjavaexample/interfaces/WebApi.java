package com.example.rxjavaexample.interfaces;

import com.example.rxjavaexample.Define;
import com.example.rxjavaexample.DeviceInfo;
import com.example.rxjavaexample.model.InboxRes;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 인박스 목록 조회
 */
public interface WebApi {
    // 3.3.2. 인박스 목록 조회
    // HashMap 으로 Request Body 전달 시 @FormUrlEncoded 태그 후 @FieldMap 이용(@Body 이용 X)
    @Headers(Define.MsgVersion.HEADER_DEFAULT_MSG_VER_2)
    @FormUrlEncoded
    @POST(Define.Server.INBOX)
    Single<InboxRes> inboxSingleRes (@FieldMap Map<String, String> params);

    @Headers(Define.Headers.HEADER_STARBUCKS_WEB_CALL)
    @POST
    Single<ResponseBody> inboxDetailSingleRes(@Url String url,
                                              @Header ("agent") String agent,
                                              @Header("appVersion") String appVersion,
                                              @Header("jsessionid") String jsessionid,
                                              @Body Map<String, String> params);
}

