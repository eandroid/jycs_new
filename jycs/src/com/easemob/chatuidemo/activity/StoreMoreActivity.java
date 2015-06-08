package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.StoreMoreListAdapter;
import com.easemob.chatuidemo.domain.dao.Store;

public class StoreMoreActivity extends Activity {
    private ListView lv_store;// 门店列表
    private ImageView img_back_title;// 返回按钮
    private List<String> list_detail = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_more);
        lv_store = (ListView) this.findViewById(R.id.lv_store);
        img_back_title = (ImageView) this.findViewById(R.id.img_back_title);
        Intent intent = getIntent();
        List<Store> list_store = (List<Store>) intent.getSerializableExtra("store");
        StoreMoreListAdapter adapter = new StoreMoreListAdapter(list_store, StoreMoreActivity.this);
        lv_store.setAdapter(adapter);
      
        img_back_title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });
       
    }

}
