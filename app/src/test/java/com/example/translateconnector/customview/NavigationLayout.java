package com.example.translateconnector.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imoktranslator.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ducpv on 3/27/18.
 */

public class NavigationLayout extends LinearLayout {

    @BindView(R.id.img_icon)
    ImageView imgIcon;
    @BindView(R.id.txt_box_value)
    TextView txtBoxValue;

    private TypedArray typedArray;
    private String boxValue;

    public NavigationLayout(Context context) {
        super(context);
    }

    public NavigationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NavigationLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.navigation_layout, this, true);

        ButterKnife.bind(this);
        typedArray = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.navigation_view);

        if (typedArray.hasValue(R.styleable.navigation_view_navigationIcon)) {
            int iconSelected = typedArray.getResourceId(R.styleable.navigation_view_navigationIcon, R.drawable.ic_navi_hamburger);
            imgIcon.setImageResource(iconSelected);
        }

        boolean showBoxValue = typedArray.getBoolean(R.styleable.navigation_view_showBoxValue, false);

        txtBoxValue.setVisibility(showBoxValue ? VISIBLE : GONE);
        //Don't forget this
        typedArray.recycle();
        setGravity(Gravity.CENTER);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public String getBoxValue() {
        return boxValue;
    }

    /**
     * need to call showBoxValue method to show box
     *
     * @param boxValue
     */
    public void setBoxValue(String boxValue) {
        this.boxValue = boxValue;
        txtBoxValue.setText(boxValue);
    }

    public void showBoxValue(String boxValue) {
        setShowBoxValue(!boxValue.equals("0"));
        this.boxValue = boxValue;
        txtBoxValue.setText(boxValue);
    }

    public boolean isShowBoxValue() {
        return txtBoxValue.getVisibility() == VISIBLE;
    }

    public void setShowBoxValue(boolean isShow) {
        txtBoxValue.setVisibility(isShow ? VISIBLE : GONE);
    }
}
