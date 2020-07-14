package com.visight.ui.summary;

import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.visight.R;
import com.visight.data.Global;
import com.visight.data.TickerPair;
import com.visight.util.tickerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class SummaryFragment extends Fragment
{
    View root;
    private ArrayList<TickerPair> sList = null;
    private SimpleCursorAdapter adapter;
    ArrayList<String> suggestionList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        final String[] from = new String[] {"_id", "Symbol", "Description"};
        final int[] to = new int[] {android.R.id.text1, android.R.id.text1, android.R.id.text1};

        SummaryViewModel summaryViewModel = ViewModelProviders.of(this).get(SummaryViewModel.class);
        root = inflater.inflate(R.layout.fragment_summary, container, false);
        ListView stockList = root.findViewById(R.id.stock_list);
        stockList.setAdapter(new tickerAdapter(Objects.requireNonNull(getContext()), 0));
        stockList.setOnItemClickListener(listener);

        SearchView sView = root.findViewById(R.id.ticker_search);
        adapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_2,
                null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        sView.setSuggestionsAdapter(adapter);
        sView.setOnQueryTextListener(queryListener);
        sView.setOnSuggestionListener(suggestionListener);
        return root;
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            TickerPair pair = Global.NYSE_Stock_Array.get(position);
            String symbol = pair.ticker;
            Bundle params = new Bundle();
            params.putString("Symbol", symbol);
            Navigation.findNavController(root).navigate(R.id.nav_home, params);
        }
    };

    private SearchView.OnSuggestionListener suggestionListener = new SearchView.OnSuggestionListener()
    {
        @Override
        public boolean onSuggestionSelect(int position)
        {
            return false;
        }

        @Override
        public boolean onSuggestionClick(int position)
        {
            if(suggestionList!=null)
            {
                String ticker = suggestionList.get(position);
                Bundle params = new Bundle();
                params.putString("Symbol", ticker);
                Navigation.findNavController(root).navigate(R.id.nav_home, params);
            }
            return false;
        }
    };

    private SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener()
    {
        @Override
        public boolean onQueryTextSubmit(String query)
        {
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean onQueryTextChange(String newText)
        {
            if(newText==null||newText.trim().length()==0)
                sList = null;
            else
            {
                suggestionList.clear();
                String n = newText.trim();
                if(sList == null)
                {
                    sList = (ArrayList<TickerPair>)Global.NYSE_Stock_Array.clone();
                }
                MatrixCursor c = new MatrixCursor(new String [] {"_id", "Symbol", "Description"});
                for(TickerPair p:sList)
                {
                    if(p.ticker.toLowerCase().contains(n.toLowerCase())||p.companyName.toLowerCase().contains(n.toLowerCase()))
                    {
                        String [] row = {"0", p.ticker, "("+p.ticker+") "+p.companyName};
                        c.addRow(row);
                        suggestionList.add(p.ticker);
                    }
                }
                adapter.changeCursor(c);
            }
            return false;
        }
    };
}
