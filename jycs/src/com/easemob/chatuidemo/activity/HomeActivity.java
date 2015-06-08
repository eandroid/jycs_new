package com.easemob.chatuidemo.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.util.AbSharedUtil;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.widget.TopView;
import com.easemob.chatuidemo.widget.base.Global;

@SuppressWarnings("deprecation")
public class HomeActivity extends TabActivity {

    private static TopView topView;

    private TabHost tabhost;
    private TabHost.TabSpec first;
    private TabHost.TabSpec second;
    private TabHost.TabSpec third;
    private TabHost.TabSpec fourth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initScreenData();
        init();
    }

    private void init() {
        topView = (TopView) findViewById(R.id.top_title);
        topView.setActivity(this);
        topView.showRightBtn();
        tabhost = this.getTabHost();

        first = tabhost.newTabSpec("1");
        second = tabhost.newTabSpec("2");
        third = tabhost.newTabSpec("3");
        fourth = tabhost.newTabSpec("4");

        first.setIndicator(createContent("消息", R.drawable.imgbtn_message_press));
        second.setIndicator(createContent("联系人",
                R.drawable.imgbtn_contract_normal));
        third.setIndicator(createContent("广场", R.drawable.imgbtn_square_normal));
        fourth.setIndicator(createContent("我", R.drawable.imgbtn_me_normal));
        String category = AbSharedUtil.getString(getApplicationContext(), "category");
        // 绑定显示的页面
        // first.setContent(R.id.ll_first);
        String userid = AbSharedUtil.getString(getApplicationContext(), "userId");
//        if (userid.equals("866")){
//            first.setContent(new Intent(this, NewsActivity.class));
//            second.setContent(new Intent(this, AnonymityActivity.class));
//            third.setContent(new Intent(this, AnonymityActivity1.class));
//            fourth.setContent(new Intent(this, AnonymityActivity2.class));
//        } else {
//            if (category.equals("游客")){
//                first.setContent(new Intent(this, NewsActivity.class));
//                second.setContent(new Intent(this, AnonymityActivity.class));
//                third.setContent(new Intent(this, AnonymityActivity1.class));
//                fourth.setContent(new Intent(this, InvidateActivity.class));  
//            } else {
                first.setContent(new Intent(this, NewsActivity.class));
                second.setContent(new Intent(this, MainActivity.class));
                third.setContent(new Intent(this, SquareActivity.class));
                fourth.setContent(new Intent(this, InvidateActivity.class));
//            }
//        }
        // 将选项卡加进TabHost
        tabhost.addTab(first);
        tabhost.addTab(second);
        tabhost.addTab(third);
        tabhost.addTab(fourth);
        tabhost.setCurrentTab(0);
        // 设置tabHost切换时动态更改图标
        tabhost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                tabChanged(tabId);
            }

        });
    }

    public static TopView getTopView() {
        return topView;
    }

    // 捕获tab变化事件
    private void tabChanged(String tabId) {
        // 当前选中项
        if (tabId.equals("1")) {

            ImageView iv = (ImageView) tabhost.getTabWidget().getChildAt(0)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_message_press));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(1)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_contract_normal));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(2)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_square_normal));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(3)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_me_normal));
        } else if (tabId.equals("2")) {
            ImageView iv = (ImageView) tabhost.getTabWidget().getChildAt(0)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_message_normal));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(1)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_contract_press));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(2)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_square_normal));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(3)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_me_normal));
        } else if (tabId.equals("3")) {
            ImageView iv = (ImageView) tabhost.getTabWidget().getChildAt(0)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_message_normal));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(1)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_contract_normal));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(2)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_square_press));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(3)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_me_normal));
        } else if (tabId.equals("4")) {
            ImageView iv = (ImageView) tabhost.getTabWidget().getChildAt(0)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_message_normal));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(1)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_contract_normal));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(2)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_square_normal));
            iv = (ImageView) tabhost.getTabWidget().getChildAt(3)
                    .findViewById(R.id.img_name);
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.imgbtn_me_press));
        }
    }

    // 返回单个选项
    private View createContent(String text, int resid) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.layout_tabwidget, null, false);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        iv_icon = (ImageView) view.findViewById(R.id.img_name);
        tv_name.setText(text);
        iv_icon.setBackgroundResource(resid);
        return view;
    }

    private void initScreenData() {
        // TODO Auto-generated method stub
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        Rect rect = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        if (dm.widthPixels > 0 && dm.heightPixels > 0) {
            AbSharedUtil.putInt(this, Global.KEY_SCREEN_WIDTH, dm.widthPixels);
            AbSharedUtil
                    .putInt(this, Global.KEY_SCREEN_HEIGHT, dm.heightPixels);
            AbSharedUtil.putInt(this, Global.KEY_SCREEN_TOPBAR, rect.top);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            exitBy2Click();
            return false;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private ImageView iv_icon;
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;


    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
        }
    }
}
