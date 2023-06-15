package com.iehms.strawberrymarket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.iehms.strawberrymarket.model.HttpResult;
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

public class ProductDetailActivity extends AppCompatActivity {

    public static String KEY_PRODUCT_ID = "productId";
    private int productId;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getIntExtra(KEY_PRODUCT_ID, -1);
        toolbar = findViewById(R.id.tool_bar);

        // 툴바 메뉴 클릭 리스너 추가
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.menu_delete) {
                    // 삭제 여부를 묻는 다이얼로그
                    AlertDialog dialog = new AlertDialog.Builder(ProductDetailActivity.this)
                            .setTitle("삭제 하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new ProductDeleteApiTask().execute();
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create();

                    dialog.show();


                }
                return false;
            }
        });

        new ProductDetailApiTask().execute();
    }

    /**
     * 제품 정보 조회 API
     */
    private class ProductDetailApiTask extends AsyncTask<String, String, HttpResult> {
        @Override
        protected HttpResult doInBackground(String... params) {
            HttpsURLConnection conn;

            try {

                // 제품 정보 획득 API URL 생성
                URL url = new URL(Constant.BASE_URL + "/products/" + productId);

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
                conn.setRequestProperty("Authorization", "Bearer " + AuthManager.getToken(ProductDetailActivity.this));

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
                    // 통신에 성공했을 경우 (200)
                    JSONObject json = new JSONObject(httpResult.getResult());   // json으로 파싱
                    JSONObject user = json.getJSONObject("user");         // 해당 게시물의 업로드 유저 정보 획득
                    int userId = user.getInt("id");                       // 유저 id 획득
                    if(userId == Global.getUserInfo().getId()) {                // 내 유저 id와 동일할 경우 (내 게시물일 경우)
                        toolbar.inflateMenu(R.menu.menu_product_detail);        // 메뉴 추가 (삭제 버튼)
                    }
                    // TODO : httpResult.getResult()인 String을 JSON으로 파싱 후 화면에 보여주는 코드 작성
                } else {
                    String msg = new JSONObject(httpResult.getResult()).getString("msg");
                    Toast.makeText(ProductDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 제품 삭제 API
     */
    private class ProductDeleteApiTask extends AsyncTask<String, String, HttpResult> {
        @Override
        protected HttpResult doInBackground(String... params) {
            HttpsURLConnection conn;

            try {

                // 제품 정보 획득 API URL 생성
                URL url = new URL(Constant.BASE_URL + "/products/" + productId);

                // URL 연결
                conn = (HttpsURLConnection) url.openConnection();

                // 서버 접속 Timeout 시간 (10s)
                conn.setConnectTimeout(10 * 1000);

                // Read Timeout 시간 (10s)
                conn.setReadTimeout(10 * 1000);

                // 요청 방식 (POST)
                conn.setRequestMethod("DELETE");

                // 헤더 타입 설정
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "*/*");
                conn.setRequestProperty("Authorization", "Bearer " + AuthManager.getToken(ProductDetailActivity.this));

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
                    // 통신에 성공했을 경우 (200)
                    Toast.makeText(ProductDetailActivity.this, "삭제했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String msg = new JSONObject(httpResult.getResult()).getString("msg");
                    Toast.makeText(ProductDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }


}