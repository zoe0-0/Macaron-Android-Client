package me.duohui.android.macaron;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import me.duohui.android.macaron.Api.MacaronApi;
import me.duohui.android.macaron.Model.Shop;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LinupDeletePopup extends AppCompatActivity implements Button.OnClickListener{

    private  Button btn_cancel,btn_delete;
    private  int lineupNumber;
    private  int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_linup_delete_popup);

        //Intent
        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);
        lineupNumber = intent.getIntExtra("lineupNumber",0);
        initView();

    }

    private void initView(){
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_cancel.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }


    private void deleteLineup() {
       Call<Boolean> call = MacaronApi.service.deleteLineup(lineupNumber);
       call.enqueue(new Callback<Boolean>() {
           @Override
           public void onResponse(Call<Boolean> call, Response<Boolean> response) {
               if(response.body()){
                   Toast.makeText(getApplicationContext(),"라인업 삭제 성공",Toast.LENGTH_SHORT).show();
                   Intent resultIntent = new Intent();
                   resultIntent.putExtra("position",position);
                   setResult(RESULT_OK,resultIntent);
                   finish();
               }else{
                   Toast.makeText(getApplicationContext(),"라인업 삭제 실패",Toast.LENGTH_SHORT).show();
                   finish();
               }
           }

           @Override
           public void onFailure(Call<Boolean> call, Throwable t) {
               Toast.makeText(getApplicationContext(),"라인업 삭제 실패",Toast.LENGTH_SHORT).show();
               finish();
           }
       });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //취소버튼
            case R.id.btn_cancel :
                finish();
                break ;
            //확인(삭제)버튼
            case R.id.btn_delete:
                deleteLineup();
                break ;
        }
    }
}
