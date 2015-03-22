package com.nexters.nochat;

import android.widget.BaseAdapter;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import java.util.ArrayList;

//리스트를 위한 어댑터
public class MyListAdapter extends BaseAdapter {
    public static class FriendData {
        public int fr_id;
        public String fr_name;
        public String fr_nick;
    }
    private Context ctx;
    private ArrayList<FriendData> userDataList;
    public ArrayList<FriendData> getDataList() {
        return userDataList;
    }
    public MyListAdapter(Context ctx, ArrayList<FriendData> _userDataList) {
        this.ctx = ctx;
        this.userDataList = _userDataList;
    }
    static class ViewHolder {
        RelativeLayout container;
        TextView userName;
        TextView userNickName;
    }
    @Override
    public int getCount() {
        return userDataList.size();
    }
    @Override
    public Object getItem(int arg0) {
        return userDataList.get(arg0);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(ctx).inflate(R.layout.list_row_item, null);
        final ViewHolder holder = new ViewHolder();
        holder.container = (RelativeLayout)convertView.findViewById(R.id.container);
        holder.userName = (TextView)convertView.findViewById(R.id.user_name);
        holder.userName.setText(userDataList.get(position).fr_name);
        holder.userNickName = (TextView)convertView.findViewById(R.id.user_nick);
        holder.userNickName.setText(userDataList.get(position).fr_nick);
        //    convertView.setTag(holder);
        return convertView;
    }
}
