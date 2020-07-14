package com.visight.data;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class regressionFormula
{
    private double a, b;
    private boolean ready=false;
    regressionFormula()
    {
        a=0;
        b=0;
    }

    public double getA()
    {
        return a;
    }

    public double getB()
    {
        return b;
    }

    public double getExpectedPrice(int day)
    {
        return day*a+b;
    }

    public double getFlux(int day, double price)
    {
        return price - (day*a+b);
    }

    @SuppressLint("DefaultLocale")
    String toStringFormula()
    {
        if(!ready)
            return null;

        String aString = String.format("%.4f", a);
        if(a<0)
            aString = "<font color='red'>"+aString+"</font>";
        else
            aString = "<font color='green'>"+aString+"</font>";

        String bString = " + <font color='green'>"+String.format("%.4f", b)+"</font>";
        return "f(x)="+aString+"x"+bString;
    }

    void calculateFromArray(ArrayList<Double> arrayList)
    {
        Log.e("Regression", "Calculating");
        //reverse?
        Collections.reverse(arrayList);
        int size = arrayList.size();
        double x = SumX(size),
                x2 = SumX2(size),
                y= SumY(arrayList),
                xy = SumXY(arrayList);

        double bTop = size*xy - x*y;
        double bBottom = size*x2 - x*x;
        a = bTop/bBottom;

        double aTop = y-a*x;
        double aBottom = size;

        b = aTop/aBottom;
        ready=true;
        Log.e("Data Size", size+"");
        Log.e("Smallest", Collections.min(arrayList)+"");
        Log.e("Largest", Collections.max(arrayList)+"");
    }

    private double SumY(ArrayList<Double> data)
    {
        double ret=0;
        for(Double d:data)
        {
            ret+=d;
        }
        return ret;
    }

    private int SumX(int Size)
    {
        int ret = 0;
        for(int i=0; i<Size; i++)
        {
            ret+=i;
        }
        return ret;
    }

    private int SumX2(int Size)
    {
        int ret = 0;
        for (int i=0; i<Size; i++)
        {
            ret+=(i*i);
        }
        return ret;
    }

    private double SumXY(ArrayList<Double> data)
    {
        double ret = 0;
        for(int i=0; i<data.size(); i++)
            ret+=(i*data.get(i));
        return ret;
    }

    public double difference(double price, int day)
    {
        double expected = a*day+b;
        return price - expected;
    }
}
