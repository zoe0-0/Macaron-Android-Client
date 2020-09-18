package me.duohui.android.macaron;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.duohui.android.macaron.Api.MacaronApi;
import me.duohui.android.macaron.Model.Lineup;
import me.duohui.android.macaron.Model.LineupMenu;
import me.duohui.android.macaron.Model.Shop;
import me.duohui.android.macaron.Model.Showcase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShopLineup extends AppCompatActivity {

    private static final String TAG = "ShopLineupActivity";
    static final int SHOWCASE_UPDATE_REQUEST = 1;
    static final int LINEUP_DELETE_REQUEST = 2;
    static final int OPEN_SETTING_REQUEST = 3;
    static final int SHOWCASE_SIZE_SETTING_REQUEST = 4;

    private Shop shop;
    private Showcase showcase;
    private int shopNumber;
    private int col, row;
    private List<LineupMenu> lineupMenuList;
    private ImageButton btn_set_showcase;
    private Button btn_openToggle;
    private int totalBoxCount;
    private int colCountperLine;
    private RecyclerView recyclerView;
    private GridRecyclerViewAdapter adapter;
    private BottomSheetDialog.OnDismissListener onDismissListener;
    //private GridRecyclerViewAdapter.OnClickLevelListener onClickLevelListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_lineup);

        Intent intent = getIntent();
        shopNumber = intent.getIntExtra("shopNumber", 0);

        recyclerView = findViewById(R.id.recyclerView);
        getShop();



/*        onClickLevelListener = new GridRecyclerViewAdapter.OnClickLevelListener(){
            @Override
            public void onClickedLevelButton(int level, int lineupNumber) {
                updateCountLevel(level,lineupNumber);
            }
        };*/

    }


  /*  private void updateCountLevel(int level, int lineupNumber){
        Call<Boolean> call = MacaronApi.service.updateLevel(lineupNumber,level);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.body()){
                    Toast.makeText(getApplicationContext(),"재고 업데이트 성공",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"재고 업데이트 실패",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"재고 업데이트 실패",Toast.LENGTH_SHORT).show();
            }
        });
    }*/


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }


    private void initNavigation() {

        btn_openToggle = findViewById(R.id.btn_openToggle);

        if (shop.getOpen() == 0) btn_openToggle.setText("CLOSED");
        else btn_openToggle.setText("OPEN");
        btn_openToggle.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopLineup.this, ShopOpenPopup.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("shop", shop);
                intent.putExtras(bundle);
                startActivityForResult(intent,OPEN_SETTING_REQUEST);
            }
        });

        btn_set_showcase = findViewById(R.id.btn_set_showcase);
        btn_set_showcase.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopLineup.this, ShopShowcaseUpdate.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("showcase", showcase);
                intent.putExtra("shopNumber", shopNumber);
                intent.putExtras(bundle);
                startActivityForResult(intent,SHOWCASE_UPDATE_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if(resultCode == RESULT_OK){
            if (requestCode == SHOWCASE_UPDATE_REQUEST) {
                showcase = (Showcase) data.getSerializableExtra("showcase");
                lineupMenuList = new ArrayList<>();
                row = showcase.getRow();
                col = showcase.getCol();
                totalBoxCount = row * col;
                colCountperLine = totalBoxCount / row;
                setShowcase();
            }
            else if(requestCode == LINEUP_DELETE_REQUEST){
                int position = data.getIntExtra("position",0);
                adapter.removeItem(position);
            }
            else if(requestCode == OPEN_SETTING_REQUEST){
                getShop();
            }
            else if(requestCode == SHOWCASE_SIZE_SETTING_REQUEST){
                //쇼케이스 사이즈 셋팅 성공시
                getShop();
            }
        }

    }

    private void setShowcase() {

        recyclerView.setLayoutManager(new GridLayoutManager(this, col));
        LineupMenu[] newLineupArr = new LineupMenu[totalBoxCount];

        for (int i = 0; i < lineupMenuList.size(); i++) {
            LineupMenu lineup = lineupMenuList.get(i);
            int col = lineup.getCol();
            int row = lineup.getRow();
            int index = colCountperLine * row + col;
            Log.d(TAG, "setShowcase: row "+row+" col "+col+" index "+index);
            Log.d(TAG, "setShowcase: "+lineup.toString());
            newLineupArr[index] = lineup;
        }

        adapter = new GridRecyclerViewAdapter(newLineupArr,this, shopNumber, colCountperLine);
        //adapter.setOnClickLevelListener(onClickLevelListener);
        recyclerView.setAdapter(adapter);

    }


    private void getShop() {
        Call<Shop> call = MacaronApi.service.getShopByShopNumber(shopNumber);
        call.enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {
                shop = response.body();
                if (shop != null) {
                    col = shop.getCol();
                    row = shop.getRow();

                    showcase = new Showcase();
                    showcase.setShopNumber(shopNumber);
                    showcase.setCol(col);
                    showcase.setRow(row);

                    initNavigation();

                    //행열중 한개라도 0이면 쇼케이스 사이즈 셋팅 팝업으로 이동
                    if (col == 0 || row == 0) {
                        Intent intent = new Intent(getApplicationContext(), ShopShowcaseSetting.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("showcase", showcase);
                        intent.putExtras(bundle);
                        startActivityForResult(intent,SHOWCASE_SIZE_SETTING_REQUEST);
                    } else {
                        totalBoxCount = row * col;
                        colCountperLine = totalBoxCount / row;
                        getLineupList();
                    }
                }
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {

            }
        });
    }

    //getLineupList
    private void getLineupList() {
        Call<List<LineupMenu>> call = MacaronApi.service.getLineupList(shopNumber);
        call.enqueue(new Callback<List<LineupMenu>>() {
            @Override
            public void onResponse(Call<List<LineupMenu>> call, Response<List<LineupMenu>> response) {
                if(response.isSuccessful()){
                    lineupMenuList = response.body();
                    if(lineupMenuList == null){
                        Log.d(TAG, "onResponse: +라인업 받아오기 실패");
                        //Toast.makeText(getApplicationContext(),"라인업 메뉴 받아오기 실패",Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(TAG, "onResponse: +라인업 받아오기 성공");
                        //Toast.makeText(getApplicationContext(),"라인업 메뉴 받아오기 성공",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: lineupMenuList.size"+lineupMenuList.size());
                        setShowcase();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LineupMenu>> call, Throwable t) {
                Log.d(TAG, "onResponse: +라인업 받아오기 실패");

            }
        });


    }

    public void onClickBack(View view){
        finish();
    }

}