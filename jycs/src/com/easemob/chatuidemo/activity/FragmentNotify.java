package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.NotifyAdapter;

public class FragmentNotify extends Fragment implements OnHeaderRefreshListener {
    private AbPullToRefreshView news_refresh;// 下拉刷新
    private ListView lv_news_slide;// 通知列表
    private NotifyAdapter adapter;
    private List<EMConversation> list_Conversations = new ArrayList<EMConversation>();
    private ArrayList<EMMessage> list_msg = new ArrayList<EMMessage>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_notify, null);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        initViews(getView());
        list_msg.clear();
        list_Conversations.clear();
        list_Conversations.addAll(loadConversationsWithRecentChat());
        for (EMConversation conversation : list_Conversations) {
            Log.i("FragmentNitify", "size:"+conversation.getAllMessages().size());
            list_msg.addAll(conversation.getAllMessages());
        }
        Log.i("FragmentNitify", list_Conversations.size() + "");
        adapter = new NotifyAdapter(list_msg, getActivity());
        lv_news_slide.setAdapter(adapter);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
     
    }

    /**
     * 获取所有会话
     * 
     * @param context
     * @return +
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager
                .getInstance().getAllConversations();
        List<EMConversation> list = new ArrayList<EMConversation>();
        // 过滤掉messages seize为0的conversation
        for (EMConversation conversation : conversations.values()) {
            // boolean isGroup = conversation.getUserName().startsWith("g");
            if (conversation.isGroup()) {
                if (conversation.getAllMessages().size() != 0) {

                    EMGroup group = EMGroupManager.getInstance().getGroup(
                            conversation.getUserName());
                    String name = group.getGroupName();
                    Log.i("FragmentNitify", "name:" + group.getGroupName()
                            + "id;" + group.getGroupId());
                    if (name.startsWith("g")) {
                        list.add(conversation);
                    }
                }
            }
        }
        // 排序
        sortConversationByLastChatTime(list);
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     * 
     * @param usernames
     */
    private void sortConversationByLastChatTime(
            List<EMConversation> conversationList) {
        Collections.sort(conversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(final EMConversation con1,
                    final EMConversation con2) {

                EMMessage con2LastMessage = con2.getLastMessage();
                EMMessage con1LastMessage = con1.getLastMessage();
                if (con2LastMessage.getMsgTime() == con1LastMessage
                        .getMsgTime()) {
                    return 0;
                } else if (con2LastMessage.getMsgTime() > con1LastMessage
                        .getMsgTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    /**
     * 初始化控件
     */
    private void initViews(View v) {
        news_refresh = (AbPullToRefreshView) v.findViewById(R.id.news_refresh);
        lv_news_slide = (ListView) v.findViewById(R.id.lv_news_slide);
        news_refresh.setOnHeaderRefreshListener(this);
        news_refresh.setLoadMoreEnable(false);
        // 设置进度条的样式
        news_refresh.getHeaderView().setHeaderProgressBarDrawable(
                this.getResources().getDrawable(R.drawable.progress_circular));
    }

    @Override
    public void onHeaderRefresh(AbPullToRefreshView view) {
        list_msg.clear();
        list_Conversations.clear();
        list_Conversations.addAll(loadConversationsWithRecentChat());
        for (EMConversation conversation : list_Conversations) {
            Log.i("FragmentNitify", "size:"+conversation.getAllMessages().size());
            list_msg.addAll(conversation.getAllMessages());
        }
        adapter.notifyDataSetChanged();
        news_refresh.onHeaderRefreshFinish();
    }

}
