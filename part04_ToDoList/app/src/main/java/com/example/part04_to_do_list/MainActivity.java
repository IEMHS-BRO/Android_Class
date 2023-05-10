package com.example.part04_to_do_list;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton floatingActionButton;
    ToDoListAdapter adapter = new ToDoListAdapter(this);
    Parcelable listViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("MainActivity", "onCreate");

        // View 바인딩
        listView = findViewById(R.id.listview);
        floatingActionButton = findViewById(R.id.floating);

        // 리스트뷰 어댑터 설정
        listView.setAdapter(adapter);

        // 플로팅 버튼 클릭 리스너 추가
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddListDialog();
            }
        });

        // 리스트뷰에 길게 클릭 리스너 추가
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                showEditOrDeleteSelectDialog(position);
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("MainActivity", "onRestart, listViewState : " + listViewState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(listViewState != null) {
            listView.onRestoreInstanceState(listViewState);
        }
        Log.e("MainActivity", "resume, listViewState : " + listViewState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        listViewState = listView.onSaveInstanceState();
        Log.e("MainActivity", "pause, listViewState : " + listViewState);
    }

    // 리스트에 추가할 To-Do를 작성하는 다이얼로그
    private void showAddListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText editText = new EditText(new ContextWrapper(this));
        AlertDialog dialog = builder.setTitle("추가할 To Do 작성")
                .setView(editText)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String inputText = editText.getText().toString();
                        adapter.addItem(inputText);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    // 리스트를 수정할 To-Do를 작성하는 다이얼로그
    private void showEditListDialog(int itemIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText editText = new EditText(new ContextWrapper(this));
        AlertDialog dialog = builder.setTitle("수정할 To Do 작성")
                .setView(editText)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String inputText = editText.getText().toString();
                        adapter.editItem(itemIndex, inputText);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    // 길게 터치 시 수정 및 삭제 중 한 가지를 선택하는 다이얼로그
    private void showEditOrDeleteSelectDialog(int itemIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setItems(R.array.delete_or_edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                if(index == 0) {
                    // 수정
                    showEditListDialog(itemIndex);
                } else {
                    // 삭제
                    adapter.removeItem(itemIndex);
                }
            }
        }).create();
        dialog.show();
    }
}