/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbToastUtil;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.NewFriendsMsgAdapter;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.easemob.chatuidemo.domain.InviteMessage;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;

/**
 * 申请与通知
 * 
 */
@SuppressLint("HandlerLeak")
public class NewFriendsMsgActivity extends BaseActivity {
    private ListView listView;
    private static final String TAG = "NewFriendsMsgActivity";
    private AbHttpUtil abHttpUtil;// 网络请求工具类
    private DemoApplication application;
    private String imInviteName = "";
    private Result<User2> ret;
    private static final int CODE = 0000;
    private List<User2> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_msg);
        abHttpUtil = AbHttpUtil.getInstance(NewFriendsMsgActivity.this);
        abHttpUtil.setTimeout(20000);
        application = (DemoApplication) getApplication();
        if (!application.isNetworkConnected()) {
            AbToastUtil.showToast(NewFriendsMsgActivity.this,
                    R.string.network_not_connected);
        }
        listView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty);  
//        ViewGroup parentView = (ViewGroup) listView.getParent();  
//        parentView.addView(emptyView, 2); // 你需要在这儿设置正确的位置，以达到你需要的效果。  
        listView.setEmptyView(emptyView);  
        getUserFromImUsername();
        
    }
   /**
    * 得到本地服务器上用户信息
    */
    private void getUserFromImUsername() {
        InviteMessgeDao dao = new InviteMessgeDao(this);
        msgs = dao.getMessagesList();
        for (InviteMessage msg : msgs) {
            String imname = msg.getFrom();
            Log.i(TAG, imname);
            imInviteName += imname + ",";
        }
//        imInviteName = imInviteName.substring(0, imInviteName.length()-1);
        Log.i(TAG, imInviteName);
        AbRequestParams params = new AbRequestParams();
        params.put("imUsernames", imInviteName);
        abHttpUtil.get(Url.URL_SEARCH, params,
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
                        ret = (Result<User2>) JSONUtils.parseObject(content,
                                new TypeReference<Result<User2>>() {
                                });
                        Log.i(TAG, "返回值："+ret);
                        if (ret != null) {
                            list = ret.getResults();
                            if (list.size() > 0 && list != null) {
                                handler.sendEmptyMessage(CODE);
                                Log.i(TAG, "好友请求数量"+list.size());
                            }
                        }
                    }
                });
    }
    Handler handler = new Handler(){
      public void handleMessage(android.os.Message msg) {
          if (msg.what == CODE){
              NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(
                      NewFriendsMsgActivity.this, 1, msgs,list);
              listView.setAdapter(adapter);
              DemoApplication.getInstance().getContactList()
              .get(Constant.NEW_FRIENDS_USERNAME)
              .setUnreadMsgCount(0);
          }
      };  
    };
    private List<InviteMessage> msgs;
    public void back(View view) {
        finish();
    }

}
