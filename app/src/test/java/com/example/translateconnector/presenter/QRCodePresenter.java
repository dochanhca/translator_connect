package com.example.translateconnector.presenter;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.imoktranslator.R;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.firebase.Friend;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

/**
 * Created by TuanNM on 4/2/2018.
 */

public class QRCodePresenter extends BasePresenter {

    public final static int WIDTH = 500;

    private QRCodeView view;
    private Bitmap qrcodeBm;
    private String currentUserKey;
    private PersonalInfo personalInfo;

    public QRCodePresenter(Context context, QRCodeView view) {
        super(context);
        this.view = view;
        currentUserKey = LocalSharedPreferences.getInstance(getContext()).getKeyUser();
        personalInfo = LocalSharedPreferences.getInstance(getContext()).getPersonalInfo();
    }

    private void generateQRCode(boolean hideTabContainer) {
        try {
            if (qrcodeBm == null) {
                qrcodeBm = encodeAsBitmap(currentUserKey + Constants.QR_CODE_SEPARATE
                        + personalInfo.getId() + Constants.QR_CODE_SEPARATE + (personalInfo.isTranslator()
                        ? "1" : "0"));
            }
            if (hideTabContainer) {
                view.displayQRCodeOnly(qrcodeBm, personalInfo.getName());
            } else {
                view.displayQRCode(qrcodeBm, personalInfo.getName());
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void requestCameraPermission(Activity activity, boolean hideTabContainer) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.CAMERA
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        generateQRCode(hideTabContainer);

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }


    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public void readQrCode(String code) {
        String[] encodedString = code.split(",");
        try {
            String userKey = encodedString[0];
            int userId = Integer.parseInt(encodedString[1]);
            if (userKey.equals(currentUserKey)) {
                view.onReadMyQr();
                return;
            }
            if (Integer.parseInt(encodedString[2]) != (personalInfo.isTranslator() ? 1 : 0)) {
                view.notify(getContext().getString(R.string.TB_1080));
                return;
            }
            view.showProgress();
            FireBaseDataUtils.getInstance().getFirebaseReference()
                    .child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                    .child(currentUserKey)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean isFriend = false;
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                try {
                                    Friend friend = dsp.getValue(Friend.class);
                                    if (friend.getUserKey().equals(userKey)) {
                                        isFriend = true;
                                        break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            view.onReadUserInfo(userKey, userId, isFriend);
                            view.hideProgress();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view.hideProgress();
                            view.onReadQRError();
                            view.notify(getContext().getString(R.string.TB_1080));
                        }
                    });

        } catch (Exception e) {
            view.onReadQRError();
            view.notify(getContext().getString(R.string.TB_1080));
        }
    }


    public interface QRCodeView extends BaseView {
        void displayQRCode(Bitmap bitmap, String userName);

        void displayQRCodeOnly(Bitmap bitmap, String userName);

        void onReadUserInfo(String userKey, int userId, boolean isFriend);

        void onReadMyQr();

        void onReadQRError();
    }

}
