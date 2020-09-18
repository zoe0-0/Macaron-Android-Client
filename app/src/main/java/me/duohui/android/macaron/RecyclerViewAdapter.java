package me.duohui.android.macaron;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.duohui.android.macaron.Api.MacaronApi;
import me.duohui.android.macaron.Model.Lineup;
import me.duohui.android.macaron.Model.LineupMenu;
import me.duohui.android.macaron.Model.Menu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "RECYCLERVIEWADAPTER";
    private List<Menu> menuList;
    private Lineup lineup;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView macaronImg;
        private TextView menuName;
        private TextView menuPrice;
        private Button btn_menu_register;

        MyViewHolder(View view){
            super(view);
            macaronImg = view.findViewById(R.id.macaron_img);
            menuName = view.findViewById(R.id.menu_name);
            menuPrice = view.findViewById(R.id.menu_price);
            btn_menu_register = view.findViewById(R.id.btn_menu_register);
        }
    }

    RecyclerViewAdapter(List<Menu> menuList){
        this.menuList = menuList;
    }

    public void setLineup(Lineup lineup){
        this.lineup = lineup;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_col_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final  int pos = position;
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        //myViewHolder.macaronImg.setImageResource(menuList.get(position).drawableId);
        myViewHolder.macaronImg.setImageResource(R.drawable.macaron);
        myViewHolder.menuName.setText(menuList.get(pos).getMenuName());
        myViewHolder.menuPrice.setText(menuList.get(pos).getPrice()+" 원");
        myViewHolder.btn_menu_register.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                lineup.setMenuNumber(menuList.get(pos).getMenuNumber());
                createLineup();
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    private void createLineup(){
        Call<Lineup> call = MacaronApi.service.createLineup(lineup);
        call.enqueue(new Callback<Lineup>() {
            @Override
            public void onResponse(Call<Lineup> call, Response<Lineup> response) {
                if(response.isSuccessful()){
                    if(response.body() != null) {
                        Log.d(TAG, "onResponse: " + "라인업 에드 성공");
                        mListener.onMenuAdded(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<Lineup> call, Throwable t) {

            }
        });
    }


    //callback
    private RecyclerViewAdapter.OnMenuAddedListener mListener;

    interface OnMenuAddedListener {
        void onMenuAdded(Lineup lineup);
    }

    public void setOnMenuAddedListener(RecyclerViewAdapter.OnMenuAddedListener listener){
        mListener = listener;
    }



}
