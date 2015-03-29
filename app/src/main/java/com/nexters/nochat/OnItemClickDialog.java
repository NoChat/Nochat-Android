package com.nexters.nochat;

import android.widget.AdapterView;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.os.Handler;

//Item 클릭시 Dialog 띄우기
public class OnItemClickDialog implements AdapterView.OnItemClickListener, View.OnClickListener {

    private Handler mHandler;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    LinearLayout layout_front;
    LinearLayout layout_back;

    //이미지 변경
    ImageView img0;


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyListAdapter adapter = (MyListAdapter)parent.getAdapter();
        MyListAdapter.FriendData fr = adapter.getDataList().get(position);
        //Toast.makeText(parent.getContext(), "user " + fr.fr_name, Toast.LENGTH_SHORT).show();

        Context mContext = parent.getContext();

        layout_front = (LinearLayout)view.findViewById(R.id.layout_front);
        layout_back = (LinearLayout)view.findViewById(R.id.layout_back);

        builder = new AlertDialog.Builder(mContext);
        LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout= li.inflate(R.layout.customdialog, null);

        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();
        //alertDialog.setView(layout);
//        alertDialog.setContentView(layout);


        //클릭시 레이아웃 변경
        //View cglayout= li.inflate(R.layout.list_row_item, null);
        //Dialog->listitem result
        img0 = (ImageView)layout_back.findViewById(R.id.img0);

        //alertDialog
        ImageView img1 = (ImageView)layout.findViewById(R.id.img1);
        img1.setOnClickListener(this);
        ImageView img2 = (ImageView)layout.findViewById(R.id.img2);
        img2.setOnClickListener(this);
        ImageView img3 = (ImageView)layout.findViewById(R.id.img3);
        img3.setOnClickListener(this);
        ImageView cancel = (ImageView)layout.findViewById(R.id.bt_right);
        cancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img1 :

                mHandler = new Handler();
                //이름 보이는 화면 ->안보이게

                img0.setImageResource(R.drawable.ic_launcher);
                layout_front.setVisibility(View.GONE);
                layout_back.setVisibility(View.VISIBLE);


                //3초 동안 보여질 화면
                // -> 밥?(back의 이미지가 3초동안 밥으로 바뀌고
                Runnable mMyTask = new Runnable() {
                    @Override
                    public void run() {

                        Log.d("test","timer");
                        //3초후에 실행되는 일들 ->이름이 보여야지
                        layout_front.setVisibility(View.VISIBLE);
                        layout_back.setVisibility(View.GONE);
                    }
                };
                mHandler.postDelayed(mMyTask, 2000); // 3초후에 실행 mMyTask를 실행
                //3초 후에 이루어질 일을 등록
                alertDialog.dismiss();
                break;


            case R.id.img2 :


                alertDialog.dismiss();
                break;


            case R.id.img3 :


                alertDialog.dismiss();
                break;

            case R.id.bt_right :
                alertDialog.dismiss();
                break;
        }

    }
}
