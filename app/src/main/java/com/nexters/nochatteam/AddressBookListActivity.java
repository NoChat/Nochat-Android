package com.nexters.nochatteam;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddressBookListActivity extends ListActivity
{

    private static final String TAG = "주소록";
    private static final String STAG = "초성검색";
    private String searchKeyword; //주소록 검색어가 저장되는 변수
    private ArrayList<AddressBook> contactlist; //onListItem 에서 사용하기 위해 선언

    private ListView lv_contactlist;
    private EditText inviteSearchText;
    private Button addressBookBackBtn;
    private TextView inviteViewText;

    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressbooklist);
        setFont(); //폰트적용
        inviteSearchText = (EditText) findViewById(R.id.inviteSearchText);
        lv_contactlist = (ListView) findViewById(android.R.id.list);
        addressBookBackBtn = (Button) findViewById(R.id.addressBookBackBtn);
        inviteViewText = (TextView) findViewById(R.id.inviteViewText);
        inviteViewText.setTypeface(typeface);
        lv_contactlist.setSelector(R.drawable.list_selector);

        try{
            inviteSearchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.i(STAG,"in onTextChanged");
                    try {
                        searchKeyword = s.toString();
                        Log.i("초성검색:searchKeyword =",searchKeyword);
                        displayList();  // 검색된 리스트를 보여 준다
                    } catch (Exception e) {
                        Log.e("", e.getMessage(), e);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            displayList();  //맨 처음 시작시 전체 리스트 보여주기 위한 용도
        }catch (Exception e){
            e.printStackTrace();
        }


        //밑코드는 바로 메세지창으로 이동
        lv_contactlist
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> contactSMSList, View v,
                                            int position, long resid) { //contactlist -> contactSMSList(바꿈)
                        Log.i(TAG, "onItemClick");

                        AddressBook phonenumber = (AddressBook) contactSMSList
                                .getItemAtPosition(position);

                        //폰번호가 null일때
                        if (phonenumber == null) {
                            return;
                        }
                        //Toast.makeText(getApplicationContext(), "전화번호:" + phonenumber.getPhonenum(), Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            //데이터값중에 "-" 제거.
                            sendIntent.putExtra(FriendsListActivity.SELECTED_PHONE, phonenumber.getPhonenum().replaceAll("-", ""));
                            String smsLink = "http://bit.ly/1EtTJNr";
                            String smsBody = "친한 친구끼리만 쓴다는 메신저<노챗>"+" "+smsLink;
                            sendIntent.putExtra("address", phonenumber.getPhonenum()); // 받는사람 번호
                            sendIntent.putExtra("sms_body", smsBody); // 보낼 문자
                            sendIntent.setType("vnd.android-dir/mms-sms");
                            startActivity(sendIntent);
                            finish();
                        }else {
                            //안드로이드버전 4.4이상일때 예외처리해야함.
                            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                            sendIntent.setData(Uri.parse("smsto:" + Uri.encode(phonenumber.getPhonenum())));
                            String smsLink = "http://bit.ly/1EtTJNr";
                            String smsBody = "친한 친구끼리만 쓴다는 메신저<노챗>"+" "+smsLink;
                            sendIntent.putExtra("address", phonenumber.getPhonenum()); // 받는사람 번호
                            sendIntent.putExtra("sms_body", smsBody); // 보낼 문자
                            startActivity(sendIntent);
                        }
                    }
                });
        addressBookBackBtn.setOnClickListener(addressBookBackBtnListener); //backBtn
    }

        /**
         * 연락처를 가져오는 메소드.(자신핸드폰 주소록)    ====>검색기능 넣은 후 반복적인 cursor작업으로 앱이 느려지는거같다(업데이트해결해보자)
         *
         * @return contactlist
         */
        private ArrayList<AddressBook> getContactList () {
            //핸드폰 정보가져오기.
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            String[] projection = new String[]{
                    //내기기 번호
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    //내기기 유저 name
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

            String[] selectionArgs = null;

            String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    + " COLLATE LOCALIZED ASC";

            Cursor contactCursor = managedQuery(uri, projection, null,
                    selectionArgs, sortOrder);

            //Contact List 생성
            contactlist = new ArrayList<AddressBook>();
            while (contactCursor.moveToNext()) {//순차적으로 하나의 row 씩 읽어 나간다.
                Log.i(TAG, "contactCursor");

                String phonenumber = contactCursor.getString(0).replaceAll("-","");
                if (phonenumber.length() == 10) {
                    phonenumber = phonenumber.substring(0, 3) + "-"
                            + phonenumber.substring(3, 6) + "-"
                            + phonenumber.substring(6);
                } else if (phonenumber.length() > 8) {
                    phonenumber = phonenumber.substring(0, 3) + "-"
                            + phonenumber.substring(3, 7) + "-"
                            + phonenumber.substring(7);
                }
                String name = contactCursor.getString(1);

                try {
                    addContact(contactlist, name, phonenumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            return contactlist;

        }

    /*  초성검색을 하기위해 세팅    */
    private void addContact(List<AddressBook> contactlist, String name,
                            String phonenumber) throws Exception {
        Log.i(STAG,"In addContact");
        if (contactlist == null) {
            throw new NullPointerException("contactList가 null 입니다.");
        }
        boolean isAdd = false;
        if (searchKeyword != null && "".equals(searchKeyword.trim()) == false) {
            String iniName = HangulUtils.getHangulInitialSound(name,
                    searchKeyword);
            if (iniName.indexOf(searchKeyword) >= 0) {
                isAdd = true;
            }
        } else {
            isAdd = true;
        }
        if (isAdd) {
            contactlist.add(new AddressBook(name, phonenumber));
        }
    }


    /* 주소록 검색했을때 보여질 adapter의 view */
    private void displayList() throws Exception {
        Log.i(STAG,"in displayList");

        contactlist = getContactList();
        AddressBookSearchAdapter<AddressBook> searchAdapter = new AddressBookSearchAdapter<AddressBook>(
                this, R.layout.layout_phonelist, contactlist);
        setListAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }

    private void setFont() {
        if(typeface==null) {
            typeface = Typeface.createFromAsset(getAssets(), TYPEFACE_NAME);
        }else{
            Log.e(TAG,"폰트가 없습니다.");
        }
    }
    @Override
    public void setContentView(int viewId) {
        View view = LayoutInflater.from(this).inflate(viewId, null);
        ViewGroup group = (ViewGroup)view;
        int childCnt = group.getChildCount();
        for(int i=0; i<childCnt; i++){
            View v = group.getChildAt(i);
            if(v instanceof TextView){
                ((TextView)v).setTypeface(typeface);
            }else if(v instanceof Button){
                ((Button)v).setTypeface(typeface);
            }
        }
        super.setContentView(view);
    }


    /*  뒤로가기 Listener */
    View.OnClickListener addressBookBackBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG, "in addressBookBackBtnListener");
            Intent intent = new Intent(AddressBookListActivity.this, SettingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };
 }
