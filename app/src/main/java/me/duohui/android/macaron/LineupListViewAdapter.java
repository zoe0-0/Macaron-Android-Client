package me.duohui.android.macaron;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.duohui.android.macaron.Model.LineupMenu;

public class LineupListViewAdapter extends BaseAdapter {

    private static final String TAG = "LINEUPLISTVIEWADAPTER";

    private List<LineupMenu> list = new ArrayList<>();
    private TextView menu_name, menu_price, menu_level;

    public LineupListViewAdapter(){
        super();
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItemList(List<LineupMenu> list){
        this.list = list;
        Log.d(TAG, "addItemList: list"+this.list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView: ");
        final Context context = parent.getContext();
        final LineupMenu lineupMenu = list.get(position);
        
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lineup_listview_item, parent, false);
        }

        Log.d(TAG, "getView: " + lineupMenu.toString());
        menu_name =  convertView.findViewById(R.id.menu_name);
        menu_price = convertView.findViewById(R.id.menu_price);
        menu_level = convertView.findViewById(R.id.menu_level);


        menu_name.setText(lineupMenu.getMenuName());
        menu_price.setText(lineupMenu.getPrice()+"원");

        if(lineupMenu.getLevel()==0) menu_level.setText("많음");
        else if(lineupMenu.getLevel()==1) menu_level.setText("보통");
        else menu_level.setText("SOLD OUT");


        return convertView;
    }
}
