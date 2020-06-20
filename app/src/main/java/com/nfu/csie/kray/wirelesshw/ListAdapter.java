package com.nfu.csie.kray.wirelesshw;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private LayoutInflater mLayInf;
    private ArrayList<WifiList> APlist;

    public ListAdapter(Context context, ArrayList<WifiList> list){
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        APlist = list;
    }

    @Override
    public int getCount() {
        return APlist.size();
    }

    @Override
    public Object getItem(int position) {
        return APlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //設定與回傳 convertView 作為顯示在這個 position 位置的 Item 的 View。
        View v = mLayInf.inflate(R.layout.listview_item, parent, false);

        TextView ssid_tv = (TextView) v.findViewById(R.id.ssid);
        TextView mac_tv = (TextView) v.findViewById(R.id.mac);
        TextView level_tv = (TextView) v.findViewById(R.id.Signal_level);

        ssid_tv.setText(APlist.get(position).SSID);
        mac_tv.setText(APlist.get(position).MacAddr);
        level_tv.setText(String.valueOf(APlist.get(position).level));

        return v;
    }
}
