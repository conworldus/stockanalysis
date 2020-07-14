package com.visight.util;
import android.os.AsyncTask;
import android.util.Log;

import com.visight.data.Global;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequest extends AsyncTask<String, Integer, String>
{
    public boolean compact = false;
    public interface ConcurrentProcedures
    {
        void PreExec();
        void Update();
        void PostExec(String... params);
    }
    public ConcurrentProcedures procs;
    public static enum SearchType
    {
        COMPANYINFO,
        TIMESERIES,
        CURRENTPRICE
    }

    public void setType(SearchType type)
    {
        this.type=type;
    }
    private SearchType type = SearchType.TIMESERIES; //this is default
    private String url = Global.alphavantageURL;
    @Override
    protected String doInBackground(String... ticker)
    {
        try
        {
            switch (type)
            {
                case COMPANYINFO:
                    url = setSymbolSearchParameters(url, ticker[0]);
                    break;
                case TIMESERIES:
                    url = setTimeSeriesParameters(url, ticker[0]);
                    break;
                case CURRENTPRICE:
                    url = setCurrentPriceParameters(url, ticker[0]);
                    break;
            }
            URL sUrl = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) sUrl.openConnection();
            //conn.setRequestMethod("GET");
            //conn.setDoOutput(true);
           // conn.connect();
            StringBuilder builder = new StringBuilder();
            if(conn.getResponseCode()== HttpsURLConnection.HTTP_OK)
            {
                Log.e("OK", "OK");
                BufferedInputStream str = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(str));
                String seq = "";
                while((seq=reader.readLine())!=null)
                {
                    builder.append(seq);

                }
                while((seq = reader.readLine())!=null)
                {
                    builder.append(seq);
                }
                if(type==SearchType.CURRENTPRICE)
                    Log.e("AA", builder.toString());
                return builder.toString();
            }
            else
            {
                Log.e("No Res", conn.getResponseMessage());
                return "";
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if(procs!=null)
            procs.PreExec();
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        if(procs!=null)
            procs.PostExec(s);
    }

    private String setCurrentPriceParameters(String url, String s)
    {
        url+= addParameters(Global.function, Global.global_quote);
        url+="&";
        url+= addParameters(Global.symbol, s);
        url+="&";
        url+=addParameters(Global.apikey, Global.alphavantageKEY);
        return url;
    }

    private String setSymbolSearchParameters(String url, String keywords)
    {
        url+= addParameters(Global.function, Global.symbol_search);
        url+="&";
        url+= addParameters(Global.keywords, keywords);
        url+="&";
        url+=addParameters(Global.apikey, Global.alphavantageKEY);
        return url;
    }

    private String setTimeSeriesParameters(String url, String ticker)
    {
        url+= addParameters(Global.function, Global.time_series_daily);
        url+="&";
        url+= addParameters(Global.symbol, ticker);
        url+="&";
        if(!compact)
            url+= addParameters(Global.outputsize, Global.full);
        else url+= addParameters(Global.outputsize, Global.compat);
        url+="&";
        url+=addParameters(Global.apikey, Global.alphavantageKEY);
        return url;
    }

    private String addParameters(String name, String val)
    {
        return name+"="+val;
    }
}
