package com.easemob.chatuidemo.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.UserDetailActivity;
import com.easemob.chatuidemo.domain.LocalIM;
import com.easemob.chatuidemo.http.Url;

public class AddContactAdapter extends BaseAdapter {
    private Context context;
    private List<LocalIM> list_im;
  //图片下载器
    private AbImageLoader mAbImageLoader = null;
    
    public AddContactAdapter(Context context, List<LocalIM> list_im) {
        super();
        this.context = context;
        this.list_im = list_im;
        //图片下载器
        mAbImageLoader = new AbImageLoader(context);
        mAbImageLoader.setDesiredWidth(0);
        mAbImageLoader.setDesiredHeight(0);
        mAbImageLoader.setLoadingImage(R.drawable.image_loading);
        mAbImageLoader.setErrorImage(R.drawable.image_error);
        mAbImageLoader.setEmptyImage(R.drawable.image_empty);
    }

    @Override
    public int getCount() {
        return list_im.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list_im.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View convetView, ViewGroup arg2) {
        ViewHolder holder;
        if (convetView == null){
            holder = new ViewHolder();
            convetView = LayoutInflater.from(context).inflate(R.layout.item_add_contact, null);
            holder.avatar = (ImageView) convetView.findViewById(R.id.avatar);
            holder.name = (TextView) convetView.findViewById(R.id.name);
            convetView.setTag(holder);
        } else {
            holder = (ViewHolder) convetView.getTag();
        }
        final LocalIM im = list_im.get(arg0);
        String url = im.getUrl();
        String img_url = Url.url_base_img_news + url;
        mAbImageLoader.display(holder.avatar, img_url);
        String name = im.getUsername();
        holder.name.setText(name+"");
        holder.avatar.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context,UserDetailActivity.class);
                intent.putExtra("localim", im);
                context.startActivity(intent);                
            }
        });
        return convetView;
    }
    class ViewHolder{
        ImageView avatar;//头像
        TextView name;//姓名
    }
}
