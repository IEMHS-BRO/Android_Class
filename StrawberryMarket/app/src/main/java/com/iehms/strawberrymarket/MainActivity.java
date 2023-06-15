package com.iehms.strawberrymarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.iehms.strawberrymarket.adapter.ProductListAdapter;
import com.iehms.strawberrymarket.model.HttpResult;
import com.iehms.strawberrymarket.model.Product;
import com.iehms.strawberrymarket.model.UserInfo;
import com.iehms.strawberrymarket.util.AuthManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ListView lvProduct;
    ProductListAdapter adapter;
    Toolbar toolbar;
    ExtendedFloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvProduct = findViewById(R.id.list_product);
        toolbar = findViewById(R.id.tool_bar);
        floatingActionButton = findViewById(R.id.btn_go_to_post_product);

        adapter = new ProductListAdapter(this);

        lvProduct.setAdapter(adapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPostProduct();
            }
        });

        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Product product = (Product) adapter.getItem(position);
                int productId = product.getId();
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, productId);
                startActivity(intent);
            }
        });

        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menu_profile) {
                    goToProfile();
                }
                return false;
            }
        });


        new ProductListApiTask().execute();
        new UserInfoApiTask().execute();
    }

    private void goToPostProduct() {
        Intent intent = new Intent(this, PostProductActivity.class);
        startActivity(intent);
    }

    private void goToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // API 통신 (리스트 조회 API 연동)
    private class ProductListApiTask extends AsyncTask<String, String, HttpResult> {

        @Override
        protected HttpResult doInBackground(String... params) {
            HttpsURLConnection conn;

            try {
                // 로그인 API URL 생성
                URL url = new URL(Constant.BASE_URL + "/products/");

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
                conn.setRequestProperty("Authorization", "Bearer " + AuthManager.getToken(MainActivity.this));

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
                    // TODO : httpResult.getResult()인 String을 JSON으로 파싱 후 ArrayList<Product>로 만들어 Adapter에 전달하는 코드 작성
                } else {
                    String msg = new JSONObject(httpResult.getResult()).getString("msg");
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // API 통신 (사용자 정보 조회)
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
                conn.setRequestProperty("Authorization", "Bearer " + AuthManager.getToken(MainActivity.this));

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
                    // TODO : 파싱된 json 데이터를 ToolBar Title에 세팅되도록 코드 작성 (Figma와 동일한 문구)
                } else {
                    String msg = new JSONObject(httpResult.getResult()).getString("msg");
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}