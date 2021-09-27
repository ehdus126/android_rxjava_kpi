package com.example.rxjavaexample.model;

import android.graphics.drawable.Drawable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rx Java RecyclerView 아이템 class
 */
@Data
@NoArgsConstructor
// staticName : 클래스에 선언된 모든 필드를 이용하여 객체를 생성 (new 연산자 X)
@AllArgsConstructor(staticName = "of")
public class ListItemVo {
    public Drawable image;
    public String text;
}
