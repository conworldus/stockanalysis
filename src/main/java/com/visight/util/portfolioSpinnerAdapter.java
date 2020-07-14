package com.visight.util;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.visight.R;
import com.visight.data.Global;
public class portfolioSpinnerAdapter extends BaseAdapter
{
    Context context;
    public portfolioSpinnerAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return Global.myPortfolios.size();
    }

    @Override
    public Object getItem(int position)
    {
        return Global.myPortfolios.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if(view==null)
            view = LayoutInflater.from(context).inflate(R.layout.adapter_spinner_text, null);
        TextView textView = view.findViewById(R.id.mainText);
        textView.setText(Global.myPortfolios.get(position).getPortfolioName());
        return view;
    }
}
