package me.duohui.android.macaron;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.duohui.android.macaron.Api.MacaronApi;
import me.duohui.android.macaron.Model.Like;
import me.duohui.android.macaron.Model.Menu;
import me.duohui.android.macaron.Model.Shop;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GridViewAdapter extends BaseAdapter {

    private static final String TAG = "GridViewAdapter";

    private ImageView img;
    private TextView shop_name, shop_address;
    private FrameLayout shop_container;
    private int customerNumber;

    private List<Shop> gridViewItemList = new ArrayList<Shop>() ;



    public GridViewAdapter(int customerNumber){
        super();
        this.customerNumber = customerNumber;
    }



    @Override
    public int getCount() {
        return gridViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return gridViewItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final Shop shop = gridViewItemList.get(position);


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.customer_grid_item, parent, false);
        }


        shop_container = convertView.findViewById(R.id.shop_container);
        shop_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,CustomerShopInfo.class);
                //intent.putExtra("shop",shop);
                intent.putExtra("shopNumber",shop.getShopNumber());
                context.startActivity(intent);
            }
        });
        img = convertView.findViewById(R.id.macaron_img);
        if(shop.getShopImage()!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(shop.getShopImage(), 0, shop.getShopImage().length);
            img.setImageBitmap(bitmap);

        }else{
            img.setImageResource(R.drawable.macaron_shop);
        }


        shop_name =  convertView.findViewById(R.id.shop_name);
        shop_name.setText(shop.getShopName());
        shop_address = convertView.findViewById(R.id.shop_address);
        shop_address.setText("[ "+shop.getGu()+" ]");

        if(shop.getOpen()==0)  {
            //TODO - 투명한 회색 레이아웃 덮어쓰기. 문 닫은 가게

            // Apply grayscale filter
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            img.setColorFilter(filter);
        }

        return convertView;
    }



    public void addItemList(List<Shop> listViewItemList) {
        this.gridViewItemList = listViewItemList;
    }

    public void addItem(Shop shop){
        gridViewItemList.add(shop);
        //dataChange();
    }

    public void removeItem(int position){
        gridViewItemList.remove(position);
        dataChange();
    }


    public void dataChange() {
         this.notifyDataSetChanged();
    }




}
