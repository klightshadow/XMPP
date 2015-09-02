package com.miiixer.xmpptest.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miiixer.xmpptest.R;
import com.miiixer.xmpptest.models.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lightshadow on 15/1/21.
 */
public class ChatAdapter extends BaseAdapter {

    private List<Chat> list = new ArrayList<Chat>();
    //private Activity activity;
    LayoutInflater layoutInflater;
    public ChatAdapter(Context context) {
        //this.activity = activity;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(String name, String content, int from) {
        addList(new Chat(name, content, from));
    }

    private void addList(Chat chat) {
        list.add(chat);
        notifyDataSetChanged();
    }

    public void clearAll() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (list.get(position).getFrom() == 0) ? 0 : 1;
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
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            int from = getItemViewType(position);//list.get(position).getFrom(); // 0 = self, 1 = other
            if (from == 0) {
                convertView = layoutInflater.inflate(R.layout.chatlist_right_item, parent, false);
                viewHolder.chatName = (TextView)convertView.findViewById(R.id.tv_rChatName);
                viewHolder.chatContent = (TextView)convertView.findViewById(R.id.tv_rChatContent);
            } else {
                convertView = layoutInflater.inflate(R.layout.chatlist_left_item, parent, false);
                viewHolder.chatName = (TextView)convertView.findViewById(R.id.tv_lChatName);
                viewHolder.chatContent = (TextView)convertView.findViewById(R.id.tv_lChatContent);
            }
            //Log.e("from", String.valueOf(from));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.chatName.setText(list.get(position).getName());
        viewHolder.chatContent.setText(list.get(position).getContent());
        return convertView;
    }

    static class ViewHolder {
        TextView chatName, chatContent;
    }
}
