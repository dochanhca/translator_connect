package com.example.translateconnector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ton on 4/2/18.
 */

public class DisplayImageProfileActivity extends BaseActivity {

    public static final String KEY_IMAGE_PATH = "key_image_path";

    @BindView(R.id.bt_back)
    ImageView btBack;
    @BindView(R.id.image_profile)
    ImageView imageProfile;

    private String imagePath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_display_image_profile;
    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePath = getIntent().getStringExtra(KEY_IMAGE_PATH);

        if (!TextUtils.isEmpty(imagePath)) {
            Glide.with(this)
                    .load(imagePath)
                    .fitCenter()
                    .into(imageProfile);
        }
    }

    @OnClick(R.id.bt_back)
    public void onBackClicked(View view) {
        onBackPressed();
    }

    public static void startActivity(BaseActivity activity, String imagePath) {
        Intent intent = new Intent(activity, DisplayImageProfileActivity.class);
        intent.putExtra(DisplayImageProfileActivity.KEY_IMAGE_PATH, imagePath);
        activity.startActivity(intent);
    }

    public static void startActivity(BaseFragment fragment, String imagePath) {
        Intent intent = new Intent(fragment.getActivity(), DisplayImageProfileActivity.class);
        intent.putExtra(DisplayImageProfileActivity.KEY_IMAGE_PATH, imagePath);
        fragment.startActivity(intent);
    }
}
