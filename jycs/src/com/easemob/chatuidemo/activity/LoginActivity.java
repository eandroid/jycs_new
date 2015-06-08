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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest.permission;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbSharedUtil;
import com.ab.util.AbToastUtil;
import com.alibaba.fastjson.TypeReference;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.IM;
import com.easemob.chatuidemo.domain.InvidateUser;
import com.easemob.chatuidemo.domain.Permission;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.domain.dao.InvidateUserInsideDao;
import com.easemob.chatuidemo.domain.dao.UserInsideDao;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends BaseActivity {
    public static final int REQUEST_CODE_SETNICK = 1;
    private static final String TAG = "LoginActivity";
    private EditText et_username;
    private EditText et_password;

    private boolean progressShow;
    private boolean autoLogin = false;
    private static final String USERAME = "mobile";
    private static final String PASSWORD = "plainPassword";
    private String currentUsername;
    private String currentPassword;
    private AbHttpUtil abHttpUtil = null;// 请求网络工具类
    private Result<User2> ret;
    private UserInsideDao userInsideDao = new UserInsideDao(LoginActivity.this);
    private InvidateUserInsideDao insideDao = new InvidateUserInsideDao(
            LoginActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 如果用户名密码都有，直接进入主页面
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            return;
        }
        abHttpUtil = AbHttpUtil.getInstance(LoginActivity.this);
        abHttpUtil.setTimeout(20000);
        setContentView(R.layout.activity_login);

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        // 如果用户名改变，清空密码
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
//                et_password.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 登录
     * 
     * @param view
     */
    public void login(View view) {
        if (!CommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        currentUsername = et_username.getText().toString().trim();
        currentPassword = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(this, R.string.User_name_cannot_be_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, R.string.Password_cannot_be_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(LoginActivity.this,
                com.easemob.chatuidemo.activity.AlertDialog.class);
        intent.putExtra("editTextShow", true);
        intent.putExtra("titleIsCancel", true);
        intent.putExtra("msg",
                getResources().getString(R.string.please_set_the_current));
        intent.putExtra("edit_text", currentUsername);
        startActivityForResult(intent, REQUEST_CODE_SETNICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SETNICK) {
                DemoApplication.currentUserNick = data
                        .getStringExtra("edittext");
                loginJYCS();
                ;
            }
        }
    }

    /**
     * 登录教育超市服务器
     */
    private void loginJYCS() {
        progressShow = true;
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progressShow = false;
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();
        AbRequestParams params = new AbRequestParams();
        params.put(LoginActivity.USERAME, currentUsername);
        params.put(LoginActivity.PASSWORD, currentPassword);
        abHttpUtil.get(Url.URL_LOGIN, params,
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
                        pd.cancel();
                        String msg = "";
                        Log.i(TAG, "错误码" + statusCode);
                        switch (statusCode) {
                        case 403:
                            msg = "用户名不存在";
                            break;
                        case 406:
                            msg = "密码错误";
                            break;
                        case 408:
                            msg = "用户名存在非中文以外其他字符";
                            break;
                        default:
                            msg = "服务器异常";
                            break;
                        }
                        AbToastUtil.showToast(LoginActivity.this, msg);
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(int statusCode, String content) {
                        String im_name = null;
                        String im_pass = null;
                        Log.i(TAG, content);
                        ret = (Result<User2>) JSONUtils.parseObject(content,
                                new TypeReference<Result<User2>>() {
                                });
                        if (ret != null) {
                            List<User2> list_user = ret.getResults();
                            if (list_user != null && list_user.size() > 0) {
                                insideDao.startWritableDatabase(false);
                                insideDao.deleteAll();
                                Log.i(TAG, "返回的user2的数量:" + list_user.size());
                                InvidateUser invidateUser = new InvidateUser();
                                User2 user2 = list_user.get(0);
                                if (user2 != null) {
                                    String userid = user2.getId();
                                    if (!TextUtils.isEmpty(userid)) {
                                        AbSharedUtil.putString(
                                                getApplicationContext(),
                                                "userId", userid);
                                    }
                                    invidateUser.setName(user2.getName());
                                    AbSharedUtil.putString(
                                            getApplicationContext(),
                                            "name", user2.getName());
                                    invidateUser.setMobile(user2.getMobile());
                                    invidateUser.setCategory(user2
                                            .getCategory());
                                    invidateUser.setScreenName(user2.getScreenName());
                                    AbSharedUtil.putString(
                                            getApplicationContext(),
                                            "category", user2.getScreenName());
                                    List<Permission> list_permisson = user2
                                            .getPermissions();
                                    if (list_permisson.size() > 0
                                            && list_permisson != null) {
                                        for (int i = 0; i < list_permisson
                                                .size(); i++) {
                                            Permission name = list_permisson
                                                    .get(i);
                                            String type = name.getId();
                                            Log.i(TAG, "类型" + type);
                                            if (!TextUtils.isEmpty(type)) {
                                                if (type.equals("13")) {
                                                    invidateUser.setIsLC("1");
                                                }
                                                if (type.equals("15")) {
                                                    invidateUser.setIsYG("1");
                                                }
                                                if (type.equals("17")) {
                                                    invidateUser.setIsGYX("1");
                                                }
                                            }
                                        }
                                    }
                                }
                                UploadedFile photo = user2.getAvatar();
                                if (photo != null) {
                                    if (!TextUtils.isEmpty(photo
                                            .getThumbnailUrl())) {

                                        invidateUser.set_url(photo
                                                .getThumbnailUrl());
                                        AbSharedUtil
                                                .putString(
                                                        getApplication(),
                                                        "url",
                                                        Url.url_base_img_news
                                                                + photo.getThumbnailUrl());
                                    }
                                }
                                IM im = user2.getIm();
                                if (im != null) {
                                    im_name = im.getUsername();
                                    im_pass = im.getPassword();
                                }
                                long id = insideDao.insert(invidateUser);
                                Log.i(TAG, "插入头像" + id);
                                insideDao.closeDatabase();
                            }
                        } else {
                            AbToastUtil.showToast(LoginActivity.this,
                                    "服务器数据库调用失败");
                        }

                        loginHuanXin(im_name, im_pass);
                    }

                    private void loginHuanXin(String name, String psw) {
                        final long start = System.currentTimeMillis();
                        // 调用sdk登陆方法登陆聊天服务器
                        currentUsername = name;
                        currentPassword = psw;
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(psw)) {
                            AbToastUtil.showToast(LoginActivity.this,
                                    "用户聊天信息获取失败，请联系管理员");
                            pd.cancel();
                            return;
                        }
                        EMChatManager.getInstance().login(currentUsername,
                                currentPassword, new EMCallBack() {

                                    @Override
                                    public void onSuccess() {

                                        if (!progressShow) {
                                            return;
                                        }
                                        // 登陆成功，保存用户名密码
                                        DemoApplication.getInstance()
                                                .setUserName(currentUsername);
                                        DemoApplication.getInstance()
                                                .setPassword(currentPassword);

                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                pd.setMessage(getString(R.string.list_is_for));
                                            }
                                        });
                                        try {
                                            // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                                            // ** manually load all local groups
                                            // and
                                            // conversations in case we are auto
                                            // login
                                            EMGroupManager.getInstance()
                                                    .loadAllGroups();
                                            EMChatManager.getInstance()
                                                    .loadAllConversations();
                                            // 处理好友和群组
                                            processContactsAndGroups();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            // 取好友或者群聊失败，不让进入主页面
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    pd.dismiss();
                                                    DemoApplication
                                                            .getInstance()
                                                            .logout(null);
                                                    Toast.makeText(
                                                            getApplicationContext(),
                                                            R.string.login_failure_failed,
                                                            1).show();
                                                }
                                            });
                                            return;
                                        }
                                        // 更新当前用户的nickname
                                        // 此方法的作用是在ios离线推送时能够显示用户nick
                                        boolean updatenick = EMChatManager
                                                .getInstance()
                                                .updateCurrentUserNick(
                                                        DemoApplication.currentUserNick
                                                                .trim());
                                        if (!updatenick) {
                                            Log.e("LoginActivity",
                                                    "update current user nick fail");
                                        }
                                        if (!LoginActivity.this.isFinishing())
                                            pd.dismiss();
                                        // 进入主页面
                                        startActivity(new Intent(
                                                LoginActivity.this,
                                                HomeActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onProgress(int progress,
                                            String status) {
                                    }

                                    @Override
                                    public void onError(final int code,
                                            final String message) {
                                        if (!progressShow) {
                                            return;
                                        }
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                pd.dismiss();
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        getString(R.string.Login_failed)
                                                                + message,
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                                    }
                                });
                    }
                });
    }

    /**
     * 匿名登录
     * 
     * @param v
     */
    public void anonymity(View v) {
        AbSharedUtil.putString(getApplicationContext(), "userId", "866");
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 忘记密码
     * 
     * @param v忘记密码
     */
    public void forgetpsw(View v) {
        Intent intent = new Intent(LoginActivity.this, ResetPswActivity.class);
        startActivity(intent);
        // finish();
    }

    private void processContactsAndGroups() throws EaseMobException {
        // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
        List<String> usernames = EMContactManager.getInstance()
                .getContactUserNames();
        EMLog.d("roster", "contacts size: " + usernames.size());
        Map<String, User> userlist = new HashMap<String, User>();
        for (String username : usernames) {
            User user = new User();
            user.setUsername(username);
            setUserHearder(username, user);
            userlist.put(username, user);
        }
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 存入内存
        DemoApplication.getInstance().setContactList(userlist);
        System.out.println("----------------" + userlist.values().toString());
        // 存入db
        UserDao dao = new UserDao(LoginActivity.this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);

        // 获取黑名单列表
        List<String> blackList = EMContactManager.getInstance()
                .getBlackListUsernamesFromServer();
        // 保存黑名单
        EMContactManager.getInstance().saveBlackList(blackList);

        // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
        EMGroupManager.getInstance().getGroupsFromServer();
    }

    /**
     * 注册
     * 
     * @param view
     */
    public void register(View view) {
        startActivityForResult(new Intent(this, RegisterActivity.class), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     * 
     * @param username
     * @param user
     */
    protected void setUserHearder(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance()
                    .get(headerName.substring(0, 1)).get(0).target.substring(0,
                    1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
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
