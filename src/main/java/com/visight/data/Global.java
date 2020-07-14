package com.visight.data;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.visight.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Global
{
    static final String dateFormat = "yyyy-MM-dd";
    public static final String alphavantageURL = "https://www.alphavantage.co/query?";
    public static final String alphavantageKEYOLD = "TSA0DI4WX98AUWIY";
    public static final String alphavantageKEY = "M5S9FI7H9PMUYW8J";
    public static final String function = "function",
                        symbol = "symbol",
                        outputsize="outputsize",
                        datatype="datatype",
                        apikey ="apikey",
                        time_series_daily = "TIME_SERIES_DAILY",
                        symbol_search="SYMBOL_SEARCH",
                        global_quote = "GLOBAL_QUOTE",
                        keywords = "keywords",
                        compat = "compact",
                        full = "full",
                        json = "json",
                        csv = "csv";
    public static final String NA = "N/A";
    public static StockEntry currentEntry;
    public static HashMap<String, String> NYSE_Stock_List= new HashMap<>();
    public static ArrayList<TickerPair> NYSE_Stock_Array = new ArrayList<>();

    public static ArrayList<Portfolio> myPortfolios = new ArrayList<>();
    private static Portfolio cPortfolio = null;
    public interface onCreatePortfolio
    {
        void process(Portfolio param);
    }

    public static void addPortfolios(Context context, final onCreatePortfolio func)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_new_profil, null);
        final EditText name = view.findViewById(R.id.input_profile_name), amount = view.findViewById(R.id.input_profile_amount);
        Button create = view.findViewById(R.id.btn_create_profile);
        create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String nameStr = name.getEditableText().toString().trim(),
                        amountStr = amount.getEditableText().toString().trim();
                if(nameStr.length()==0)
                    nameStr = new Date().toString();
                if(amountStr.length()==0)
                    amountStr = "5000"; //default profile amount
                Portfolio nPortfolio = new Portfolio(nameStr, Double.parseDouble(amountStr));
                myPortfolios.add(nPortfolio); // size
                func.process(nPortfolio);
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    public static Portfolio getCurrentSelect()
    {
        return cPortfolio;
    }

    public static void setCurrentPortfolio(Portfolio p)
    {
        cPortfolio = p;
    }


    public static void loadPortfolios()
        {
        File f = new File(Environment.getDataDirectory(), "portfolio.json");
        if (!f.exists())
            try
            {
                boolean c = f.createNewFile();
            }
            catch (IOException e)
            {
                Log.e("Error", Objects.requireNonNull(e.getLocalizedMessage()));
                e.printStackTrace();
            }
    }

    public static void savePortfolios()
    {

    }
}
