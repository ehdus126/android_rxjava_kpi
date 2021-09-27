package com.example.rxjavaexample.model;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 3.3.2 인박스 리스트 조회 응답 파라미터
 */
@Data
@NoArgsConstructor
// staticName : 클래스에 선언된 모든 필드를 이용하여 객체를 생성 (new 연산자 X)
@AllArgsConstructor(staticName = "of")
public class InboxRes implements Serializable {
    public AppVo data;

    public static class AppVo {
        public String page;                         // 현재 조회페이지 번호
        public String pageSize;                     // 현재 조회페이지 크기
        public String totalCnt;                     // 전체 총 건수
        public ArrayList<InboxListVo> list;         // 인박스 목록
    }

    public static class InboxListVo {
        public String num;                          // 순번
        public String title;                        // 제목
        public String detailUrl;                    // 상세보기 링크 정보
        public String imgUrl;                       // 썸네일 이미지 경로
        public String startDate;                    // 게시일 (yyyy.mm.dd hh:mm)
        public String gbn;                          // 구분 값 (01 : What’s New , 02: 공지사항)
    }
}
