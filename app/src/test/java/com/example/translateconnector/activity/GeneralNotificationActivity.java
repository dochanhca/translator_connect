package com.example.translateconnector.activity;

import android.content.Intent;
import android.view.View;

import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.fragment.AddFriendFragment;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.fragment.CommonNotificationFragment;
import com.imoktranslator.fragment.FragmentBox;
import com.imoktranslator.fragment.FragmentController;
import com.imoktranslator.presenter.GeneralNotificationPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class GeneralNotificationActivity extends BaseActivity implements FragmentBox, HeaderView.BackButtonClickListener, GeneralNotificationPresenter.GeneralNotificationView {

    public static final String OPEN_ADD_FRIEND = "OPEN_ADD_FRIEND";

    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.txt_notification)
    OpenSansSemiBoldTextView txtNotification;
    @BindView(R.id.txt_friend_invitation)
    OpenSansSemiBoldTextView txtFriendInvitation;

    private FragmentController fm;
    private AddFriendFragment rightFragment;
    private CommonNotificationFragment leftFragment;

    private boolean isLeftFragmentShowing;

    private GeneralNotificationPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_general_notification;
    }

    @Override
    protected void initViews() {
        presenter = new GeneralNotificationPresenter(this, this);
        header.setTittle(getString(R.string.MH19_007));
        header.setCallback(this);

        boolean isOpenAddFriend = getIntent().getBooleanExtra(OPEN_ADD_FRIEND, false);

        fm = new FragmentController(getSupportFragmentManager(), getContainerViewId());

        rightFragment = AddFriendFragment.newInstance();
        leftFragment = CommonNotificationFragment.newInstance();
        if (isOpenAddFriend) {
            isLeftFragmentShowing = true;
            switchToRightFragment();
        } else {
            switchToLeftFragment();
        }
    }

    @Override
    public int getContainerViewId() {
        return R.id.container_notification;
    }

    @Override
    public void switchFragment(BaseFragment baseFragment, FragmentController.Option option) {
        if (fm != null && baseFragment != null && option != null) {
            fm.switchFragment(baseFragment, option);
        }
    }

    private void showFragment(BaseFragment fragment) {
        FragmentController.Option option = new FragmentController.Option.Builder()
                .useAnimation(false)
                .addToBackStack(false)
                .setType(FragmentController.Option.TYPE.REPLACE)
                .build();
        switchFragment(fragment, option);
    }

    @OnClick({R.id.txt_notification, R.id.txt_friend_invitation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_notification:
                switchToLeftFragment();
                break;
            case R.id.txt_friend_invitation:
                switchToRightFragment();
                break;
        }
    }

    private void switchToRightFragment() {
        if (!isLeftFragmentShowing) {
            return;
        }
        txtFriendInvitation.setTextColor(getResources().getColor(R.color.dark_sky_blue));
        txtFriendInvitation.setBackgroundColor(getResources().getColor(R.color.white));
        txtNotification.setTextColor(getResources().getColor(R.color.text_brown));
        txtNotification.setBackgroundColor(getResources().getColor(R.color.white));
        showFragment(rightFragment);
        isLeftFragmentShowing = false;
    }

    private void switchToLeftFragment() {
        if (isLeftFragmentShowing) {
            return;
        }
        txtNotification.setTextColor(getResources().getColor(R.color.dark_sky_blue));
        txtNotification.setBackgroundColor(getResources().getColor(R.color.white));
        txtFriendInvitation.setTextColor(getResources().getColor(R.color.text_brown));
        txtFriendInvitation.setBackgroundColor(getResources().getColor(R.color.white));
        showFragment(leftFragment);
        isLeftFragmentShowing = true;
    }

    public void setNotificationCount(int numOfNotification) {
        if (numOfNotification > 0) {
            txtNotification.setText(getString(R.string.MH47_002) + "(" + numOfNotification + ")");
        } else {
            txtNotification.setText(getString(R.string.MH47_002));
        }
    }

    public void setFriendRequestCount(int numFriendRequest) {
        if (numFriendRequest > 0) {
            txtFriendInvitation.setText(getString(R.string.MH35_011) + "(" + numFriendRequest + ")");
        } else {
            txtFriendInvitation.setText(getString(R.string.MH35_011));
        }
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @Override
    public void onCountNotification(int numFriendRequest) {
        setFriendRequestCount(numFriendRequest);
    }

    public static void startActivity(BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, GeneralNotificationActivity.class);
        baseActivity.startActivity(intent);
    }

}
