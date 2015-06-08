package com.easemob.chatuidemo.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.StoreListAdapter;
import com.easemob.chatuidemo.adapter.ViewPagerAdapterMessage;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.dao.Store;
import com.easemob.chatuidemo.domain.dao.StoreInsideDao;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;
import com.easemob.chatuidemo.widget.ChildViewPager;

@SuppressLint("HandlerLeak")
public class StoreActivity extends Activity implements OnHeaderRefreshListener {
    private FrameLayout fm_back_title;// 返回按钮
    private AbPullToRefreshView store_refresh;// 下拉刷新
    private ListView lv_store_list;// 门店列表
    private AbHttpUtil abHttpUtil;
    private DemoApplication application;
    private View headview;
    private Result<Store> ret;
    private ChildViewPager vp_store;// 轮播门店
    private ImageView img_point1;// 轮播圆点
    private ImageView img_point2;// 轮播圆点
    private ImageView img_point3;// 轮播圆点
    private TextView tv_store_intro;// 门店简介
    private List<Store> list_all_store = new ArrayList<Store>();
    private List<Store> list_slide_store = new ArrayList<Store>();
    private List<Store> list_list_store = new ArrayList<Store>();
    private List<String> imgList = new ArrayList<String>();
    private List<String> tipsList = new ArrayList<String>();
    private ProgressDialog progressDialog;
    private StoreListAdapter adapter;
    private LinearLayout tagImageLayout;
    private ImageHandler handler = new ImageHandler(
            new WeakReference<StoreActivity>(this));
    private List<String> list_province = new ArrayList<String>();
    private List<String> list_url_slide = new ArrayList<String>();
   private Map<String, List<Store>> list_store_grid = new HashMap<String, List<Store>>();
   private StoreInsideDao storeInsideDao;
   private static final String TAG = "StoreActivity";
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store2);
        application = (DemoApplication) getApplication();
        if (!application.isNetworkConnected()) {
            AbToastUtil.showToast(StoreActivity.this,
                    R.string.network_not_connected);
        }
        abHttpUtil = AbHttpUtil.getInstance(application);
        abHttpUtil.setTimeout(20000);
        initViews();
        storeInsideDao = new StoreInsideDao(StoreActivity.this);
        storeInsideDao.startReadableDatabase();
        list_store = storeInsideDao.queryList();
        storeInsideDao.closeDatabase();
        Log.i(TAG, ""+list_store.size());
//        if (list_store.size() > 0){
//          msg.what = 2;
//          mHandler.sendMessage(msg);
//            Log.i(TAG, "insidedatebase");
//        } else {
            Log.i(TAG, "onheadrefresh");
           initStore();
//        }
    }

    private void initViews() {
        fm_back_title = (FrameLayout) this.findViewById(R.id.fm_back_title);
        fm_back_title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });
        store_refresh = (AbPullToRefreshView) this
                .findViewById(R.id.store_refresh);
        lv_store_list = (ListView) this.findViewById(R.id.lv_store_list);
        store_refresh.setOnHeaderRefreshListener(this);
        store_refresh.setLoadMoreEnable(false);
        // 设置进度条的样式
        store_refresh.getHeaderView().setHeaderProgressBarDrawable(
                this.getResources().getDrawable(R.drawable.progress_circular));

        initHeadView();
        lv_store_list.addHeaderView(headview);
