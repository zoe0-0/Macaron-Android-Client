package me.duohui.android.macaron;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import me.duohui.android.macaron.Api.MacaronApi;
import me.duohui.android.macaron.Model.Customer;
import me.duohui.android.macaron.Model.Shop;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerMain extends AppCompatActivity {

    private static final String TAG = "CustomerMenuActivity";
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1000;

    private GridViewAdapter adapter;
    private GridView gridView;
    private int memberNumber;
    private Customer customer;
    private List<Shop> shopList;

    private TextView text_location;
    private ImageButton btn_gps, btn_sort;
    private LocationManager locationManager;
    private Geocoder geocoder;
    private List<Address> addressList;

    //address
    private String fullAddress;
    private String[] address;
    private double longitude, latitude;

    private boolean isAllList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        Intent intent = getIntent();
        memberNumber = intent.getIntExtra("memberNumber", 0);
        initView();
        initLocationManager();
        getCustomer();

    }



    private void initView() {
        gridView = findViewById(R.id.gridView);
        text_location = findViewById(R.id.text_location);
        btn_gps = findViewById(R.id.btn_gps);
        //btn_sort = findViewById(R.id.btn_sort);
    }

    private void initLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(getApplicationContext());
    }


    public void onClickGpsBtn(View view) {
        //LocationManager로 현재 위치 받아오기 > reverse geocoding > 주소에서 00구 parsing > lan,long,gu,address db저장 > setText(gu)
        updateLocation();
    }

    public void onClickSortBtn(View view) {
        //TODO - 00시 > 00구 선택하기


    }


    //getCustomer -> getShopList
    private void getCustomer() {
        Call<Customer> call = MacaronApi.service.getCustomer(memberNumber);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()) {
                    customer = response.body();
                    if (customer != null) {
                        Log.d(TAG, "success get customer");
                        if (customer.getGu() == null) {
                            isAllList = true;
                            text_location.setText("서울특별시 / 전체");
                            getShopListAll();
                        } else {
                            isAllList = false;
                            text_location.setText(customer.getAddress().split("\\s")[1] + " / " + customer.getGu());
                            getShopListInGu(customer.getGu());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {

            }
        });
    }

    private void getShopListInGu(String gu) {
        final String guInfo = gu;
        Call<List<Shop>> call = MacaronApi.service.getShopListInGuByDistance(customer.getCustomerNumber(), gu);
        call.enqueue(new Callback<List<Shop>>() {
            @Override
            public void onResponse(Call<List<Shop>> call, Response<List<Shop>> response) {
                if (response.isSuccessful()) {
                    shopList = response.body();
                    if (shopList != null) {
                        Log.d(TAG, "success get shop List in " + guInfo);
                        //GridListView와 Adapter 연결
                        adapter = new GridViewAdapter(customer.getCustomerNumber());
                        gridView.setAdapter(adapter);
                        adapter.addItemList(shopList);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Shop>> call, Throwable t) {

            }
        });
    }

    private void getShopListAll() {
        Call<List<Shop>> call = MacaronApi.service.getShopListByDistance(customer.getCustomerNumber());
        call.enqueue(new Callback<List<Shop>>() {
            @Override
            public void onResponse(Call<List<Shop>> call, Response<List<Shop>> response) {
                if (response.isSuccessful()) {
                    shopList = response.body();
                    if (shopList != null) {
                        Log.d(TAG, "success get all shop List");

                        //GridListView와 Adapter 연결
                        adapter = new GridViewAdapter(customer.getCustomerNumber());
                        gridView.setAdapter(adapter);
                        adapter.addItemList(shopList);
                    }else{
                        Log.d(TAG, "fail get all shop List1");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Shop>> call, Throwable t) {
                Log.d(TAG, "fail get all shop List2 "+t.getMessage()
                );
            }
        });

    }


    private void updateLocation() {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(CustomerMain.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);

        } else {

            text_location.setText("위치 업데이트 중");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    1000, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    locationListener);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    1000, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "onRequestPermissionsResult: permission ok");
                    updateLocation();


                } else {
                    Log.d(TAG, "onRequestPermissionsResult: permission fail");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.

            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            Log.d(TAG, "onLocationChanged: " + longitude + " " + latitude);

            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 10); // 얻어올 값의 개수
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "입출력 오류 - 서버에서 주소변환시 에러발생");
            }
            if (addressList != null) {
                if (addressList.size() == 0) {
                    Log.d(TAG, "해당되는 주소 정보는 없습니다");
                } else {
                    locationManager.removeUpdates(locationListener);
                    //TODO - full address에서 gu만 parsing
                    fullAddress = addressList.get(0).getAddressLine(0).toString().trim();
                    address = addressList.get(0).getAddressLine(0).trim().split("\\s");
                    updateCustomerLocation(customer.getCustomerNumber());

                }
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    private void updateCustomerLocation(int customerNumber) {

        me.duohui.android.macaron.Model.Location myLocation = new me.duohui.android.macaron.Model.Location();
        myLocation.setCustomerNumber(customerNumber);
        myLocation.setLat(latitude);
        myLocation.setLon(longitude);
        myLocation.setAddress(fullAddress);
        myLocation.setGu(address[2]);
        Call<Boolean> call = MacaronApi.service.updateLocation(myLocation);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body()) {
                    Toast.makeText(getApplicationContext(), "위치 업데이트 성공", Toast.LENGTH_SHORT).show();
                    text_location.setText(address[1] + " / " + address[2]);
                    getCustomer();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }


    public void onClickRefreshBtn(View view) {
        if (isAllList) {
            getShopListAll();
        } else {
            getShopListInGu(customer.getGu());
        }
    }

    public void onClickTotalBtn(View view) {
        isAllList = true;
        text_location.setText("서울특별시 / 전체");
        getShopListAll();
    }


    @Override
    public void onBackPressed() {
    }
}