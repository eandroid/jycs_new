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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.ContactAdapter;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.IM;
import com.easemob.chatuidemo.domain.LocalIM;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.domain.dao.LocalIMinsideDao;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;
import com.easemob.chatuidemo.widget.Sidebar;
import com.easemob.exceptions.EaseMobException;

/**
 * 联系人列表页
 * 
 */
public class ContactlistFragment extends Fragment {
    private ContactAdapter adapter;
    private List<User> contactList;
    private List<User> list_request;
    private ListView listView;
    private boolean hidden;
    private Sidebar sidebar;
    private InputMethodManager inputMethodManager;
    private List<String> blackList;
    ImageButton clearSearch;
    EditText query;
    private static final String TAG = "ContactlistFragment";
    private AbHttpUtil abHttpUtil;
    private Map<String,LocalIM> list_localim = new HashMap<String,LocalIM>();
    private LocalIMinsideDao imdao;
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;
        abHttpUtil = AbHttpUtil.getInstance(getActivity());
        abHttpUtil.setTimeout(20000);

        inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        listView = (ListView) getView().findViewById(R.id.list);
        sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
        sidebar.setListView(listView);
        // 黑名单列表
        blackList = EMContactManager.getInstance().getBlackListUsernames();
        contactList = new ArrayList<User>();
        list_request = new ArrayList<User>();
        imdao = new LocalIMinsideDao(getActivity());
        // 获取设置contactlist
        getContactList();
        getLocalIMInfo();
        // 搜索框
        query = (EditText) getView().findViewById(R.id.query);
        query.setHint(R.string.search);
        clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                adapter.getFilter().filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

