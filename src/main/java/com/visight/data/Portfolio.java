package com.visight.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Portfolio
{
    private String portfolioName;
    private Date creationDate;
    private HashMap<String, ArrayList<StockTransaction>> holdings;
    private double USDBalance;

    public static class ResultCodes
    {
        public static final int ERR_INSUFFICIENT_BALANCE = 100,
                        ERR_INSUFFICIENT_SHARES = 101,
                        SUCCESS = 102;
    }

    public Portfolio(String name, double balance)
    {
        portfolioName = name;
        this.USDBalance = balance;
        holdings = new HashMap<>();
        creationDate = new Date();
    }

    public HashMap<String, ArrayList<StockTransaction>> getAllHoldings()
    {
        return holdings;
    }

    public int addTransaction(StockTransaction transaction)
    {
        if(!holdings.containsKey(transaction.getTicker()))
        {
            if(transaction.getQuantity()>0)
            {
                if(USDBalance<transaction.getQuantity()*transaction.getSalePrice())
                    return ResultCodes.ERR_INSUFFICIENT_BALANCE; //not enough money
                ArrayList<StockTransaction> tran = new ArrayList<>();
                tran.add(transaction);
                holdings.put(transaction.getTicker(), tran);
                return ResultCodes.SUCCESS;
            }
            else
                return ResultCodes.ERR_INSUFFICIENT_SHARES;  //Can't sell what you don't have
        }
        ArrayList<StockTransaction> tran = holdings.get(transaction.getTicker());
        //get shares count
        long quantity = 0;
        for(StockTransaction t:tran)
            quantity+=t.getQuantity();
        if((transaction.getQuantity()+quantity)<0)
            return ResultCodes.ERR_INSUFFICIENT_SHARES;
        tran.add(transaction);
        return ResultCodes.SUCCESS;
    }


    public ArrayList<StockHolding> getHolding()
    {
        ArrayList<StockHolding> ret = new ArrayList<>();
        for (String ticker : holdings.keySet())
        {
            ArrayList<StockTransaction> transaction = holdings.get(ticker);
            StockHolding nHolding = new StockHolding(ticker);
            if(transaction!=null)
            {
                long quantity = 0;
                double consideration = 0;
                for (StockTransaction tran : transaction)
                {
                    if (nHolding.getPurchaseDate().before(tran.getDatetime()))
                        nHolding.setPurchaseDate(tran.getDatetime());  //

                    quantity+=tran.getQuantity();
                    consideration += tran.getQuantity()*tran.getSalePrice();
                }
                nHolding.setAmount(quantity);
                nHolding.setPurchasePrice(consideration/quantity);  //Average Price
            }
        }

        return ret;
    }



    public String getPortfolioName()
    {
        return portfolioName;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }
    public double getUSDBalance()
    {
        return USDBalance;
    }
}
