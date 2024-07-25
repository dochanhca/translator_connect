package com.example.translateconnector.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.R;
import com.imoktranslator.activity.BaseActivity;
import com.imoktranslator.activity.DisplayImageProfileActivity;
import com.imoktranslator.adapter.AttachImageAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.PersonalInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AttachFileFragment extends BaseFragment implements HeaderView.BackButtonClickListener,
        AttachImageAdapter.OnImageClickListener {
    @BindView(R.id.header_attach_file)
    HeaderView headerView;
    @BindView(R.id.txt_upload)
    OpenSansBoldTextView txtUpload;
    @BindView(R.id.recycler_view)
    RecyclerView rvAttachFiles;

    private AttachImageAdapter adapter;
    private boolean isHandleBackEvent = true;

    private PersonalInfo personalInfo;
    private List<Image> backupImages;
    private List<Image> imageList;

    public static AttachFileFragment newInstance(PersonalInfo personalInfo) {

        Bundle args = new Bundle();

        AttachFileFragment fragment = new AttachFileFragment();
        fragment.setArguments(args);
        fragment.personalInfo = personalInfo;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_attach_file;
    }

    @Override
    protected void initViews() {
        headerView.setCallback(this);
        headerView.setTittle(getString(R.string.MH09_061));

        imageList = personalInfo.getAttachPaths() == null ? new ArrayList<>() : getCachedImages();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rvAttachFiles.setLayoutManager(layoutManager);
        adapter = new AttachImageAdapter(getActivity().getApplicationContext(), imageList);
        adapter.setOnItemClickListener((view, position) ->
                DisplayImageProfileActivity.startActivity(this, imageList.get(position).path));

        adapter.setOnImageClickListener(this);
        rvAttachFiles.setAdapter(adapter);

        backupImages = new ArrayList<>();
        backupImages.addAll(imageList);
    }

    @OnClick(R.id.txt_upload)
    public void selectPhotos() {
        Intent intent = new Intent(getContext(), AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT,
                com.imoktranslator.utils.Constants.MAX_CERTIFICATE_IMAGES);// follow requirement
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    @OnClick(R.id.bt_save)
    public void saveFiles() {
        personalInfo.setAttachPaths(imageList);
        updateCertificateFragmentView(imageList);
        isHandleBackEvent = false;
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        if (isHandleBackEvent) {
            checkDataNotChange();
            return true;
        } else {
            return false;
        }
    }

    private List<Image> getCachedImages() {
        List<Image> cachedList = new ArrayList<>();
        for (Image image : personalInfo.getAttachPaths()) {
            if (image != null && image.path != null) {
                cachedList.add(image);
            }
        }
        return cachedList;
    }

    private void checkDataNotChange() {
        if (backupImages.size() != imageList.size()) {
            showNoticeDataChanged();
        } else {
            boolean dataNotChange = true;
            for (int i = 0; i < backupImages.size(); i++) {
                if (!backupImages.get(i).path.equals(imageList.get(i).path)) {
                    dataNotChange = false;
                    break;
                }
            }

            if (!dataNotChange) {
                showNoticeDataChanged();
            } else {
                isHandleBackEvent = false;
                getFragmentManager().popBackStackImmediate();
            }
        }
    }

    public void showNoticeDataChanged() {
        ((BaseActivity) getActivity()).showNotifyDialog(getString(R.string.MH09_027), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                isHandleBackEvent = false;
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    private CertificateInfoFragment getCertificateFragment() {
        BaseFragment certificateFragment = (BaseFragment)
                getFragmentManager().findFragmentByTag(CertificateInfoFragment.class.getSimpleName());
        if (certificateFragment != null && certificateFragment instanceof CertificateInfoFragment) {
            return (CertificateInfoFragment) certificateFragment;
        }
        return null;
    }

    @Override
    public void backButtonClicked() {
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            List<Image> newImageList = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            addNewItem(imageList, newImageList);
            adapter.setImageList(imageList);
            resetBtnUpload();
        }
    }

    private void resetBtnUpload() {
        int count = imageList.size();
        txtUpload.getBackground().setAlpha(count >= com.imoktranslator.utils.Constants.MAX_CERTIFICATE_IMAGES ? 128 : 255);
        txtUpload.setEnabled(count < com.imoktranslator.utils.Constants.MAX_CERTIFICATE_IMAGES);
    }

    private void addNewItem(List<Image> imageList, List<Image> newImageList) {
        for (Image image : newImageList) {
            if (imageList.size() < 20 && image != null) {
                imageList.add(image);
            } else {
                break;
            }
        }
    }

    private void updateCertificateFragmentView(List<Image> imageList) {
        if (getCertificateFragment() != null) {
            getCertificateFragment().setFileCount(imageList.size());
        }
    }

    @Override
    public void onDeleteImage() {
        resetBtnUpload();
    }
}
