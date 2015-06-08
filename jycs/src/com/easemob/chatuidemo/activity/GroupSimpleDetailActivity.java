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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;
import com.easemob.exceptions.EaseMobException;

public class GroupSimpleDetailActivity extends BaseActivity {
    private Button btn_add_group;
    private TextView tv_admin;
    private TextView tv_name;
    private TextView tv_introduction;
    private EMGroup group;
    private String groupid;
    private ProgressBar progressBar;
    private AbHttpUtil abHttpUtil;
    private static final String TAG = "GroupSimpleDetailActivity";
    private static final int OWNER = 0000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_simle_details);
        abHttpUtil = AbHttpUtil.getInstance(GroupSimpleDetailActivity.this);
        abHttpUtil.setTimeout(20000);
        tv_name = (TextView) findViewById(R.id.name);
        tv_admin = (TextView) findViewById(R.id.tv_admin);
        btn_add_group = (Button) findViewById(R.id.btn_add_to_group);
        tv_introduction = (TextView) findViewById(R.id.tv_introduction);
        progressBar = (ProgressBar) findViewById(R.id.loading);

        EMGroupInfo groupInfo = (EMGroupInfo) getIntent().getSerializableExtra(
                "groupinfo");
        String groupname = groupInfo.getGroupName();
        groupid = groupInfo.getGroupId();

        tv_name.setText(groupname);
        Log.i(TAG, "群名字:" + groupname);

        new Thread(new Runnable() {

            public void run() {
                // 从服务器获取详情
                try {
                    group = EMGroupManager.getInstance().getGroupFromServer(
                            groupid);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                            // 获取详情成功，并且自己不在群中，才让加入群聊按钮可点击
                            if (!group.getMembers().contains(
                                    EMChatManager.getInstance()
                                            .getCurrentUser()))
                                btn_add_group.setEnabled(true);
                            tv_name.setText(group.getGroupName());
                            handler.sendEmptyMessage(GroupSimpleDetailActivity.OWNER);
                            // tv_admin.setText(group.getOwner());
                            // AbRequestParams params = new AbRequestParams();
                            // params.put(attr, file);
                            // abHttpUtil.get(Url.URL_SEARCH, responseListener)
                            tv_introduction.setText(group.getDescription());
                        }
                    });
                } catch (final EaseMobException e) {
                    e.printStackTrace();
                    final String st1 = getResources().getString(
                            R.string.Failed_to_get_group_chat_information);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(GroupSimpleDetailActivity.this,
                                    st1 + e.getMessage(), 1).show();
                        }
                    });
                }

            }
        }).start();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String name = group.getOwner();
            AbRequestParams params = new AbRequestParams();
            params.put("imUsernames", name);
            abHttpUtil.get(Url.URL_SEARCH, params,
                    new AbStringHttpResponseListener() {

                        @Override
                        public void onStart() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onFinish() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onFailure(int statusCode, String content,
                                Throwable error) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onSuccess(int statusCode, String content) {
                            @SuppressWarnings("unchecked")
                            Result<User2> ret = (Result<User2>) JSONUtils
                                    .parseObject(content,
                                            new TypeReference<Result<User2>>() {
                                            });
                            if (ret != null) {
                                List<User2> list_user = ret.getResults();
                                if (list_user != null && list_user.size() > 0) {
                                    User2 user2 = list_user.get(0);
                                    String name = user2.getName();
                                    if (!TextUtils.isEmpty(name)) {
                                        tv_admin.setText(name);
                                    }
                                }
                            }
                        }
                    });
        }
    };

    // 加入群聊
    public void addToGroup(View view) {
        String st1 = getResources().getString(R.string.Is_sending_a_request);
        final String st2 = getResources().getString(R.string.Request_to_join);
        final String st3 = getResources().getString(
                R.string.send_the_request_is);
        final String st4 = getResources().getString(
                R.string.Join_the_group_chat);
        final String st5 = getResources().getString(
                R.string.Failed_to_join_the_group_chat);
        final ProgressDialog pd = new ProgressDialog(this);
        // getResources().getString(R.string)
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 如果是membersOnly的群，需要申请加入，不能直接join
                    if (group.isMembersOnly()) {
                        EMGroupManager.getInstance().applyJoinToGroup(groupid,
                                st2);
                    } else {
                        EMGroupManager.getInstance().joinGroup(groupid);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            if (group.isMembersOnly())
                                Toast.makeText(GroupSimpleDetailActivity.this,
                                        st3, 0).show();
                            else
                                Toast.makeText(GroupSimpleDetailActivity.this,
                                        st4, 0).show();
                            btn_add_group.setEnabled(false);
                        }
                    });
                } catch (final EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(GroupSimpleDetailActivity.this,
                                    st5 + e.getMessage(), 0).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View view) {
        finish();
    }
}
