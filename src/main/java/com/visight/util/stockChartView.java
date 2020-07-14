package com.visight.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.visight.R;
import com.visight.data.StockEntry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class stockChartView extends View
{
    public interface extraClickDisp
    {
        void perform(String information);
    }

    private extraClickDisp perform;

    public void setClickEvent(extraClickDisp perform)
    {
        this.perform = perform;
    }

    private float cX=-1;
    private int clickIndex = -1;
    private double cY=0;

    private float highPrice, lowPrice;

    private int axisColor=0, dotColor=0, plotColor=0, distanceColor=0;
    private float chartWidth = 0, chartHeight=0;
    Paint myPaint = new Paint();
    int margin = 100;

    private float stepSize = 0;

    private double a=-1, b=-1;
   // private ArrayList<Double> plotPoints = new ArrayList<>();
    private HashMap<String, ArrayList<Double>> plotCollection = new HashMap<>();

    private ArrayList<Float> stepPoints = new ArrayList<>();
    public stockChartView(Context context)
    {
        super(context);
    }

    public void setPlot(ArrayList<Double> plot)
    {
        plotCollection.clear(); //first, clear the hashmap
        ArrayList<Double> nPlot = new ArrayList<>(plot);
        Collections.reverse(nPlot);
        plotCollection.put("Price", nPlot);  // clone the data just in case
        plotCollection.put("MovAvg", StockEntry.getMovingAverage(nPlot, 150));  //temporarily set 150 day moving average
        invalidate();
    }

    public void addPlot(String Name, ArrayList<Double> data, boolean Reverse)
    {
        ArrayList<Double> d = new ArrayList<>(data);
        if(Reverse)
            Collections.reverse(d);
        plotCollection.put(Name, d);
    }

    public stockChartView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initColors(attrs);
    }

    public stockChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initColors(attrs);
    }

    public stockChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        initColors(attrs);
    }

    private void initColors(AttributeSet attrs)
    {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.stockChartView);
        axisColor = a.getColor(R.styleable.stockChartView_axisColor, 0);
        dotColor = a.getColor(R.styleable.stockChartView_dotColor, 0);
        plotColor = a.getColor(R.styleable.stockChartView_plotColor, 0);
        distanceColor = a.getColor(R.styleable.stockChartView_distanceMeasureColor, 0);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        chartWidth = xNew;
        chartHeight = yNew;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(plotCollection.size()==0)
            return false;
        ArrayList<Double> plotPoints = plotCollection.get("Price");
        if(plotPoints==null)
            return false;

        float x = event.getX();
        int index  = 0;
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            //find closest index
            if(stepSize==0)
                return false;
            index = Math.round((x - margin) / stepSize);
            if(index<0)
                index=0;

            if(index>=plotPoints.size())
                index=plotPoints.size()-1;


            //now draw a picture
            cX = index*stepSize+margin;
            clickIndex = index;
            cY = plotPoints.get(index);  //this is the price
            //lets get the coords

            float diff = highPrice - lowPrice; //distance
            float p = 1-(float)(cY-lowPrice)/diff;
            cY = (chartHeight-margin*2)*p+margin;

        }

        if(perform!=null)
        {
            String information;
            //expected price
            //actual price
            //deviation from average difference

            //first get average difference
            double sum = 0;
            for (int i = 0; i < plotPoints.size(); i++)
                sum+=Math.abs(plotPoints.get(i)-(a*i+b));
            double cPrice = plotPoints.get(index);
            @SuppressLint("DefaultLocale") String cps = String.format("%.2f", cPrice);
            @SuppressLint("DefaultLocale") String avg = String.format("%.2f", sum/plotPoints.size());
            @SuppressLint("DefaultLocale") String exp = String.format("%.2f", (a*index+b));
            double percent =  (cPrice-(a*index+b))/(sum/plotPoints.size());
            @SuppressLint("DefaultLocale") String pstr = "%"+String.format("%.2f", percent*100);

            information = "Current Price: "+cps+"\n"+
                    "Expected Price: "+exp+"\n"+
                    "Average Difference: "+avg+"\n"+
                    "Deviation: "+pstr;
            perform.perform(information);
        }
        //this.invalidate();
        return false;
    }


    Typeface face = ResourcesCompat.getFont(getContext(), R.font.lmroman7regular);
    double low, high;
    ArrayList<Double> plotPoints;
    ArrayList<Double> avgPoints;

    @Override
    protected void onDraw(Canvas canvas)
    {
        Log.e("Ondraw", "Called");
        super.onDraw(canvas);
        myPaint.setColor(axisColor);
        myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        myPaint.setStrokeWidth(3);
        myPaint.setTextSize(36);
        myPaint.setTypeface(face);
        canvas.drawLine(margin-25, margin-25, margin-25, (chartHeight-margin)+25, myPaint);
        canvas.drawLine(margin-25, (chartHeight-margin)+25, (chartWidth-margin)+25, (chartHeight-margin)+25, myPaint);
        canvas.drawText("Price", margin-75, margin-50, myPaint);
        canvas.drawText("Day", (chartWidth-(margin*2)), chartHeight-margin+75, myPaint);
        if(plotCollection.size()>0)
        {
            plotPoints = plotCollection.get("Price");
            avgPoints = plotCollection.get("MovAvg");
            boolean avg=true;
            if(avgPoints==null)
                avg=false;
            if(plotPoints==null)
                return;
            if(plotPoints.size()>0)
            {
                low = Collections.min(plotPoints, doubleComparator);
                high = Collections.max(plotPoints, doubleComparator);
            }
            else
            {
                low=0; high=100;
            }
            highPrice = (float)high;
            lowPrice = (float)low;
            if(plotPoints.size()>0)
            {
                //calculate step size
                stepSize = (chartWidth-margin*2)/plotPoints.size();  //match the size to data

                double span = high-low;
                float preX=0, preY=0;
                float preAY=0;
                for (int i = 0; i < plotPoints.size(); i++)
                {
                    myPaint.setColor(Color.WHITE);
                    double price = plotPoints.get(i);
                    float plotX = margin+stepSize*i;
                    stepPoints.add(plotX);
                    double distance = price - low;
                    float percentile = 1-(float)(distance/span);
                    //               Log.e("P", price+"");
                    float plotY = (chartHeight-margin*2)*percentile+margin;
                    canvas.drawPoint(plotX, plotY, myPaint);
                    if(i!=0)
                    {
                        canvas.drawLine(preX, preY, plotX, plotY, myPaint);
                    }

                    if(avg)
                    {
                        myPaint.setColor(Color.BLUE);
                        double aPrice = avgPoints.get(i);
                        if(aPrice!=0)
                        {
                            //plotX is the same
                            //preX is the same
                            distance = aPrice - low;
                            percentile = 1 - (float) (distance / span);
                            //               Log.e("P", price+"");
                            float plotAY = (chartHeight - margin * 2) * percentile + margin;
                            if(preAY!=0)
                            {
                                canvas.drawPoint(plotX, plotAY, myPaint);
                                canvas.drawLine(preX, preAY, plotX, plotAY, myPaint);
                            }
                            preAY = plotAY;
                        }
                    }
                    preX = plotX;
                    preY = plotY;
                }

                /*--------------------------plot the regression line*************/
                //No need for thread?
                float startYP = (float)b;
                float endYP = (float)(b+(plotPoints.size()-1)*a);

                float startY = getYCoord(startYP, high, low);
                float endY = getYCoord(endYP, high, low);

                Log.e("Y", startY+" "+endY);
                Log.e("YP", startYP+" "+endYP);
                myPaint.setColor(Color.YELLOW);
                canvas.drawLine(margin, startY, chartWidth-margin, endY, myPaint);
                /*--------------------------plot the regression line*************/
            }

            if(cX>0)
            {
                //get expect
                float eY = getYCoord((a*clickIndex+b), high, low);
                myPaint.setColor(Color.CYAN);
                canvas.drawLine(cX, eY, cX, (float)cY, myPaint);
                //canvas.drawLine()
            }
        }
    }

    private float getYCoord(double price, double high, double low)
    {
        int margin = 100;
        double span = high-low;
        double distance = price - low;
        float percentile = 1-(float)(distance/span);
        return (chartHeight-margin*2)*percentile+margin;
    }

    public void setLine(double a, double b)
    {
        this.a = a;
        this.b = b;
    }



    Comparator<Double> doubleComparator = new Comparator<Double>()
    {
        @Override
        public int compare(Double o1, Double o2)
        {
            if(o1>o2)
                return 1;
            else if(o1<o2)
                return -1;
            else
                return 0;
        }
    };

}
