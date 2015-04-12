package com.nexters.nochat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendsListActivity extends Activity {

    private static final String TAG = "FriendsListActivity";
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

    // 롤=1, 술=2, 밥=3, 커피=4 고정
    private static final String lol = "1";
    private static final String alcohol = "2";
    private static final String rice = "3";
    private static final String coffee = "4";

    private Handler mHandler;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    /*
    * layout_nameSpace =>   friendslist_row_item.xml의 Layout 부분 -->다이로그 선택시 3초후 다시 이름 보여주는 부분
    * layout_dialog =>    friendslist_row_item.xml의 Layout 부분 -->다이로그 선택시 3초동안 선택된 다이로그 보여주는 부분
    */
    LinearLayout layout_nameSpace;
    LinearLayout layout_dialog;

    //이미지 변경
    ImageView img0;

    //list에 부여된 친구 user_id값
    String getPositionId;
    String apiToken; //폰에 저장된(SharedPreferences) 토큰값
    /*----------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendslist);
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
                Toast.makeText(getApplicationContext(), "선택된아이템은  " + getPositionId, Toast.LENGTH_LONG).show();

                Context mContext = parent.getContext(); //FriendsListAdapter의 view 정보

                layout_nameSpace = (LinearLayout)view.findViewById(R.id.layout_nameSpace);
                layout_dialog = (LinearLayout)view.findViewById(R.id.layout_dialog);

                builder = new AlertDialog.Builder(mContext);
                LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout= li.inflate(R.layout.customdialog, null);

                builder.setView(layout);
                alertDialog = builder.create();
                alertDialog.show();
                //alertDialog.setContentView(layout);

                //클릭시 레이아웃 변경
                img0 = (ImageView)layout_dialog.findViewById(R.id.img0); //지금은 하나. =>ex) lol 클릭시 "lol보냄!" 이미지 보여주는 곳. 차후에 종류별로 만들어야한다

                //alertDialog
                ImageView bt1_lol = (ImageView)layout.findViewById(R.id.bt1_lol);
                ImageView bt2_alcohol = (ImageView)layout.findViewById(R.id.bt2_alcohol);
                ImageView bt3_rice = (ImageView)layout.findViewById(R.id.bt3_rice);
                ImageView bt4_coffee = (ImageView)layout.findViewById(R.id.bt4_coffee);
                ImageView cancel = (ImageView)layout.findViewById(R.id.bt_cancel);

                bt1_lol.setOnClickListener(bt_ItemClickListener);
                bt2_alcohol.setOnClickListener(bt_ItemClickListener);
                bt3_rice.setOnClickListener(bt_ItemClickListener);
                bt4_coffee.setOnClickListener(bt_ItemClickListener);
                cancel.setOnClickListener(bt_ItemClickListener);


            }
        });

        Button inviteBtn = (Button)findViewById(R.id.inviteBtn);
        inviteBtn.setOnClickListener(inviteBtnListener);//초대하기 눌렀을때

    }

    View.OnClickListener bt_ItemClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(OTAG, "In OnItem");
            String chatTypeId = null;
            switch (v.getId()) {
                case R.id.bt1_lol :
                    chatTypeId = lol;
                    dialogItemEvent(R.id.bt1_lol, chatTypeId);
                    break;

                case R.id.bt2_alcohol :
                    chatTypeId = alcohol;
                    dialogItemEvent(R.id.bt2_alcohol, chatTypeId);
                    break;

                case R.id.bt3_rice :
                    chatTypeId = rice;
                    dialogItemEvent(R.id.bt3_rice, chatTypeId);
                    break;

                case R.id.bt4_coffee :
                    chatTypeId = coffee;
                    dialogItemEvent(R.id.bt4_coffee, chatTypeId);
                    break;
            }
        }
    };

    /*  Item클릭시 다이로그 이벤트 처리 */
    public void dialogItemEvent(int bt_id, String chatTypeId){
        Log.i(TAG, "case R.id.");

        AsyncHttpClient(chatTypeId);                                                             //이거 위치가 중요한대.. 조금 더 생각해봐야함

        mHandler = new Handler();
        //이름 보이는 화면 ->안보이게
        img0.setImageResource(R.drawable.ic_launcher);
        layout_nameSpace.setVisibility(View.GONE);
        layout_dialog.setVisibility(View.VISIBLE);

        /*3초 동안 보여질 화면
         -> 이미지가 3초동안 밥으로 바뀜 */
        Runnable mMyTask = new Runnable() {
            @Override
            public void run() {

                Log.d("test","timer");
                //3초후에 실행되는 일들 ->이름이 보여야지
                layout_nameSpace.setVisibility(View.VISIBLE);
                layout_dialog.setVisibility(View.GONE);
            }
        };
        mHandler.postDelayed(mMyTask, 1000); // 3초후에 실행 mMyTask를 실행
        //3초 후에 이루어질 일을 등록
        alertDialog.dismiss();

    }

    /* AsyncHttpClient 사용해 서버와 통신*/
    private void AsyncHttpClient(String chatTypeId) {
        //GCM => http://leminity.tistory.com/27
        RequestParams paramData = new RequestParams();
        paramData.put("userId",getPositionId);
        paramData.put("chatTypeId",chatTypeId); // lol, alcohol, rice, coffee
        paramData.put("apiToken",apiToken);

        Log.i(CTAG, "#서버와 통신 준비중#" + "PARAMDATA:" + paramData);
        AsyncHttpClient mClient = new AsyncHttpClient();
        Header[] headers = {new BasicHeader("apiToken",apiToken)};
        mClient.post(this, URL, headers, paramData,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(CTAG, "json response Success");
                System.out.println("인증요청관련 response : " + response.toString());
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i(CTAG, "json response Failure");


            }
        });
    }

    /*  초대하기 관련 Listener */
    View.OnClickListener inviteBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG, "showContactlist");
            showAddressBooklist();
        }
    };

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

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

    private void showAddressBooklist() {
        Intent intent = new Intent(FriendsListActivity.this,
                AddressBookListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, 10001);
    }

}
