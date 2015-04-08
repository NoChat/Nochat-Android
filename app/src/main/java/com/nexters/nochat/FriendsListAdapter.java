package com.nexters.nochat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

//리스트를 위한 어댑터
public class FriendsListAdapter extends BaseAdapter {

    private static final String TAG = "FriendsListAdapter";
    private Context mContext;
    LayoutInflater mImflater;

    ArrayList<String> mUsr_FNL;

    public FriendsListAdapter(Context context, ArrayList<String> usr_FNL) {
        Log.i(TAG, "FriendsListAdapter 생성");
        this.mContext = context;
        this.mUsr_FNL = usr_FNL;
        this.mImflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mUsr_FNL.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mUsr_FNL.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        PersonViewHolder holder = new PersonViewHolder();

        // 캐시된 뷰가 없을 경우 새로 생성하고 뷰홀더를 생성한다
        if(convertView == null) {
            Log.i(TAG, "캐시된 뷰가 없음 -> 뷰홀더 생성");
            convertView = LayoutInflater.from(mContext).inflate(R.layout.friendslist_row_item, null);
            holder.container = (RelativeLayout) convertView.findViewById(R.id.container);
            holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.user_name.setText(mUsr_FNL.get(position));
            convertView.setTag(holder);
        }

        // 캐시된 뷰가 있을 경우 저장된 뷰홀더를 사용한다
        else{
            Log.i(TAG, "캐시된 뷰가 있음 -> 저장된 뷰홀더 사용");
            holder = (PersonViewHolder) convertView.getTag();
        }

        return convertView;
    }

    /*  ViewHolder를 사용함으로 실행에 드는 비용 줄임    */
    public class PersonViewHolder
    {
        public RelativeLayout container;
        public TextView user_name;
    }

}