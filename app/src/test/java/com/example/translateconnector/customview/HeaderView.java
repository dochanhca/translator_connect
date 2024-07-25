package com.example.translateconnector.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;

import butterknife.BindAnim;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tontn on 3/25/18.
 */

public class HeaderView extends RelativeLayout {
    @BindView(R.id.txt_title)
    TextView tvTitle;
    @BindView(R.id.btBack)
    ImageView btBack;
    @BindView(R.id.img_right)
    ImageView imgRight;
    @BindView(R.id.tv_right)
    OpenSansBoldTextView tvRight;

    private BackButtonClickListener callback;

    public HeaderView(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.header_view, this, true);
        ButterKnife.bind(this);

        if (attrs != null) {
            TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.HeaderView, defStyleAttr, defStyleRes);
            if (ta != null) {
                if (ta.hasValue(R.styleable.HeaderView_title_header)) {
                    String title = ta.getString(R.styleable.HeaderView_title_header);
                    setTittle(title);
                }
                ta.recycle();
            }
        }
    }

    @OnClick(R.id.btBack)
    public void clickBackButton() {
        if (callback != null) {
            callback.backButtonClicked();
        }
    }

    public void setTittle(String tittle) {
        tvTitle.setText(tittle);
    }

    public void setCallback(BackButtonClickListener callback) {
        this.callback = callback;
    }

    public void setImgRightRes(int resId) {
        imgRight.setVisibility(VISIBLE);
        imgRight.setImageResource(resId);
    }

    public void setBtnRightClick(OnClickListener onClickListener) {
        imgRight.setOnClickListener(onClickListener);
    }

    public void setBtnRightVisible(int visible) {
        imgRight.setVisibility(visible);
    }

    public void setTvRightValue(int stringId) {
        tvRight.setVisibility(VISIBLE);
        tvRight.setText(stringId);
    }

    public void setTvRightOnClick(OnClickListener onClickListener) {
        tvRight.setOnClickListener(onClickListener);
    }

    public OpenSansBoldTextView getTvRight() {
        return tvRight;
    }

    public interface BackButtonClickListener {
        void backButtonClicked();
    }
}
