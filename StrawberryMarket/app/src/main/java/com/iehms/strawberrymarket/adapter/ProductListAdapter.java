package com.iehms.strawberrymarket.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iehms.strawberrymarket.Constant;
import com.iehms.strawberrymarket.R;
import com.iehms.strawberrymarket.model.Product;

import java.util.ArrayList;

public class ProductListAdapter extends BaseAdapter {

    ArrayList<Product> productList = new ArrayList<>();
    Context context;

    public ProductListAdapter(Context context) {
        this.context = context;
    }

    public void setItem(ArrayList<Product> productList) {
        this.productList.clear();
        this.productList.addAll(productList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 0; //TODO : 알맞은 리턴값은?
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_product, viewGroup, false);
        Product product = productList.get(position);

        ImageView productImage = itemView.findViewById(R.id.iv_product_image);
        TextView tvProductName = itemView.findViewById(R.id.tv_product_name);
        TextView tvPrice = itemView.findViewById(R.id.tv_price);
        TextView tvName = itemView.findViewById(R.id.tv_name);
        TextView tvCreatedAt = itemView.findViewById(R.id.tv_created_at);

        // TODO : product 데이터를 각각의 View에 세팅해주는 코드 작성

        return itemView;
    }
}
