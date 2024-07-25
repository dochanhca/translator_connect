package com.example.translateconnector.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.imoktranslator.R;
import com.imoktranslator.adapter.ImagePagerAdapter;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.ImagesManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ImageDetailActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_more_actions)
    ImageView imgMoreActions;
    @BindView(R.id.viewpager_images)
    ViewPager viewpagerImages;

    private ImagePagerAdapter imagePagerAdapter;
    private List<FileModel> fileModels;
    private int selectedPosition;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_detail;
    }

    @Override
    protected void initViews() {
        fileModels = getIntent().getParcelableArrayListExtra(Constants.FILE_MODELS_KEY);
        selectedPosition = getIntent().getIntExtra(Constants.SELECTED_POSITION_KEY, 0);

        initViewPager();

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
    }

    private void initViewPager() {
        imagePagerAdapter = new ImagePagerAdapter(fileModels, this);
        viewpagerImages.setAdapter(imagePagerAdapter);
        viewpagerImages.setCurrentItem(selectedPosition);
        viewpagerImages.addOnPageChangeListener(onPageChangeListener);
    }

    @OnClick({R.id.img_back, R.id.img_more_actions})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                this.finish();
                break;
            case R.id.img_more_actions:
                showPopUpMenu(imgMoreActions);
                break;
        }
    }

    private void showPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_image_options);
        // ...
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.image_share_fb:
                    //implement share fb
                    fbShareImageLink();
                    break;
                case R.id.image_download:
                    ImagesManager.downloadImageFromUrl(this,
                            fileModels.get(selectedPosition).getUrlFile());
                    break;
            }
            return false;
        });
    }

    private void fbShareImageLink() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(fileModels.get(selectedPosition).getUrlFile()))
                .build();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(content);
        }
    }

    private void fbShareSinglePhoto() {
        showProgress();
        Glide.with(this).load(fileModels.get(selectedPosition).getUrlFile())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap image, GlideAnimation<? super Bitmap> glideAnimation) {
                        hideProgress();
                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(image)
                                .build();
                        SharePhotoContent content = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .build();

                        if (ShareDialog.canShow(SharePhotoContent.class)) {
                            shareDialog.show(content);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        hideProgress();
                    }
                });
    }

    public static void showActivity(BaseActivity activity, List<FileModel> fileModels, int selectedPosition) {
        Intent intent = new Intent(activity, ImageDetailActivity.class);
        intent.putParcelableArrayListExtra(Constants.FILE_MODELS_KEY, (ArrayList<? extends Parcelable>) fileModels);
        intent.putExtra(Constants.SELECTED_POSITION_KEY, selectedPosition);

        activity.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
