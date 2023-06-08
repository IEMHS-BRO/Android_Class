package com.iehms.strawberrymarket.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {

    private static String PREFS_NAME = "Auth";
    private static String PREFS_KEY_TOKEN = "token";

    /**
     * Authentication 정보를 저장할 SharedPreference 초기화
     * @param context   Android Context
     * @return          SharedPreference 객체
     */
    private static SharedPreferences init(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Token이 있는지 확인하는 함수
     * @param context   Android Context
     * @return          boolean (true : Token 존재, false : Token 없음)
     */
    public static boolean hasToken(Context context) {
        SharedPreferences prefs = init(context);
        String token = prefs.getString(PREFS_KEY_TOKEN, "");
        return !token.isEmpty();
    }

    /**
     * Token을 저장하는 함수
     * @param context   Android Context
     * @param token     저장할 Token
     */
    public static void setToken(Context context, String token) {
        SharedPreferences prefs = init(context);
        prefs.edit().putString(PREFS_KEY_TOKEN, token).apply();
    }

    /**
     * Token을 제거하는 함수
     * @param context   Android Context
     */
    public static void clearToken(Context context) {
        SharedPreferences prefs = init(context);
        prefs.edit().remove(PREFS_KEY_TOKEN).apply();
    }

    /**
     * 저장된 Token을 가져오는 함수
     * @param context   Android Context
     * @return          저장된 Access Token
     */
    public static String getToken(Context context) {
        SharedPreferences prefs = init(context);
        return prefs.getString(PREFS_KEY_TOKEN, "");
    }

}
