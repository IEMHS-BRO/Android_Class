package com.iehms.strawberrymarket;

import com.iehms.strawberrymarket.model.UserInfo;

/**
 * 전역 변수
 */
public class Global {

    /**
     * 사용자 데이터를 담는 전역 변수
     */
    private static UserInfo userInfo;

    public static UserInfo getUserInfo() {
        return userInfo;
    }

    public static void setUserInfo(UserInfo userInfo) {
        Global.userInfo = userInfo;
    }
}
