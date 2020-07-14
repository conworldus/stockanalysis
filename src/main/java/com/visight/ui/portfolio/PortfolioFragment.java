package com.visight.ui.portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.visight.R;
import com.visight.data.Global;
import com.visight.data.Portfolio;
import com.visight.util.portfolioSpinnerAdapter;

public class PortfolioFragment extends Fragment
{

    private PortfolioViewModel portfolioViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        portfolioViewModel =
                ViewModelProviders.of(this).get(PortfolioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_portfolio, container, false);
        Spinner portfolioSelect = root.findViewById(R.id.portfolioSelect);
        ImageView addButton = root.findViewById(R.id.addPortfolio);
        final TextView name = root.findViewById(R.id.profile_name),
                current_balance = root.findViewById(R.id.current_balance),
                date_created = root.findViewById(R.id.date_created);
        final portfolioSpinnerAdapter adapter = new portfolioSpinnerAdapter(getContext());
        portfolioSelect.setAdapter(adapter);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Global.onCreatePortfolio func = new Global.onCreatePortfolio()
                {
                    @Override
                    public void process(Portfolio param)
                    {
                        name.setText(param.getPortfolioName());
                        current_balance.setText(String.valueOf(param.getUSDBalance()));
                        date_created.setText(param.getCreationDate().toString());
                    }
                };
                Global.addPortfolios(getContext(), func);
                adapter.notifyDataSetChanged();
            }
        });

        portfolioSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Portfolio p = Global.myPortfolios.get(position);
                name.setText(p.getPortfolioName());
                current_balance.setText(String.valueOf(p.getUSDBalance()));
                date_created.setText(p.getCreationDate().toString());
                Global.setCurrentPortfolio(p);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        return root;
    }
}
