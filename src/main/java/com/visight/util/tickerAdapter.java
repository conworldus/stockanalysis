package com.visight.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.visight.R;
import com.visight.data.DailyEntry;
import com.visight.data.Global;
import com.visight.data.TickerPair;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class tickerAdapter extends ArrayAdapter<TickerPair>
{
    Context context;
    int ResourcePt;
    public tickerAdapter(@NonNull Context context, int resource)
    {
        super(context, resource);
        this.context = context;
        ResourcePt = resource;
    }

    @Override
    public int getCount()
    {
        return Global.NYSE_Stock_List.size();
    }

    @Nullable
    @Override
    public TickerPair getItem(int position)
    {
      /*  if(position<Global.NYSE_Stock_List.size())
        {
            Object [] keySet = Global.NYSE_Stock_List.keySet().toArray();
            String ticker = keySet[position].toString();
            Log.w("Ticker", ticker);
            String companyName = Global.NYSE_Stock_List.get(ticker);
            TickerPair pair = new TickerPair(ticker, companyName);
            return pair;
        }*/
        return Global.NYSE_Stock_Array.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView;
        if(view==null)
            view = LayoutInflater.from(context).inflate(R.layout.adapter_stock_list, null);

        TextView tickerTitle = view.findViewById(R.id.ticker),
                companyName = view.findViewById(R.id.company_name);

        TickerPair pair = getItem(position);
        if(pair!=null)
        {
            tickerTitle.setText(pair.ticker);
            companyName.setText(pair.companyName);
        }
        return view;
    }
}
