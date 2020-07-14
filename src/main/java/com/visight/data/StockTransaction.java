package com.visight.data;

import java.util.Date;

public class StockTransaction
{
    private Date datetime;
    private String ticker;
    private long quantity;  //positive for buys, negative for sells
    private double salePrice;

    public StockTransaction(Date datetime, String ticker, long quantity, double salePrice)
    {
        this.datetime = datetime;
        this.ticker = ticker;
        this.quantity = quantity;
        this.salePrice = salePrice;
    }
    public Date getDatetime()
    {
        return datetime;
    }

    public String getTicker()
    {
        return ticker;
    }

    public long getQuantity()
    {
        return quantity;
    }

    public double getSalePrice()
    {
        return salePrice;
    }
}
