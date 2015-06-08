package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.LocalActivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.MyViewPagerAdapter;
import com.easemob.chatuidemo.widget.ChildViewPager;
import com.easemob.chatuidemo.widget.TopView;

public class MessageActivity extends AbActivity implements OnClickListener {
    private LinearLayout btn1;// 资讯
    private ImageView img_btn1;// 资讯背景图片
    private ImageView img_btn2;//
    private ImageView img_btn3;//
    private LinearLayout btn2;// 消息
    private LinearLayout btn3;// 通知
    private ChildViewPager viewpager_contact;
    private List<Fragment> list_fragment = new ArrayList<Fragment>();// fragment列表
    private Fragment fragment;
    private TextView tv_btn1;// 资讯字体
    private TextView tv_btn2;// 消息
    private TextView tv_btn3;// 通知
    @SuppressWarnings("deprecation")
    private LocalActivityManager lam;
    private TopView topView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;
        setAbContentView(R.layout.fragment_message);
        // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        lam = new LocalActivityManager(MessageActivity.this, true);
        lam.dispatchCreate(savedInstanceState);
//        topView = 
        initViews();
        initEvents();
        initActivities();
    }

    /**
     * 初始化activity
     */
    @SuppressWarnings("deprecation")
    private void initActivities() {
        fragment = new NewsFragment2();
        list_fragment.add(fragment);
        fragment = new ChatAllHistoryFragment();
        list_fragment.add(fragment);
        // fragment = new FragmentNotify();
        // list_fragment.add(fragment);
       MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(), list_fragment);
        viewpager_contact.setAdapter(adapter);
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
       
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        viewpager_contact.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                setImg(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     * 初始化视图控件
     * 
     * @param view
     */
    private void initViews() {
        tv_btn1 = (TextView) findViewById(R.id.tv_btn1);
        tv_btn2 = (TextView) findViewById(R.id.tv_btn2);
        tv_btn3 = (TextView) findViewById(R.id.tv_btn3);
        btn1 = (LinearLayout) findViewById(R.id.btn1);
        btn2 = (LinearLayout) findViewById(R.id.btn2);
        btn3 = (LinearLayout) findViewById(R.id.btn3);
        img_btn1 = (ImageView) findViewById(R.id.img_btn1);
        img_btn2 = (ImageView) findViewById(R.id.img_btn2);
        img_btn3 = (ImageView) findViewById(R.id.img_btn3);
        viewpager_contact = (ChildViewPager)
                findViewById(R.id.viewpager_contact);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn1:
            setImg(0);
            viewpager_contact.setCurrentItem(0);
            break;
        case R.id.btn2:
            setImg(1);
            viewpager_contact.setCurrentItem(1);
            break;
        case R.id.btn3:
            setImg(2);
            viewpager_contact.setCurrentItem(2);
            break;
        }
    }

    /**
     * 设置背景图
     * 
     * @param i
     *            当前点选的按钮
     */
    private void setImg(int i) {
        switch (i) {
        case 0:
            tv_btn2.setTextColor(getResources().getColor(R.color.gray_normal));
            tv_btn3.setTextColor(getResources().getColor(R.color.gray_normal));
            tv_btn1.setTextColor(getResources().getColor(R.color.black_deep));
            img_btn1.setBackgroundResource(R.color.massager_background_line_press);
            img_btn2.setBackgroundResource(R.color.massager_background_line_normal);
            img_btn3.setBackgroundResource(R.color.massager_background_line_normal);
            break;
        case 1:
            tv_btn1.setTextColor(getResources().getColor(R.color.gray_normal));
            tv_btn3.setTextColor(getResources().getColor(R.color.gray_normal));
            tv_btn2.setTextColor(getResources().getColor(R.color.black_deep));
            img_btn2.setBackgroundResource(R.color.massager_background_line_press);
            img_btn1.setBackgroundResource(R.color.massager_background_line_normal);
            img_btn3.setBackgroundResource(R.color.massager_background_line_normal);
            break;
        case 2:
            tv_btn1.setTextColor(getResources().getColor(R.color.gray_normal));
            tv_btn2.setTextColor(getResources().getColor(R.color.gray_normal));
            tv_btn3.setTextColor(getResources().getColor(R.color.black_deep));
            img_btn3.setBackgroundResource(R.color.massager_background_line_press);
            img_btn2.setBackgroundResource(R.color.massager_background_line_normal);
            img_btn1.setBackgroundResource(R.color.massager_background_line_normal);
            break;

        default:
            break;
        }
    }
}
