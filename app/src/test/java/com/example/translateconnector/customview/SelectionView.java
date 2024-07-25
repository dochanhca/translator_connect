package com.example.translateconnector.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imoktranslator.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tvoer on 4/4/18.
 */

public class SelectionView extends LinearLayout {
    @BindView(R.id.selection_value)
    OpenSansTextView selectionValue;
    @BindView(R.id.selection_hint)
    OpenSansTextView selectionHint;
    @BindView(R.id.txt_required)
    OpenSansTextView txtRequired;
    @BindView(R.id.txt_error)
    TextView txtError;

    public SelectionView(Context context) {
        super(context);
        initView(null, 0, 0);
    }

    public SelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs, 0, 0);
    }

    public SelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    private void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_selection_view, this, true);
        ButterKnife.bind(this);

        if (attrs != null) {
            TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SelectionView, defStyleAttr, defStyleRes);
            boolean isRequired = ta.getBoolean(R.styleable.SelectionView_isSelectRequired, false);

            if (ta != null) {
                txtRequired.setVisibility(isRequired ? VISIBLE : GONE);

                if (ta.hasValue(R.styleable.SelectionView_drawableRight)) {
                    Drawable drawable = ta.getDrawable(R.styleable.SelectionView_drawableRight);
                    selectionValue.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    selectionHint.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                }

                if (ta.hasValue(R.styleable.SelectionView_selectionHint)) {
                    String hint = ta.getString(R.styleable.SelectionView_selectionHint);
                    setHint(hint);
                }


                int textColor = ta.getColor(R.styleable.SelectionView_textColor, ContextCompat.getColor(getContext(), R.color.text_brown));
                selectionValue.setTextColor(textColor);

                String error = ta.getString(R.styleable.SelectionView_selection_error);
                txtError.setText(error);

                ta.recycle();
            }
        }
    }

    public void setSelectionValue(String value) {
        if (TextUtils.isEmpty(value)) {
            selectionValue.setText(value);
            selectionValue.setVisibility(GONE);
            selectionHint.setVisibility(VISIBLE);
        } else {
            selectionValue.setText(value);
            selectionValue.setVisibility(VISIBLE);
            selectionHint.setVisibility(GONE);
        }
    }

    public void setHint(String hint) {
        selectionHint.setText(hint);
        selectionValue.setVisibility(GONE);
        selectionHint.setVisibility(VISIBLE);
    }

    public void setSelectionValue(int stringId) {
        selectionValue.setText(stringId);
        selectionValue.setVisibility(VISIBLE);
        selectionHint.setVisibility(GONE);
    }

    public String getSelectionValue() {
        return selectionValue.getText().toString().trim();
    }

    public TextView getSeSelectionView() {
        return selectionValue;
    }

    public void setSelectionValueFont(Typeface typeface) {
        selectionValue.setTypeface(typeface);
    }


    public boolean hasValue() {
        return selectionValue.getVisibility() == VISIBLE;
    }

    public void setError(String error) {
        txtError.setVisibility(TextUtils.isEmpty(error) ? GONE : VISIBLE);
        txtError.setText(error);
    }

    public void setErrorVisible(boolean isError) {
        txtError.setVisibility(isError ? VISIBLE : GONE);
    }

    public boolean isError() {
        return txtError.getVisibility() == VISIBLE;
    }

}
