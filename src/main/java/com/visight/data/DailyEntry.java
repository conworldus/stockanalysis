package com.visight.data;
import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static com.visight.data.Global.dateFormat;

public class DailyEntry
{
    private Date date;
    private double open, close, low, high;
    private long volume;

    @SuppressLint("SimpleDateFormat")
    public DailyEntry(String date, double open, double close, double low, double high, long volume)
    {
        try
        {
            this.date = (new SimpleDateFormat(dateFormat)).parse(date);
        }catch (ParseException e)
        {
            e.printStackTrace();
        }
        this.open = open;
        this.close=close;
        this.low = low;
        this.high =high;
        this.volume = volume;
    }

    public Date getDate()
    {
        return date;
    }

    public String getDateString(Locale loc)
    {
        return DateFormat.getDateInstance(DateFormat.MEDIUM, loc).format(date);
    }

    String getDateString()
    {
        return DateFormat.getDateInstance().format(date);
    }

    public double getOpen()
    {
        return open;
    }

    public double getClose()
    {
        return close;
    }

    public double getLow()
    {
        return low;
    }
    public double getFlux()
    {
        return high-low;
    }
    public double getHigh()
    {
        return high;
    }

    public long getVolume()
    {
        return volume;
    }
}
