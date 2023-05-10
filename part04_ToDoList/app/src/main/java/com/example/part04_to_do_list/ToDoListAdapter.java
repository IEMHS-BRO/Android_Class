package com.example.part04_to_do_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class ToDoListAdapter extends BaseAdapter {

    ArrayList<String> toDoList = new ArrayList<>();
    Context context;

    public ToDoListAdapter(Context context) {
        this.context = context;
    }

    public void addItem(String item) {
        toDoList.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int index) {
        toDoList.remove(index);
        notifyDataSetChanged();
    }

    public void editItem(int index, String item) {
        toDoList.remove(index);
        toDoList.add(index, item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return toDoList.size();
    }

    @Override
    public Object getItem(int position) {
        return toDoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_do_to, viewGroup, false);
        CheckBox checkBox = itemView.findViewById(R.id.check_box);
        checkBox.setText(toDoList.get(position));
        return itemView;
    }
}