//        initStore();
    }
    Message msg = Message.obtain();
    /**
     * 请求网络得到date
     */
    private void initStore() {
        abHttpUtil.get(Url.URL_STORE, new AbStringHttpResponseListener() {
            @Override
            public void onStart() {
                AbDialogUtil.showLoadDialog(StoreActivity.this,
                        R.drawable.progress_circular, "数据加载中...");
            }

            @Override
            public void onFinish() {
                store_refresh.onHeaderRefreshFinish();
                AbDialogUtil.removeDialog(StoreActivity.this);
            }

            @Override
            public void onFailure(int statusCode, String content,
                    Throwable error) {
                msg.what = -2;
                mHandler.sendMessage(msg);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(int statusCode, String content) {
                Log.i("huwenbao", content + "");
                ret = (Result<Store>) JSONUtils.parseObject(content,
                        new TypeReference<Result<Store>>() {
                        });
                list_all_store.clear();
                if (ret != null) {
                    list_all_store = ret.getResults();
                }
                if (list_all_store !=null && list_all_store.size() >0){
                    if (list_all_store.size() > list_store.size()){
                        storeInsideDao.startWritableDatabase(true);
                        storeInsideDao.deleteAll();
                        for (int i = 0 ; i < list_all_store.size();i++){
                            Store store = list_all_store.get(i);
                            if (store != null){
                                store.setuId("100"+i);
                                UploadedFile photo_all = store.getPhoto();
                                if (photo_all != null){
                                    photo_all.setuId("100"+i);
                                    store.setPhoto(photo_all);
                                    long id = storeInsideDao.insert(store);
                                    Log.i(TAG, id+"[store]"+store.toString());
                                }
                            }
                        }
                    }
                    storeInsideDao.closeDatabase();
                    msg.what = 1;
                } else {
                    msg.what = -1;
                }
                mHandler.sendMessageDelayed(msg, 3000);
            }
        });
    }

    Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                getDateByList(list_all_store);
                String type = "stores";
                vp_store.setAdapter(new ViewPagerAdapterMessage(imgList,list_url_slide,type,
                        StoreActivity.this));
                adapter = new StoreListAdapter(StoreActivity.this,
                        list_store_grid, list_province);
                lv_store_list.setAdapter(adapter);
            } else if (msg.what == 2) {
                getDateByList(list_store);
                String type = "stores";
                vp_store.setAdapter(new ViewPagerAdapterMessage(imgList,list_url_slide,type,
                        StoreActivity.this));
                adapter = new StoreListAdapter(StoreActivity.this,
                        list_store_grid, list_province);
                lv_store_list.setAdapter(adapter);
            } else if (msg.what ==3){
                store_refresh.onHeaderRefreshFinish();
            }else if (msg.what == -1) {
                AbToastUtil.showToast(StoreActivity.this, "没有数据");
            } else if (msg.what == -2) {
                AbToastUtil.showToast(StoreActivity.this,
                        R.string.xml_parser_failed);
            }
        }

    };
    private List<Store> list_store;
    private void getDateByList(List<Store> list) {
        imgList.clear();
        tipsList.clear();
        list_list_store.clear();
        list_province.clear();
        list_slide_store.clear();
       
        if (list != null && list.size() > 0) {
            for (int i = 0 ; i < list.size(); i++) {
                Store store = list.get(i);
                if (store != null) {
                    String type = store.getType();
                    if (type.equals("slideshow")) {
                        list_slide_store.add(store);
                        String url = store.getUrl();
//                        if (!TextUtils.isEmpty(url)){
                            list_url_slide.add(url);
//                        }
                        UploadedFile photo = store.getPhoto();
                        if (photo != null) {
                            String url2 = photo.getUrl();
                            imgList.add(Url.url_base_img_news + url2);
                            String intro = store.getStoreName();
                            if ( !TextUtils.isEmpty(intro)){
                                tipsList.add(intro);
                            }
                        }
                    } 
//                    else if (type.equals("listshow")) {
                        list_list_store.add(store);
                        String province = store.getProvince();
                        if ( !TextUtils.isEmpty(province)){
                            if (!list_province.contains(province)){
                                list_province.add(province);
                            }
                        }
//                    }
                }
                for (Store store2 :list_list_store){
                    String province = store2.getProvince();
                    List<Store> list_item = new ArrayList<Store>();
                    for (Store store3 : list_list_store){
                        if (province.equals(store3.getProvince())){
                            list_item.add(store3);
                        }
                    }
                    list_store_grid.put(province, list_item);
                }
            }
        }
    }

    private void initHeadView() {
        headview = LayoutInflater.from(this).inflate(
                R.layout.layout_head_store, null);
        tv_store_intro = (TextView) headview.findViewById(R.id.tv_store_intro);
        img_point1 = (ImageView) headview.findViewById(R.id.img_point1);
        img_point2 = (ImageView) headview.findViewById(R.id.img_point2);
        img_point3 = (ImageView) headview.findViewById(R.id.img_point3);
        vp_store = (ChildViewPager) headview.findViewById(R.id.vp_store);
        vp_store.setCurrentItem(Integer.MAX_VALUE / 2);// 默认在中间，使用户看不到边界
        // 开始轮播效果
        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE,
                ImageHandler.MSG_DELAY);
        vp_store.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int index = position % tipsList.size();
                tv_store_intro.setText(tipsList.get(index));
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
            break;
        case 1:
            img_point1.setImageResource(R.drawable.tagvewpager_point02);
            img_point2.setImageResource(R.drawable.tagvewpager_point01);
            img_point3.setImageResource(R.drawable.tagvewpager_point02);
            break;
        case 2:
            img_point1.setImageResource(R.drawable.tagvewpager_point02);
            img_point2.setImageResource(R.drawable.tagvewpager_point02);
            img_point3.setImageResource(R.drawable.tagvewpager_point01);
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

        tagImageLayout = new LinearLayout(StoreActivity.this);
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

    @Override
    public void onHeaderRefresh(AbPullToRefreshView view) {
//        initStore();
        mHandler.sendEmptyMessageDelayed(3, 3000);
          
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
        private WeakReference<StoreActivity> weakReference;
        private int currentItem = 0;

        protected ImageHandler(WeakReference<StoreActivity> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Log.d(LOG_TAG, "receive message " + msg.what);
            StoreActivity activity = weakReference.get();
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

                activity.vp_store.setCurrentItem(currentItem);
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
