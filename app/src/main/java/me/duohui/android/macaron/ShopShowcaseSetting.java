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
import android.widget.Toast;

import me.duohui.android.macaron.Api.MacaronApi;
import me.duohui.android.macaron.Model.Showcase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopShowcaseSetting extends AppCompatActivity {

    private static final String TAG = "ShowcaseSettingActivity";
    private Showcase showcase;
    private EditText row,col;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_showcase_setting);

        Intent intent = getIntent();
        showcase = (Showcase) intent.getSerializableExtra("showcase");
        Log.d(TAG, "onCreate: " + showcase.toString());
        initView();
    }

    private void initView(){
        row = findViewById(R.id.row);
        col = findViewById(R.id.col);
        row.setText(showcase.getRow()+"");
        col.setText(showcase.getCol()+"");
    }



    public void onClickCancel(View view){
        Intent intent = new Intent(getApplicationContext(),ShopMain.class);
        // clear_top :  shopmain > lineup > showcasettin_popup 을 모두 스택에서 pop 시키고 shopmain을 다시 push
        // single_top : shopmain 역시 새로 스택에 push 되기 때문에 기존에 가지고 있던 member_number가 초기화되어 shop을 받아올 수 없다.
        // shopmain은 스택에 single top으로 남겨두면 이 값이 유지가 된다
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void onClickRegister(View view){
        String c = col.getText().toString().trim();
        String r = row.getText().toString().trim();
        if(c==null || c.equals("") || r == null || r.equals("")) return;
        if(c.equals("0") || r.equals("0")) return;
        updateShowcase(Integer.parseInt(c),Integer.parseInt(r));
        //쇼케이스 업데이트
    }

    private void updateShowcase(int col, int row){
        showcase.setCol(col);
        showcase.setRow(row);
        Call<Boolean> call = MacaronApi.service.updateShowcase(showcase);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.body()){
                    Toast.makeText(getApplicationContext(),"쇼케이스 사이즈 세팅 성공", Toast.LENGTH_SHORT);
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK,resultIntent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"쇼케이스 사이즈 세팅 실패", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"쇼케이스 사이즈 세팅 실패", Toast.LENGTH_SHORT);
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


