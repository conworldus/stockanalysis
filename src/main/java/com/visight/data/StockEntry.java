package com.visight.data;

import java.util.ArrayList;

public class StockEntry
{
    //    private Date startingDate;
//    private int dataSize;
    public final static int OPEN = 100, CLOSE = 101, LOW=102, HIGH=103, ABS_ERROR =104;
    private String exchangeName;
    private String countryName;
    private String stockTicker;
    private ArrayList<DailyEntry> dataSeries;
 //   private ArrayList<Double> dailyFlux = new ArrayList<>();
    private regressionFormula myFormula = new regressionFormula();
    public StockEntry(String exchangeName, String countryName, String stockTicker)
    {
        this.exchangeName = exchangeName;
        this.countryName = countryName;
        this.stockTicker = stockTicker;
    }
    public regressionFormula getFormula()
    {
        return myFormula;
    }
    public static ArrayList<Double> getMovingAverage(ArrayList<Double> source, int dayCount)
    {
        ArrayList<Double> ret = new ArrayList<>();
        if(dayCount < 1 || source.size()<dayCount)
        {
            for(int i=0; i<source.size(); i++)
            {
                ret.add(0d); //add all zeroes
            }
            return ret;
        }
        //maintain a temporary Sum
        double tempSum = 0;
        for(int i=0; i<source.size(); i++)
        {
            if(i<dayCount)
            {
                //add the closing price
                ret.add(0d); //value is zero for anything before the average starts
            }
            else
            {
                //first calculate the average
                ret.add(tempSum/dayCount);  //this is the moving average
                tempSum-=source.get(i-dayCount); //subtract the first number
            }
            tempSum+=source.get(i);
        }
        return ret;
    }

    public double getAverageMove()
    {
        int size = dataSeries.size()-1;
        double total=0;
        for(int i=1; i<size; i++)
            total+=(dataSeries.get(i).getClose()-dataSeries.get(i-1).getClose());

        return total/size;
    }

    public String getExchangeName()
    {
        return exchangeName;
    }

    public String getCountryName()
    {
        return countryName;
    }

    public String getStockTicker()
    {
        return stockTicker;
    }

    public ArrayList<DailyEntry> getDataSeries()
    {
        return dataSeries;
    }

    public void initDataSeries()
    {
        dataSeries = new ArrayList<>();
    }

    public int dataSize()
    {
        if (dataSeries == null)
            return 0;
        else return dataSeries.size();
    }

    public String startingDate()
    {
        if (dataSeries == null)
            return null;
        else return dataSeries.get(0).getDateString();
    }

    public String getOpenPriceForumla()
    {
        ArrayList<Double> openPrices = getSerialPrice(OPEN);
        if(openPrices==null)
            return null;
        myFormula.calculateFromArray(openPrices);
        return myFormula.toStringFormula();
    }

    public String getClosePriceForumla()
    {
        ArrayList<Double> closePrices = getSerialPrice(CLOSE);
        if(closePrices==null)
            return null;
        myFormula.calculateFromArray(closePrices);
        return myFormula.toStringFormula();
    }

    public ArrayList<Double> getSerialPrice(int TYPE)
    {
        if (dataSeries == null)
            return null;
        ArrayList<Double> ret = new ArrayList<>();
        switch (TYPE)
        {
            case OPEN:
                for (DailyEntry d : dataSeries)
                    ret.add(d.getOpen());
                break;
            case CLOSE:
                for (DailyEntry d : dataSeries)
                    ret.add(d.getClose());
                break;
            case HIGH:
                for (DailyEntry d : dataSeries)
                    ret.add(d.getHigh());
                break;
            case LOW:
                for (DailyEntry d : dataSeries)
                    ret.add(d.getLow());
                break;
            case ABS_ERROR:  //for closing priec
                for (int i=0; i<dataSeries.size(); i++)
                    ret.add(Math.abs(myFormula.difference(dataSeries.get(i).getClose(), i)));
                break;
        }
        return ret;
    }
}
