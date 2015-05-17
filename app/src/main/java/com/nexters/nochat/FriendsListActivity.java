package com.nexters.nochat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FriendsListActivity extends Activity {

    private static final String TAG = "FriendsListActivity";
    private static final String getFriendsURL = "http://todaytrend.cafe24.com:9000/users/friend";
    public static final String SELECTED_PHONE = "selectedphone";
    public static final int SUCCESS = 1;
    public static final int FAIL = -1;

    FriendsListAdapter friendsListAdapter;
    DataManager dm;
    DataManager2 dm2;
    ArrayList<UsrFriendsVO> usr_NameList = null;
    ArrayList<UsrIdVO> usr_IdList = null;

    ArrayList<String> usr_FNL = null;
    ArrayList<String> usr_FNL2 = null;

    /*----------------------------------------------------------------------------------------------*/
    //OnItemClickDialog 관련 변수들.
    private static final String OTAG = "OnItemClickDialog";
    private static final String CTAG = "AsyncHttpClient";
    private static final String URL = "http://todaytrend.cafe24.com:9000/chats/new";

    // 롤=1, 술=2, 밥=3, 커피=4 담배=5 고정
    private static final String lol = "1";
    private static final String alcohol = "2";
    private static final String rice = "3";
    private static final String coffee = "4";
    private static final String smoking = "5";

    private Handler mHandler;

    Dialog dialog;

    /*
    * layout_nameSpace =>   friendslist_row_item.xml의 Layout 부분 -->다이로그 선택시 3초후 다시 이름 보여주는 부분
    * layout_dialog =>    friendslist_row_item.xml의 Layout 부분 -->다이로그 선택시 3초동안 선택된 다이로그 보여주는 부분
    */
    private RelativeLayout layout_nameSpace;
    private RelativeLayout layout_dialog;
    private TextView FImageViewText; //문구
    private TextView dialogTextView; // "밥보냄! , 술보냄! 등등"
    private Button refreshBtn;
    private Button settingGoBtn;

    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";


    //이미지 변경
    //ImageView img0;

    //list에 부여된 친구 user_id값
    private String getPositionId;
    private String apiToken; //폰에 저장된(SharedPreferences) 토큰값
    /*----------------------------------------------------------------------------------------------*/
        /*주소록관련 변수들*/
    private String phoneNumberValue; //본인 폰번호값(SharedPreferences에 저장된값)
    private RequestParams AddressBookparamData; // 주소록 정보 보내기 관련 param data
    private HashMap<String,String> hashPhoneMap; //주소록(name,phoneNumber)
    private HashMap<String,String> serverMap; // 서버에서 얻어온 번호에 해당하는 HashMap

    private DataManager dataManager;
    private DataManager2 dataManager2;
    private DataManager3 dataManager3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFont(); //폰트적용
        setContentView(R.layout.activity_friendslist);

        FImageViewText = (TextView)findViewById(R.id.FImageViewText);
        refreshBtn = (Button)findViewById(R.id.refreshBtn);
        FImageViewText.setTypeface(typeface);

        dm = new DataManager(this);
        dm2 = new DataManager2(this);

        usr_NameList = new ArrayList<UsrFriendsVO>(); //유저이름 가져오도록 생성
        usr_NameList = dm.getUsrNameInfo();

        usr_IdList = new ArrayList<UsrIdVO>();
        usr_IdList = dm2.getUsrIdInfo();

        usr_FNL = new ArrayList<String>(); //폰번호에 해당하는 =>유저이름
        usr_FNL2 = new ArrayList<String>();//폰번호에 해당하는 =>유저id

        SharedPreferences preferencesApiToken = PreferenceManager.getDefaultSharedPreferences(this); //폰에 저장된 토큰값 가져오기
        apiToken = preferencesApiToken.getString("apiToken"," ");
        SharedPreferences preferencesPhoneNumberValue = PreferenceManager.getDefaultSharedPreferences(this); //폰에 저장된 본인 폰번호 가져오기
        phoneNumberValue = preferencesPhoneNumberValue.getString("phoneNumberValue", " ");

        dataManager = new DataManager(this);
        dataManager2 = new DataManager2(this);
        dataManager3 = new DataManager3(this);

        if (usr_NameList.size() != 0) {
            for (int i = 0; i < usr_NameList.size(); i++) {
                System.out.println("친구리스트()에 해당하는 이름 호출:" + usr_NameList.get(i).getUsr_Name());
                //친구리스트를 adapter에 붙치기전에 List에 담는다
                usr_FNL.add(usr_NameList.get(i).getUsr_Name());
            }
        }else{
            Log.i(TAG,"에러: 친구리스트가 없습니다.");
        }

        if (usr_IdList.size() != 0) {
            for (int i = 0; i < usr_IdList.size(); i++) {
                System.out.println("친구리스트()에 해당하는 Id 호출:" + usr_IdList.get(i).getUsr_Id());
                //친구리스트를 adapter에 붙치기전에 List에 담는다
                usr_FNL2.add(usr_IdList.get(i).getUsr_Id());
            }
        }else{
            Log.i(TAG,"에러: 친구리스트가 없습니다.");
        }


        friendsListAdapter = new FriendsListAdapter(this, usr_FNL,usr_FNL2);
        friendsListAdapter.notifyDataSetChanged();//화면 중간에 추가됬다면 변경사항을 즉각반영

        ListView listView = (ListView) findViewById(R.id.listView1);//activity_friendslist.xml - listView1
        listView.setAdapter(friendsListAdapter);

        //listView.setOnItemClickListener(new OnItemClickDialog());//list item 눌렀을때(기존- Dialog부분 클래스화)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //item별 id를 가져온다.
                getPositionId = (String)friendsListAdapter.getItem2(position);
                //Toast.makeText(getApplicationContext(), "선택된아이템은  " + getPositionId, Toast.LENGTH_LONG).show();

                Context mContext = parent.getContext(); //FriendsListAdapter의 view 정보

                layout_nameSpace = (RelativeLayout)view.findViewById(R.id.layout_nameSpace);
                layout_dialog = (RelativeLayout)view.findViewById(R.id.layout_dialog);
                dialogTextView = (TextView)findViewById(R.id.dialogTextView);
                dialogTextView.setTypeface(typeface);

                dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout= li.inflate(R.layout.customdialog, null);//layout_root
                dialog.setContentView(layout);
                dialog.show();

                //클릭시 레이아웃 변경
                //img0 = (ImageView)layout_dialog.findViewById(R.id.img0); //지금은 하나. =>ex) lol 클릭시 "lol보냄!" 이미지 보여주는 곳. 차후에 종류별로 만들어야한다

                //Dialog layout
                ImageView bt1_lol = (ImageView)layout.findViewById(R.id.bt1_lol);
                ImageView bt2_alcohol = (ImageView)layout.findViewById(R.id.bt2_alcohol);
                ImageView bt3_rice = (ImageView)layout.findViewById(R.id.bt3_rice);
                ImageView bt4_coffee = (ImageView)layout.findViewById(R.id.bt4_coffee);
                ImageView bt5_smoking = (ImageView)layout.findViewById(R.id.bt5_smoking);
                ImageView bt_cancel = (ImageView)layout.findViewById(R.id.bt_cancel);

                bt1_lol.setOnClickListener(bt_ItemClickListener);
                bt2_alcohol.setOnClickListener(bt_ItemClickListener);
                bt3_rice.setOnClickListener(bt_ItemClickListener);
                bt4_coffee.setOnClickListener(bt_ItemClickListener);
                bt5_smoking.setOnClickListener(bt_ItemClickListener);
                bt_cancel.setOnClickListener(bt_ItemClickListener);


            }
        });

        settingGoBtn = (Button)findViewById(R.id.settingGoBtn);
        settingGoBtn.setOnClickListener(settingGoBtnListener);//초대하기 눌렀을때
        refreshBtn.setOnClickListener(refreshBtnListener);

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

    View.OnClickListener bt_ItemClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(OTAG, "In OnItem");
            String chatTypeId = null;
            switch (v.getId()) {
                case R.id.bt1_lol :
                    Log.i(OTAG, "In bt1_lol");
                    chatTypeId = lol;
                    dialogItemEvent(R.id.bt1_lol, chatTypeId);
                    break;

                case R.id.bt2_alcohol :
                    Log.i(OTAG, "In bt2_alcohol");
                    chatTypeId = alcohol;
                    dialogItemEvent(R.id.bt2_alcohol, chatTypeId);
                    break;

                case R.id.bt3_rice :
                    Log.i(OTAG, "In bt3_rice");
                    chatTypeId = rice;
                    dialogItemEvent(R.id.bt3_rice, chatTypeId);
                    break;

                case R.id.bt4_coffee :
                    Log.i(OTAG, "In bt4_coffee");
                    chatTypeId = coffee;
                    dialogItemEvent(R.id.bt4_coffee, chatTypeId);
                    break;

                case R.id.bt5_smoking :
                    Log.i(OTAG, "In bt5_smoking");
                    chatTypeId = smoking;
                    dialogItemEvent(R.id.bt5_smoking, chatTypeId);
                    break;

                case R.id.bt_cancel :
                    Log.i(OTAG, "In bt_cancel");
                    dialog.cancel();
                    break;

            }
        }
    };

    /*  Item클릭시 다이로그 이벤트 처리 */
    public void dialogItemEvent(int bt_id, String chatTypeId){
        Log.i(TAG, "case R.id."+bt_id);
        Log.d("test","다이로그 들어옴1");
        mHandler = new Handler();

        //다이로그 눌렀을때 친구이름은 감추고, chatTypeId보여줌
        AsyncHttpClient(chatTypeId);

    }

    /* AsyncHttpClient 사용해 서버와 통신*/
    private void AsyncHttpClient(String chatTypeId) {

        RequestParams paramData = new RequestParams();
        paramData.put("userId",getPositionId);//userId = 번호값으로 변경
        paramData.put("chatTypeId",chatTypeId); // lol, alcohol, rice, coffee , smoking
        paramData.put("apiToken",apiToken);

        Log.i(CTAG, "#서버와 통신 준비중#" + "PARAMDATA:" + paramData);
        AsyncHttpClient mClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("apiToken",apiToken)};
        mClient.post(this, URL, headers, paramData,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(CTAG, "json response Success");
                System.out.println("인증요청관련 response : " + response.toString());

                JSONObject resData = null;
                JSONObject chatType = null;
                String jsonTypeName = null;
                try {
                    resData = response.getJSONObject("data");
                    chatType = resData.getJSONObject("chatType");
                    jsonTypeName = chatType.getString("name");
                    Log.i(CTAG,"jsonTypeName값 :"+jsonTypeName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //다이로그 클릭시 일어나는 일
                dialogTextView.setText(jsonTypeName+" "+"보냄!");
                layout_nameSpace.setVisibility(View.GONE);
                layout_dialog.setVisibility(View.VISIBLE);
                Log.d("test","보냄!눌림, 보냄메세지보여야함 2");
                runnableTask();

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i(CTAG, "json response Failure");


            }
        });
    }

    /*  3초후 */
    protected void runnableTask(){
        //3초후에 실행되는 일 ->(원래대로)친구이름이 보여줌
        Runnable mMyTask = new Runnable() {
            @Override
            public void run() {

                Log.d("test","timer");
                layout_dialog.setVisibility(View.GONE);
                layout_nameSpace.setVisibility(View.VISIBLE);
                Log.d("test","원래대로 친구이름을 보여줘야함 3");
            }
        };
        mHandler.postDelayed(mMyTask, 1000); // 3초후에 실행 mMyTask를 실행
        dialog.dismiss();
    }

    /*  setting Listener */
    View.OnClickListener settingGoBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG, "in settingGoBtnListener");
            Intent intent = new Intent(FriendsListActivity.this, SettingActivity.class);
            startActivity(intent);
        }
    };
    /*  refreshBtn Listener */
    View.OnClickListener refreshBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG, "in refreshBtnListener");
            getAddressBook(getApplicationContext()); //FriendsListActivity가 실행시 주소록을 업로드한다.
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
        if (requestCode == 10001) {
            if (resultCode == SUCCESS) {
                Log.d(TAG,"Success");
                //((TextView)findViewById(R.id.tv_selected_phone)).setText(data.getStringExtra(SELECTED_PHONE));
            } else {
                Log.d(TAG,"Fail");
                //((TextView)findViewById(R.id.tv_selected_phone)).setText("");
            }
        }
    }

    /* 주소록정보 가져오기*/
    private void getAddressBook(Context context) {
        ArrayList<String> phoneList = new ArrayList<String>();
        hashPhoneMap = new HashMap<String,String>();
        String phone;
        String name;
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                Log.i(TAG, "contactCursor");
                do {
                    int phone_idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int name_idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    phone = cursor.getString(phone_idx).replaceAll("-", "");
                    name = cursor.getString(name_idx);
                    phoneList.add(phone);
                    hashPhoneMap.put(phone,name); //서버에 등록된 친구리스트와 내 주소록을 비교하기 위해
                } while (cursor.moveToNext());

            } else {
                Toast.makeText(this.getApplicationContext(), "주소록 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        PhoneNumberParser(phoneList);
    }

    /*주소록 데이터 파싱 */
    private void PhoneNumberParser(List<String> phoneList){
        String ppList = phoneList.toString();
        ArrayList<String> ppPhoneList = new ArrayList<String>();
        Log.i(TAG, "PhoneNumberParser");
        String ppp = "(01[0|1|6|7|8|9])(\\d{4}|\\d{3})(\\d{4})";
        String str;
        Pattern p = Pattern.compile(ppp);
        Matcher m = p.matcher(ppList);
        while(m.find()){
            str = m.group(0);
            ppPhoneList.add(str);
        }
        jsonAddressBookDateSetting(ppPhoneList);
    }


    /* 친구목록 관련 서버로 보낼 json data 세팅*/
    private void jsonAddressBookDateSetting(List<String> ppPhoneList){
        Log.i(TAG, "jsonAddressBookDateSetting");
        String ListToStringValues;
        ListToStringValues = ListToStringConvert(ppPhoneList); // ppPhoneList -> String
        System.out.println(">>>>>>>>>>>>>>>"+ListToStringValues);
        try{
            Log.i(CTAG,"서버로 보낼 주소록 param data 세팅중");
            AddressBookparamData = new RequestParams();
            AddressBookparamData.put("phoneNumbers",ListToStringValues); //한글x,공백x,중복 아이디 체크 ->alert띄우기
            AddressBookparamData.put("apiToken",apiToken);

        }catch (Exception e){
            e.printStackTrace();
        }
        AddressBookAsyncHttpClient(AddressBookparamData);
    }
    /* AsyncHttpClient 사용해 서버와 통신*/
    private void AddressBookAsyncHttpClient(RequestParams AddressBookparamData) {
        String param = AddressBookparamData.toString();
        Log.i(CTAG, "#서버와 통신 준비중#" + "PARAMDATA:" + param);
        AsyncHttpClient mClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("apiToken",apiToken)};
        mClient.post(this, getFriendsURL, headers, AddressBookparamData,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //reqBuilder.setHeader(String name, String value);
                Log.i(CTAG, "json response Success");
                System.out.println("인증요청관련 response : " + response.toString());
                try{
                    JSONArray phoneJsonArray = null;
                    JSONObject phoneJsonObject = null;
                    String serverFriend = null; //서버에 등록되어있는 친구번호

                    JSONObject userIdJsonObject = null;
                    String serverUserId = null; //서버에 등록되어있는 친구ID

                    phoneJsonArray = response.getJSONArray("data");
                    ArrayList<Map<String,String>> sfL = new ArrayList<Map<String,String>>();

                    //기존 디비내용 삭제
                    dataManager.deleteAll();
                    dataManager2.deleteAll2();
                    dataManager3.deleteAll3();

                    // for문을 돌면서 phoneNumber 값들을 가져온다
                    for(int i = 0; i<phoneJsonArray.length(); i ++) {
                        phoneJsonObject = (JSONObject) phoneJsonArray.get(i);
                        serverFriend = (String)phoneJsonObject.get("phoneNumber");

                        userIdJsonObject = (JSONObject) phoneJsonArray.get(i);
                        //serverUserId = (String)userIdJsonObject.get("id");
                        serverUserId = String.valueOf(userIdJsonObject.get("id"));

                        System.out.println("ServerFriendsList : "+serverFriend+" UserId:"+serverUserId);
                        //서버에서 얻은 친구리스트에 해당하는 hashMap 객체 생성
                        serverMap = new HashMap<String,String>();
                        // Map에서 저장된 Key들을 가져올 Set을 만든다.
                        Set<String> set = hashPhoneMap.keySet();
                        // 서버에 저장되어 있는 key값에 해당하는 친구리스트를 주소록에서 찾는다.
                        if(set.contains(serverFriend)){

                            //hashPhoneMap은 serverFriend(번호) 대한 value(내주소록 친구이름) 호출
                            String hashValue = hashPhoneMap.get(serverFriend);
                            Log.i(CTAG,"hashValue값은 :"+hashValue);

                            /*UserId를 쓰기위해 serverMap에 담는다
                                확인후 디비에 추가해야함 */
                            serverMap.put(serverFriend,serverUserId);
                            sfL.add(serverMap);                                                     //임시용.(콘솔확인용)

                            //DB 저장
                            dataManager.insertUsrFriends(serverFriend, hashValue);
                            dataManager2.insertUsrId(serverFriend, serverUserId);
                            dataManager3.insertUserInfo(serverUserId, hashValue);

                        }else{
                            Log.i("set.contains(serverFriend)","if에 대한 set처리 실패");
                        }

                    }
                    System.out.println("서버에서 얻어온 친구리스트에 해당하는 이름 호출:"+ sfL.toString()); //(형식) 01087389278=rangken

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i(CTAG, "json response Failure");


            }
        });
    }

    /*List -> String 변환*/
    private String ListToStringConvert(List<String> ppPhoneList){
        Log.i("ListToStringConvert", "List -> String 변환");
        StringBuilder sb = new StringBuilder();
        String[] sArrays = ppPhoneList.toArray(new String[ppPhoneList.size()]);
        for(String s : sArrays){
            sb.append(s);
            sb.append(",");
        }
        return String.valueOf(sb);
    }

}
