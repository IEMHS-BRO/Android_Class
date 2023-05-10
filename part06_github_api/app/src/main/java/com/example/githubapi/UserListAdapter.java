package com.example.githubapi;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {

    ArrayList<User> userList = new ArrayList<>();
    Context context;

    public UserListAdapter(Context context) { this.context = context; }

    public void setItem(ArrayList<User> userList) {
        this.userList.clear();
        this.userList.addAll(userList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_user, viewGroup, false);
        User user = userList.get(position);

        ImageView imageView = itemView.findViewById(R.id.img_avatar);
        Glide.with(context).load(Uri.parse(user.photoUrl)).into(imageView);

        TextView tvUserId = itemView.findViewById(R.id.tv_user_id);
        tvUserId.setText(user.userId);

        TextView tvUrl = itemView.findViewById(R.id.tv_url);
        tvUrl.setText(user.url);

        return itemView;
    }

}

