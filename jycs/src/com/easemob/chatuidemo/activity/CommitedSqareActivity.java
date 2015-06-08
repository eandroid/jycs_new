package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbSharedUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.SquareAdapter;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.Square;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;

public class CommitedSqareActivity extends Activity implements OnHeaderRefreshListener {
    private FrameLayout fm_add;// 三个按钮键
    private TextView tv_title;// 我的广场
    private FrameLayout fm_back_title;// 返回键
    private AbPullToRefreshView qunaner_pull_to_refresh;//下拉刷新控件
    private ListView lv_square;//广场列表
    private AbHttpUtil abHttpUtil ;//请求网络工具类
    private DemoApplication application;
    private ProgressDialog progressDialog;
    private Result<Square> ret = null;
    private List<Square> list_Squares = new ArrayList<Square>();
    private SquareAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quaner);
        abHttpUtil = AbHttpUtil.getInstance(getApplicationContext());
        abHttpUtil.setTimeout(5000);
        application = (DemoApplication) getApplication();
        if ( !application.isNetworkConnected()){
            AbToastUtil.showToast(CommitedSqareActivity.this, R.string.network_not_connected);
        }
        initViews();
        initSquare();
    }

    private void initSquare() {
        progressDialog.show();
        String userId = AbSharedUtil.getString(application, "userId");
      
        abHttpUtil.get(Url.URL_GET_MY_SQUARE + userId,
                new AbStringHttpResponseListener() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {
                        qunaner_pull_to_refresh.onHeaderRefreshFinish();
                        progressDialog.cancel();
                    }

                    @Override
                    public void onFailure(int statusCode, String content,
                            Throwable error) {
                        Toast.makeText(application,
                                content + "statusCode" + statusCode,
                                Toast.LENGTH_SHORT).show();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(int statusCode, String content) {
                        ret = (Result<Square>) JSONUtils.parseObject(content,
                                new TypeReference<Result<Square>>() {
                                });
                        if (ret == null) {
                            Toast.makeText(application, "暂无信息",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        list_Squares.clear();
                        list_Squares = ret.getResults();
                        if (list_Squares != null && list_Squares.size()>=0){
                            
                            adapter = new SquareAdapter(list_Squares, CommitedSqareActivity.this);
                            lv_square.setAdapter(adapter);
                        }
                    }
                });
    }

    /**
     * 初始化
     */
    private void initViews() {
        fm_add = (FrameLayout) this.findViewById(R.id.fm_add);
        fm_add.setVisibility(View.GONE);
        tv_title = (TextView) this.findViewById(R.id.tv_title);
        tv_title.setText("我的发布");
        fm_back_title = (FrameLayout) this.findViewById(R.id.fm_back_title);
        fm_back_title.setVisibility(View.VISIBLE);
        fm_back_title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onBackPressed();
                finish();
            }
        });
        qunaner_pull_to_refresh = (AbPullToRefreshView) this.findViewById(R.id.qunaner_pull_to_refresh);
        qunaner_pull_to_refresh.setOnHeaderRefreshListener(this);
        qunaner_pull_to_refresh.setLoadMoreEnable(false);// 设置不能上拉加载更多
        // 设置进度条样式
        qunaner_pull_to_refresh.getHeaderView().setHeaderProgressBarDrawable(
                this.getResources().getDrawable(R.drawable.progress_circular));
        lv_square = (ListView) this.findViewById(R.id.lv_square);
        progressDialog = ProgressDialog.show(CommitedSqareActivity.this, null,
                "正在加载，请稍等...", true, true);
    }

    @Override
    public void onHeaderRefresh(AbPullToRefreshView view) {
        initSquare();
    }
}
