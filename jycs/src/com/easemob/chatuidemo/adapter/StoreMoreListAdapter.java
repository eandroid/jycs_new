package com.easemob.chatuidemo.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.NewsDetailActivity;
import com.easemob.chatuidemo.activity.StoreMoreActivity;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.dao.Store;
import com.easemob.chatuidemo.http.Url;

public class StoreMoreListAdapter extends BaseAdapter {
    private List<Store> list_store;// 门店列表
    private Context context;
    private AbImageLoader loader;

    public StoreMoreListAdapter(List<Store> list_store, Context context) {
        super();
        this.list_store = list_store;
        this.context = context;
        loader = AbImageLoader.getInstance(context);
        loader.setLoadingImage(R.drawable.image_loading);
        loader.setEmptyImage(R.drawable.image_empty);
        loader.setErrorImage(R.drawable.image_error);
        loader.setDesiredWidth(0);
        loader.setDesiredHeight(0);
    }

    @Override
    public int getCount() {
        return list_store.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list_store.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_store_more, null);
            holder.img_store = (ImageView) convertView
                    .findViewById(R.id.img_store);
            holder.tv_store_name = (TextView) convertView
                    .findViewById(R.id.tv_store_name_02);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Store store = list_store.get(arg0);
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("type", "stores");
                String url = store.getUrl();
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });
        String name = store.getStoreName();
        holder.tv_store_name.setText(name);
        UploadedFile avator = store.getPhoto();
        if (avator != null) {
            String url = avator.getThumbnailUrl();
            loader.display(holder.img_store, Url.url_base_img_news + url);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView img_store;// 门店图片
        TextView tv_store_name;// 门店名
    }
}
