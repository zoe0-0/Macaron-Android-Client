package me.duohui.android.macaron;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.duohui.android.macaron.Model.Shop;
import me.duohui.android.macaron.Api.MacaronApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopOpenPopup extends AppCompatActivity implements Button.OnClickListener
{

    private static final String TAG = "ShopOpenPopupActivity";
    private  TextView txtText;
    private  Button btn_cancel,btn_confirm;
    private  Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_open_popup);

        //Intent
        Intent intent = getIntent();
        shop = (Shop)intent.getSerializableExtra("shop");

        initView();
    }

    private void initView(){
        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);
        if(shop.getOpen()==1)  txtText.setText("문 닫을꺼얌??");
        else  txtText.setText("문 열꺼야??");

        btn_cancel =  findViewById(R.id.btn_cancel);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

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
            //확인버튼
            case R.id.btn_confirm:
                updateOpenClose();
                break ;
        }
    }

    private void updateOpenClose() {
        Call<Boolean> call;
        int shopNumber = shop.getShopNumber();
        final int openNum = shop.getOpen();
        if(openNum==0)
            call = MacaronApi.service.open(shopNumber);
        else
            call = MacaronApi.service.close(shopNumber);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()){
                    boolean isUpdated = response.body();
                    if(isUpdated){   //업데이트 성공
                        Toast.makeText(getApplicationContext(),"업데이트 성공",Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK,resultIntent);
                        finish();
                    }else{      //업데이트 실패
                        Toast.makeText(getApplicationContext(),"업데이트 실패",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"업데이트 실패",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


}
