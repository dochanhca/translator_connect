package com.example.translateconnector.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.fragment.FragmentBox;
import com.imoktranslator.model.UserNeedReview;
import com.imoktranslator.presenter.BaseView;
import com.imoktranslator.transform.CircleTransform;
import com.imoktranslator.utils.AppAction;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocaleHelper;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by ducpv on 3/23/18.
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    protected String TAG;

    protected long mLastClickTime;

    protected abstract int getLayoutId();

    protected abstract void initViews();

    private BroadcastReceiver voteUserReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UserNeedReview userNeedReview = intent.getParcelableExtra(VotePartnerActivity.USER_NEED_REVIEW);
            if (userNeedReview != null && !(BaseActivity.this instanceof VotePartnerActivity)) {
                Intent intentVote = new Intent(BaseActivity.this, VotePartnerActivity.class);
                intentVote.putExtra(VotePartnerActivity.USER_NEED_REVIEW, userNeedReview);
                startActivity(intentVote);
            }
        }
    };

    private BroadcastReceiver updateProfileFilterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppAction.ACTION_ACCEPT_SIGN_UP_TRANSLATOR)) {
                showNotifyUpdateInfo(getString(R.string.TB_1037));
                FireBaseDataUtils.getInstance().updateUserTranslator(getApplicationContext());
            } else if (action.equals(AppAction.ACTION_REJECT_SIGN_UP_TRANSLATOR)) {
                showNotifyUpdateInfo(getString(R.string.TB_1038));
            } else if (action.equals(AppAction.ACTION_ACCEPT_TRANSLATOR_UPDATE_INFO)) {
                showNotifyUpdateInfo(getString(R.string.TB_1039));
            } else if (action.equals(AppAction.ACTION_REJECT_TRANSLATOR_UPDATE_INFO)) {
                showNotifyUpdateInfo(getString(R.string.TB_1040));
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initViews();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

//    private ValueEventListener valueEventListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            User user = dataSnapshot.getValue(User.class);
//            Log.d(TAG, "User Info Changed");
//            LocalSharedPreferences.getInstance(BaseActivity.this).saveCurrentFirebaseUser(user);
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//    };

//    private void getUserInfoFromFireBase() {
//        User currentFirebaseUser = LocalSharedPreferences.getInstance(this).getCurrentFirebaseUser();
//        if (currentFirebaseUser == null) {
//            return;
//        }
//
//        String userKey = currentFirebaseUser.getKey();
//        if (TextUtils.isEmpty(userKey)) {
//            return;
//        }
//
//        FireBaseDataUtils.getInstance().getFirebaseReference()
//                .child(FireBaseDataUtils.USERS_COLLECTION)
//                .child(userKey)
//                .addValueEventListener(valueEventListener);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(voteUserReceiver,
                new IntentFilter(AppAction.ACTION_VOTE_USER));

        registerUpdateProfileBroadcastReceiver();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void registerUpdateProfileBroadcastReceiver() {
        IntentFilter updateProfileFilter = new IntentFilter();
        updateProfileFilter.addAction(AppAction.ACTION_ACCEPT_SIGN_UP_TRANSLATOR);
        updateProfileFilter.addAction(AppAction.ACTION_REJECT_SIGN_UP_TRANSLATOR);
        updateProfileFilter.addAction(AppAction.ACTION_ACCEPT_TRANSLATOR_UPDATE_INFO);
        updateProfileFilter.addAction(AppAction.ACTION_REJECT_TRANSLATOR_UPDATE_INFO);
        LocalBroadcastManager.getInstance(this).registerReceiver(updateProfileFilterReceiver, updateProfileFilter);
    }

    private void showNotifyUpdateInfo(String message) {
        showNotifyDialog(message,
                getString(R.string.MH12_008),
                null,
                true,
                new NotifyDialog.OnNotifyCallback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOk(Object... obj) {
                        restartApp();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(voteUserReceiver);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateProfileFilterReceiver);
    }

//    private void detachFireBaseListener() {
//        User currentFirebaseUser = LocalSharedPreferences.getInstance(this).getCurrentFirebaseUser();
//        if (currentFirebaseUser == null) {
//            return;
//        }
//
//        String userKey = currentFirebaseUser.getKey();
//        if (TextUtils.isEmpty(userKey)) {
//            return;
//        }
//
//        FireBaseDataUtils.getInstance().getFirebaseReference()
//                .child(FireBaseDataUtils.USERS_COLLECTION)
//                .child(userKey)
//                .removeEventListener(valueEventListener);
//    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy called");
        super.onDestroy();
    }

    public void showNotifyDialog(String message) {
        showNotifyDialog(message, null,
                null, true, null);
    }

    public void showNotifyDialog(String message,
                                 String positiveTitle) {
        showNotifyDialog(message, positiveTitle,
                null, true, null);
    }

    public void showNotifyDialog(String message,
                                 NotifyDialog.OnNotifyCallback callback) {
        showNotifyDialog(message, null,
                null, false, callback);
    }

    public void showNotifyDialog(String message, String positiveTitle, NotifyDialog.OnNotifyCallback callback) {
        showNotifyDialog(message, positiveTitle, null, true, callback);
    }

    private final void showNotifyDialog(String message, String positiveTitle,
                                        String negativeTitle, boolean hideNegativeButton, NotifyDialog.OnNotifyCallback callback) {
        NotifyDialog notifyDialog = new NotifyDialog();
        notifyDialog.showDialog(getSupportFragmentManager(), message, positiveTitle,
                negativeTitle, hideNegativeButton, callback);
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View view = this.getCurrentFocus();
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!view.hasFocus()) {
            view.requestFocus();
        }
        view.post(() -> imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT));
    }

    protected List<String> convertArrToListString(int arrID) {
        String[] arr = getResources().getStringArray(arrID);
        return Arrays.asList(arr);
    }

    public void loadUserAvatar(String url, ImageView imageView) {
        Glide.with(this)
                .load(url)
                .error(R.drawable.img_avatar_default)
                .placeholder(R.drawable.img_avatar_default)
                .dontAnimate()
                .transform(new CircleTransform(this))
                .into(imageView);
    }


    @Override
    public void showProgress() {
        DialogUtils.showProgress(this);
    }

    @Override
    public void hideProgress() {
        DialogUtils.hideProgress();
    }

    @Override
    public void notify(String errMessage) {
        showNotifyDialog(errMessage);
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();

        if (this instanceof FragmentBox) {
            FragmentBox fragmentBox = (FragmentBox) this;
            Fragment fragment = getSupportFragmentManager().findFragmentById(fragmentBox.getContainerViewId());
            if (fragment != null && fragment instanceof BaseFragment && ((BaseFragment) fragment).isHandleBackPressedOnFragment()) {
                //implement inside fragment
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
