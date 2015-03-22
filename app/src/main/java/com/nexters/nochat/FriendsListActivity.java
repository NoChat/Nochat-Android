package com.nexters.nochat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class FriendsListActivity extends ActionBarActivity {

    private static final String TAG = "FriendsListActivity";
    public static final String SELECTED_PHONE = "selectedphone";
    public static final int SUCCESS = 1;
    public static final int FAIL = -1;
    ListView listView;
    ArrayList<MyListAdapter.FriendData> friends = null;
    //String[] names={"User 1","User 2","User 3","User 4","User 5","User 6"};

    private Button inviteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendslist);
        //리스트뷰 출력 - lactivity_main.xml - listView1
        listView = (ListView) findViewById(R.id.listView1);
        friends = new ArrayList<MyListAdapter.FriendData>();
        for (int i = 0; i < 10; i++) {
            String name = "User" + i;
            String nick = "NickName" + i;
            MyListAdapter.FriendData fr_data = new MyListAdapter.FriendData();
            fr_data.fr_id = i;
            fr_data.fr_name = name;
            fr_data.fr_nick = nick;
            friends.add(fr_data);
        }
        MyListAdapter adapter = new MyListAdapter(this, friends);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickDialog());
        inviteBtn = (Button)findViewById(R.id.inviteBtn);
        //초대하기 눌렀을때
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "showContactlist");
                showAddressBooklist();
            }
        });
    }

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
