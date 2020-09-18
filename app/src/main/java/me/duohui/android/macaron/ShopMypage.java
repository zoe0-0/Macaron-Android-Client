package me.duohui.android.macaron;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ShopMypage extends AppCompatActivity {

    private static final String TAG = "ShopMyPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_mypage);
    }

    public void onClickLogout(View v){
        Intent intent = new Intent(this,MainActivity.class);
        //clear_top flag를 사용하면 이 액티비티 위에 있는 다른 액티비티를 모두 종료 시킨다. 메인 화면과 같은 항상 우선하는 액티비티를 만들때 주로 사용
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
