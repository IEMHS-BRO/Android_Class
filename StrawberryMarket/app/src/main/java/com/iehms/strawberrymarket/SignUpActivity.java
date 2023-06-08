package com.iehms.strawberrymarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.iehms.strawberrymarket.model.HttpResult;

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

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout layoutPw, layoutPwAgain;
    TextInputEditText inputId, inputPw, inputPwAgain, inputName, inputNumber;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inputId = findViewById(R.id.input_id);
        inputPw = findViewById(R.id.input_pw);
        inputPwAgain = findViewById(R.id.input_pw_again);
        inputName = findViewById(R.id.input_name);
        inputNumber = findViewById(R.id.input_number);

        layoutPw = findViewById(R.id.layout_input_pw);
        layoutPwAgain = findViewById(R.id.layout_input_pw_again);

        btnSignUp = findViewById(R.id.btn_sign_up);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String id = inputId.getText().toString();
                    String pw = inputPw.getText().toString();
                    String pwAgain = inputPwAgain.getText().toString();
                    String name = inputName.getText().toString();
                    String number = inputNumber.getText().toString();

                    if(id.isEmpty() || pw.isEmpty() || pwAgain.isEmpty() || name.isEmpty() || number.isEmpty()) {
                        Toast.makeText(SignUpActivity.this, "입력하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!pwAgain.equals(pw)) {
                        layoutPwAgain.setError("비밀번호가 일치하지 않습니다.");
                    } else {
                        layoutPwAgain.setError(null);

                        JSONObject json = new JSONObject();
                        json.put("username", id);
                        json.put("password", pw);
                        json.put("name", name);
                        json.put("phone", number);

                        new SignUpApiTask().execute(json.toString());
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(SignUpActivity.this, "입력하지 않은 항목이 있습니다.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private class SignUpApiTask extends AsyncTask<String, String, HttpResult> {
        @Override
        protected HttpResult doInBackground(String... params) {
            HttpsURLConnection conn;

            try {
                // 회원가입 API URL 생성
                URL url = new URL(Constant.BASE_URL + "/auth/register");

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
                if(conn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
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
                JSONObject json = new JSONObject(httpResult.getResult());

                if(httpResult.getCode() == HttpsURLConnection.HTTP_CREATED) {
                    // 회원가입 성공했을 경우
                    Toast.makeText(SignUpActivity.this, "회원가입에 성공했습니다.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // 통신에 실패했거나 회원가입에 실패했을 경우
                    String msg = json.getString("msg");
                    Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("SignUpActivity", "오류 발생. httpResult : " + httpResult);
                Toast.makeText(SignUpActivity.this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}