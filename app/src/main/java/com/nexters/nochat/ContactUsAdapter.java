package com.nexters.nochat;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactUsAdapter extends BaseAdapter {
    private static final String TAG = "ContactUsAdapter";
    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";
    private Context mContext;
    LayoutInflater mImflater;

    private ArrayList<String> contactUsList;     //설정창 목록

    public ContactUsAdapter(Context context, ArrayList<String> contactUsList) {
        Log.i(TAG, "FriendsListAdapter2 생성");
        this.mContext = context;
        this.contactUsList = contactUsList;
        this.mImflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contactUsList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactUsList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        typeface = Typeface.createFromAsset(mContext.getAssets(), TYPEFACE_NAME);

        PersonViewHolder holder = new PersonViewHolder();

        // 캐시된 뷰가 없을 경우 새로 생성하고 뷰홀더를 생성한다
        if(convertView == null) {
            Log.i(TAG, "캐시된 뷰가 없음 -> 뷰홀더 생성");
            convertView = LayoutInflater.from(mContext).inflate(R.layout.contactuslist_row_item, null);
            holder.cu_layout = (LinearLayout) convertView.findViewById(R.id.cu_layout);
            holder.cu_name = (TextView) convertView.findViewById(R.id.cu_name);
            holder.cu_name.setTypeface(typeface);
            holder.cu_name.setText(contactUsList.get(position));
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
        public LinearLayout cu_layout;
        public TextView cu_name;
    }

}
