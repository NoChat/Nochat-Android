package com.nexters.nochat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class AddressBookSearchAdapter<T extends AddressBook> extends ArrayAdapter<T> {

    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";

    private List<T> contactsList;
    private Context context;

    public AddressBookSearchAdapter(Context context, int textViewResourceId, List<T> items) { //constructor
        super(context, textViewResourceId, items);
        this.context = context;
        contactsList = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        typeface = Typeface.createFromAsset(context.getAssets(), TYPEFACE_NAME);

        View view = convertView;
        if (view == null) {//레이아웃 리소스로부터 뷰를 인플레이트 시킨다,
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.layout_phonelist, null);
        }
        T contacts = contactsList.get((int)getItemId(position));
        if (contacts != null) {
            //주소록 이름
            TextView viewName = (TextView) view.findViewById(R.id.tv_name);
            viewName.setTypeface(typeface);

            if (viewName != null) {
                viewName.setText(contacts.getName());
            }
            //주소록 번호
            TextView viewPoneNumber = (TextView) view.findViewById(R.id.tv_phonenumber);
            viewPoneNumber.setTypeface(typeface);

            if (viewPoneNumber != null) {
                viewPoneNumber.setText(contacts.getPhonenum());
            }
        }
        return view;
    }
}
