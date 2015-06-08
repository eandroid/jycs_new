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
package com.easemob.chatuidemo.adapter;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.image.AbImageLoader;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.easemob.chatuidemo.domain.InviteMessage;
import com.easemob.chatuidemo.domain.InviteMessage.InviteMesageStatus;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.http.Url;

public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage> {
    private String url; 
    private String username;
    private String imname;
    private Context context;
    private InviteMessgeDao messgeDao;
    private List<User2> list;
    private static final String TAG = "NewFriendsMsgAdapter";
  //图片下载器
    private AbImageLoader mAbImageLoader = null;
    public NewFriendsMsgAdapter(Context context, int textViewResourceId,
            List<InviteMessage> objects, List<User2> list) {
        super(context, textViewResourceId, objects);
        this.context = context;
        messgeDao = new InviteMessgeDao(context);
        this.list = list;
      //图片下载器
        mAbImageLoader = new AbImageLoader(context);
        mAbImageLoader.setDesiredWidth(0);
        mAbImageLoader.setDesiredHeight(0);
        mAbImageLoader.setLoadingImage(R.drawable.image_loading);
        mAbImageLoader.setErrorImage(R.drawable.image_error);
        mAbImageLoader.setEmptyImage(R.drawable.image_empty);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.row_invite_msg, null);
            holder.avator = (ImageView) convertView.findViewById(R.id.avatar);
            holder.reason = (TextView) convertView.findViewById(R.id.message);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.status = (Button) convertView.findViewById(R.id.user_state);
            holder.user_state2 = (Button) convertView.findViewById(R.id.user_state2);
            holder.groupContainer = (LinearLayout) convertView
                    .findViewById(R.id.ll_group);
            holder.groupname = (TextView) convertView
                    .findViewById(R.id.tv_groupName);
            // holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String str1 = context.getResources().getString(
                R.string.Has_agreed_to_your_friend_request);
        String str2 = context.getResources().getString(R.string.agree);

        String str3 = context.getResources().getString(
                R.string.Request_to_add_you_as_a_friend);
        String str4 = context.getResources().getString(
                R.string.Apply_to_the_group_of);
        String str5 = context.getResources().getString(R.string.Has_agreed_to);
        String str6 = context.getResources().getString(R.string.Has_refused_to);
        final InviteMessage msg = getItem(position);
        imname = msg.getFrom();
        User2 user = list.get(position);
        if (user != null){
            username = user.getName();
            holder.name.setText(username);
        }
        if (msg != null) {
            if (msg.getGroupId() != null) { // 显示群聊提示
                holder.groupContainer.setVisibility(View.VISIBLE);
                holder.groupname.setText(msg.getGroupName());
            } else {
                holder.groupContainer.setVisibility(View.GONE);
            }
            holder.reason.setText(msg.getReason());
            // holder.name.setText(msg.getFrom());
            // holder.time.setText(DateUtils.getTimestampString(new
            // Date(msg.getTime())));
            if (msg.getStatus() == InviteMesageStatus.BEAGREED) {
                holder.status.setVisibility(View.INVISIBLE);
                holder.reason.setText(str1);
            } else if (msg.getStatus() == InviteMesageStatus.BEINVITEED
                    || msg.getStatus() == InviteMesageStatus.BEAPPLYED) {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setEnabled(true);
                holder.status
                        .setBackgroundResource(android.R.drawable.btn_default);
                holder.status.setText(str2);
                if (msg.getStatus() == InviteMesageStatus.BEINVITEED) {
                    if (msg.getReason() == null) {
                        // 如果没写理由
                        holder.reason.setText(str3);
                    }
                } else { // 入群申请
                    if (TextUtils.isEmpty(msg.getReason())) {
                        holder.reason.setText(str4 + msg.getGroupName());
                    }
                }
                // 设置点击事件
                holder.status.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 同意别人发的好友请求
                        acceptInvitation(holder.status, msg);
                        holder.user_state2.setVisibility(View.INVISIBLE);
                    }
                });
                holder.user_state2.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View arg0) {
                        //拒绝别人发的好友请求
                        refuseInvitation(holder.user_state2,msg);
                        holder.status.setVisibility(View.INVISIBLE);
                    }
                });
            } else if (msg.getStatus() == InviteMesageStatus.AGREED) {
                holder.status.setText(str5);
                holder.status.setBackgroundDrawable(null);
                holder.status.setEnabled(false);
            } else if (msg.getStatus() == InviteMesageStatus.REFUSED) {
                holder.status.setText(str6);
                holder.status.setBackgroundDrawable(null);
                holder.status.setEnabled(false);
            }
            // 设置用户头像
            UploadedFile avater = user.getAvatar();
            if (avater != null) {
               url = Url.url_base_img_news+ user.getAvatar().getThumbnailUrl();
                //图片的下载
                mAbImageLoader.display(holder.avator,url);
                Log.i(TAG,"URL"+url);
            }
        }

        return convertView;
    }
    /**
     * 拒绝好友请求或者群申请
     * @param user_state2
     * @param msg
     */
    protected void refuseInvitation(final Button user_state2, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_refuse_with);
        final String str2 = context.getResources().getString(
                R.string.Has_refused_to);
        final String str3 = context.getResources().getString(
                R.string.Refuse_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                    if (msg.getGroupId() == null) //拒绝好友请求
                        EMChatManager.getInstance().refuseInvitation(
                                msg.getFrom());
                    else
//                         同意加群申请
                        EMGroupManager.getInstance().declineApplication(
                                msg.getFrom(), msg.getGroupId(),"不同意");
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            user_state2.setText(str2);
                            msg.setStatus(InviteMesageStatus.REFUSED);
                            // 更新db
                            ContentValues values = new ContentValues();
                            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                                    .getStatus().ordinal());
                            messgeDao.updateMessage(msg.getId(), values);
                            user_state2.setBackgroundDrawable(null);
                            user_state2.setEnabled(false);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), 1)
                                    .show();
                        }
                    });

                }
            }
        }).start();
    }

    /**
     * 同意好友请求或者群申请
     * 
     * @param button
     * @param username
     */
    private void acceptInvitation(final Button button, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_agree_with);
        final String str2 = context.getResources().getString(
                R.string.Has_agreed_to);
        final String str3 = context.getResources().getString(
                R.string.Agree_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                    if (msg.getGroupId() == null) // 同意好友请求
                        EMChatManager.getInstance().acceptInvitation(
                                msg.getFrom());
                    else
                        // 同意加群申请
                        EMGroupManager.getInstance().acceptApplication(
                                msg.getFrom(), msg.getGroupId());
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            button.setText(str2);
                            msg.setStatus(InviteMesageStatus.AGREED);
                            // 更新db
                            ContentValues values = new ContentValues();
                            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                                    .getStatus().ordinal());
                            messgeDao.updateMessage(msg.getId(), values);
                            button.setBackgroundDrawable(null);
                            button.setEnabled(false);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), 1)
                                    .show();
                        }
                    });

                }
            }
        }).start();
    }

    private static class ViewHolder {
        ImageView avator;
        TextView name;
        TextView reason;
        Button status;
        LinearLayout groupContainer;
        TextView groupname;
        Button user_state2;
        // TextView time;
    }

}
