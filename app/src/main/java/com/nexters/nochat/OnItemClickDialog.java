package com.nexters.nochat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.AdapterView;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.os.Handler;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.util.ArrayList;

//Item 클릭시 Dialog 띄우기
public class OnItemClickDialog implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "OnItemClickDialog";
    private static final String CTAG = "AsyncHttpClient";
    private static final String getURL = "http://todaytrend.cafe24.com:9000/chats/type/all";
    private static final String URL = "http://todaytrend.cafe24.com:9000/chats/new";

    // 롤=1, 술=2, 밥=3, 커피=4 고정
    private static final short lol = 1;
    private static final short alcohol = 2;
    private static final short rice = 3;
    private static final short coffee = 4;

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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //FriendsListAdapter adapter = (FriendsListAdapter)parent.getAdapter();
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

        bt1_lol.setOnClickListener(this);
        bt2_alcohol.setOnClickListener(this);
        bt3_rice.setOnClickListener(this);
        bt4_coffee.setOnClickListener(this);
        cancel.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "In OnItem");

        switch (v.getId()) {
            case R.id.bt1_lol :
                dialogItemEvent(R.id.bt1_lol);
                break;

            case R.id.bt2_alcohol :
                dialogItemEvent(R.id.bt2_alcohol);
                break;

            case R.id.bt3_rice :
                dialogItemEvent(R.id.bt3_rice);
                break;

            case R.id.bt4_coffee :
                dialogItemEvent(R.id.bt4_coffee);
                break;
        }

    }

    /*  Item클릭시 다이로그 이벤트 처리 */
    public void dialogItemEvent(int id){
        Log.i(TAG, "case R.id.");

        //getAsyncHttpClient();

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
        mHandler.postDelayed(mMyTask, 3000); // 3초후에 실행 mMyTask를 실행
        //3초 후에 이루어질 일을 등록
        alertDialog.dismiss();
    }

    /* AsyncHttpClient 사용해 서버와 통신*/
    /*private void getAsyncHttpClient() {
        Log.i(CTAG,"get방식 => AsyncHttpClient 통신 준비중");
        //lol ,alcohol , rice , coffee
        AsyncHttpClient mClient = new AsyncHttpClient();

        //Log.i(CTAG, "json response Success");
        //System.out.println("인증요청관련 response : " + response.toString());

        mClient.get(getURL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                Log.i(CTAG, "json response Success");
                System.out.println("인증요청관련 response : " + response.toString());
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i(CTAG, "json response Failure");
            }
        });

    }*/

}
