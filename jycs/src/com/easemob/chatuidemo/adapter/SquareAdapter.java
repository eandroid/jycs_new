package com.easemob.chatuidemo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.ab.util.AbLogUtil;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.ImagePagerActivity;
import com.easemob.chatuidemo.activity.UserDetailActivity;
import com.easemob.chatuidemo.domain.LocalIM;
import com.easemob.chatuidemo.domain.Square;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.widget.NoScrollGridView;

@SuppressLint("SimpleDateFormat")
public class SquareAdapter extends BaseAdapter {
    private List<Square> listSquares;
    private Activity context;
    private AbImageLoader loader;
    private ArrayList<String> list_url = new ArrayList<String>();
//    private ArrayList<String> urls = new ArrayList<String>();
    public SquareAdapter(List<Square> listSquares, Activity context) {
        super();
        this.listSquares = listSquares;
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
        return listSquares.size();
    }

    @Override
    public Object getItem(int arg0) {
        return listSquares.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_quaner_list, null);
            holder = new ViewHolder();
            holder.img_avater = (ImageView) convertView
                    .findViewById(R.id.img_avater);
            holder.tv_detail = (TextView) convertView
                    .findViewById(R.id.tv_detail);
            holder.tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.img_detail = (NoScrollGridView) convertView
                    .findViewById(R.id.img_detail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String date = listSquares.get(arg0).getCreateTime();
        if (date != null) {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//            String time = sdf.format(date);
            String time = date.toString();
            holder.tv_time.setText(time);
        } else {
            holder.tv_time.setText("");
        }
        
        String detail = listSquares.get(arg0).getInformation();
        if (!TextUtils.isEmpty(detail)) {
            holder.tv_detail.setText(detail);
        } else {
            holder.tv_detail.setText("");
        }
        final User2 user = listSquares.get(arg0).getUser();
        if (user != null) {
            String name = user.getName();
            if (!TextUtils.isEmpty(name)) {
                holder.tv_title.setText(name);
            } else {
                holder.tv_title.setText("");
            }
            String url_img_avater = "";
            UploadedFile file = user.getAvatar();
            if (file != null) {
                String url = file.getUrl();
                if (TextUtils.isEmpty(url) || url.endsWith("portrait.gif")) {
                    holder.img_avater.setImageResource(R.drawable.umeng_socialize_share_pic);
                } else {
                    url_img_avater   = Url.url_base_img_news + url;
                    loader.display(holder.img_avater,url_img_avater);
                }
            } 
            final String mobile = user.getMobile();
            if (!TextUtils.isEmpty(mobile)){
                /**
                 * 跳转到用户信息页面
                 */
                holder.img_avater.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View arg0) {
                        LocalIM im = new LocalIM();
                        im.setCategory(user.getCategory());
                        im.setPhonenumber(user.getMobile());
                        im.setScreenName(user.getScreenName());
                        UploadedFile avatar = user.getAvatar();
                        if (avatar != null){
                            im.setUrl(user.getAvatar().getThumbnailUrl());
                        }
                        im.setUsername(user.getName());
                        Intent intent = new Intent(context,UserDetailActivity.class);
                        intent.putExtra("localim", im);
                        context.startActivity(intent);
                    }
                });
            }
        }
        Square square = listSquares.get(arg0);
        list_url.clear();
       final ArrayList<String> urls = new ArrayList<String>();
        if (square != null) {
            List<UploadedFile> set = square.getPhotos();
            if (set != null && set.size() > 0) {
                for (UploadedFile photo : set) {
                    String url = photo.getThumbnailUrl();
                    if (url != null) {
                     
                        list_url.add(url);
                    }
                    String url2 = photo.getUrl();
//                    if (url2 != null){
                        urls.add(Url.url_base_img_news + url2);
//                    }
                }
            }
            AbLogUtil.i(SquareAdapter.class, "大图个数" + urls.size());
        }
        if (list_url.size() > 0 && list_url != null) {
            holder.img_detail.setVisibility(View.VISIBLE);
            holder.img_detail.setAdapter(new GridAdapter(context,urls));
           holder.img_detail.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                    long arg3) {
//                imageBrown(position, urls);                
            }
        });
        } else {
            holder.img_detail.setVisibility(View.GONE);
        }
        return convertView;
    }

    protected void imageBrown(int position, ArrayList<String> urls) {
       AbLogUtil.i(SquareAdapter.class,"点击的位置"  + position);
        Intent intent = new Intent(context, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        context.startActivity(intent);
    }

    class ViewHolder {
        ImageView img_avater;// 头像
        TextView tv_title;// 标题
        TextView tv_detail;// 详情
        TextView tv_time;// 时间
        NoScrollGridView img_detail;// 九宫格图片展示
    }

    class GridAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<String> urls;
        public GridAdapter(Context context,ArrayList<String> urls) {
            super();
            this.urls = urls;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list_url.size();
        }

        @Override
        public Object getItem(int position) {
            return list_url.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder2 holder2;
            if (convertView == null) {
                holder2 = new ViewHolder2();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_square_grid_adapter, null);
                holder2.img_grid = (ImageView) convertView
                        .findViewById(R.id.img_grid);
                convertView.setTag(holder2);
            } else {
                holder2 = (ViewHolder2) convertView.getTag();
            }
            String url = list_url.get(position);
            if (TextUtils.isEmpty(url) || url.endsWith("portrait.gif")) {
                holder2.img_grid.setImageResource(R.drawable.umeng_socialize_share_pic);
            } else {
                String url_img_avater = Url.url_base_img_news + url;
                loader.display(holder2.img_grid,url_img_avater);
                holder2.img_grid.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View arg0) {
                        for (String url : urls){
                            AbLogUtil.i(SquareAdapter.class, url);
                        }
                        imageBrown(position, urls);
                        
                    }
                });
            }
            return convertView;
        }

        class ViewHolder2 {
            ImageView img_grid;// 图片
        }
    }
}
