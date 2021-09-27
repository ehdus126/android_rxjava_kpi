package com.example.rxjavaexample;

/**
 * Copyright ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.| Confidential
 *
 * @ Description : 용어 정의
 * @ Class : Define
 * @ Created by : limdoyeon
 * @ Created Date : 2021. 04. 24.
 */
public class Define {
    public static class QueryExample {
        public static final String TV = "TV";
        public static final String CAMERA = "CAMERA";
        public static final String PHONE = "PHONE";
    }

    public static class HeartBeatExample {
        public static final String SERVER_URL = "https://api.github.com/zen";
    }

    public static class Server {
        public static final String WEB_URL = "https://dev-www.starbucks.co.kr:7643/";

        // 3.3.2. 인박스 리스트 조회
        public static final String INBOX = "app/inboxList.do";

        public static final int ERROR_307 = 307;
    }

    public static class MsgVersion {
        public static final String HEADER_DEFAULT_MSG_VER_2 = "msgVersion: 2";
        public static final String HEADER_MSG_VER_3 = "msgVersion: 3";
        public static final String HEADER_MSG_VER_4 = "msgVersion: 4";
    }

    public static class Headers {
        public static final String HEADER_AGENT = "agent: " ;
        public static final String HEADER_APP_VERSION = "appVersion: ";
        public static final String HEADER_JSESSIONID = "jsessionid: ";
        public static final String HEADER_STARBUCKS_WEB_CALL = "starbucksWebCall: android";
    }

    public static class Inbox {
        public static final String SERVER_STATUS_ERROR_CODE_0009 = "0009"; // 조회된 데이터가 없는 경우

        public static final String GBN = "gbn";
        public static final String PAGE = "page";
        public static final String PAGE_SIZE = "pageSize";

        public static final String AGENT = "agent";
    }
}
