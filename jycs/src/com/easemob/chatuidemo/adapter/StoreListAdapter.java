package com.easemob.chatuidemo.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.NewsDetailActivity;
import com.easemob.chatuidemo.activity.StoreMoreActivity;
import com.easemob.chatuidemo.domain.dao.Store;
import com.easemob.chatuidemo.http.Url;

public class StoreListAdapter extends BaseAdapter {
    private Activity context;

    private List<String> list_store_name;
    ViewHolder holder = null;
    private AbImageLoader loader;
    private Map<String, List<Store>> map_item_store;
    private String url;
    private static final String TAG = "StoreListAdapter";
    public StoreListAdapter(Activity context,
            Map<String, List<Store>> map_item_store,
            List<String> list_store_name) {
        super();
        this.context = context;
        this.map_item_store = map_item_store;
        this.list_store_name = list_store_name;
        loader = AbImageLoader.getInstance(context);
        loader.setLoadingImage(R.drawable.image_loading);
        loader.setEmptyImage(R.drawable.image_empty);
        loader.setErrorImage(R.drawable.image_error);
        loader.setDesiredWidth(0);
        loader.setDesiredHeight(0);
    }

    @Override
    public int getCount() {
        return list_store_name.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list_store_name.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View convertview, ViewGroup arg2) {

        if (convertview == null) {
            holder = new ViewHolder();
            convertview = LayoutInflater.from(context).inflate(
                    R.layout.item_store, null);
            holder.img_more = (ImageView) convertview
                    .findViewById(R.id.img_more);
            holder.tv_store_name = (TextView) convertview
                    .findViewById(R.id.tv_store_name);
            holder.img_detail = (GridView) convertview
                    .findViewById(R.id.img_detail);

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }
        String storeName = list_store_name.get(arg0);
        holder.tv_store_name.setText(storeName);
        final List<Store> list_item = map_item_store.get(storeName);
        holder.img_detail.setAdapter(new GridAdapter(list_item));
        holder.img_more.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // 跳往更多界面
                Intent intent = new Intent(context, StoreMoreActivity.class);
                intent.putExtra("store", (Serializable)list_item);
                context.startActivity(intent);
            }
        });
        return convertview;
    }

    class GridAdapter extends BaseAdapter {
        private List<Store> list_item = new ArrayList<Store>();

        public GridAdapter(List<Store> list_item) {
            super();
            this.list_item = list_item;
        }

        @Override
        public int getCount() {

            return list_item.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list_item.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup arg2) {
            ViewHolder holder = null;
           
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_store_grid, null);
                holder = new ViewHolder();
                holder.img_store = (ImageView) convertView
                        .findViewById(R.id.img_store);
                holder.tv_store = (TextView) convertView
                        .findViewById(R.id.tv_store);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Store store = list_item.get(position);
            if (store != null){
                Log.i(TAG, "url"+store.getUrl());
                holder.img_store.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Log.i(TAG, "当前点击的item:"+position);
                        Intent intent = new Intent(context,
                                NewsDetailActivity.class);
                        intent.putExtra("type", "stores");
                        String url = store.getUrl();
                        intent.putExtra("url", url);
                        context.startActivity(intent);

                    }
                });
                if (!TextUtils.isEmpty(store.getStoreName())) {

                    holder.tv_store.setText(store.getStoreName());
                }

                String msgUrl_01 = store.getPhoto().getThumbnailUrl();
                msgUrl_01 = Url.url_base_img_news + msgUrl_01;
                Log.i("huwenbao", msgUrl_01 + "");
                loader.display(holder.img_store, msgUrl_01);
            }
//            url = store.getUrl();
          
            return convertView;
        }

    }

    class ViewHolder {
        ImageView img_more;// 更多
        TextView tv_store_name;// 地区划分
        GridView img_detail;// 门店详情
        ImageView img_store;// 门店展示图
        TextView tv_store;// 门店名称
    }
}
