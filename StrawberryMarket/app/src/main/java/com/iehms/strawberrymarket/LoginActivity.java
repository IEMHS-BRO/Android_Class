package com.iehms.strawberrymarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
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

public class LoginActivity extends AppCompatActivity {

    TextInputEditText inputId, inputPw;
    Button btnLogin;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputId = findViewById(R.id.input_id);
        inputPw = findViewById(R.id.input_pw);
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // btn_login 버튼 클릭 시
                try {
                    // 입력한 id, pw 획득
                    String id = inputId.getText().toString();
                    String pw = inputPw.getText().toString();

                    // JSON으로 변환
                    JSONObject json = new JSONObject();
                    json.put("username", id);
                    json.put("password", pw);

                    // API 비동기 작업 실행 (파라미터 : 파싱된 json을 string으로 변환하여 전달)
                    new LoginApiTask().execute(json.toString());
                } catch (NullPointerException e) {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignUp();
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    // API 통신 (로그인 API 연동)
    private class LoginApiTask extends AsyncTask<String, String, HttpResult> {

        @Override
        protected HttpResult doInBackground(String... params) {
            HttpsURLConnection conn;

            try {
                // 로그인 API URL 생성
                URL url = new URL(Constant.BASE_URL + "/auth/login");

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

                // POST로 넘겨줄 body 설정
                byte[] data = params[0].getBytes(StandardCharsets.UTF_8);
                OutputStream os = conn.getOutputStream();
                os.write(data);
                os.close();

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
        protected void onPostExecute(HttpResult result) {
            try {
                JSONObject json = new JSONObject(result.getResult());

                if(result.getCode() == HttpsURLConnection.HTTP_OK) {
                    // 통신에 성공했을 경우 (200)
                    String token = json.getString("access_token");
                    AuthManager.setToken(LoginActivity.this, token);
                    goToMain();
                } else {
                    // 통신에 실패했을 경우
                    String msg = json.getString("msg");
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}