package com.mobitant.bestfood.lib;

import android.content.Context;
import android.widget.Toast;

/**
 * 토스트 편의 클래스
 * 토스트를 좀 더 편하게 사용하기 위한 메소드로 구성
 */
public class MyToast {
    public static void s(Context context, int id) {
        Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
    }

    public static void s(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void l(Context context, int id) {
        Toast.makeText(context, id, Toast.LENGTH_LONG).show();
    }

    public static void l(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
