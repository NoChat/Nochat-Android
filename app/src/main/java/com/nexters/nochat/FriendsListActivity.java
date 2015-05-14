package com.nexters.nochat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private LinearLayout layout_nameSpace;
    private LinearLayout layout_dialog;
    private TextView myImageViewText; //문구
    private TextView dialogTextView; // "밥보냄! , 술보냄! 등등"

    private Typeface typeface = null; //font
    private static final String TYPEFACE_NAME = "NOCHAT-HANNA.ttf";


    //이미지 변경
    //ImageView img0;

    //list에 부여된 친구 user_id값
    String getPositionId;
    String apiToken; //폰에 저장된(SharedPreferences) 토큰값
    /*----------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFont(); //폰트적용
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
                myImageViewText = (TextView)findViewById(R.id.myImageViewText);
                dialogTextView = (TextView)findViewById(R.id.dialogTextView);

                myImageViewText.setTypeface(typeface);
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

        Button inviteBtn = (Button)findViewById(R.id.inviteBtn);
        inviteBtn.setOnClickListener(inviteBtnListener);//초대하기 눌렀을때

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
        //이름 보이는 화면 ->안보이게
        //img0.setImageResource(R.drawable.ic_launcher);

        //다이로그 눌렀을때 친구이름은 감추고, chatTypeId보여줌
        AsyncHttpClient(chatTypeId);                                                             //이거 위치가 중요한대.. 조금 더 생각해봐야함

    }

    /* AsyncHttpClient 사용해 서버와 통신*/
    private void AsyncHttpClient(String chatTypeId) {
        //GCM => http://leminity.tistory.com/27
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
        Log.d("test","모든 다이로그 작업이 끈남 4");
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
