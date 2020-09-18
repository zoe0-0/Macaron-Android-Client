package me.duohui.android.macaron;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.duohui.android.macaron.Api.MacaronApi;
import me.duohui.android.macaron.Model.Lineup;
import me.duohui.android.macaron.Model.Menu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetDialog extends BottomSheetDialogFragment{

    private int shopNumber;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private LinearLayout linearLayout;
    private int col,row;
    private RecyclerViewAdapter.OnMenuAddedListener onMenuAddedListener;



    public void setShopNumber(int shopNumber){

        this.shopNumber = shopNumber;
    }

    public void setShowcaseLocation(int row, int col){
        this.row = row;
        this.col = col;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.lineup_add_popup, container,false);
        linearLayout = view.findViewById(R.id.linearLayout);
        recyclerView = view.findViewById(R.id.recyler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        //recyclerview에서 라인업메뉴 추가성공했을 때

       onMenuAddedListener = new RecyclerViewAdapter.OnMenuAddedListener() {
           @Override
           public void onMenuAdded(Lineup lineup) {
               dismiss();
               mListener.onDismiss(lineup);
           }
       };

        getMenu();
        return view;
    }


    //callback
    private BottomSheetDialog.OnDismissListener mListener;

    interface OnDismissListener {
        void onDismiss(Lineup lineup);
    }

    public void setOnDismissListener(BottomSheetDialog.OnDismissListener listener){
        mListener = listener;
    }



    private void getMenu(){
        Call<List<Menu>> call = MacaronApi.service.getMenuList(shopNumber);
        call.enqueue(new Callback<List<Menu>>() {
            @Override
            public void onResponse(Call<List<Menu>> call, Response<List<Menu>> response) {
                if(response.isSuccessful()){
                    //메뉴리스트 받아오기
                    List<Menu> menuList =  response.body();

                    //Adapter 연결
                    Lineup lineup = new Lineup();
                    lineup.setCol(col);
                    lineup.setRow(row);
                    lineup.setLevel(0);
                    lineup.setShopNumber(shopNumber);

                    adapter = new RecyclerViewAdapter(menuList);
                    adapter.setOnMenuAddedListener(onMenuAddedListener);
                    adapter.setLineup(lineup);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Menu>> call, Throwable t) {

            }
        });

    }
}
