package com.visight.ui.StockDetail;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.visight.R;
import com.visight.data.DailyEntry;
import com.visight.data.Global;
import com.visight.data.StockEntry;
import com.visight.data.StockTransaction;
import com.visight.util.TextPairView;
import com.visight.util.ValueHolder;
import com.visight.util.stockChartView;
import com.visight.util.HttpRequest;
import com.visight.util.TwoColAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class StockDetailFragment extends Fragment
{

    private StockEntry stockEntry;
    private StockDetailViewModel stockDetailViewModel;

  //  private MutableLiveData<String> formulaText = new MutableLiveData<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState)
    {
        stockDetailViewModel = ViewModelProviders.of(this).get(StockDetailViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_stock_detail, container, false);
        final TextView textView = root.findViewById(R.id.text_meta);
        final stockChartView sView = root.findViewById(R.id.myStockChart);
        final EditText ticker = root.findViewById(R.id.tickerInput);
        final ListView dataView = root.findViewById(R.id.metaDataList);

        if(this.getArguments()!=null&&this.getArguments().getString("Symbol")!=null)
        {
            String cSymbol = this.getArguments().getString("Symbol");
            assert cSymbol != null;
            Log.e("cSymbol", cSymbol);
            processStockData(cSymbol, root, sView, dataView, textView);
        }
        root.findViewById(R.id.enter).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String tickerStr = ticker.getEditableText().toString();
                processStockData(tickerStr, root, sView, dataView, textView);
            }
        });

        root.findViewById(R.id.trade).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(stockEntry!=null)
                {
                    AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_trade_stock, null);
                    final EditText tradeQuantity = view.findViewById(R.id.trade_quantity);
                    final RadioGroup tradeTypeSelect = view.findViewById(R.id.trade_type);
                    final ValueHolder.Double price = new ValueHolder.Double();
                    price.value = 0;
                    final TextPairView price_pair = view.findViewById(R.id.c_price);
                    final TextPairView total_pair = view.findViewById(R.id.c_value);
                    //now get current price
                    try
                    {
                         price.value = new JSONObject(new HttpRequest().execute(stockEntry.getStockTicker()).get()).getJSONObject("Global Quote").getDouble("05. price");
                         price_pair.setContent(String.valueOf(price.value));
                    } catch (InterruptedException | ExecutionException | JSONException e)
                    {
                        e.printStackTrace();
                        return;
                    }
                    final ValueHolder.Double totalDouble = new ValueHolder.Double();
                    totalDouble.value = 0;
                    tradeQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener()
                    {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus)
                        {
                            if(!hasFocus)
                            {
                                String c = tradeQuantity.getEditableText().toString().trim();
                                if(c.length()>0)
                                {
                                    totalDouble.value = Double.parseDouble(c)*price.value;
                                    total_pair.setContent(String.valueOf(totalDouble.value));
                                }
                            }
                        }
                    });

                    view.findViewById(R.id.trade_submit).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(price.value!=0)
                            {
                                int quantity = Math.abs(Integer.parseInt(tradeQuantity.getEditableText().toString()));
                                int checked = tradeTypeSelect.getCheckedRadioButtonId();
                                if (checked == R.id.radio_sell)
                                    quantity = -quantity;
                            }

                           // StockTransaction transaction = new StockTransaction(new Date(), stockEntry.getStockTicker(), quantity, //current price)
                        }
                    });
                    dialog.setView(view);
                    dialog.show();
                }
            }
        });
        stockDetailViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    textView.setText(Html.fromHtml(stockDetailViewModel.getText().getValue(), Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
                } else
                {
                    textView.setText(Html.fromHtml(stockDetailViewModel.getText().getValue()), TextView.BufferType.SPANNABLE);
                }
                if(sView!=null&&stockEntry!=null)
                {
                    sView.setLine(stockEntry.getFormula().getA(), stockEntry.getFormula().getB());
                    sView.setPlot(stockEntry.getSerialPrice(StockEntry.CLOSE));
                }
            }
        });

        return root;
    }
    private double openingAverage(ArrayList<DailyEntry> list)
    {
        if(list.size()==0)
            return 0;
        double sum=0;
        for (DailyEntry e : list)
        {
            sum+=e.getOpen();
        }
        return sum/list.size();
    }
    private double closingAverage(ArrayList<DailyEntry> list)
    {
        if(list.size()==0)
            return 0;
        double sum=0;
        for (DailyEntry e : list)
        {
            sum+=e.getClose();
        }
        return sum/list.size();
    }

    private double middleAverage(ArrayList<DailyEntry> list)
    {
        if(list.size()==0)
            return 0;
        double sum=0;
        for (DailyEntry e : list)
        {
            sum+=e.getClose()+e.getOpen();
        }
        return sum/list.size()/2;
    }

    private ArrayList<Double> averagePlot()
    {
        ArrayList<Double> ret = new ArrayList<>();
        return ret;
    }

    private void processStockData(final String tickerStr, final View root, final stockChartView sView, final ListView dataView, final TextView textView)
    {

        HttpRequest request = new HttpRequest();
        try
        {
            final ConstraintLayout loadingScreen = root.findViewById(R.id.loadingScreen);
            request.setType(HttpRequest.SearchType.COMPANYINFO);
            String response = request.execute(tickerStr).get();
            Log.e("Res:", response);
            JSONArray arr = (new JSONObject(response)).getJSONArray("bestMatches");
            if(arr.length()==0)
                return;
            JSONObject dataObj = arr.getJSONObject(0);  //always get the first one for now
            Iterator<String> metaKeys = dataObj.keys();
            //StringBuilder metaString = new StringBuilder();
            HashMap<String, String> stockInfo = new HashMap<>();
            while (metaKeys.hasNext())
            {
                String key = metaKeys.next();
                String ckey = key.substring(3);
                ckey = ckey.substring(0, 1).toUpperCase()+ckey.substring(1);
                //metaString.append(ckey).append(": ").append(dataObj.get(key)).append("\n");
                stockInfo.put(ckey, dataObj.getString(key));
            }
            dataView.setAdapter(new TwoColAdapter(getContext(), stockInfo, 0));
            //dataView.setText(metaString.toString());

            HttpRequest nrequest = new HttpRequest();
            nrequest.setType(HttpRequest.SearchType.TIMESERIES);
            nrequest.procs = new HttpRequest.ConcurrentProcedures()
            {
                @Override
                public void PreExec()
                {
                    loadingScreen.setVisibility(View.VISIBLE);
                }

                @Override
                public void Update()
                {

                }

                @Override
                public void PostExec(String... params)
                {
                    try
                    {
                        String response = params[0];
                        JSONObject obj = new JSONObject(response);
                        JSONObject timeSeries = obj.getJSONObject("Time Series (Daily)");

                        stockEntry = new StockEntry(Global.NA, Global.NA, tickerStr);
                        stockEntry.initDataSeries();
                        Iterator<String> keys = timeSeries.keys();

                        //get a 150 day moving average
                        int limited;
                        EditText inputField = root.findViewById(R.id.dayInput);
                        String input = inputField.getEditableText().toString();
                        if (input.trim().length() == 0)
                            limited = 150; //default
                        else limited = Integer.parseInt(inputField.getEditableText().toString());
                        int counter = 0;
                        while (keys.hasNext())
                        {
                            //if(counter<limited)
                            //    counter++;
                            //else
                            //    break;
                            String key = keys.next();
                            JSONObject temp = timeSeries.getJSONObject(key);
                            DailyEntry entry = new DailyEntry(key, temp.getDouble("1. open"), temp.getDouble("4. close"),
                                    temp.getDouble("3. low"), temp.getDouble("2. high"), temp.getLong("5. volume"));
                            stockEntry.getDataSeries().add(entry);
                            //Log.e("Added:" , key);
                        }

                        //=================================================================
                        Thread priceFormulaThread = new Thread()
                        {
                            public void run()
                            {
                                String text = stockEntry.getClosePriceForumla();
                                stockDetailViewModel.postText(text);
                            }
                        };
                        priceFormulaThread.start();
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    loadingScreen.setVisibility(View.GONE);
                }
            };
            nrequest.execute(tickerStr);
        }catch (ExecutionException | InterruptedException | JSONException e)
        {
            e.printStackTrace();
        }

        sView.setClickEvent(new stockChartView.extraClickDisp()
        {
            @Override
            public void perform(String information)
            {
               // Log.e("Information", information);
               // ((TextView)root.findViewById(R.id.pointDATA)).setText(information);
                //display the information
            }
        });
    }
}
