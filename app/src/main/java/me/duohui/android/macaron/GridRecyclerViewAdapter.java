package me.duohui.android.macaron;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

public class GridRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int VIEW_TYPE_SET = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    static final int LINEUP_DELETE_REQUEST = 2;

    private BottomSheetDialog.OnDismissListener onDismissListener;
    private Context context;
    private static int shopNumber;

    private static final String TAG = "GRIDRECYCLERVIEWADAPTER";
    private LineupMenu[] lineupArr;
    private int colCountperLine;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageButton btn_delete;
        private ImageButton btn_register;
        private Button btn_soldout, btn_middle, btn_full;
        private TextView menu_name, menu_price;

        MyViewHolder(View view, int viewType, final ViewGroup parent){
            super(view);
            if(viewType == VIEW_TYPE_SET) {
                btn_delete = view.findViewById(R.id.btn_delete);
                menu_name = view.findViewById(R.id.menu_name);
                //menu_price = view.findViewById(R.id.menu_price);
                btn_soldout = view.findViewById(R.id.soldout);
                btn_middle = view.findViewById(R.id.middle);
                btn_full = view.findViewById(R.id.full);
            }
            else{
                btn_register = view.findViewById(R.id.btn_register);
            }
        }
    }


    GridRecyclerViewAdapter(LineupMenu[] lineupArr, Context context, int shopNumber, int colCountperLine){
        this.colCountperLine = colCountperLine;
        this.shopNumber = shopNumber;
        this.context = context;
        this.lineupArr = lineupArr;
        Log.d(TAG, "GridRecyclerViewAdapter: Arr size "+lineupArr.length);


        onDismissListener = new BottomSheetDialog.OnDismissListener() {
            @Override
            public void onDismiss(Lineup lineup) {
                getMenu(lineup.getMenuNumber(),lineup);
            }
        };

    }

    private void getMenu(int menuNumber, final Lineup lineup){
        final Lineup tempLineup = lineup;
        Call<Menu> call = MacaronApi.service.getMenu(menuNumber);
        call.enqueue(new Callback<Menu>() {
            @Override
            public void onResponse(Call<Menu> call, Response<Menu> response) {
                Menu menu;
                if(response.isSuccessful()){
                    menu = response.body();
                    if(menu != null){
                        Toast.makeText(context,"라인업 등록 성공",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: success getMenu");
                        LineupMenu lineupMenu = new LineupMenu();
                        lineupMenu.setLineupNumber(tempLineup.getLineupNumber());
                        lineupMenu.setRow(tempLineup.getRow());
                        lineupMenu.setCol(tempLineup.getCol());
                        lineupMenu.setLevel(tempLineup.getLevel());
                        lineupMenu.setMenuName(menu.getMenuName());
                        lineupMenu.setPrice(menu.getPrice());

                        addItem(lineupMenu.getRow()*colCountperLine+lineupMenu.getCol(),lineupMenu);
                    }
                }
            }

            @Override
            public void onFailure(Call<Menu> call, Throwable t) {
                Log.d(TAG, "onResponse: fail getMenu");
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (lineupArr[position]!=null) {
            return VIEW_TYPE_SET;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    public void removeItem(int position){
        lineupArr[position] =  null;
        this.notifyDataSetChanged();
    }


    public void update(){
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return lineupArr.length;
    }

    public void addItem(int position, LineupMenu lineupMenu){
        lineupArr[position] = lineupMenu;
        this.notifyDataSetChanged();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == VIEW_TYPE_SET) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.showcase_box_item, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.showcase_box_empty,parent,false) ;
        }
        return new MyViewHolder(v,viewType,parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int pos = position;
        final MyViewHolder myViewHolder = (MyViewHolder) holder;

        if(lineupArr[position]!=null) {
            final int lineupNumber = lineupArr[pos].getLineupNumber();

            myViewHolder.menu_name.setText(lineupArr[position].getMenuName()+" "+lineupArr[position].getPrice()+"원");
            //myViewHolder.menu_price.setText(lineupArr[position].getPrice() + "원");
            myViewHolder.btn_delete.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,LinupDeletePopup.class);
                    intent.putExtra("lineupNumber",lineupNumber);
                    intent.putExtra("position",pos);
                    ((ShopLineup)context).startActivityForResult(intent,LINEUP_DELETE_REQUEST);
                }
            });

            if(lineupArr[pos].getLevel()==0){  //많음
                myViewHolder.btn_soldout.setBackgroundResource(R.drawable.round_btn_full);
                myViewHolder.btn_middle.setBackgroundResource(R.drawable.round_btn_full);
                myViewHolder.btn_full.setBackgroundResource(R.drawable.round_btn_full);
            }else if(lineupArr[pos].getLevel()==1){  //보통
                myViewHolder.btn_soldout.setBackgroundResource(R.drawable.round_btn_middle);
                myViewHolder.btn_middle.setBackgroundResource(R.drawable.round_btn_middle);
                myViewHolder.btn_full.setBackgroundResource(R.drawable.round_btn_soldout);
            }else{ //soldout
                myViewHolder.btn_soldout.setBackgroundResource(R.drawable.round_btn_soldout);
                myViewHolder.btn_middle.setBackgroundResource(R.drawable.round_btn_soldout);
                myViewHolder.btn_full.setBackgroundResource(R.drawable.round_btn_soldout);
            }


            myViewHolder.btn_soldout.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //mListener.onClickedLevelButton(2,lineupNumber);
                    updateCountLevel(2,lineupNumber,myViewHolder);
                }
            });

            myViewHolder.btn_middle.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //mListener.onClickedLevelButton(1,lineupNumber);
                    updateCountLevel(1,lineupNumber,myViewHolder);
                }
            });
            myViewHolder.btn_full.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //mListener.onClickedLevelButton(0,lineupNumber);
                    updateCountLevel(0,lineupNumber,myViewHolder);

                }
            });



        } else{
            myViewHolder.btn_register.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                    bottomSheetDialog.setOnDismissListener(onDismissListener);
                    bottomSheetDialog.setShopNumber(shopNumber);
                    bottomSheetDialog.setShowcaseLocation(pos/colCountperLine,pos%colCountperLine);
                    bottomSheetDialog.show(((ShopLineup)context).getSupportFragmentManager(),"botton sheet");
                }
            });
        }


    }



    private void updateCountLevel(int level, int lineupNumber, RecyclerView.ViewHolder holder){
        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        final int finalLevel = level;
        Call<Boolean> call = MacaronApi.service.updateLevel(lineupNumber,level);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body()) {
                    String s = "";
                    if (finalLevel == 2) {
                        myViewHolder.btn_soldout.setBackgroundResource(R.drawable.round_btn_soldout);
                        myViewHolder.btn_middle.setBackgroundResource(R.drawable.round_btn_soldout);
                        myViewHolder.btn_full.setBackgroundResource(R.drawable.round_btn_soldout);
                        s = "(SOLD OUT)";
                    } else if (finalLevel == 1) {
                        myViewHolder.btn_soldout.setBackgroundResource(R.drawable.round_btn_middle);
                        myViewHolder.btn_middle.setBackgroundResource(R.drawable.round_btn_middle);
                        myViewHolder.btn_full.setBackgroundResource(R.drawable.round_btn_soldout);
                        s = "(보통)";
                    } else {
                        myViewHolder.btn_soldout.setBackgroundResource(R.drawable.round_btn_full);
                        myViewHolder.btn_middle.setBackgroundResource(R.drawable.round_btn_full);
                        myViewHolder.btn_full.setBackgroundResource(R.drawable.round_btn_full);
                        s = "(많음)";
                    }

                    Toast.makeText(context, "재고 업데이트 " + s + " 성공", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "재고 업데이트 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(context, "재고 업데이트 실패", Toast.LENGTH_SHORT).show();
            }

        });
    }




/*    //callback
    private GridRecyclerViewAdapter.OnClickLevelListener mListener;

    interface OnClickLevelListener {
        void onClickedLevelButton(int level, int lineupNumber);
    }

    public void setOnClickLevelListener(GridRecyclerViewAdapter.OnClickLevelListener listener){
        mListener = listener;
    }*/





}
