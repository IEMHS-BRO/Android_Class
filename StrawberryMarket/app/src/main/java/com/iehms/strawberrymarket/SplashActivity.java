package com.iehms.strawberrymarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.iehms.strawberrymarket.model.HttpResult;
import com.iehms.strawberrymarket.util.AuthManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 1000 ms 이후에 실행될 코드
                if(AuthManager.hasToken(SplashActivity.this)) {
                    // 토큰이 있을 경우 자동 로그인 API에 전송하여 유효한지 확인
                    new AutoLoginApiTask().execute();
                } else {
                    // 없을 경우 로그인 페이지로 이동
                    goToLogin();
                }
            }
        }, 1000);
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // API 통신 (자동 로그인)
    private class AutoLoginApiTask extends AsyncTask<String, String, HttpResult> {
        @Override
        protected HttpResult doInBackground(String... params) {
            HttpsURLConnection conn;

            try {
                // 로그인 API URL 생성
                URL url = new URL(Constant.BASE_URL + "/auth/auto-login");

                // URL 연결
                conn = (HttpsURLConnection) url.openConnection();

                // 서버 접속 Timeout 시간 (10s)
                conn.setConnectTimeout(10 * 1000);

                // Read Timeout 시간 (10s)
                conn.setReadTimeout(10 * 1000);

                // 요청 방식 (POST)
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // 헤더 타입 설정
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "*/*");
                conn.setRequestProperty("Authorization", "Bearer " + AuthManager.getToken(SplashActivity.this)); // 토큰을 헤더에 설정

                // HTTP 요청 응답 수신
                InputStream is;
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // 통신에 성공(200) 했을 경우 InputStream 획득
                    is = conn.getInputStream();
                } else {
                    // 통신에 실패했을 경우 ErrorStream 획득
                    is = conn.getErrorStream();
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                String result;
                while ((result = br.readLine()) != null) {
                    sb.append(result + '\n');
                }

                return new HttpResult(conn.getResponseCode(), sb.toString());
            } catch (IOException e) {
                return new HttpResult(-1, e.toString());
            }
        }

        @Override
        protected void onPostExecute(HttpResult httpResult) {
            try {
                if(httpResult.getCode() == HttpsURLConnection.HTTP_OK) {
                    JSONObject json = new JSONObject(httpResult.getResult());
                    // 통신에 성공했을 경우 (200)
                    String token = json.getString("access_token");
                    AuthManager.setToken(SplashActivity.this, token); // 새로 발급된 Access Token을 저장
                    goToMain();
                } else {
                    Toast.makeText(SplashActivity.this, "로그인이 만료되었습니다.", Toast.LENGTH_SHORT).show();
                    goToLogin();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}