package com.iehms.part02_bmi_calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * View 변수 선언
     */
    EditText etHeight_cm, etWeight_kg;
    Button btnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
        initView();
    }

    /**
     * 뷰 바인딩
     */
    private void bindView() {
        etHeight_cm = findViewById(R.id.et_height);
        etWeight_kg = findViewById(R.id.et_weight);
        btnCalculate = findViewById(R.id.btn_calculate);
    }

    /**
     * 뷰 초기화
     * (Set Click Listener)
     */
    private void initView() {

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputHeight = etHeight_cm.getText().toString();
                String inputWeight = etWeight_kg.getText().toString();

                if(checkValidation(inputHeight, inputWeight)) {
                    float height = Float.parseFloat(inputHeight);
                    float weight = Float.parseFloat(inputWeight);
                    goToResultPage(height, weight);
                }
            }
        });

    }

    /**
     * 입력한 값 유효성 검사
     * (입력되지 않았을 경우 토스트로 입력 유도)
     *
     * @param height    키
     * @param weight    몸무게
     * @return
     */
    private boolean checkValidation(String height, String weight) {
        if(height.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "키 또는 몸무게를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    /**
     * 결과 페이지 이동
     * (키와 몸무게 데이터와 함께)
     *
     * @param height    키
     * @param weight    몸무게
;    */
    private void goToResultPage(float height, float weight) {
        Intent intent = new Intent(getBaseContext(), BmiResultActivity.class);
        intent.putExtra("height", height);
        intent.putExtra("weight", weight);
        startActivity(intent);
    }
}