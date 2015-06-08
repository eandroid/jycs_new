package com.easemob.chatuidemo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.ab.util.AbLogUtil;
import com.ab.util.AbToastUtil;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.LocalIM;
import com.easemob.chatuidemo.http.Url;

public class UserDetailActivity extends Activity {
    private FrameLayout fm_back_title;
    private ImageView img_avater;// 用户头像
    private TextView tv_avater_name;// 用户名
    private TextView tv_avater_invidator;// 帐号
    private TextView tv_phone;// 手机号
    private TextView tv_address;// 地区
    private TextView tv_category;// 职务
    private DemoApplication application;
    private ProgressDialog progressDialog;// 请求网络进度dialog
    private AbImageLoader abImageLoader = null;
    private String imgURL;
    private LocalIM im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (DemoApplication) getApplication();
        if (!application.isNetworkConnected()) {
            AbToastUtil.showToast(UserDetailActivity.this,
                    R.string.Connection_failure);
        }
      abImageLoader =  application.getLoader();
        setContentView(R.layout.activity_userdetail);
        Intent intent = getIntent();
        im = (LocalIM) intent.getSerializableExtra("localim");
        AbLogUtil.i(UserDetailActivity.class, im.toString());
        initViews();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        tv_avater_name = (TextView) this.findViewById(R.id.tv_avater_name);
        tv_avater_name
                .setText( im.getUsername()+"");
        tv_avater_invidator = (TextView) this
                .findViewById(R.id.tv_avater_invidator);
        tv_avater_invidator.setText("帐号：" + im.getPhonenumber());
        tv_phone = (TextView) this.findViewById(R.id.tv_phone);
        tv_phone.setText(im.getUsername());
        tv_address = (TextView) this.findViewById(R.id.tv_address);
        tv_address.setText(im.getCategory() + "");
        tv_category = (TextView) this.findViewById(R.id.tv_category);
        tv_category.setText(im.getPhonenumber() + "");
        TextView tv_category2 = (TextView) this.findViewById(R.id.tv_category2);
        tv_category2.setText(im.getScreenName());
        img_avater= (ImageView) this.findViewById(R.id.img_avater);
        abImageLoader.display(img_avater, Url.url_base_img_news + im.getUrl());
        
    }
    public void call(View v){
        Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+
                tv_category.getText().toString().trim()));
        startActivity(intent);
    }
    public void onback(View v) {
        onBackPressed();
    }
}