        // // 设置adapter
        // adapter = new ContactAdapter(getActivity(), R.layout.row_contact,
        // contactList,list_localim);
        // listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String username = adapter.getItem(position).getUsername();
                Log.i(TAG, "用户名称" + username);
                if (Constant.NEW_FRIENDS_USERNAME.equals(username)) {
                    // 进入申请与通知页面
                    User user = DemoApplication.getInstance().getContactList()
                            .get(Constant.NEW_FRIENDS_USERNAME);
                    user.setUnreadMsgCount(0);
                    startActivity(new Intent(getActivity(),
                            NewFriendsMsgActivity.class));
                } else if (Constant.GROUP_USERNAME.equals(username)) {
                    // 进入群聊列表页面
                    startActivity(new Intent(getActivity(),
                            GroupsActivity.class));
                } else {
                    // demo中直接进入聊天页面，实际一般是进入用户详情页
                    Intent intent = new Intent(getActivity(),ChatActivity.class);
//                    String imName = adapter.getItem(position).getUsername();
                    intent.putExtra("userId",username);
                    LocalIM im = list_localim.get(username);
                    intent.putExtra("type","single");
                    intent.putExtra("localim", im);
                    Log.i(TAG, "imname" + im.getUsername() +"imurl" + im.getUrl());
//                    startActivity(new Intent(getActivity(), ChatActivity.class)
//                            .putExtra("userId", adapter.getItem(position)
//                                    .getUsername()));
                    startActivity(intent);
                }
            }
        });
        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(
                                getActivity().getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    final int position, long arg3) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());
                builder.setMessage("你确定要删除好友吗？")
                        .setCancelable(true)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                        User tobeDeleteUser = adapter
                                                .getItem(position);
                                        // 删除此联系人
                                        deleteContact(tobeDeleteUser);
                                        // 删除相关的邀请消息
                                        InviteMessgeDao dao = new InviteMessgeDao(
                                                getActivity());
                                        dao.deleteMessage(tobeDeleteUser
                                                .getUsername());
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }
                                }).show();
                return false;
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    /**
     * 删除联系人
     * 
     * @param toDeleteUser
     */
    public void deleteContact(final User tobeDeleteUser) {
        String st1 = getResources().getString(R.string.deleting);
        final String st2 = getResources().getString(R.string.Delete_failed);
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().deleteContact(
                            tobeDeleteUser.getUsername());
                    // 删除db和内存中此用户的数据
                    UserDao dao = new UserDao(getActivity());
                    dao.deleteContact(tobeDeleteUser.getUsername());
                    DemoApplication.getInstance().getContactList()
                            .remove(tobeDeleteUser.getUsername());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            adapter.remove(tobeDeleteUser);
                            adapter.notifyDataSetChanged();

                        }
                    });
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st2 + e.getMessage(),
                                    1).show();
                        }
                    });

                }

            }
        }).start();

    }

    /**
     * 把user移入到黑名单
     */
    private void moveToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st1 = getResources().getString(R.string.Is_moved_into_blacklist);
        final String st2 = getResources().getString(
                R.string.Move_into_blacklist_success);
        final String st3 = getResources().getString(
                R.string.Move_into_blacklist_failure);
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 加入到黑名单
                    EMContactManager.getInstance().addUserToBlackList(username,
                            false);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st2, 0).show();
                            refresh();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st3, 0).show();
                        }
                    });
                }
            }
        }).start();

    }

    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getContactList();
                    getLocalIMInfo();
                    // adapter.notifyDataSetChanged();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据contactlist得到本地服务器用户的信息
     */
    private void getLocalIMInfo() {
        progressDialog = ProgressDialog.show(getActivity(), null,
                "正在加载，请稍等...", true, true);
        String res_params = "";
        for (User user : list_request) {
            String name = user.getUsername();
            if (!TextUtils.isEmpty(name) && !name.contains("item")) {
                res_params += name + ",";
            }
        }
        Log.i(TAG, "请求的参数：" + res_params);
        AbRequestParams params = new AbRequestParams();
        params.put("imUsernames", res_params);
        abHttpUtil.get(Url.URL_SEARCH, params,
                new AbStringHttpResponseListener() {

                    @Override
                    public void onStart() {
                      
                    }

                    @Override
                    public void onFinish() {
                        progressDialog.cancel();
                    }

                    @Override
                    public void onFailure(int statusCode, String content,
                            Throwable error) {
                        AbToastUtil.showToast(getActivity(), "status"
                                + statusCode);
                    }

                    @Override
                    public void onSuccess(int statusCode, String content) {
                        @SuppressWarnings("unchecked")
                        Result<User2> ret = (Result<User2>) JSONUtils
                                .parseObject(content,
                                        new TypeReference<Result<User2>>() {
                                        });
                        list_localim.clear();
//                        list_localim.put("",new LocalIM());
//                        list_localim.add("",new LocalIM());
                        if (ret != null) {
                            LocalIM im;
                            imdao.startWritableDatabase(false);
                            imdao.deleteAll();
                            List<User2> list_user2 = ret.getResults();
                            if (list_user2 != null && list_user2.size() > 0) {
                                for (User2 user2 : list_user2) {
                                    if (user2 != null) {
                                        im = new LocalIM();
                                        String name = user2.getName();

                                        if (!TextUtils.isEmpty(name)) {
                                            im.setUsername(name);
                                        }
                                        String phonenumber = user2.getMobile();
                                        if (!TextUtils.isEmpty(phonenumber)){
                                            im.setPhonenumber(phonenumber);
                                        }
                                        String category = user2.getCategory();
                                        if (!TextUtils.isEmpty(category)){
                                            im.setCategory(category);
                                        }
                                        String screenName = user2.getScreenName();
                                        if (!TextUtils.isEmpty(screenName)){
                                            im.setScreenName(screenName);
                                        }
                                        UploadedFile avater = user2.getAvatar();
                                        if (avater != null) {
                                            String url = avater
                                                    .getThumbnailUrl();
                                            if (!TextUtils.isEmpty(url)) {
                                                im.setUrl(url);
                                            }
                                        }
                                        IM im2 = user2.getIm();
                                        String imname = "";
                                        if (im2 != null) {
                                             imname = im2.getUsername();
                                            im.setImName(imname);
                                            Log.i(TAG, "当前用户环信用户名为：" + imname
                                                    + "---本地服务器用户名为" + name);
                                        }
                                        long id = imdao.insert(im);
                                        Log.i(TAG, "插入结果为:" + id);
                                        list_localim.put(imname,im);
                                        int size = list_localim.size() - 2;
                                        Log.i(TAG, "本地好友数量是:" + size);
                                    }
                                }
                                imdao.closeDatabase();
                            }
                            // 设置adapter
                            Log.i(TAG, "环信好友数量" + contactList.size()
                                    + "：本地服务器好友数量" + list_localim.size());
                            adapter = new ContactAdapter(getActivity(),
                                    R.layout.row_contact, contactList,
                                    list_localim);
                            listView.setAdapter(adapter);
                        }
                    }
                });
    }

    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    private void getContactList() {
        contactList.clear();
        list_request.clear();
        // 获取本地好友列表
        Map<String, User> users = DemoApplication.getInstance()
                .getContactList();
        Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, User> entry = iterator.next();
            if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME)
                    && !entry.getKey().equals(Constant.GROUP_USERNAME)
                    && !blackList.contains(entry.getKey())){
                contactList.add(entry.getValue());
                list_request.add(entry.getValue());
            }
        }
        // 排序
        Collections.sort(list_request, new Comparator<User>() {

            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getUsername().compareTo(rhs.getUsername());
            }
        });

        // 加入"申请与通知"和"群聊"
        if (users.get(Constant.GROUP_USERNAME) != null)
            contactList.add(0, users.get(Constant.GROUP_USERNAME));
        // 把"申请与通知"添加到首位
        if (users.get(Constant.NEW_FRIENDS_USERNAME) != null)
            contactList.add(0, users.get(Constant.NEW_FRIENDS_USERNAME));
    }

    void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity()
                        .getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }

    }
}
