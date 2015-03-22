package com.nexters.nochat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddressBookListActivity extends Activity {

    private ListView lv_contactlist;
    private ContactsAdapter adapter;
    private static final String TAG = "주소록";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressbooklist);
        lv_contactlist = (ListView) findViewById(R.id.lv_contactlist);
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new ContactsAdapter(AddressBookListActivity.this,
                R.layout.layout_phonelist, getContactList());


        lv_contactlist.setAdapter(adapter);
        lv_contactlist
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> contactlist, View v,
                                            int position, long resid) {
                        Log.i(TAG, "onItemClick");

                        if(contactlist.isClickable()==true){

                        }


                        AddressBook phonenumber = (AddressBook) contactlist
                                .getItemAtPosition(position);

                        //폰번호가 null일때
                        if (phonenumber == null) {
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "전화번호:" + phonenumber.getPhonenum(), Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            //데이터값중에 "-" 제거.
                            sendIntent.putExtra(FriendsListActivity.SELECTED_PHONE, phonenumber.getPhonenum().replaceAll("-", ""));

                            String smsBody = "우지민우지지지지민";
                            sendIntent.putExtra("address", phonenumber.getPhonenum()); // 받는사람 번호
                            sendIntent.putExtra("sms_body", smsBody); // 보낼 문자
                            sendIntent.setType("vnd.android-dir/mms-sms");
                            startActivity(sendIntent);
                            finish();
                        }else {
                            //안드로이드버전 4.4이상일때 예외처리해야함.
                            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                            sendIntent.setData(Uri.parse("smsto:" + Uri.encode(phonenumber.getPhonenum())));
                            String smsBody = "우지민우지지지지민";
                            sendIntent.putExtra("address", phonenumber.getPhonenum()); // 받는사람 번호
                            sendIntent.putExtra("sms_body", smsBody); // 보낼 문자
                            startActivity(sendIntent);
                        }
                    }
                });

    }

    /**
     * 연락처를 가져오는 메소드.(자신핸드폰 주소록)
     *
     * @return
     */
    private ArrayList<AddressBook> getContactList() {
        //핸드폰 정보가져오기.
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[] {
                //핸드폰에 등록되어있는 기본사진
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                //내기기 번호
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                //내기기 유저 name
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

        String[] selectionArgs = null;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = managedQuery(uri, projection, null,
                selectionArgs, sortOrder);

        //Contact List 생성
        ArrayList<AddressBook> contactlist = new ArrayList<AddressBook>();

        if (contactCursor.moveToFirst()) {
            Log.i(TAG, "contactCursor");
            do {
                //주소록 가져왔을때 폰번호 보여주는 거 세팅.
                String phonenumber = contactCursor.getString(1).replaceAll("-",
                        "");
                if (phonenumber.length() == 10) {
                    phonenumber = phonenumber.substring(0, 3) + "-"
                            + phonenumber.substring(3, 6) + "-"
                            + phonenumber.substring(6);
                } else if (phonenumber.length() > 8) {
                    phonenumber = phonenumber.substring(0, 3) + "-"
                            + phonenumber.substring(3, 7) + "-"
                            + phonenumber.substring(7);
                }

                AddressBook acontact = new AddressBook();
                // acontact.setPhotoid(contactCursor.getLong(0));
                acontact.setPhonenum(phonenumber);

                //유저name(getString(2))
                acontact.setName(contactCursor.getString(2));

                contactlist.add(acontact);
            } while (contactCursor.moveToNext());
        }

        return contactlist;

    }

    private class ContactsAdapter extends ArrayAdapter<AddressBook> {

        private int resId;
        private ArrayList<AddressBook> contactlist;
        private LayoutInflater Inflater;
        private Context context;

        public ContactsAdapter(Context context, int textViewResourceId,
                               List<AddressBook> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            resId = textViewResourceId;
            contactlist = (ArrayList<AddressBook>) objects;
            Inflater = (LayoutInflater) ((Activity) context)
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * 주소록 ListView에  list 추가 설정.
         */
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder holder;
            if (v == null) {
                v = Inflater.inflate(resId, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView) v.findViewById(R.id.tv_name);
                holder.tv_phonenumber = (TextView) v
                        .findViewById(R.id.tv_phonenumber);
                //holder.iv_photoid = (ImageView) v.findViewById(R.id.iv_photo);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            AddressBook acontact = contactlist.get(position);

            if (acontact != null) {
                holder.tv_name.setText(acontact.getName());
                holder.tv_phonenumber.setText(acontact.getPhonenum());

				/*
				 * Bitmap bm = openPhoto(acontact.getPhotoid()); // 사진없으면 기본 사진
				 * 보여주기 if (bm != null) { holder.iv_photoid.setImageBitmap(bm);
				 * } else { holder.iv_photoid.setImageDrawable(getResources()
				 * .getDrawable(R.drawable.ic_launcher)); }
				 */

            }

            return v;
        }

        private class ViewHolder {
            //ImageView iv_photoid;
            TextView tv_name;
            TextView tv_phonenumber;
        }

    }

}

