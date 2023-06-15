package com.iehms.strawberrymarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.iehms.strawberrymarket.adapter.ProductListAdapter;
import com.iehms.strawberrymarket.model.HttpResult;
import com.iehms.strawberrymarket.model.Product;
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

public class MyProductListActivity extends AppCompatActivity {

    ListView listMyProduct;
    ProductListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product_list);

        adapter = new ProductListAdapter(this);
        listMyProduct = findViewById(R.id.list_my_product);
        listMyProduct.setAdapter(adapter);

        listMyProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                Product product = (Product) adapter.getItem(position);

                Intent intent = new Intent(MyProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, product.getId());
                startActivity(intent);
            }
        });

        new ProductListApiTask().execute();
    }

    // API 통신 (나의 물건 리스트 조회 API 연동)
    private class ProductListApiTask extends AsyncTask<String, String, HttpResult> {

        @Override
        protected HttpResult doInBackground(String... params) {
            HttpsURLConnection conn;

            try {
                // 로그인 API URL 생성
                URL url = new URL(Constant.BASE_URL + "/products/?authenticated=true");

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
                conn.setRequestProperty("Authorization", "Bearer " + AuthManager.getToken(MyProductListActivity.this));

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
                    JSONObject json = new JSONObject(httpResult.getResult());
                    JSONArray array = json.getJSONArray("products");
                    ArrayList<Product> productList = new ArrayList<>();

                    for(int i = 0; i < array.length(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        String createdAt = item.getString("created_at");
                        String title = item.getString("title");
                        String userName = item.getJSONObject("user").getString("name");
                        int price = item.getInt("price");
                        String imageUrl = item.getString("image_url");
                        int id = item.getInt("id");

                        Product product = new Product(imageUrl, title, price, userName, createdAt, id);
                        productList.add(product);
                    }

                    adapter.setItem(productList);
                } else {
                    String msg = new JSONObject(httpResult.getResult()).getString("msg");
                    Toast.makeText(MyProductListActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}