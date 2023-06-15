package com.iehms.strawberrymarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.iehms.strawberrymarket.model.HttpResult;
import com.iehms.strawberrymarket.model.UserInfo;
import com.iehms.strawberrymarket.util.AuthManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // TODO : 프로필 화면 작성 및 API 통신 및 click listener 추가
    }

    private void logout() {
        AuthManager.clearToken(this);
        finish();
        goToSplash();
    }

    private void goToMyProductList() {
        Intent intent = new Intent(this, MyProductListActivity.class);
        startActivity(intent);
    }

    private void goToSplash() {
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
    }

    /**
     * Api 통신 (사용자 정보 조회)
     */
    private class UserInfoApiTask extends AsyncTask<String, String, HttpResult> {
        @Override
        protected HttpResult doInBackground(String... params) {
            HttpsURLConnection conn;

            try {
                // 로그인 API URL 생성
                URL url = new URL(Constant.BASE_URL + "/auth/user");

                // URL 연결
                conn = (HttpsURLConnection) url.openConnection();

                // 서버 접속 Timeout 시간 (10s)
                conn.setConnectTimeout(10 * 1000);

                // Read Timeout 시간 (10s)
                conn.setReadTimeout(10 * 1000);

                // 요청 방식 (POST)
                conn.setRequestMethod("GET");

                // 헤더 타입 설정
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "*/*");
                conn.setRequestProperty("Authorization", "Bearer " + AuthManager.getToken(ProfileActivity.this));

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
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(HttpResult httpResult) {
            try {
                if(httpResult.getCode() == HttpsURLConnection.HTTP_OK) {
                    JSONObject json = new JSONObject(httpResult.getResult());
                    String createdAt = json.getString("created_at");
                    int id = json.getInt("id");
                    String name = json.getString("name");
                    String phone = json.getString("phone");
                    String updatedAt = json.getString("updated_at");
                    String username = json.getString("username");

                    UserInfo userInfo = new UserInfo(createdAt, id, name, phone, updatedAt, username);
                    Global.setUserInfo(userInfo);
                    // TODO : 파싱된 json 데이터를 각 뷰에 세팅하는 코드 작성 (Figma와 동일하게)
                } else {
                    String msg = new JSONObject(httpResult.getResult()).getString("msg");
                    Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}