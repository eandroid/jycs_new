package com.easemob.chatuidemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.widget.webview.FDWebView;

public class NewsDetailActivity extends AbActivity {
    private ImageView img_back_title;// 返回
    private FDWebView web_news;// 详情页面
    private TextView tv_title;// 标题
    private String url = "https://www.baidu.com/";
    private static final String TAG = "NewsDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsdetail);
        img_back_title = (ImageView) this.findViewById(R.id.img_back_title);
        tv_title = (TextView) this.findViewById(R.id.tv_title);
        web_news = (FDWebView) this.findViewById(R.id.web_news);
        img_back_title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        String type = intent.getExtras().getString("type");
        if ("news".equals(type)) {
            tv_title.setText("资讯详情");
        } else if ("stores".equals(type)) {
            tv_title.setText("门店详情");
        }
        url = intent.getExtras().getString("url");
        if (!TextUtils.isEmpty(url)) {
            Log.i("url", url);
            web_news.loadUrl(url);
        } else {
            new Thread() {
                public void run() {
                    try {
//                        AbDialogUtil.showLoadDialog(NewsDetailActivity.this,
//                                R.drawable.progress_circular, "正在加载...");
                        sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                };
            }.start();

//            AbDialogUtil.removeDialog(NewsDetailActivity.this);
            web_news.loadUrl("file:///android_asset/error.html");
        }

    }
}
