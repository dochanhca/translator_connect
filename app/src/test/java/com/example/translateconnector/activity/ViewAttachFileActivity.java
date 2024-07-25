package com.example.translateconnector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.R;
import com.imoktranslator.adapter.AttachImageAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.CertificateModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.presenter.BaseProfileActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ViewAttachFileActivity extends BaseActivity implements HeaderView.BackButtonClickListener, AttachImageAdapter.OnImageClickListener {
    public static final String KEY_ADDITIONAL_IMAGES = "key_additional_images";

    @BindView(R.id.header_attach_file)
    HeaderView headerView;
    @BindView(R.id.recycler_view)
    RecyclerView rvAttachFiles;
    @BindView(R.id.txt_upload)
    OpenSansBoldTextView txtUpload;

    AttachImageAdapter adapter;
    private ArrayList<Image> imageList = new ArrayList<>();
    private int initialSize;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_attach_file;
    }

    @Override
    protected void initViews() {
        headerView.setCallback(this);
        headerView.setTittle(getString(R.string.MH09_061));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageList();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        rvAttachFiles.setLayoutManager(layoutManager);
        adapter = new AttachImageAdapter(this, imageList);
        adapter.setOnItemClickListener((view, position) ->
                DisplayImageProfileActivity.startActivity(this, imageList.get(position).path));
        adapter.setOnImageClickListener(this);
        rvAttachFiles.setAdapter(adapter);
    }

    private void initImageList() {
        PersonalInfo personalInfo = getIntent().getParcelableExtra(BaseProfileActivity.KEY_PERSONAL_INFO);
        if (personalInfo != null && personalInfo.getCertificates() != null) {
            Image image;
            for (CertificateModel certificateModel : personalInfo.getCertificates()) {
                image = new Image(-1, certificateModel.getName(), certificateModel.getFilePath(), false);
                imageList.add(image);
            }
        }
        initialSize = imageList.size();
    }

    @OnClick(R.id.txt_upload)
    public void selectPhotos() {
        Intent intent = new Intent(this, AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT,
                com.imoktranslator.utils.Constants.MAX_CERTIFICATE_IMAGES);// follow requirement
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    @OnClick(R.id.bt_save)
    public void saveFiles() {
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra(KEY_ADDITIONAL_IMAGES, imageList);
        setResult(RESULT_OK, resultIntent);
        back();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            List<Image> newImageList = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            addNewItem(imageList, newImageList);
            adapter.notifyDataSetChanged();
            resetBtnUpload();
        }
    }

    private void resetBtnUpload() {
        int count = imageList.size() - initialSize;
        txtUpload.getBackground().setAlpha(count >= com.imoktranslator.utils.Constants.MAX_CERTIFICATE_IMAGES ? 128 : 255);
        txtUpload.setEnabled(count < com.imoktranslator.utils.Constants.MAX_CERTIFICATE_IMAGES);
    }

    private void addNewItem(List<Image> imageList, List<Image> newImageList) {
        for (Image image : newImageList) {
            if (imageList.size() - initialSize < 20) {
                imageList.add(image);
            } else {
                break;
            }
        }
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (initialSize == imageList.size()) {
            back();
        } else {
            showNoticeDataChanged();
        }
    }

    public void showNoticeDataChanged() {
        showNotifyDialog(getString(R.string.MH09_027), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                back();
            }
        });
    }

    private void back() {
        super.onBackPressed();
    }

    @Override
    public void onDeleteImage() {
        resetBtnUpload();
    }
}
