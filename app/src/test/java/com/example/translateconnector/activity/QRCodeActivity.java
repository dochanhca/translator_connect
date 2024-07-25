package com.example.translateconnector.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.zxing.Result;
import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.presenter.QRCodePresenter;

import butterknife.BindView;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by TuanNM on 4/2/2018.
 */

public class QRCodeActivity extends BaseActivity implements HeaderView.BackButtonClickListener,
        ZXingScannerView.ResultHandler, QRCodePresenter.QRCodeView {

    private ZXingScannerView mScannerView;

    public static String SHOW_QR_CODE_ONLY = "SHOW_MY_QR_CODE_ONLY";

    @BindView(R.id.header_qrcode)
    HeaderView headerView;
    @BindView(R.id.button_container)
    RelativeLayout layoutContainerButton;
    @BindView(R.id.scan_qr_code)
    OpenSansBoldTextView txtScanQRcode;
    @BindView(R.id.display_qr_code)
    OpenSansBoldTextView txtDisplayQRcode;
    @BindView(R.id.camera_scan_qrcode)
    FrameLayout cameraScanQRCode;
    @BindView(R.id.image_qrcode)
    ImageView igmQRCode;
    @BindView(R.id.qrcode_view_container)
    LinearLayout qrCodeViewContainer;
    @BindView(R.id.user_name)
    OpenSansBoldTextView txtUserName;

    private QRCodePresenter presenter;

    private boolean needResumeHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_qrcode;
    }

    @Override
    protected void initViews() {
        presenter = new QRCodePresenter(this, this);
        headerView.setCallback(this);
        mScannerView = new ZXingScannerView(this);

        initCameraScanner();
        //Start camera scan QR code when start activity
        if (getIntent().getBooleanExtra(SHOW_QR_CODE_ONLY, false)) {
            presenter.requestCameraPermission(this, true);
        } else {
            presenter.requestCameraPermission(this, false);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mScannerView.startCamera();
        if (needResumeHandler) {
            mScannerView.resumeCameraPreview(this);
            needResumeHandler = false;
        }
    }

    @Override
    protected void onStop() {
        mScannerView.stopCamera();
        super.onStop();
    }

    @OnClick({R.id.scan_qr_code, R.id.display_qr_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_qr_code:
                switchView(true);
                break;
            case R.id.display_qr_code:
                switchView(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @Override
    public void handleResult(Result result) {
        needResumeHandler = true;
        presenter.readQrCode(result.getText());
    }

    private void initCameraScanner() {
        //initialize camera scan view
        // Register ourselves as a handler for scan results.
        cameraScanQRCode.addView(mScannerView);
        mScannerView.setResultHandler(this);
    }

    private void switchView(boolean isShowCamera) {

        txtScanQRcode.setTextColor(getResources().getColor( isShowCamera
                ? R.color.medium_blue : R.color.text_grey));
        txtDisplayQRcode.setTextColor(getResources().getColor(isShowCamera
                ? R.color.text_grey : R.color.medium_blue));

        cameraScanQRCode.setVisibility(isShowCamera ? View.VISIBLE : View.GONE);
        qrCodeViewContainer.setVisibility(isShowCamera ? View.GONE : View.VISIBLE);
        if (isShowCamera) {
            mScannerView.startCamera();
        } else {
            mScannerView.stopCamera();
        }
    }

    @Override
    public void onReadUserInfo(String userKey, int userId, boolean isFriend) {
        UserInfoActivity.startActivity(this, userKey, userId, isFriend);
    }

    @Override
    public void onReadMyQr() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onReadQRError() {
        mScannerView.startCamera();
        if (needResumeHandler) {
            mScannerView.resumeCameraPreview(this);
            needResumeHandler = false;
        }
    }

    @Override
    public void displayQRCodeOnly(Bitmap bitmap, String userName) {
        layoutContainerButton.setVisibility(View.GONE);
        igmQRCode.setImageBitmap(bitmap);
        txtUserName.setText(userName);
        switchView(false);
    }

    @Override
    public void displayQRCode(Bitmap bitmap, String userName) {
        igmQRCode.setImageBitmap(bitmap);
        txtUserName.setText(userName);
        switchView(true);
    }

    public static void startActivity(BaseActivity activity, boolean isShowQRCodeOnly) {
        Intent intent = new Intent(activity, QRCodeActivity.class);
        intent.putExtra(QRCodeActivity.SHOW_QR_CODE_ONLY, isShowQRCodeOnly);
        activity.startActivity(intent);
    }
}
