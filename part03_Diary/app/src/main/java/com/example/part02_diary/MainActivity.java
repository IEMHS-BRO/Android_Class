package com.example.part02_diary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnCheck;
    NumberPicker numberDigitFirst, numberDigitSecond, numberDigitThird;

    final int PASSWORD_FIRST = 3;
    final int PASSWORD_SECOND = 2;
    final int PASSWORD_THIRD = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheck = findViewById(R.id.btn_check);
        numberDigitFirst = findViewById(R.id.number_first_digits);
        numberDigitSecond = findViewById(R.id.number_second_digits);
        numberDigitThird = findViewById(R.id.number_third_digits);

        numberDigitFirst.setMaxValue(9);
        numberDigitSecond.setMaxValue(9);
        numberDigitThird.setMaxValue(9);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int digitFirst = numberDigitFirst.getValue();
                int digitSecond = numberDigitSecond.getValue();
                int digitThird = numberDigitThird.getValue();

                if(digitFirst == PASSWORD_FIRST && digitSecond == PASSWORD_SECOND && digitThird == PASSWORD_THIRD) {
                    // 비밀번호 맞음
                    Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
                    startActivity(intent);
                } else {
                    // 비밀번호 틀림
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    AlertDialog dialog = builder.setTitle("비밀번호가 일치하지 않습니다.").setMessage("확인 후 다시 시도해 주세요.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
                    dialog.show();
                }
            }
        });
    }
}