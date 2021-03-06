package com.nexters.nochatteam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final String CTAG = "AsyncHttpClient";
    private static final String URL = "http://todaytrend.cafe24.com:9000/users/signin";
    private static final String getFriendsURL = "http://todaytrend.cafe24.com:9000/users/friend";

    private Button backMain2; //메인화면으로 이동
    private TextView membershipTitle2; //타이틀 문구
    private EditText memberShipId2; //ID입력
    private EditText memberShipPassword2; //password입력
    private TextView certifyCheck2; //중복된 아이디가 있는지 검사
    private Button memberShipBtn2; //노챗시작하기

    private String loginId;
    private String password;
    //private String deviceToken;
    private String locale = "ko_KR"; //일단 한국어만
    private String os = "Android";

    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";

    private RequestParams paramData; //회원가입 요청 관련 param data
    private String regId; // preferences에서 뽑아와서 담을 변수

    private String apiToken; //본인 apiToken값(SharedPreferences에 저장된값)
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFont(); //폰트적용
        setContentView(R.layout.activity_login);

        backMain2 = (Button)findViewById(R.id.backMain2);
        membershipTitle2 = (TextView)findViewById(R.id.membershipTitle2);
        memberShipId2 = (EditText)findViewById(R.id.memberShipId2);
        memberShipPassword2 = (EditText)findViewById(R.id.memberShipPassword2);
        certifyCheck2 = (TextView)findViewById(R.id.certifyCheck2);
        memberShipBtn2 = (Button)findViewById(R.id.memberShipBtn2);

        memberShipBtn2.setTypeface(typeface); //버튼안 text에서도 font 적용
        membershipTitle2.setTypeface(typeface);
        memberShipId2.setTypeface(typeface);
        memberShipPassword2.setTypeface(typeface);
        certifyCheck2.setTypeface(typeface);

        //휴대폰 넓이 보다 텍스트가 길 경우 마키 처리
        certifyCheck2.setSingleLine(true);
        certifyCheck2.setEllipsize(android.text.TextUtils.TruncateAt.END);

        //폰에 저장된 regId 가져오기
        SharedPreferences preferencesRegId = PreferenceManager.getDefaultSharedPreferences(this);
        regId = preferencesRegId.getString("regId"," ");
        SharedPreferences preferencesApiToken = PreferenceManager.getDefaultSharedPreferences(this); //폰에 저장된 토큰값 가져오기
        apiToken = preferencesApiToken.getString("apiToken", " ");
        SharedPreferences preferencesPhoneNumberValue = PreferenceManager.getDefaultSharedPreferences(this); //폰에 저장된 본인 폰번호 가져오기
        phoneNumberValue = preferencesPhoneNumberValue.getString("phoneNumberValue", " ");

        dataManager = new DataManager(this);
        dataManager2 = new DataManager2(this);
        dataManager3 = new DataManager3(this);

        backMain2.setOnClickListener(backMainListener);
        memberShipBtn2.setOnClickListener(memberShipBtnListener);
    }
    private void setFont() {
        if(typeface==null) {
            typeface = Typeface.createFromAsset(getAssets(), TYPEFACE_NAME);
        }else{
            Log.e(TAG, "폰트가 없습니다.");
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

    /*메인화면으로 이동*/
    View.OnClickListener backMainListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };
    /* 노챗 가입하기*/
    View.OnClickListener memberShipBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"노챗 가입하기 버튼눌림");
            loginId = memberShipId2.getText().toString();
            password = memberShipPassword2.getText().toString();
            jsonDateSetting(loginId,password);
        }
    };

    /* 서버로 보낼 json data 세팅*/
    private void jsonDateSetting(String loginId, String password){
        try{
            Log.i(CTAG,"서버로 보낼 param data 세팅중");
            paramData = new RequestParams();
            paramData.put("loginId",loginId); //한글x,공백x,중복 아이디 체크 ->alert띄우기
            paramData.put("password",password);

        }catch (Exception e){
            e.printStackTrace();
        }
        AsyncHttpClient(paramData);
    }

    /* AsyncHttpClient 사용해 서버와 통신*/
    private void AsyncHttpClient(RequestParams paramData) {
        String param = paramData.toString();
        Log.i(CTAG, "#서버와 통신 준비중#" + "PARAMDATA:" + param);
        AsyncHttpClient mClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("apiToken","")};
        mClient.post(this, URL, headers, paramData,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //reqBuilder.setHeader(String name, String value);
                Log.i(CTAG, "json response Success");
                System.out.println("로그인관련 response : " + response.toString());
                String certifyMSG = null;
                try {
                    certifyMSG = response.getString("code");
                    if (certifyMSG.equals("12000")) {   //로그인 실패시
                        certifyCheck2.setVisibility(View.VISIBLE);
                        onStop();
                    }else if (certifyMSG.equals("12001")) {
                        certifyCheck2.setVisibility(View.VISIBLE);
                        onStop();
                    }else
                    { //로그인 성공했을시 친구리스트가져온다
                        getAddressBook(getApplicationContext());
                    }

                } catch (JSONException e) {
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

    /*FriendsListActivity으로 이동*/
    private void startLoginActivity(){
        Log.i(TAG,"CertifyActivity로 이동");
        certifyCheck2.setVisibility(View.GONE);
        Intent intent = new Intent(LoginActivity.this, FriendsListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
                        ArrayList<Map<String, String>> sfL = new ArrayList<Map<String, String>>();

                        //기존 디비내용 삭제
                        dataManager.deleteAll();
                        dataManager2.deleteAll2();
                        dataManager3.deleteAll3();

                        // for문을 돌면서 phoneNumber 값들을 가져온다
                        for (int i = 0; i < phoneJsonArray.length(); i++) {
                            phoneJsonObject = (JSONObject) phoneJsonArray.get(i);
                            serverFriend = (String) phoneJsonObject.get("phoneNumber");

                            userIdJsonObject = (JSONObject) phoneJsonArray.get(i);
                            //serverUserId = (String)userIdJsonObject.get("id");
                            serverUserId = String.valueOf(userIdJsonObject.get("id"));

                            System.out.println("ServerFriendsList : " + serverFriend + " UserId:" + serverUserId);
                            //서버에서 얻은 친구리스트에 해당하는 hashMap 객체 생성
                            serverMap = new HashMap<String, String>();
                            // Map에서 저장된 Key들을 가져올 Set을 만든다.
                            Set<String> set = hashPhoneMap.keySet();
                            // 서버에 저장되어 있는 key값에 해당하는 친구리스트를 주소록에서 찾는다.
                            if (set.contains(serverFriend)) {

                                //hashPhoneMap은 serverFriend(번호) 대한 value(내주소록 친구이름) 호출
                                String hashValue = hashPhoneMap.get(serverFriend);
                                Log.i(CTAG, "hashValue값은 :" + hashValue);

                            /*UserId를 쓰기위해 serverMap에 담는다
                                확인후 디비에 추가해야함 */
                                serverMap.put(serverFriend, serverUserId);
                                sfL.add(serverMap);                                                     //임시용.(콘솔확인용)

                                //DB 저장
                                dataManager.insertUsrFriends(serverFriend, hashValue);
                                dataManager2.insertUsrId(serverFriend, serverUserId);
                                dataManager3.insertUserInfo(serverUserId, hashValue);

                            } else {
                                Log.i("set.contains(serverFriend)", "if에 대한 set처리 실패");
                            }

                        }
                        System.out.println("서버에서 얻어온 친구리스트에 해당하는 이름 호출:" + sfL.toString()); //(형식) 01087389278=rangken

                }catch (JSONException e){
                    e.printStackTrace();
                }

                startLoginActivity();
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

