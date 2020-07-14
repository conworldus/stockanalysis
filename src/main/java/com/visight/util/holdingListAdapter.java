package com.visight.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.visight.R;
import com.visight.data.Global;
import com.visight.data.Portfolio;
import com.visight.data.StockHolding;
import com.visight.data.StockTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class holdingListAdapter extends BaseExpandableListAdapter
{
    HashMap<String, ArrayList<StockTransaction>> holdings;
    ArrayList<StockHolding> holdingArray;
    Portfolio p;
    Context context;
    public holdingListAdapter(Context context, int portfolio)
    {
        p  = Global.myPortfolios.get(portfolio);
        if(p!=null)
        {
            holdings = p.getAllHoldings();
            holdingArray = p.getHolding();
        }
        this.context = context;
    }

    public void invalidate()
    {
        holdingArray = p.getHolding();
    }


    @Override
    public int getGroupCount()
    {
        if(holdings==null)
            return 0;
        return holdings.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        if(holdings==null)
            return 0;
        if(groupPosition>=holdings.size())
            return 0;
        String ticker = holdingArray.get(groupPosition).getTicker();
        return Objects.requireNonNull(holdings.get(ticker)).size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return holdingArray.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return Objects.requireNonNull(holdings.get(holdingArray.get(groupPosition).getTicker())).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return groupPosition*1000+childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if(view ==null)
            view = LayoutInflater.from(context).inflate(R.layout.adapter_holding_parent, null);
        TextView companyName = view.findViewById(R.id.company_name),
                ticker = view.findViewById(R.id.company_ticker),
                current_price = view.findViewById(R.id.current_price),
                average_price = view.findViewById(R.id.average_price),
                total_cost = view.findViewById(R.id.totalCost),
                total_value = view.findViewById(R.id.current_value),
                gain_loss = view.findViewById(R.id.gain_loss);


        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}
