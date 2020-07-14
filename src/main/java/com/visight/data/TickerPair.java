package com.visight.data;

import android.annotation.SuppressLint;

public class TickerPair
{
    public String ticker;
    public String companyName;

    public TickerPair(String ticker, String companyName)
    {
        this.ticker = ticker;
        this.companyName = companyName;
    }

    public double percent=0;

    @SuppressLint("DefaultLocale")
    public String percentDisp()
    {
        return String.format("%.2f", percent*100)+"%";
    }
}
