package com.easemob.chatuidemo.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.ab.image.AbImageLoader;
import com.ab.util.AbToastUtil;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.NewsDetailActivity;

public class ViewPagerAdapterMessage extends PagerAdapter {
    private AbImageLoader loader;
    private List<String> list;
    private Context context;
    private List<String> list_url;
    private String type;
    public ViewPagerAdapterMessage(List<String> list,List<String> list_url,String type, Context context) {
        this.list = list;
        this.list_url = list_url;
        this.type = type;
        this.context = context;
        loader = AbImageLoader.getInstance(context);
        loader.setLoadingImage(R.drawable.image_loading);
        loader.setEmptyImage(R.drawable.image_empty);
        loader.setErrorImage(R.drawable.image_error);
        loader.setDesiredWidth(0);
        loader.setDesiredHeight(0);
    }

    /**
     * 一共有多少个数量
     */
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    /**
     * 判断当前view是不是来自object
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    /**
     * item被销毁的时候
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        ((ViewPager) container).removeView((View) object);
    }

    /**
     * item被生成的时候
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewPager vp = (ViewPager) container;
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_news_viewpager, null);
        if (list.size() == 0){
            AbToastUtil.showToast(context, "后台无数据");
            return view;
        } else {
            
            position %= list.size();
            if (position < 0){
                position = list.size() + position;
            }
            ImageView img = (ImageView) view.findViewById(R.id.img_news_title);
            loader.display(img, list.get(position));
            final String url = list_url.get(position);
            img.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View arg0) {
                    Log.i("ttt", url);
                    Intent intent = new Intent(context,NewsDetailActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("type", type);
                    context.startActivity(intent);
                }
            });
            ViewParent parent = view.getParent();
            if (parent != null){
                ViewGroup group = (ViewGroup) parent;
                group.removeView(view);
            }
            vp.addView(view);
            return view;
        }
    }

}
