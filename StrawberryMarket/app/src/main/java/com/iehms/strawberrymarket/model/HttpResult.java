package com.iehms.strawberrymarket.model;

import androidx.annotation.NonNull;

/**
 * API 통신 결과를 담는 객체
 *
 * @author dahunkim
 */
public class HttpResult {

    /**
     * 생성자
     * @param code      HTTP Code
     * @param result    HTTP Response Body
     */
    public HttpResult(int code, String result) {
        this.code = code;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    int code;
    String result;

    @NonNull
    @Override
    public String toString() {
        return "code : " + code + ", result : " + result;
    }
}
