package me.duohui.android.macaron;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import me.duohui.android.macaron.Model.Customer;
import me.duohui.android.macaron.Model.Shop;
import me.duohui.android.macaron.Api.MacaronApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "JoinActivity";

    private String gu;
    private  RadioGroup radioGroup;
    private  RadioButton radio_btn_customer, radio_btn_shop;
    private  ScrollView customer_join_form, shop_join_form;
    private  EditText customer_id, customer_pwd, customer_pwd_confirm, customer_nickname,customer_email,customer_name;
    private  EditText shop_id, shop_pwd, shop_pwd_confirm, shop_name, shop_email, shop_shopName, shop_number, shop_sns,shop_row, shop_col;
    private  EditText  shop_add, shop_img;
    private byte[] byteArray;


    static final int REQUEST_PHOTO_PICK = 1;
    static final int REQUEST_ADDRESS_SETTING = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        initView();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    private void initView(){
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);

        radio_btn_customer =  findViewById(R.id.radio_btn_customer);
        radio_btn_shop = findViewById(R.id.radio_btn_shop);

        customer_join_form = findViewById(R.id.customer_join_form);
        shop_join_form = findViewById(R.id.shop_join_form);

        //customer join form
        customer_id = findViewById(R.id.customer_id);
        customer_pwd =  findViewById(R.id.customer_pwd);
        customer_pwd_confirm =  findViewById(R.id.customer_pwd_confirm);
        customer_nickname = findViewById(R.id.customer_nickname);
        customer_email = findViewById(R.id.customer_email);
        customer_name = findViewById(R.id.customer_name);

        //shop join form
        shop_id =  findViewById(R.id.shop_id);
        shop_pwd = findViewById(R.id.shop_pwd);
        shop_pwd_confirm = findViewById(R.id.shop_pwd_confirm);
        shop_name = findViewById(R.id.shop_name);
        shop_email = findViewById(R.id.shop_email);
        shop_shopName = findViewById(R.id.shop_shopName);
        shop_number  = findViewById(R.id.shop_number);
        shop_add = findViewById(R.id.shop_address);
        shop_img = findViewById(R.id.shop_image);
        shop_sns = findViewById(R.id.shop_sns);
        shop_row = findViewById(R.id.shop_showcase_row);
        shop_col = findViewById(R.id.shop_showcase_col);

        shop_add.setKeyListener(null);
        shop_img.setKeyListener(null);
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if(i == R.id.radio_btn_customer){
            customer_join_form.setVisibility(View.VISIBLE);
            shop_join_form.setVisibility(View.INVISIBLE);
        }else{
            customer_join_form.setVisibility(View.INVISIBLE);
            shop_join_form.setVisibility(View.VISIBLE);
        }
    }


    public void onClickCustomerJoin(View v){
                String id = customer_id.getText().toString().trim();
                String pwd1 = customer_pwd.getText().toString().trim();
                String pwd2 = customer_pwd_confirm.getText().toString().trim();
                String nickname = customer_nickname.getText().toString().trim();
                String email = customer_email.getText().toString().trim();
                String name = customer_name.getText().toString().trim();

                if(id ==null || pwd1 == null || pwd2 == null || nickname == null || email == null || name == null ||
                   id.equals("") || pwd1.equals("") || pwd2.equals("")|| nickname.equals("") || email.equals("") ||  name.equals("")) return;

                if(!pwd1.equals(pwd2)) return;

                Customer customer = new Customer();
                customer.setId(id);
                customer.setPassword(pwd1);
                customer.setNickname(nickname);
                customer.setEmail(email);
                customer.setCustomerName(name);

                Log.d(TAG, "Customer "+customer.toString());

                Call<Boolean> call = MacaronApi.service.createCustomer(customer);
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.body()){
                            Log.d(TAG, "onResponse: success create customer");
                            Toast.makeText(getApplicationContext(),"회원가입 성공",Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            setResult(RESULT_OK,resultIntent);
                            finish();
                        }else{
                            Log.d(TAG, "onResponse: fail create customer");
                            Toast.makeText(getApplicationContext(),"회원가입 실패",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Log.d(TAG, "onResponse: fail create customer");
                        Toast.makeText(getApplicationContext(),"회원가입 실패",Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void onClickShopJoin(View v){

        //필수
        String id = shop_id.getText().toString().trim();
        String pwd1 = shop_pwd.getText().toString().trim();
        String pwd2 = shop_pwd_confirm.getText().toString().trim();
        String name = shop_name.getText().toString().trim();
        String email = shop_email.getText().toString().trim();
        String shopName = shop_shopName.getText().toString().trim();
        String shopPhoneNumber = shop_number.getText().toString().trim();
        String shopAdd = shop_add.getText().toString().trim();

        //선택
        String shopSns = shop_sns.getText().toString().trim();
        String shopRow = shop_row.getText().toString().trim();
        String shopCol = shop_col.getText().toString().trim();


        if(id ==null || pwd1 == null || pwd2 == null || name == null || email == null || shopName == null || shopPhoneNumber == null || shopAdd == null||
            id.equals("") || pwd1.equals("") || pwd2.equals("")|| name.equals("") || email.equals("") ||  shopName.equals("") || shopPhoneNumber.equals("")|| shopAdd.equals("")) return;

        if(!pwd1.equals(pwd2)) return;


        Shop shop = new Shop();
        shop.setId(id);
        shop.setPassword(pwd1);
        shop.setOwnerName(name);
        shop.setEmail(email);
        shop.setShopName(shopName);
        shop.setShopImage(byteArray);
        shop.setPhoneNumber(shopPhoneNumber);
        shop.setAddress(shopAdd);
        //lat, long, gu parsing
        shop.setGu(gu);
        //geocoding - get Lat, Lon
        Geocoder coder = new Geocoder(this);
        List<Address> addr = null;
        try {
            addr = coder.getFromLocationName(shopAdd, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addr != null) {
            for (int i = 0; i < addr.size(); i++) {
                Address lating = addr.get(i);
                double lat = lating.getLatitude(); // 위도가져오기
                double lon = lating.getLongitude(); // 경도가져오기
                shop.setLat(lat);
                shop.setLon(lon);
            }
        }

        //
        if(shopRow == null || shopRow.equals("")) shop.setRow(0);
        else shop.setRow(Integer.parseInt(shopRow));

        if(shopCol == null || shopCol.equals("")) shop.setCol(0);
        else shop.setCol(Integer.parseInt(shopCol));

        if(shopSns == null || shopSns.equals("")) shop.setSns(null);
        else shop.setSns(shopSns);


        Log.d(TAG, "Shop "+shop.toString());

        Call<Boolean> call = MacaronApi.service.createShop(shop);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.body()){
                    Log.d(TAG, "onResponse: success create shop");
                    Toast.makeText(getApplicationContext(),"회원가입 성공",Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK,resultIntent);
                    finish();
                }else{
                    Log.d(TAG, "onResponse: fail create shop");
                    Toast.makeText(getApplicationContext(),"회원가입 실패",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.d(TAG, "onResponse: fail create shop");
                Toast.makeText(getApplicationContext(),"회원가입 실패",Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void onClickAdressAddBtn(View view){
        Intent intent = new Intent(JoinActivity.this,DaumWebViewActivity.class);
        startActivityForResult(intent,REQUEST_ADDRESS_SETTING);
    }

    public void onClickImageAddBtn(View view){
        Intent intent = new Intent(JoinActivity.this,PhotoPickPopup.class);
        startActivityForResult(intent,REQUEST_PHOTO_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO_PICK && resultCode == RESULT_OK) {

            String photoPath = data.getStringExtra("currentPhotoPath");
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byteArray = baos.toByteArray();

            if(byteArray != null)
                    shop_img.setText("사진이 선택되었습니다");


        }else if(requestCode == REQUEST_ADDRESS_SETTING && resultCode == RESULT_OK){
            String address = data.getStringExtra("result");
            gu = data.getStringExtra("guName");
            shop_add.setText(address);
        }
    }




}
