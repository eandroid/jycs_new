package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbSharedUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.SquareAdapter;
import com.easemob.chatuidemo.domain.InvidateUser;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.Square;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.domain.dao.InvidateUserInsideDao;
import com.easemob.chatuidemo.domain.dao.SquareInsideDao;
import com.easemob.chatuidemo.domain.dao.UserInsideDao;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;
import com.easemob.chatuidemo.widget.TopView;

/**
 * 广场信息列表
 * 
 * @author 让雨灭了火
 * 
 */
public class SquareActivity extends AbActivity implements OnClickListener,
        OnHeaderRefreshListener {
    private static final String TAG = "SquareActivity";
    private TopView topView;
    private AbPullToRefreshView qunaner_pull_to_refresh;// 下拉刷新
    private ListView lv_square;// 广场列表
    private AbHttpUtil abHttpUtil;// 请求网络
    private DemoApplication application;
    private UserInsideDao userInsideDao;
    private SquareInsideDao squareInsideDao;
    private ProgressDialog progressDialog;
    private String userid;// 用户id
    private List<Square> list_square = new ArrayList<Square>();
//    private List<User2> list_user = new ArrayList<User2>();
    protected SquareAdapter adapter;
    private InvidateUser user;
    InvidateUserInsideDao dao = new InvidateUserInsideDao(
            SquareActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);
        application = (DemoApplication) getApplication();
        abHttpUtil = AbHttpUtil.getInstance(application);
        abHttpUtil.setTimeout(40000);
        if (!application.isNetworkConnected()) {
            AbToastUtil.showToast(SquareActivity.this,
                    R.string.network_not_connected);
        }
        
        dao.startReadableDatabase();
        user = dao.queryList().get(0);
        initViews();
        // if (list_square != null && list_square.size() > 0) {
        // Log.i(TAG, "广场列表信息:" + list_square.size());
        // adapter = new SquareAdapter(list_square, SquareActivity.this);
        // lv_square.setAdapter(adapter);
        // } else {
        refreSquare();
        // }
        //
    }

    private void initViews() {
        lv_square = (ListView) this.findViewById(R.id.lv_square);
        qunaner_pull_to_refresh = (AbPullToRefreshView) this
                .findViewById(R.id.qunaner_pull_to_refresh);
        qunaner_pull_to_refresh.setOnHeaderRefreshListener(this);
        qunaner_pull_to_refresh.setLoadMoreEnable(false);// 设置不能上拉加载更多
        // 设置进度条样式
        qunaner_pull_to_refresh.getHeaderView().setHeaderProgressBarDrawable(
                this.getResources().getDrawable(R.drawable.progress_circular));
        progressDialog = ProgressDialog.show(SquareActivity.this, null,
                "正在加载，请稍等...", true, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        topView = HomeActivity.getTopView();
        topView.setTitle("广场");
        topView.hideBackBtn();
        topView.hideLefttBtn();
        Log.i(TAG,user.getIsGYX() +user.getIsLC()
                + user.getIsYG());
        if (user.getIsGYX().equals("0") && user.getIsLC().equals("0")
                && user.getIsYG().equals("0")) {
            topView.hideRightBtn();
        } else {
            topView.showRightBtn();
            topView.setRightBtnDrawable(R.drawable.background_add);
            topView.getRightBtn().setOnClickListener(this);
        }
        refreSquare();
    }

    /**
     * 跳转到图片
     * 
     * @param v
     */
    public void gointent(View v) {
        Intent intent = new Intent(SquareActivity.this, AddPhotoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_right:
            gointent(v);// 跳转
            break;

        default:
            break;
        }
    }

    @Override
    public void onHeaderRefresh(AbPullToRefreshView view) {
        refreSquare();
    }

    private void refreSquare() {
        AbRequestParams params = new AbRequestParams();
        userid = AbSharedUtil.getString(application, "userId");
        params.put("userId", userid);
        Log.i(TAG, userid);
        abHttpUtil.get(com.easemob.chatuidemo.http.Url.URL_GET_ALL_SQUARE,
                params, new AbStringHttpResponseListener() {

                    private Result<Square> ret;

                    @Override
                    public void onStart() {
                       
                    }

                    @Override
                    public void onFinish() {
                       progressDialog.cancel();
                        qunaner_pull_to_refresh.onHeaderRefreshFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, String content,
                            Throwable error) {
                        Toast.makeText(application, content + statusCode,
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
                        list_square.clear();
//                        list_user.clear();
                        list_square = ret.getResults();
                        squareInsideDao = new SquareInsideDao(
                                SquareActivity.this);
                        squareInsideDao.deleteAll();
                        squareInsideDao.startWritableDatabase(false);
                        if (list_square != null && list_square.size() >= 0) {
                            for (int i = 0; i < list_square.size(); i++) {
                                Square square = list_square.get(i);
                                square.setId("100" + i);
                                List<UploadedFile> list_photo = square
                                        .getPhotos();
                                if (list_photo != null && list_photo.size() > 0) {
                                    for (int j = 0; j < list_photo.size(); j++) {
                                        UploadedFile file = list_photo.get(j);
                                        file.setImgId("100" + i);
                                    }
                                }
//                                User2 user2 = square.getUser();
//                                list_user.add(user2);
                                square.setPhotos(list_photo);
                            }
                            long[] ids = squareInsideDao
                                    .insertList(list_square);
                            for (int i = 0; i < ids.length; i++) {
                                Log.i(TAG, "插入数据" + ids[i]);
                            }
                            squareInsideDao.closeDatabase();
                            adapter = new SquareAdapter(list_square,
                                    SquareActivity.this);
                            lv_square.setAdapter(adapter);
                        }
                    }
                });
    }
}
