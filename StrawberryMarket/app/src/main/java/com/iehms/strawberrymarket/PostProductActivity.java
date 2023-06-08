package com.iehms.strawberrymarket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class PostProductActivity extends AppCompatActivity {

    CardView cardImageProduct;
    ImageView ivProduct;

    private Uri chooseImageUri;

    private final int IMAGE_CHOOSER_REQUEST_CODE = 2349;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_product);

        cardImageProduct = findViewById(R.id.card_image_product);
        ivProduct = findViewById(R.id.iv_product);

        cardImageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "사진을 선택하세요."), IMAGE_CHOOSER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            chooseImageUri = selectedImageUri;
            Glide.with(this).load(selectedImageUri).into(ivProduct);
        }
    }
}