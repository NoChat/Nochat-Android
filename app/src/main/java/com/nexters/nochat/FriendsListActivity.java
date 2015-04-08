package com.nexters.nochat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class FriendsListActivity extends ActionBarActivity {

    private static final String TAG = "FriendsListActivity";
    public static final String SELECTED_PHONE = "selectedphone";
    public static final int SUCCESS = 1;
    public static final int FAIL = -1;

    FriendsListAdapter friendsListAdapter;
    DataManager dm;
    ArrayList<UsrFriendsVO> usr_NameList = null;
    ArrayList<String> usr_FNL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendslist);
        dm = new DataManager(this);
        usr_NameList = new ArrayList<UsrFriendsVO>(); //유저이름 가져오도록 생성
        usr_NameList = dm.getUsrNameInfo();

        usr_FNL = new ArrayList<String>();

        if (usr_NameList.size() != 0) {
            for (int i = 0; i < usr_NameList.size(); i++) {
                System.out.println("친구리스트()에 해당하는 이름 호출:" + usr_NameList.get(i).getUsr_Name());
                //친구리스트를 adapter에 붙치기전에 List에 담는다
                usr_FNL.add(usr_NameList.get(i).getUsr_Name());
            }
        }else{
            Log.i(TAG,"에러: 친구리스트가 없습니다.");
        }
        friendsListAdapter = new FriendsListAdapter(this, usr_FNL);
        friendsListAdapter.notifyDataSetChanged();//화면 중간에 추가됬다면 변경사항을 즉각반영

        ListView listView = (ListView) findViewById(R.id.listView1);//activity_friendslist.xml - listView1
        listView.setAdapter(friendsListAdapter);
        listView.setOnItemClickListener(new OnItemClickDialog());//list item 눌렀을때

        Button inviteBtn = (Button)findViewById(R.id.inviteBtn);
        inviteBtn.setOnClickListener(inviteBtnListener);//초대하기 눌렀을때

    }
    View.OnClickListener inviteBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG, "showContactlist");
            showAddressBooklist();
        }
    };

    @Override
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
    }

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
