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

public class ShopShowcaseUpdate extends AppCompatActivity implements Button.OnClickListener {

    private static final String TAG = "ShowcaseUpdateActivity";

    private Showcase showcase;
    private int shopNumber;

    private EditText row,col;
    private Button btn_cancel, btn_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_showcase_update);

        Intent intent = getIntent();
        showcase = (Showcase) intent.getSerializableExtra("showcase");
        shopNumber = intent.getIntExtra("shopNumber",0);
        initView();
    }


    private void initView(){
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_update = (Button)findViewById(R.id.btn_update);
        btn_cancel.setOnClickListener(this);
        btn_update.setOnClickListener(this);

        row = (EditText)findViewById(R.id.row);
        col = (EditText)findViewById(R.id.col);
        row.setText(showcase.getRow()+"");
        col.setText(showcase.getCol()+"");
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_cancel :
                finish();

                break;

            case R.id.btn_update :
                String c = col.getText().toString().trim();
                String r = row.getText().toString().trim();
                if(c==null || c.equals("") || r == null || r.equals("")) {
                    Toast.makeText(getApplicationContext(),"빈칸을 채워주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(c.equals("0") || r.equals("0")){
                    Toast.makeText(getApplicationContext(),"행열은 0일 수 없습니다",Toast.LENGTH_SHORT).show();
                    return;
                }
                updateShowcase(Integer.parseInt(c),Integer.parseInt(r));
                break;
        }

    }



    private void updateShowcase(int col, int row){
        showcase.setCol(col);
        showcase.setRow(row);
        Log.d(TAG, "updateShowcase: showcase "+showcase.toString());
        Call<Boolean> call = MacaronApi.service.updateShowcase(showcase);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.body()){
                    //Toast.makeText(getApplicationContext(),"쇼케이스 정보 업데이트 성공", Toast.LENGTH_SHORT);
                    Log.d(TAG, "쇼케이스 업데이트 성공");
                    deleteLineup();
                }else{
                    //Toast.makeText(getApplicationContext(),"쇼케이스 정보 업데이트 실패", Toast.LENGTH_SHORT);
                    Log.d(TAG, "쇼케이스 업데이트 실패");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                 //Toast.makeText(getApplicationContext(),"쇼케이스 정보 업데이트 실패", Toast.LENGTH_SHORT);
                 Log.d(TAG, "쇼케이스 업데이트 실패"+t.getMessage());
           }
        });

    }

    //TODO - 쇼케이스 사이즈 업데이트 성공 시, 그 가게의 라인업 초기화
    private void deleteLineup(){
        Call<Boolean> call = MacaronApi.service.deleteAllLineup(shopNumber);
        Log.d(TAG, "deleteLineup/shopNumer "+shopNumber);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.body()){
                    Toast.makeText(getApplicationContext(),"쇼케이스 수정 + 라인업 삭제 성공",Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("showcase",showcase);
                    setResult(RESULT_OK,resultIntent);
                }else{
                    Toast.makeText(getApplicationContext(),"쇼케이스 수정 + 라인업 삭제 실패",Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"쇼케이스 수정 + 라인업 삭제 실패",Toast.LENGTH_SHORT).show();
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
