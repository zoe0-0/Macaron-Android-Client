package me.duohui.android.macaron;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;

import me.duohui.android.macaron.Model.Member;
import me.duohui.android.macaron.Api.MacaronApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    static final int JOIN_MEMBER_REQUEST = 1;
    private  EditText edit_id, edit_pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initView();

    }

    private void initView() {
        edit_id = findViewById(R.id.edit_id);
        edit_pwd = findViewById(R.id.edit_pwd);
    }

    public void clickLoginBtn(View v){

        String id = edit_id .getText().toString().trim();
        String pwd = edit_pwd.getText().toString().trim();

        if (id == null || pwd == null || id.equals("") || pwd.equals("")) {
            return;
        }

        Member member = new Member();
        member.setId(id);
        member.setPassword(pwd);
        checkLogin(member);
    }

    public void clickJoinBtn(View v){
        Intent intent = new Intent(MainActivity.this,JoinActivity.class);
        startActivityForResult(intent,JOIN_MEMBER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //from join page
        if(resultCode==RESULT_OK){
          resetEditText();
        }
    }

    private void checkLogin(Member member) {
        Call<Member> call = MacaronApi.service.getMember(member);
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if(response.isSuccessful()){
                    Member m = response.body();
                    Log.d(TAG, "success login");
                    int memberNumber = m.getMemberNumber();
                    int memberType = m.getType();

                    if(memberType==1){   //손님일때
                        Intent intent = new Intent(MainActivity.this,CustomerMain.class);
                        intent.putExtra("memberNumber",memberNumber);
                        startActivity(intent);
                    }else{  //가게일때(0)
                        Intent intent = new Intent(MainActivity.this,ShopMain.class);
                        intent.putExtra("memberNumber",memberNumber);
                        startActivity(intent);
                    }

                }else{
                    resetEditText();
                    Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                resetEditText();
                Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();

    }

    private void resetEditText(){
        edit_id.setText("");
        edit_pwd.setText("");
        edit_id.requestFocus();
    }

}