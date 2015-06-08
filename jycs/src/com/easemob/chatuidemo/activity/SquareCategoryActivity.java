package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.UnitAdapter;
import com.easemob.chatuidemo.domain.InvidateUser;
import com.easemob.chatuidemo.domain.dao.InvidateUserInsideDao;

public class SquareCategoryActivity extends Activity implements
        OnItemClickListener, OnClickListener {
    private TextView tv_title;//标题
    private ImageView img_back;// 返回按钮
    private ListView lv_unit;// 所在单位列表
    private UnitAdapter adapter;
    private List<String> list_categoty = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);
        initViews();
        InvidateUserInsideDao dao = new InvidateUserInsideDao(SquareCategoryActivity.this);
        dao.startReadableDatabase();
        InvidateUser user = dao.queryList().get(0);
        if (user.getIsGYX().equals("1")){
            list_categoty.add("供应商");
        } 
        if (user.getIsLC().equals("1")){
            list_categoty.add("联采");
        }
        if (user.getIsYG().equals("1")){
            list_categoty.add("员工");
        }
        adapter = new UnitAdapter(SquareCategoryActivity.this, list_categoty);
        lv_unit.setAdapter(adapter);

    }

    /**
     * 初始化控件
     */
    private void initViews() {
        img_back = (ImageView) this.findViewById(R.id.img_back);
        lv_unit = (ListView) this.findViewById(R.id.lv_unit);
        tv_title = (TextView) this.findViewById(R.id.tv_title);
        tv_title.setText("广场类别");
        lv_unit.setOnItemClickListener(this);
        img_back.setOnClickListener(this);
    }

    /**
     * 点击事件
     * 
     * @param v
     *            当前被点击v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.img_back:
            onBackPressed();
            break;

        default:
            break;
        }
    }

    /**
     * 每个item被点击
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        String category = list_categoty.get(position);
        Intent intent = getIntent();
        intent.putExtra("category", category);
        setResult(RESULT_OK, intent);
        finish();
    }
}
