package com.nexters.nochatteam;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

//리스트를 위한 어댑터
public class FriendsListAdapter extends BaseAdapter {

    private static final String TAG = "FriendsListAdapter";
    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";

    private Context mContext;
    LayoutInflater mImflater;

    private ArrayList<String> mUsr_FNL;     //폰번호에 해당하는 =>유저이름
    private ArrayList<String> mUsr_FNL2;    //폰번호에 해당하는 =>유저id

    /*  List화면에 (유저이름+유저id) 같이 보여줄때   */
    public FriendsListAdapter(Context context, ArrayList<String> usr_FNL, ArrayList<String> usr_FNL2) {
        Log.i(TAG, "FriendsListAdapter2 생성");
        this.mContext = context;
        this.mUsr_FNL = usr_FNL;
        this.mUsr_FNL2 = usr_FNL2;
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
    public Object getItem2(int arg0) {
        return mUsr_FNL2.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        typeface = Typeface.createFromAsset(mContext.getAssets(), TYPEFACE_NAME);
        PersonViewHolder holder = new PersonViewHolder();
        View view = convertView;
        if(view == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.friendslist_row_item, null);
        }

            holder.container = (LinearLayout) view.findViewById(R.id.container);
            holder.user_name = (TextView) view.findViewById(R.id.user_name);
            holder.dialogTextView = (TextView) view.findViewById(R.id.dialogTextView);

            holder.user_name.setTypeface(typeface);
            holder.user_name.setText(mUsr_FNL.get(position));
            holder.dialogTextView.setTypeface(typeface);
            view.setTag(holder);

        return view;
    }

    /*  ViewHolder를 사용함으로 실행에 드는 비용 줄임    */
    public class PersonViewHolder
    {
        public LinearLayout container;
        public TextView user_name;
        public TextView dialogTextView;
    }

}
