package com.visight.ui.StockDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StockDetailViewModel extends ViewModel
{

    private MutableLiveData<String> mText;

    public StockDetailViewModel()
    {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText()
    {
        return mText;
    }

    public void setText(String param)
    {
        mText.setValue(param);
    }

    public void postText(String param)
    {
        mText.postValue(param);
    }
}