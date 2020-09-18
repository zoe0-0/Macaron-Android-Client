package me.duohui.android.macaron;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.duohui.android.macaron.Api.MacaronApi;
import me.duohui.android.macaron.Model.Menu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListViewAdapter extends BaseAdapter {

    private static final String TAG = "ListViewAdapter";


    private ImageButton btn_mark, btn_delete;
    private TextView name, price;


    private Context context;
    private List<Menu> listViewItemList = new ArrayList<Menu>() ;


   //callback
    private OnBookmarkUpdatedListener mListener;

    interface OnBookmarkUpdatedListener {
         void onBookmarkUpdated(int index);
    }

    public void setOnBookmarkUpdatedListener(OnBookmarkUpdatedListener listener){
          mListener = listener;
    }


    public ListViewAdapter(Context context){
        super();
        this.context = context;
    }


    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final Menu menu = listViewItemList.get(position);


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shop_menu_item, parent, false);
        }

        name =  convertView.findViewById(R.id.name);
        price =  convertView.findViewById(R.id.price);

        btn_mark = convertView.findViewById(R.id.mark);
        btn_mark.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBookmark(position);
            }
        });
        btn_delete = convertView.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMenu(position);
            }
        });

        // 아이템 내 각 위젯에 데이터 반영
        if(menu.getBookmark()==0) btn_mark.setImageResource(R.drawable.empty_star);
        else btn_mark.setImageResource(R.drawable.filled_star);
        name.setText(menu.getMenuName());
        price.setText(menu.getPrice()+" 원");

        return convertView;
    }


    public void addItemList(List<Menu> listViewItemList) {
        this.listViewItemList = listViewItemList;
    }

    public void addItem(Menu menu){
        listViewItemList.add(menu);
        //dataChange();
    }

    public void removeItem(int position){
        listViewItemList.remove(position);
        dataChange();
    }


    public void dataChange() {
         this.notifyDataSetChanged();
    }


    //별(즐겨찾기) 버튼 눌렀을 때 서버로 update 요청 보내고, update 성공할 경우에는 list에서도 별 이미지 업데이트 해주기
    private void updateBookmark(int position){
         final int pos = position;
         Menu menu  = listViewItemList.get(pos);
         final int bookmark;
         if(menu.getBookmark()==0) bookmark=1;
         else bookmark=0;
         menu.setBookmark(bookmark);
         Call<Boolean> call = MacaronApi.service.updateMenu(menu);
         call.enqueue(new Callback<Boolean>() {
             @Override
             public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                 if(response.body()){
                     Toast.makeText(context,"업데이트 성공",Toast.LENGTH_SHORT).show();
                     listViewItemList.get(pos).setBookmark(bookmark);
                     //dataChange();
                     sortList(listViewItemList.get(pos).getMenuNumber());
                 }else{
                     Toast.makeText(context,"업데이트 실패",Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onFailure(Call<Boolean> call, Throwable t) {
                 Log.d(TAG, "onFailure: "+t.getMessage());
                 Toast.makeText(context,"업데이트 실패",Toast.LENGTH_SHORT).show();

             }
         });

    }

    //x버튼 눌렀을 때 서버로 delete 요청보내고, delete 성공할 경우에는 list에서 아이템 제거하기(removeItem)
    private void deleteMenu(int position){
        final int pos = position;
        int menuNumber = listViewItemList.get(pos).getMenuNumber();
        Call<Boolean> call = MacaronApi.service.deleteMenu(menuNumber);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.body()) {
                    removeItem(pos);
                    Toast.makeText(context,"메뉴 삭제 성공",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"메뉴 삭제 실패",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(context,"메뉴 삭제 실패",Toast.LENGTH_SHORT).show();
            }
        });
    }



    //즐겨찾기를 업데이트 할 경우. 리스트의 정렬 순서 셋팅 (order by bookmark desc, menu_number asc)
    private void sortList(int menuNumber){
        Comparator<Menu> comparator = new Comparator<Menu>() {
            @Override
            public int compare(Menu menu1, Menu menu2) {
                int ret = 0 ;

                if (menu1.getBookmark() < menu2.getBookmark()) {
                    ret = 1;
                }else if (menu1.getBookmark() == menu2.getBookmark()){
                    if(menu1.getMenuNumber()>menu2.getMenuNumber()) ret = 1;
                    else ret = -1;
                }else{
                    ret = -1;
                }


                return ret ;

            }
        } ;

        Collections.sort(listViewItemList, comparator) ;
        this.notifyDataSetChanged() ;
        setScrollView(menuNumber);

    }


    //TODO - scrollview 할 방법 찾기
    //즐겨찾기 업데이트 성공 후, 클릭한 메뉴의 인덱스번호를 서버로부터 받아와서 그 위치로 스크롤뷰 포커스 이동
   private void setScrollView(int menuNumber){
       Log.d(TAG, "!!!!!!!!!!!!!!!!menuNumber"+menuNumber);
        Call<Integer> call = MacaronApi.service.getMenuIndex(menuNumber);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    final int result = response.body();
                    if(result != -1){
                        Log.d(TAG, "!!!!!!!!!!!!!!!!indexNumber"+result);
                        //listView.smoothScrollToPosition(result);
                        mListener.onBookmarkUpdated(result);
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
   }


}
