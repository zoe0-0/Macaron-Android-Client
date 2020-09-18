package me.duohui.android.macaron;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import me.duohui.android.macaron.Api.MacaronApi;
import me.duohui.android.macaron.Model.Menu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopMenuAdd extends AppCompatActivity implements Button.OnClickListener{

    private static final String TAG = "ShopMenuAddActivity";

    private  Button btn_cancel, btn_menu_register;
    private  EditText menu_name, menu_price;
    private  int shopNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_menu_add);
        Intent intent = getIntent();
        shopNumber = intent.getIntExtra("shopNumber",0);
        initView();

    }

    private void initView(){


        menu_name = (EditText) findViewById(R.id.menu_name);
        menu_price = (EditText) findViewById(R.id.menu_price);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_menu_register = (Button) findViewById(R.id.btn_menu_register);

        btn_cancel.setOnClickListener(this);
        btn_menu_register.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel :
                Intent resultIntent = new Intent();
                resultIntent.putExtra("cancel",true);
                setResult(RESULT_OK,resultIntent);
                finish();
                break;

            case R.id.btn_menu_register :
                String name = menu_name.getText().toString().trim();
                String price = menu_price.getText().toString().trim();
                if(name==null || name.equals("") || price==null || price.equals("")) return;

                Menu menu = new Menu();
                menu.setShopNumber(shopNumber);
                menu.setMenuName(name);
                menu.setPrice(Integer.parseInt(price));
                createMenu(menu);
                break;

        }
    }


    private void createMenu(Menu menu){
        final Menu m = menu;
        Call<Menu> call = MacaronApi.service.createMenu(menu);
        call.enqueue(new Callback<Menu>() {
            Intent resultIntent = new Intent();
            @Override
            public void onResponse(Call<Menu> call, Response<Menu> response) {
                Menu newMenu = response.body();
                if(newMenu!=null){
                    resultIntent.putExtra("added menu",newMenu);   //메뉴이름,가격 외에도 디비에 생성되어 메뉴 번호까지 setting 되어 있는 객체
                    setResult(RESULT_OK,resultIntent);
                }else{
                    setResult(RESULT_CANCELED);
                }
                finish();

            }

            @Override
            public void onFailure(Call<Menu> call, Throwable t) {
                setResult(RESULT_CANCELED);
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

}
