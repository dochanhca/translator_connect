package com.example.translateconnector.customview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.imoktranslator.utils.FontUtils;

public class SuggestionView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    public SuggestionView(Context context) {
        super(context);
        initFont();
    }

    public SuggestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont();
    }

    public SuggestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFont();
    }

    private void initFont() {
        Typeface font = FontUtils.getInstance().getOpenSans();
        this.setTypeface(font);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            showDropDown();
            performFiltering(getText(), 0);
        }
    }
}
