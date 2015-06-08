package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.ChatAllHistoryAdapter;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.easemob.chatuidemo.domain.IM;
import com.easemob.chatuidemo.domain.LocalIM;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 * 
 */
public class ChatAllHistoryFragment extends Fragment {
    private static final String TAG = "ChatAllHistoryFragment";
    private InputMethodManager inputMethodManager;
    private ListView listView;
    private ChatAllHistoryAdapter adapter;
    private EditText query;
    private ImageButton clearSearch;
    public RelativeLayout errorItem;
    public TextView errorText;
    private boolean hidden;
    private List<EMConversation> conversationList = new ArrayList<EMConversation>();
    private List<String> list_conversation = new ArrayList<String>();
    private AbHttpUtil abHttpUtil;
    private Map<String,LocalIM> list_im;
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_history,
                container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressDialog = ProgressDialog.show(getActivity(), null,
                "正在加载，请稍等...", true, true);
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;
        abHttpUtil = AbHttpUtil.getInstance(getActivity());
        abHttpUtil.setTimeout(20000);
        inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

        conversationList.addAll(loadConversationsWithRecentChat());
        listView = (ListView) getView().findViewById(R.id.list);
        AbRequestParams params = new AbRequestParams();
        String request = "";
        for (int i = 0; i < list_conversation.size(); i++) {
            request += list_conversation.get(i) + ",";
        }
        Log.i(TAG, "请求的参数信息" + request);
        params.put("imUsernames", request);
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

                    }

                    @Override
                    public void onSuccess(int statusCode, String content) {
                        @SuppressWarnings("unchecked")
                        Result<User2> ret = (Result<User2>) JSONUtils.parseObject(content,
                                new TypeReference<Result<User2>>() {
                                });
                        list_im = new HashMap<String, LocalIM>();
                        if (ret != null){
                            List<User2> list = ret.getResults();
                            if (list !=null && list.size() >0){
                                LocalIM im = null;
                                for (User2 user2 : list){
                                   String name = user2.getName();
                                   im = new LocalIM();
                                   if (!TextUtils.isEmpty(name)){
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
                                   UploadedFile avator = user2.getAvatar();
                                   if (avator != null){
                                       String url = avator.getThumbnailUrl();
                                       im.setUrl(url);
                                   }
                                   IM im2 = user2.getIm();
                                   if (im2!=null){
                                       String imname = im2.getUsername();
                                       im.setImName(imname);
                                       list_im.put(imname,im);
                                   }
                                }
                            }
                            adapter = new ChatAllHistoryAdapter(getActivity(), 1,
                                    conversationList,list_im);
                            // 设置adapter
                            listView.setAdapter(adapter);
                        }
                    }
                });

        final String st2 = getResources().getString(
                R.string.Cant_chat_with_yourself);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                EMConversation conversation = adapter.getItem(position);
                String username = conversation.getUserName();
                if (username
                        .equals(DemoApplication.getInstance().getUserName()))
                    Toast.makeText(getActivity(), st2, 0).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(),
                            ChatActivity.class);
                    if (conversation.isGroup()) {
                        // it is group chat
                        intent.putExtra("type", "group");
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", username);
                    } else {
                        // it is single chat
                        intent.putExtra("type","single");
                        LocalIM im = list_im.get(username);
                        Log.i(TAG, im.getUsername() +":" + im.getUrl());
                        intent.putExtra("localim", im);
                        intent.putExtra("userId", username);
                    }
                    startActivity(intent);
                }
            }
        });
        // 注册上下文菜单
        registerForContextMenu(listView);

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                hideSoftKeyboard();
                return false;
            }

        });
        // 搜索框
        query = (EditText) getView().findViewById(R.id.query);
        String strSearch = getResources().getString(R.string.search);
        query.setHint(strSearch);
        // 搜索框中清除button
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
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
        getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
        // }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean handled = false;
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
            handled = true;
        } else if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = false;
            handled = true;
        }
        EMConversation tobeDeleteCons = adapter
                .getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
        // 删除此会话
        EMChatManager.getInstance().deleteConversation(
                tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(),
                deleteMessage);
        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
        inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
        adapter.remove(tobeDeleteCons);
        adapter.notifyDataSetChanged();

        // 更新消息未读数
        ((MainActivity) getActivity()).updateUnreadLabel();

        return handled ? true : super.onContextItemSelected(item);
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        conversationList.clear();
        conversationList.addAll(loadConversationsWithRecentChat());
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /**
     * 获取所有会话
     * 
     * @param context
     * @return +
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
        list_conversation.clear();
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager
                .getInstance().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    String name = null;
                    String username = conversation.getUserName();
                    Log.i(TAG, "当前会话的用户名" + username);
                    if (conversation.isGroup()) {
                        EMGroup group = EMGroupManager.getInstance().getGroup(
                                username);
                        if (group == null) {
                            Log.i(TAG, "当前为" + null);
                        } else {
                            name = group.getGroupName();
                        }
                        if (!name.startsWith("g")) {
                            sortList.add(new Pair<Long, EMConversation>(
                                    conversation.getLastMessage().getMsgTime(),
                                    conversation));
                        }
                    } else {
                        list_conversation.add(username);
                        Log.i(TAG, "当前单聊会话个数:" + list_conversation.size()
                                + "第一个会话用户名称" + list_conversation.get(0));
                        sortList.add(new Pair<Long, EMConversation>(
                                conversation.getLastMessage().getMsgTime(),
                                conversation));
                    }
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     * 
     * @param usernames
     */
    private void sortConversationByLastChatTime(
            List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList,
                new Comparator<Pair<Long, EMConversation>>() {
                    @Override
                    public int compare(final Pair<Long, EMConversation> con1,
                            final Pair<Long, EMConversation> con2) {

                        if (con1.first == con2.first) {
                            return 0;
                        } else if (con2.first > con1.first) {
                            return 1;
                        } else {
                            return -1;
                        }
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
        if (!hidden && !((MainActivity) getActivity()).isConflict) {
            refresh();
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
