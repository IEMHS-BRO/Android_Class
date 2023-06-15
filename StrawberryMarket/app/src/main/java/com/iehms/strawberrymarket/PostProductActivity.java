package com.iehms.strawberrymarket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.iehms.strawberrymarket.model.HttpResult;
import com.iehms.strawberrymarket.model.MultipartFormData;
import com.iehms.strawberrymarket.util.AuthManager;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;

public class PostProductActivity extends AppCompatActivity {

    CardView cardImageProduct;
    ImageView ivProduct;
    TextInputEditText inputTitle, inputPrice, inputDesc;
    TextView btnSubmit;

    private Uri chooseImageUri;

    private final int IMAGE_CHOOSER_REQUEST_CODE = 2349;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_product);

        cardImageProduct = findViewById(R.id.card_image_product);
        ivProduct = findViewById(R.id.iv_product);

        inputTitle = findViewById(R.id.input_title);
        inputPrice = findViewById(R.id.input_price);
        inputDesc = findViewById(R.id.input_desc);

        btnSubmit = findViewById(R.id.btn_submit);

        cardImageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = inputTitle.getText().toString();
                int price = Integer.parseInt(inputPrice.getText().toString());
                String desc = inputDesc.getText().toString();
                Params params = new Params(chooseImageUri, title, desc, price);
                new PostProductApiTask().execute(params);
            }
        });
    }

    /**
     * 이미지 선택기 열기
     */
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "사진을 선택하세요."), IMAGE_CHOOSER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // 이미지 선택기 결과 처리
        if(requestCode == IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            chooseImageUri = selectedImageUri;
            Glide.with(this).load(selectedImageUri).into(ivProduct);
        }
    }

    /**
     * API 통신 (제품 등록)
     */
    private class PostProductApiTask extends AsyncTask<Params, String, HttpResult> {
        @Override
        protected HttpResult doInBackground(Params... params) {
            HttpsURLConnection conn;
            String boundary = getBoundary();

            try {
                // 제품 등록 API URL 생성
                URL url = new URL(Constant.BASE_URL + "/products/");

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
//                conn.setUseCaches(false);

                // 헤더 타입 설정
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("Authorization", "Bearer " + AuthManager.getToken(PostProductActivity.this));

                // 전달할 FormData 생성
                MultipartFormData formData = new MultipartFormData();

                JSONObject json = new JSONObject();
                json.put("title", params[0].getTitle());
                json.put("price", params[0].getPrice());
                json.put("description", params[0].getDescription());

                formData.addString("json_data", json.toString(), "UTF-8");
                formData.addFile("image", getFile(params[0].getImageUri()).getPath());

                // 데이터를 전달할 스트림 획득
                OutputStream outputStream = conn.getOutputStream();
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                formData.write(boundary, bufferedOutputStream);

                bufferedOutputStream.close();
                bufferedOutputStream = null;

                // HTTP 요청 응답 수신
                InputStream is;
                if(conn.getResponseCode() == HttpsURLConnection.HTTP_CREATED) {
                    is = conn.getInputStream();
                } else {
                    is = conn.getErrorStream();
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                String result;
                while((result = br.readLine()) != null) {
                    sb.append(result + '\n');
                }

                return new HttpResult(conn.getResponseCode(), sb.toString());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(HttpResult httpResult) {
            super.onPostExecute(httpResult);
            if(httpResult.getCode() == HttpsURLConnection.HTTP_CREATED) {
                Toast.makeText(PostProductActivity.this, "제품을 등록했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(PostProductActivity.this, "제품 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * API 파라미터 데이터 클래스
     */
    class Params {
        public Params(Uri imageUri, String title, String description, int price) {
            this.imageUri = imageUri;
            this.title = title;
            this.description = description;
            this.price = price;
        }

        public Uri getImageUri() {
            return imageUri;
        }

        public void setImageUri(Uri imageUri) {
            this.imageUri = imageUri;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        private Uri imageUri;
        private String title;
        private String description;
        private int price;
    }

    /**
     * FormData boundary 데이터 획득 (랜덤값)
     * @return  Form-Data Boundary (Random)
     */
    public static String getBoundary() {
        SecureRandom random = new SecureRandom();

        byte[] randData = random.generateSeed(16);

        StringBuilder sb = new StringBuilder(randData.length * 2);

        for(byte b: randData )
            sb.append(String.format("%02x", b));

        return sb.toString();
    }

    /**
     * URI에 있는 content를 임시 File로 변환
     * @param uri   content
     * @return      Temp File
     */
    private File getFile(Uri uri) {
        File destinationFileName = new File(getFilesDir().getPath() + File.separatorChar + queryName(uri));
        try (InputStream ins = getContentResolver().openInputStream(uri)) {
            createFileFromStream(ins, destinationFileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return destinationFileName;
    }

    /**
     * InputStream에 있는 데이터를 destination (File)로 변환하는 함수
     * @param ins           데이터가 담긴 input stream
     * @param destination   input stream의 데이터를 담을 파일 (목적지)
     */
    private void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while((length = ins.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * uri의 있는 content의 이름 획득
     * @param uri   content
     * @return      name
     */
    private String queryName(Uri uri) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Cursor returnCursor = getContentResolver().query(uri, null, null, null);
            assert returnCursor != null;
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();
            return name;
        } else {
            return uri.getLastPathSegment();
        }

    }
}