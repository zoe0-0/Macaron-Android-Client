package me.duohui.android.macaron;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import me.duohui.android.macaron.Api.MacaronApi;
import me.duohui.android.macaron.Model.LineupMenu;
import me.duohui.android.macaron.Model.Shop;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerShopInfo extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "CUSTOMERSHOPINFO";

    private Shop shop;
    private LinearLayout menu_container;
    private TextView shop_name, openclose, address;
    private ListView lineup_listview;
    private ImageView shopImg;

    private List<LineupMenu> lineupMenuList;

    private LineupListViewAdapter adapter;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_shop_info);

        Intent intent = getIntent();
        //shop = (Shop)intent.getSerializableExtra("shop");
        int shopNumber = intent.getIntExtra("shopNumber",0);
        getShop(shopNumber);



    }

    private void getShop(int shopNumber){
        Call<Shop> call = MacaronApi.service.getShopByShopNumber(shopNumber);
        call.enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {
                if(response.isSuccessful()){
                    shop = response.body();
                    if(shop!=null) getLineupList();
                    else Log.d(TAG, "onResponse: fail get shop");
                }
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {
                    Log.d(TAG, "onResponse: fail get shop");

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng myPos = new LatLng(shop.getLat(), shop.getLon());
        mMap.addMarker(new MarkerOptions().position(myPos).title(shop.getShopName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos,18));
    }

    private void initView(){

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        shop_name = findViewById(R.id.shop_name);
        shop_name.setText(shop.getShopName());
        lineup_listview = findViewById(R.id.lineup_listview);
        openclose = findViewById(R.id.openclose);
        address = findViewById(R.id.address);
        menu_container = findViewById(R.id.menu_container);
        shopImg = findViewById(R.id.shopImg);
        if(shop.getShopImage()!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(shop.getShopImage(), 0, shop.getShopImage().length);
            shopImg.setImageBitmap(bitmap);
        }else{
            shopImg.setImageResource(R.drawable.macaron_shop);
        }


        int listLength = lineupMenuList.size();
        if(listLength!=0) {
            ViewGroup.LayoutParams params = lineup_listview.getLayoutParams();
            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
            final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
            params.height = listLength * height + padding;
            lineup_listview.setLayoutParams(params);
        }else{
            menu_container.removeAllViewsInLayout();
            menu_container.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            TextView textView = new TextView(this);
            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,height));
            textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            textView.setText("라인업 준비중");
            menu_container.addView(textView);
        }


        if(shop.getOpen()==1) openclose.setText("OPEN");
        else openclose.setText("CLOSED");
        address.setText(shop.getAddress());

        adapter = new LineupListViewAdapter();
        lineup_listview.setAdapter(adapter);
        adapter.addItemList(lineupMenuList);

    }



    private void getLineupList(){
        Call<List<LineupMenu>> call = MacaronApi.service.getLineupList(shop.getShopNumber());
        call.enqueue(new Callback<List<LineupMenu>>() {
            @Override
            public void onResponse(Call<List<LineupMenu>> call, Response<List<LineupMenu>> response) {
                if(response.isSuccessful()){
                    if(response.body()!= null){
                        lineupMenuList = response.body();
                        initView();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LineupMenu>> call, Throwable t) {

            }
        });
    }

}
