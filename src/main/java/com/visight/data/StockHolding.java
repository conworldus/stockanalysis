package com.visight.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class StockHolding  //create a list of holdings
{
    private String ticker;
    private long amount;
    private double purchasePrice;
    private Date purchaseDate;

    public StockHolding(String ticker, long amount, double purchasePrice, Date purchaseDate)
    {
        this.ticker = ticker;
        this.amount = amount;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
    }

    public StockHolding(String ticker)
    {
        this.ticker = ticker;
        this.amount = 0;
        this.purchaseDate = new Date();
        this.purchasePrice = 0;
        //this.purchaseDate = purchaseDate;
    }

    public String getTicker()
    {
        return ticker;
    }

    public long getAmount()
    {
        return amount;
    }

    public double getPurchasePrice()
    {
        return purchasePrice;
    }

    public Date getPurchaseDate()
    {
        return purchaseDate;
    }

    public void setAmount(long amount)
    {
        this.amount = amount;
    }

    public void setPurchaseDate(Date purchaseDate)
    {
        this.purchaseDate = purchaseDate;
    }

    public void setPurchasePrice(double purchasePrice)
    {
        this.purchasePrice = purchasePrice;
    }
}
