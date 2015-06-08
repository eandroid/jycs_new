package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbSharedUtil;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.UnitAdapter;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.Unit;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;

/**
 * 所在单位
 * 
 * @author 让火灭了雨
 * 
 */
public class UnitActivity extends AbActivity implements OnClickListener,
        OnItemClickListener {
    private ImageView img_back;// 返回按钮
    private ListView lv_unit;// 所在单位列表
    private UnitAdapter adapter;
    private List<Unit> set;
    private List<String> list_units = new ArrayList<String>();
    private AbHttpUtil abHttpUtil;
    private Result<User2> ret;
    private List<String> list_unitid = new ArrayList<String>();
    /* 结果标志 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);
        initViews();
        String userid = AbSharedUtil.getString(getApplicationContext(), "userId");
        abHttpUtil = AbHttpUtil.getInstance(UnitActivity.this);
        abHttpUtil.get("http://vps1.taoware.com:8080/jc/users?id="+userid,
                new AbStringHttpResponseListener() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onFailure(int statusCode, String content,
                            Throwable error) {

                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(int statusCode, String content) {
                        Log.i("LoginActivity", content);
                        ret = (Result<User2>) JSONUtils.parseObject(content,
                                new TypeReference<Result<User2>>() {
                                });
                        if (ret != null) {
                            List<User2> list = ret.getResults();
                            if (list != null && list.size() > 0) {
                                User2 user = list.get(0);
                                if (user != null) {
                                    set = user.getUnits();
                                    if (set != null && set.size() > 0) {
                                        for (Unit unit : set) {
                                            if (unit != null) {
                                                String str = unit.getName();
                                                if (!TextUtils.isEmpty(str)) {
                                                    list_units.add(str);
                                                }
                                                Long id = unit.getId();
                                                if (id != null){
                                                    list_unitid.add(String.valueOf(id));
                                                }
                                                
                                            }

                                        }
                                    }
                                }
                            }
                        }
                        adapter = new UnitAdapter(UnitActivity.this, list_units);
                        lv_unit.setAdapter(adapter);
                    }
                });
       
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        img_back = (ImageView) this.findViewById(R.id.img_back);
        lv_unit = (ListView) this.findViewById(R.id.lv_unit);
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
        String unit = list_units.get(position);
        Intent intent = getIntent();
        intent.putExtra("unit", unit);
        intent.putExtra("id", list_unitid.get(position));
        this.setResult(RESULT_OK, intent);
        finish();
    }
}
