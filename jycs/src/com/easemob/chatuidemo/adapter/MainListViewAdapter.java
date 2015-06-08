package com.easemob.chatuidemo.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.NewsDetailActivity;
import com.easemob.chatuidemo.domain.Information;
import com.easemob.chatuidemo.http.Url;



@SuppressLint("SimpleDateFormat")
public class MainListViewAdapter extends BaseAdapter {
	LayoutInflater inflater;
	// 定义Context
	private Context mContext;
	private ViewHolder holder;
	private int clickTemp = -1;
	private List<Information> list;
	
	 private AbImageLoader loader;
    private String url;

	public MainListViewAdapter(Activity activity, List<Information> listViewList) {
		inflater = activity.getLayoutInflater();
		mContext = activity;
		list = listViewList;
		 loader = AbImageLoader.getInstance(activity);
         loader.setLoadingImage(R.drawable.image_loading);
         loader.setEmptyImage(R.drawable.image_empty);
         loader.setErrorImage(R.drawable.image_error);
         loader.setDesiredWidth(0);
         loader.setDesiredHeight(0);
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public void setSeclection(int position) {
		clickTemp = position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_news, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.time = (TextView) convertView
					.findViewById(R.id.tv_time);
			holder.tv_detail = (TextView) convertView.findViewById(R.id.tv_detail);
			holder.img = (ImageView) convertView
					.findViewById(R.id.img_avater);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Information information = list.get(position);
		if (information != null){
		    String title = information.getTitle();
		    if (!TextUtils.isEmpty(title)){
		        holder.title.setText(title);
		    }
		    Date data = information.getCreateTime();
		    if (data != null){
		        
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        String time = sdf.format(data);
		        holder.time.setText(time);
		    }
		    String content = information.getContent();
		    if (!TextUtils.isEmpty(content)){
		        holder.tv_detail.setText(content);
		    }
		   
		}
		
		String imgURL = list.get(position).getPhoto().getThumbnailUrl();
		Log.i("huwenbao", imgURL+"");
		if (imgURL.endsWith("portrait.gif") || TextUtils.isEmpty(imgURL)) {
			holder.img.setImageResource(R.drawable.umeng_socialize_share_pic);
		} else {
				imgURL = Url.url_base_img_news + imgURL;
				Log.i("huwenbao", imgURL+"");
			loader.display(holder.img, imgURL);
		}
		
		convertView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                url = information.getUrl();
                Log.i("ttt", url);
                    Intent intent = new Intent(mContext,
                            NewsDetailActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("type", "news");
                    mContext.startActivity(intent);
            }
        });
		return convertView;
	}

	private class ViewHolder {
		TextView title;
		TextView time;
		TextView tv_detail;
		ImageView img;
	}

	@Override
	public int getCount() {
		return list.size();
	}

}
