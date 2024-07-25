package com.example.translateconnector.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.imoktranslator.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ton on 4/7/18.
 */

public class TagView extends CardView {
    @BindView(R.id.tv_tag)
    OpenSansTextView tag;
    @BindView(R.id.bt_delete)
    ImageView btDelete;

    public TagView(Context context) {
        super(context);
        initView(null, 0, 0);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs, 0, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    private void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_tag_view, this, true);
        ButterKnife.bind(this);

        if (attrs != null) {
            TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TagView, defStyleAttr, defStyleRes);
            if (ta != null) {
                int textColor = ta.getColor(R.styleable.TagView_textColorTag, ContextCompat.getColor(getContext(), R.color.text_brown));
                tag.setTextColor(textColor);
                ta.recycle();
            }
        }
    }

    public ImageView getDeleteButton() {
        return btDelete;
    }

    public void setTagName(String name) {
        tag.setText(name);
    }
}
