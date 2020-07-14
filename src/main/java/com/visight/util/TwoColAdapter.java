package com.visight.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.visight.R;
import com.visight.data.TickerPair;

import java.util.HashMap;

public class TwoColAdapter extends BaseAdapter
{
    private Context context;
    private HashMap<String, TickerPair> data;

    public TwoColAdapter(Context context, HashMap<String, TickerPair> data)
    {
        this.context = context;
        this.data = data;
    }

    public TwoColAdapter(Context context, HashMap<String, String> data, int Flag)
    {
        this.context = context;
        this.data = new HashMap<>();
        for (String n : data.keySet())
        {
            TickerPair pair = new TickerPair(n, data.get(n));
            this.data.put(n, pair);
        }
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public TickerPair getItem(int position)
    {
        Object [] keys = data.keySet().toArray();
        return data.get(keys[position].toString());
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if(view==null)
            view = LayoutInflater.from(context).inflate(R.layout.adapter_list_two_col, null);
        TextView title = view.findViewById(R.id.two_col_title),
                text = view.findViewById(R.id.two_col_text);
        Object [] keys = data.keySet().toArray();
        title.setText(keys[position].toString());
        text.setText(getItem(position).companyName);

        if(getItem(position).percent!=0)
        {
            TextView per = view.findViewById(R.id.two_col_center);
            per.setText(getItem(position).percentDisp());
            per.setVisibility(View.VISIBLE);
            if(getItem(position).percent>0)
                per.setTextColor(Color.GREEN);
            else
                per.setTextColor(Color.RED);
        }
        return view;
    }
}
