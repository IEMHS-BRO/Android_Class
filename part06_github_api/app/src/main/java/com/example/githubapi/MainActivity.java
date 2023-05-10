package com.example.githubapi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new UserListAdapter(this);
        listView = findViewById(R.id.list_user);
        listView.setAdapter(adapter);

        new GitHubApiAsyncTask().execute();
    }

    private class GitHubApiAsyncTask extends AsyncTask<String, String, String> {

        private String baseUrl = "https://api.github.com/users";


        @Nullable
        @Override
        public String doInBackground(String... strings) {
            HttpURLConnection conn;

            try {
                URL url = new URL(baseUrl);

                // URL 연결
                conn = (HttpURLConnection) url.openConnection();

                // 서버 접속 Timeout 시간
                conn.setConnectTimeout(10000);

                // Read Timeout 시간
                conn.setReadTimeout(10000);

                // 요청 방식
                conn.setRequestMethod("GET");

                // 헤더 타입 설정
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "*/*");

                InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                // HTTP 요청 응답 수신
                String result;
                while((result = br.readLine()) != null) {
                    sb.append(result + '\n');
                }

                result = sb.toString();
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                ArrayList<User> userList = new ArrayList<>();

                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject userData = jsonArray.getJSONObject(i);

                    String userId = userData.getString("login");
                    String avatarUrl = userData.getString("avatar_url");
                    String url = userData.getString("html_url");

                    User user = new User(userId, url, avatarUrl);
                    userList.add(user);
                }

                adapter.setItem(userList);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}