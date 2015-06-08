package com.easemob.chatuidemo.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chatuidemo.R;
import com.easemob.util.DateUtils;

public class NotifyAdapter extends BaseAdapter {
    private List<EMMessage> list;
    private Context context;
    private boolean isCollect = false;
    public NotifyAdapter(List<EMMessage> list, Context context) {
        super();
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_notification, null);
            holder = new ViewHolder();
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_info_notifi = (TextView) convertView
                    .findViewById(R.id.tv_info_notifi);
            holder.img_collect = (ImageView) convertView
                    .findViewById(R.id.img_collect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        EMConversation = list.get(position);
        EMMessage msg = list.get(position);
        String time = DateUtils.getTimestampString(new Date(msg.getMsgTime()));
        holder.tv_time.setText(time);
        TextMessageBody body = (TextMessageBody) msg.getBody();
        String text = body.getMessage();
        holder.tv_info_notifi.setText(text);
        if (isCollect){
            holder.img_collect.setImageResource(R.drawable.src_collect_press);
        } else {
            holder.img_collect.setImageResource(R.drawable.src_collect_normal);
        }
        holder.img_collect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
               isCollect = true;
               holder.img_collect.setImageResource(R.drawable.src_collect_normal);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_time;// 时间
        TextView tv_info_notifi;// 消息
        ImageView img_collect;// 收藏
    }
}
