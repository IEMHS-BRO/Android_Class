package com.example.part02_diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class DiaryActivity extends AppCompatActivity {

    EditText etDiary;
    final String PREFS_NAME = "MyDiary";
    final String PREFS_KEY_DIARY = "diaryContent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        etDiary = findViewById(R.id.et_diary);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String savedContent = prefs.getString(PREFS_KEY_DIARY, "");
        etDiary.setText(savedContent);

        etDiary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputText = editable.toString();
                prefs.edit().putString(PREFS_KEY_DIARY, inputText).apply();
            }
        });
    }
}