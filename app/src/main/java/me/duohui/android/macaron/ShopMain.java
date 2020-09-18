package me.duohui.android.macaron;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.duohui.android.macaron.Model.Shop;
import me.duohui.android.macaron.Api.MacaronApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopMain extends AppCompatActivity implements Button.OnClickListener {

    private static final String TAG = "ShopMainActivity";

    private  TextView textView;
    private  Button btn_menu, btn_lineup, btn_mypage;

    private  int memberNumber;
    private  Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_main);
        Intent intent = getIntent();
        memberNumber = intent.getIntExtra("memberNumber", 0);
        Log.d(TAG, "onCreate: memberNumber"+memberNumber);
        getShop(memberNumber);
    }

    private void initView() {
        textView = findViewById(R.id.testView);
        btn_menu = findViewById(R.id.btn_menu);
        btn_lineup = findViewById(R.id.btn_lineup);
        btn_mypage = findViewById(R.id.btn_mypage);

        textView.setText(shop.getOwnerName() + "님");
        btn_menu.setOnClickListener(this);
        btn_lineup.setOnClickListener(this);
        btn_mypage.setOnClickListener(this);
    }



    private void getShop(int memberNumber) {
        Call<Shop> call = MacaronApi.service.getShop(memberNumber);
        call.enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {
                if(response.isSuccessful()){
                    shop = response.body();
                    initView();
                }
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

    }



    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.btn_menu:
                intent = new Intent(this, ShopMenu.class);
                intent.putExtra("shopNumber", shop.getShopNumber());
                startActivity(intent);
                break;

            case R.id.btn_lineup:
                intent = new Intent(this, ShopLineup.class);
                intent.putExtra("shopNumber",shop.getShopNumber());
                startActivity(intent);
                break;

            case R.id.btn_mypage:
                intent = new Intent(this,ShopMypage.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onBackPressed() {
    }

}

