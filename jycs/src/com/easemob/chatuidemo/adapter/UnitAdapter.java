package com.easemob.chatuidemo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easemob.chatuidemo.R;

public class UnitAdapter extends BaseAdapter {
    private Context context;
    private List<String> list_units;
    
    public UnitAdapter(Context context, List<String> list_units) {
        super();
        this.context = context;
        this.list_units = list_units;
    }

    @Override
    public int getCount() {
        return list_units.size();
    }

    @Override
    public Object getItem(int position) {
        return list_units.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_unit, null);
            holder = new ViewHolder();
            holder.tv_publish = (TextView) convertView.findViewById(R.id.tv_publish);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String unit = list_units.get(arg0);
        holder.tv_publish.setText(unit);
        return convertView;
    }

    class ViewHolder {
        TextView tv_publish;//unit名称
    }
}
