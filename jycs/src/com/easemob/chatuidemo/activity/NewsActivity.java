package com.easemob.chatuidemo.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
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
import com.easemob.chatuidemo.adapter.MainListViewAdapter;
import com.easemob.chatuidemo.adapter.ViewPagerAdapterMessage;
import com.easemob.chatuidemo.domain.Information;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;
import com.easemob.chatuidemo.widget.ChildViewPager;
import com.easemob.chatuidemo.widget.TopView;

@SuppressLint("HandlerLeak")
public class NewsActivity extends AbActivity implements OnHeaderRefreshListener {
    private static final String TAG = "NewsActivity";
    private AbPullToRefreshView news_refresh;// 下拉刷新控件
    private ListView lv_news_slide;// 新闻列表
    private AbHttpUtil httpUtil;// 得到请求网络工具
    private View headview;// 头部布局
    private TextView tv_title_pager;
    private ChildViewPager news_viewpager;// 轮播
    private DemoApplication appContext;// 全局变量
    private MainListViewAdapter listViewAdapter;
    private List<Information> list_up = new ArrayList<Information>();
    private List<Information> list_down = new ArrayList<Information>();
    private List<String> imgList = new ArrayList<String>();
    private List<String> tipsList = new ArrayList<String>();
    private ImageHandler handler = new ImageHandler(
            new WeakReference<NewsActivity>(this));
    private ImageView img_point1;
    private ImageView img_point2;
    private ImageView img_point3;
    private ImageView img_point4;
    private List<String> list_url = new ArrayList<String>();
    private List<String> list_slide_url = new ArrayList<String>();
    private TopView topView;
    private ProgressDialog progressDialog;
    // private ViewPager
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = (DemoApplication) getApplication();
        if (!appContext.isNetworkConnected()) {
            AbToastUtil.showToast(NewsActivity.this,
                    R.string.network_not_connected);
        }
        setContentView(R.layout.fragment_news2);
        httpUtil = AbHttpUtil.getInstance(NewsActivity.this);
        httpUtil.setTimeout(5000);
        initViews();
        initData();
    }

    /**
     * 初始化试图 view 父视图
     * 
     * @param view
     */
    private void initViews() {
        news_refresh = (AbPullToRefreshView)findViewById(R.id.news_refresh);
        lv_news_slide = (ListView)findViewById(R.id.lv_news_slide);
        news_refresh.setOnHeaderRefreshListener(this);
        news_refresh.setLoadMoreEnable(false);
        // 设置进度条的样式
        news_refresh.getHeaderView().setHeaderProgressBarDrawable(
                this.getResources().getDrawable(R.drawable.progress_circular));

        initHeadView();
        lv_news_slide.addHeaderView(headview);
        progressDialog = ProgressDialog.show(NewsActivity.this, null,
                "正在加载，请稍等...", true, true);
//        lv_news_slide.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                    long arg3) {
//                Intent intent = new Intent(NewsActivity.this,
//                        NewsDetailActivity.class);
//                intent.putExtra("url", list_url.get(arg2 - 1));
//                intent.putExtra("type", "news");
//                startActivity(intent);
//            }
//        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        topView = HomeActivity.getTopView();
        topView.hideBackBtn();
        topView.hideLefttBtn();
        topView.setTitle("资讯");
        topView.setRightBtnDrawable(R.drawable.background_store);
        topView.hideRightBtn();
        topView.getRightBtn().setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsActivity.this,StoreActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String type = "news";
                if (imgList.size() > 0){
                    news_viewpager.setAdapter(new ViewPagerAdapterMessage(imgList,
                            list_slide_url, type, NewsActivity.this));
                }
                listViewAdapter = new MainListViewAdapter(NewsActivity.this,
                        list_down);
                lv_news_slide.setAdapter(listViewAdapter);
            } else if (msg.what == -1) {
                AbToastUtil.showToast(NewsActivity.this, "没有数据");
            } else if (msg.what == -2) {
                AbToastUtil
                        .showToast(NewsActivity.this, R.string.xml_parser_failed);
            }
        }
    };
    private LinearLayout tagImageLayout;

    /**
     * 加载数据
     */
    private void initData() {
        AbRequestParams params = new AbRequestParams();
        DemoApplication.getInstance().getUserName();
        String userId = AbSharedUtil.getString(appContext, "userId");
        if (TextUtils.isEmpty(userId)) {
            Log.i(TAG,"用户的userid" + userId );
            Toast.makeText(NewsActivity.this, "未获取到用户id", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        params.put("userId", userId);
        httpUtil.get(Url.url_news, params, new AbStringHttpResponseListener() {
            Message msg = Message.obtain();

            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
                news_refresh.onHeaderRefreshFinish();
                progressDialog.cancel();
            }

            @Override
            public void onFailure(int arg0, String content, Throwable arg2) {
                msg.what = -2;
                Log.i(TAG, "返回内容"+content + "错误代码:"+arg0);
                mHandler.sendMessage(msg);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(int arg0, String content) {
                Result<Information> ret = (Result<Information>) JSONUtils
                        .parseObject(content,
                                new TypeReference<Result<Information>>() {
                                });
                list_up = new ArrayList<Information>();
                list_down = new ArrayList<Information>();
                imgList.clear();
                tipsList.clear();
                List<Information> list = null;
                if (ret != null) {
                    list = ret.getResults();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            Information information = list.get(i);
                            if (information != null) {

                                String type = information.getType();
                                if (!TextUtils.isEmpty(type)) {
                                    if (Url.URL_LIST.equals(type)) {
                                        String url_detail = information
                                                .getUrl();
                                        Log.i(TAG,i + type);
//                                        if (!TextUtils.isEmpty(url_detail)) {
                                            list_url.add(url_detail);
//                                        }
                                          
                                        list_down.add(information);
                                    } else if (Url.URL_SLIDE.equals(type)) {
                                        list_up.add(information);
                                        String url_slide_detail = information
                                                .getUrl();
//                                        if (!TextUtils
//                                                .isEmpty(url_slide_detail)) {
                                            list_slide_url
                                                    .add(url_slide_detail);
//                                        }
                                        Log.e("huwenbao", list_up.size() + "");
                                        UploadedFile photo = information
                                                .getPhoto();
                                        if (photo != null) {
                                            String url = photo.getUrl();
//                                            if (!TextUtils.isEmpty(url)) {
                                                imgList.add(Url.url_base_img_news
                                                        + url);
//                                            }
                                        }
                                        String title = information.getTitle();
                                        if (!TextUtils.isEmpty(title)) {
                                            tipsList.add(title);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Log.i(TAG, "list_lenth" + list_down.size());
                    Log.i(TAG, "slide_lenth" + list_up.size());
                }
                if (list.size() <= 0) {
                    msg.what = -1;
                } else {
                    msg.what = 1;
                    // appContext.saveObject(list_down, "newslist_");
                    // appContext.saveObject(list_up, "newsslide");
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 添加轮播头
     */
    private void initHeadView() {
        headview = LayoutInflater.from(NewsActivity.this).inflate(
                R.layout.layout_news_headview, null);
        news_viewpager = (ChildViewPager) headview
                .findViewById(R.id.news_viewpager);
        tv_title_pager = (TextView) headview.findViewById(R.id.tv_title_pager);
        img_point1 = (ImageView) headview.findViewById(R.id.img_point1);
        img_point2 = (ImageView) headview.findViewById(R.id.img_point2);
        img_point3 = (ImageView) headview.findViewById(R.id.img_point3);
        img_point4 = (ImageView) headview.findViewById(R.id.img_point4);
        news_viewpager.setCurrentItem(Integer.MAX_VALUE / 2);// 默认在中间，使用户看不到边界
        // 开始轮播效果
        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE,
                ImageHandler.MSG_DELAY);
        news_viewpager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int index = position % tipsList.size();
                tv_title_pager.setText(tipsList.get(index));
                initPoint(R.drawable.tagvewpager_point01,
                        R.drawable.tagvewpager_point02, 14, 5, 2, 20);
                handler.sendMessage(Message.obtain(handler,
                        ImageHandler.MSG_PAGE_CHANGED, position, 0));
                initSlidPoint(index);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    handler.sendEmptyMessageDelayed(
                            ImageHandler.MSG_UPDATE_IMAGE,
                            ImageHandler.MSG_DELAY);
                    break;
                default:
                    break;
                }
            }
        });
    }

    /**
     * 设置滑动的圆点
     * 
     * @param position
     */
    protected void initSlidPoint(int position) {
        switch (position) {
        case 0:
            img_point1.setImageResource(R.drawable.tagvewpager_point01);
            img_point2.setImageResource(R.drawable.tagvewpager_point02);
            img_point3.setImageResource(R.drawable.tagvewpager_point02);
            img_point4.setImageResource(R.drawable.tagvewpager_point02);
            break;
        case 1:
            img_point1.setImageResource(R.drawable.tagvewpager_point02);
            img_point2.setImageResource(R.drawable.tagvewpager_point01);
            img_point3.setImageResource(R.drawable.tagvewpager_point02);
            img_point4.setImageResource(R.drawable.tagvewpager_point02);
            break;
        case 2:
            img_point1.setImageResource(R.drawable.tagvewpager_point02);
            img_point2.setImageResource(R.drawable.tagvewpager_point02);
            img_point3.setImageResource(R.drawable.tagvewpager_point01);
            img_point4.setImageResource(R.drawable.tagvewpager_point02);
            break;
        case 3:
            img_point3.setImageResource(R.drawable.tagvewpager_point02);
            img_point2.setImageResource(R.drawable.tagvewpager_point02);
            img_point4.setImageResource(R.drawable.tagvewpager_point01);
            img_point1.setImageResource(R.drawable.tagvewpager_point02);
            break;
        default:
            break;
        }
    }

    /**
     * 轮播小圆点
     */
    protected void initPoint(int id1, int id2, int size, int imageMargin,
            int gravity, int layoutMargin) {

        tagImageLayout = new LinearLayout(NewsActivity.this);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM);
        if (gravity == 2) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.setMargins(0, 0, 0, layoutMargin);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.setMargins(0, layoutMargin, 0, 0);
        }
        tagImageLayout.setLayoutParams(params);
        ((ViewGroup) headview).addView(tagImageLayout);
    }

    /**
     * 下拉刷新
     * 
     * @param arg0
     */
    @Override
    public void onHeaderRefresh(AbPullToRefreshView arg0) {
        initData();
    }

    private static class ImageHandler extends Handler {

        /**
         * 请求更新显示的View。
         */
        protected static final int MSG_UPDATE_IMAGE = 1;
        /**
         * 请求暂停轮播。
         */
        protected static final int MSG_KEEP_SILENT = 2;
        /**
         * 请求恢复轮播。
         */
        protected static final int MSG_BREAK_SILENT = 3;
        /**
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
         * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
         */
        protected static final int MSG_PAGE_CHANGED = 4;

        // 轮播间隔时间
        protected static final long MSG_DELAY = 3000;

        // 使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
        private WeakReference<NewsActivity> weakReference;
        private int currentItem = 0;

        protected ImageHandler(WeakReference<NewsActivity> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Log.d(LOG_TAG, "receive message " + msg.what);
            NewsActivity activity = weakReference.get();
            if (activity == null) {
                // Activity已经回收，无需再处理UI了
                return;
            }
            // 检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            if (activity.handler.hasMessages(MSG_UPDATE_IMAGE)) {
                activity.handler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
            case MSG_UPDATE_IMAGE:
                currentItem++;
                activity.news_viewpager.setCurrentItem(currentItem);
                // 准备下次播放
                activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE,
                        MSG_DELAY);
                break;
            case MSG_KEEP_SILENT:
                // 只要不发送消息就暂停了
                break;
            case MSG_BREAK_SILENT:
                activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE,
                        MSG_DELAY);
                break;
            case MSG_PAGE_CHANGED:
                // 记录当前的页号，避免播放的时候页面显示不正确。
                currentItem = msg.arg1;
                break;
            default:
                break;
            }
        }
    }
}
