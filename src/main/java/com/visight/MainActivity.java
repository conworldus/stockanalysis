package com.visight;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.visight.data.Global;
import com.visight.data.TickerPair;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
{

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dataPreload();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_sum,
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
}

    @Override
    public boolean onSupportNavigateUp()
        {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
    }

    private void dataPreload()
    {
        loadStockList(R.raw.nyselist);
        loadStockList(R.raw.nasdaq);
        Collections.sort(Global.NYSE_Stock_Array, new Comparator<TickerPair>()
        {
            @Override
            public int compare(TickerPair o1, TickerPair o2)
            {
                return o1.ticker.compareTo(o2.ticker);
            }
        });
    }

    private void loadStockList(int list)
    {
            JSONArray array;
            InputStream is = getResources().openRawResource(list);
            Writer writer = new StringWriter();
            char [] buffer = new char[1024];  //1kb buffer

            try
            {
                Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                int n;
                while ((n = reader.read(buffer)) != -1)
                {
                    writer.write(buffer, 0, n);
                }
                is.close();
                array = new JSONArray(writer.toString());
                for (int i = 0; i < array.length(); i++)
                {
                    String key = array.getJSONObject(i).getString("ACT Symbol");
                    String val = array.getJSONObject(i).getString("Company Name");
                    Global.NYSE_Stock_List.put(key, val);
                    Global.NYSE_Stock_Array.add(new TickerPair(key, val));
                }
            }
            catch (IOException|JSONException e)
            {
                e.printStackTrace();
            }
            finally
            {
                Log.i("NYSE Count", Global.NYSE_Stock_List.size()+"");
            }
    }

    private void processList()
    {
        if(Global.NYSE_Stock_List.size()>0)
        {
            final Thread thread = new Thread()
            {
                public void run()
                {
                    Iterator<String> keys = Global.NYSE_Stock_List.keySet().iterator();
                    try
                    {
                        while (keys.hasNext())
                        {
                            wait(500);
                            //every half second
                        }
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }
}
