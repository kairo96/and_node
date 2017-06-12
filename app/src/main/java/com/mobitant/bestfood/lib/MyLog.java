package com.mobitant.bestfood.lib;

import android.util.Log;

import com.mobitant.bestfood.BuildConfig;

/**
 * 로그 편의 클래스
 * 기존 로그를 좀 더 편하기 사용하기 위한 메소드로 구성
 * BuildConfig.DEBUG 값을 통해 개발 단계에서는 로그를 출력하고 마켓 런칭 단계에서는 로그 출력 안 함
 */
public class MyLog {
    private static boolean enabled = BuildConfig.DEBUG;

    public static void d(String tag, String text) {
        if (!enabled) return;

        Log.d(tag, text);
    }

    public static void d(String text) {
        if (!enabled) return;

        Log.d("tag", text);
    }

    public static void d(String tag, Class<?> cls, String text) {
        if (!enabled) return;

        Log.d(tag, cls.getName() + "." + text);
    }


    public static void e(String tag, String text) {
        if (!enabled) return;

        Log.e(tag, text);
    }
}
