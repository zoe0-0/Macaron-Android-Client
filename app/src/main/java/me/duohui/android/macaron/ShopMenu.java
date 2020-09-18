package me.duohui.android.macaron;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import me.duohui.android.macaron.Model.Menu;
import me.duohui.android.macaron.Api.MacaronApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



// 메뉴 추가하기, 삭제하기는 => adapter로
// 북마크 표시의 경우는 list data를 새로 받아와서 setting해야함 (즐겨찾기 추가에 따라서 리스트의 순서가 달라지기 때문에(즐겨찾기가 위로 올라간다))
public class ShopMenu extends AppCompatActivity {

    private static final String TAG = "ShopMenuActivity";

    private  ListViewAdapter adapter;
    private  ListView listView;
    private  int shopNumber;
    private  List<Menu> menuList;

    private  ListViewAdapter.OnBookmarkUpdatedListener onBookmarkUpdatedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_menu);
        Intent intent = getIntent();
        shopNumber = intent.getIntExtra("shopNumber",0);
        listView =  findViewById(R.id.listview);


        onBookmarkUpdatedListener = new ListViewAdapter.OnBookmarkUpdatedListener() {
            @Override
            public void onBookmarkUpdated(int index) {
                Log.d(TAG, "onBookmarkUpdated: "+index);
                final int i = index-1;
                //listView.smoothScrollToPosition(index);
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.smoothScrollToPositionFromTop(i,0);
                        Log.d(TAG, "run: iii  "+i);
                    }
                });
                Log.d(TAG, "onBookmarkUpdated:!!!!!!!! index "+index);
            }
        };

        getMenu();

    }


    private void getMenu(){
        Call<List<Menu>> call = MacaronApi.service.getMenuList(shopNumber);
        call.enqueue(new Callback<List<Menu>>() {
            @Override
            public void onResponse(Call<List<Menu>> call, Response<List<Menu>> response) {
                if(response.isSuccessful()){
                       //메뉴리스트 받아오기
                        menuList =  response.body();

                    //ListView와 Adapter 연결
                        adapter = new ListViewAdapter(getApplicationContext());
                        adapter.setOnBookmarkUpdatedListener(onBookmarkUpdatedListener);
                        listView.setAdapter(adapter);
                        adapter.addItemList(menuList);
                }
            }
            @Override
            public void onFailure(Call<List<Menu>> call, Throwable t) {

            }
        });

    }



    public void onClickPlus(View v){
        Intent intent = new Intent(this,ShopMenuAdd.class);
        intent.putExtra("shopNumber",shopNumber);
        startActivityForResult(intent,3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
       if(requestCode==3000){
           if(resultCode==RESULT_OK){
               if(!intent.getBooleanExtra("cancel",false)){
                   Toast.makeText(this,"메뉴 등록 성공",Toast.LENGTH_SHORT).show();
                   Menu menu = (Menu) intent.getSerializableExtra("added menu");
                   Log.d(TAG, "onActivityResult / added menu : "+menu.toString());
                   adapter.addItem(menu);
                   adapter.notifyDataSetChanged();
                   //아이템 add할 경우 추가된 아이템이 리스트뷰의 가장 마지막 위치로 들어가기 때문에 포커스를 마지막 position으로 옮겨준다
                   int position = adapter.getCount();
                   listView.smoothScrollToPositionFromTop(position,0);
               }
           }else{
               Toast.makeText(this,"메뉴 등록 실패",Toast.LENGTH_SHORT).show();
           }
       }
    }

    public void onClickBack(View v){
        finish();
    }

}
