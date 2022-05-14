package com.iehms.part02_bmi_calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BmiResultActivity extends AppCompatActivity {

    /**
     * View 변수 선언
     */
    Button btnClose;
    TextView tvResult;

    /**
     * 인텐트를 통해 가져올 정보를 담을 변수 선언
     */
    float height_cm, weight_kg = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_result);

        bindView();
        getDataFromIntent(getIntent());
        initView();
    }

    /**
     * 인텐트에서 키, 몸무게 정보 획득
     * @param intent    인텐트
     */
    private void getDataFromIntent(Intent intent) {
        height_cm = intent.getFloatExtra("height", 0f);
        weight_kg = intent.getFloatExtra("weight", 0f);
    }

    /**
     * 뷰 바인딩
     */
    private void bindView() {
        btnClose = findViewById(R.id.btn_close);
        tvResult = findViewById(R.id.tv_result);
    }

    /**
     * 뷰 초기화
     * (Set Click Listener, Text View)
     */
    private void initView() {
        float bmiResult = calculateBmi(height_cm, weight_kg);
        showBmiResult(bmiResult);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * BMI 계산
     * 계산식 : (몸무게 (kg)) / (키 (m)) ^ 2
     *
     * @param height_cm     키 (cm)
     * @param weight_kg     몸무게 (kg)
     */
    private float calculateBmi(float height_cm, float weight_kg) {
        float height_m = height_cm / 100;
        float bmiResult = weight_kg / (height_m * height_m);
        return bmiResult;
    }

    /**
     * TextView에 BMI 결과 표시
     *
     * ~ 18.5 : 저체중
     * ~ 23 : 정상
     * ~ 25 : 과체중
     * 25 ~ : 비만
     *
     * @param bmiResult     BMI 지수
     */
    private void showBmiResult(float bmiResult) {
        String msg = String.format("%.1f", bmiResult);

        if(bmiResult <= 18.5) {
            msg += " (저체중)";
        } else if(bmiResult <= 23) {
            msg += " (정상)";
        } else if(bmiResult <= 25) {
            msg += " (과체중)";
        } else {
            msg += " (비만)";
        }

        tvResult.setText(msg);
    }
}