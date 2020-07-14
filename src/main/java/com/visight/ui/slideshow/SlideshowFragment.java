package com.visight.ui.slideshow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.visight.R;
import com.visight.data.Global;
import com.visight.data.TickerPair;
import com.visight.util.HttpRequest;
import com.visight.util.TwoColAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class SlideshowFragment extends Fragment
{

    private SlideshowViewModel slideshowViewModel;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        final Button execute = root.findViewById(R.id.execute);
        execute.setOnClickListener(listener);

        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                textView.setText(s);
            }
        });
        return root;
    }

    private View.OnClickListener listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            final ListView listView = root.findViewById(R.id.selectStockList);
            final HashMap<String, TickerPair> qualifiedList = new HashMap<>();
            final MutableLiveData<String> latest = new MutableLiveData<>();  //ticker
            final MutableLiveData<Double> percentStr = new MutableLiveData<>();
            final TwoColAdapter adapter = new TwoColAdapter(getContext(), qualifiedList);
            listView.setAdapter(adapter);
            latest.observe(getViewLifecycleOwner(), new Observer<String>()
            {
                @Override
                public void onChanged(String s)
                {
                    TickerPair nPair = new TickerPair(s, Global.NYSE_Stock_List.get(s));
                    nPair.percent = percentStr.getValue();
                    qualifiedList.put(s, nPair);
                    adapter.notifyDataSetChanged();
                    Log.e("Added", s);
                }
            });
            //set artificial limit 300
            Thread thread = new Thread()
            {
                @SuppressLint("DefaultLocale")
                public void run()
                {
                    int limit = 300;
                    //get the data series
                    Iterator<String> keys = Global.NYSE_Stock_List.keySet().iterator();
                    for(int i=0; i<limit; i++)
                    {
                        String key = keys.next();  //ticker
                        HttpRequest nrequest = new HttpRequest();
                        nrequest.setType(HttpRequest.SearchType.TIMESERIES);
                        nrequest.compact = true;
                        try
                        {
                            JSONObject t_Series = new JSONObject(nrequest.execute(key).get()).getJSONObject("Time Series (Daily)");  //this blocks
                            Iterator<String> dates = t_Series.keys();
                            String firstKey = dates.next();
                            double currentPrice = t_Series.getJSONObject(firstKey).getDouble("4. close");
                            double sum=currentPrice;
                            int counter = 1;
                            while(dates.hasNext())
                            {
                                String dkey = dates.next();
                                sum+=t_Series.getJSONObject(dkey).getDouble("4. close");
                                counter++;
                            }
                            double average = sum/counter;
                            double diff = currentPrice-average;
                            double percent = diff/average;
                            Log.e(key, String.format("%.2f", percent*100));
                            if(Math.abs(percent)>=0.15) //more than 15% off its moving average
                            {
                                //percentStr.postValue(String.format("%.2f", percent*100)+"%");
                                percentStr.postValue(percent);
                                latest.postValue(key);
                            }
                            Thread.sleep(1000);
                        } catch (ExecutionException | InterruptedException | JSONException e)
                        {
                            Log.e(key, "Failed to get time series");
                        }
                    }
                    Log.e("Done", "Finished");
                }
            };
            thread.start();
        }
    };
}
