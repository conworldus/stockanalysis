package com.visight.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.visight.R;

public class TextPairView extends ConstraintLayout
{
    private TextView label, content;
    public TextPairView(Context context)
    {
        super(context);
        //setupAttribute(context)
    }

    public TextPairView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setupAttribute(context, attrs);
    }

    public TextPairView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setupAttribute(context, attrs);
    }

    private void setupAttribute(Context context, AttributeSet attrs)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.textview_pair, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        label = view.findViewById(R.id.pair_label);
        content = view.findViewById(R.id.pair_content);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextPairView, 0, 0);
        label.setText(a.getString(R.styleable.TextPairView_labelText));
        content.setText(a.getString(R.styleable.TextPairView_contentText));
        super.addView(view);
        a.recycle();
    }

    public void setLabel(String text)
    {
        label.setText(text);
    }

    public void setContent(String text)
    {
        content.setText(text);
    }

    public void setText(String [] text)
    {
        if(text.length!=2)
            return;
        label.setText(text[0]);
        content.setText(text[1]);
    }

    public void setText(String label, String content)
    {
        this.label.setText(label);
        this.content.setText(content);
    }
}
