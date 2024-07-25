package com.example.translateconnector.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SquareImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageBoxLayout extends LinearLayout {
    @BindView(R.id.img_big)
    ImageView imgBig;
    @BindView(R.id.img_first)
    SquareImageView imgFirst;
    @BindView(R.id.img_second)
    SquareImageView imgSecond;
    @BindView(R.id.layout_two_image)
    LinearLayout layoutTwoImage;
    @BindView(R.id.img_1)
    SquareImageView img1;
    @BindView(R.id.img_2)
    SquareImageView img2;
    @BindView(R.id.img_3)
    SquareImageView img3;
    @BindView(R.id.txt_image_number)
    OpenSansTextView txtImageNumber;
    @BindView(R.id.layout_three_images)
    LinearLayout layoutThreeImages;

    private List<String> urls = new ArrayList<>();
    private OnImageClickListener onImageClickListener;

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    public ImageBoxLayout(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public ImageBoxLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public ImageBoxLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_image_box, this, true);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.img_big, R.id.img_first, R.id.img_second, R.id.img_1,
            R.id.img_2, R.id.img_3})
    public void onClick(View view) {
        int pos;
        switch (view.getId()) {
            case R.id.img_second:
                pos = 1;
                break;
            case R.id.img_1:
                pos = imgBig.getVisibility() == VISIBLE ? 1 : 0;
                break;
            case R.id.img_2:
                pos = imgBig.getVisibility() == VISIBLE ? 2 : 1;
                break;
            case R.id.img_3:
                pos = imgBig.getVisibility() == VISIBLE ? 3 : 2;
                break;
            default:
                pos = 0;

        }
        onImageClickListener.onImageClick(pos);
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls, OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
        this.urls = urls;

        switch (urls.size()) {
            case 0:
                break;
            case 1:
                loadImage(imgBig, urls.get(0));
                break;
            case 2:
                handleTwoImage();
                break;
            case 3:
                handleThreeImage();
                break;
            case 4:
                handleFourImage();
                break;
            default:
                handleFourImage();
                showImageNumber();
                break;
        }

    }

    private void handleTwoImage() {
        imgBig.setVisibility(GONE);
        layoutTwoImage.setVisibility(VISIBLE);
        loadImage(imgFirst, urls.get(0));
        loadImage(imgSecond, urls.get(1));
    }

    private void handleThreeImage() {
        imgBig.setVisibility(GONE);
        layoutThreeImages.setVisibility(VISIBLE);
        loadImage(img1, urls.get(0));
        loadImage(img2, urls.get(1));
        loadImage(img3, urls.get(2));
    }

    private void handleFourImage() {
        layoutThreeImages.setVisibility(VISIBLE);
        loadImage(imgBig, urls.get(0));
        loadImage(img1, urls.get(1));
        loadImage(img2, urls.get(2));
        loadImage(img3, urls.get(3));
    }

    private void showImageNumber() {
        int rest = urls.size() - 4;
        txtImageNumber.setVisibility(VISIBLE);
        txtImageNumber.setText("+" + rest);
    }

    private void loadImage(ImageView imageView, String url) {
        Glide.with(getContext())
                .load(url)
                .error(R.drawable.img_loading_default)
                .placeholder(R.drawable.img_loading_default)
                .thumbnail(0.1f)
                .into(imageView);
    }

    public interface OnImageClickListener {
        void onImageClick(int pos);
    }
}
