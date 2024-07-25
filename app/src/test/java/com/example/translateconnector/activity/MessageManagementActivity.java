package com.example.translateconnector.activity;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.fragment.FragmentBox;
import com.imoktranslator.fragment.FragmentController;
import com.imoktranslator.fragment.GroupChatFragment;
import com.imoktranslator.fragment.PersonalMessageFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class MessageManagementActivity extends BaseActivity implements
        HeaderView.BackButtonClickListener, FragmentBox {

    @BindView(R.id.header_view)
    HeaderView headerView;
    @BindView(R.id.txt_personal_mess)
    OpenSansSemiBoldTextView txtPersonalMess;
    @BindView(R.id.txt_group_chat)
    OpenSansSemiBoldTextView txtGroupChat;
    @BindView(R.id.container_mesage_management)
    FrameLayout containerMesageManagement;

    private FragmentController fm;
    private boolean isRightFragmentShowing;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_messages;
    }

    @Override
    protected void initViews() {
        fm = new FragmentController(getSupportFragmentManager(), getContainerViewId());
        showFragment(PersonalMessageFragment.newInstance());
        headerView.setTittle(getString(R.string.MH18_001));
        headerView.setCallback(this);
        headerView.setImgRightRes(R.drawable.ic_create_message);
        headerView.setBtnRightClick(v -> NewGroupChatActivity.startActivity(this));
        headerView.setBtnRightVisible(View.GONE);
    }

    @Override
    public void backButtonClicked() {
        this.finish();
    }


    public static void startActivity(BaseActivity activity) {
        Intent intent = new Intent(activity, MessageManagementActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public int getContainerViewId() {
        return R.id.container_mesage_management;
    }

    @Override
    public void switchFragment(BaseFragment baseFragment, FragmentController.Option option) {
        if (fm != null && baseFragment != null && option != null) {
            fm.switchFragment(baseFragment, option);
        }
    }

    @OnClick({R.id.txt_personal_mess, R.id.txt_group_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_personal_mess:
                switchToLeftFragment();
                break;
            case R.id.txt_group_chat:
                switchToRightFragment();
                break;
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

    private void switchToRightFragment() {
        if (isRightFragmentShowing)
            return;
        txtPersonalMess.setTextColor(getResources().getColor(R.color.text_brown));
        txtGroupChat.setTextColor(getResources().getColor(R.color.dark_sky_blue));
        showFragment(GroupChatFragment.newInstance());
        isRightFragmentShowing = true;
        headerView.setBtnRightVisible(View.VISIBLE);
    }

    private void switchToLeftFragment() {
        if (!isRightFragmentShowing)
            return;
        txtPersonalMess.setTextColor(getResources().getColor(R.color.dark_sky_blue));
        txtGroupChat.setTextColor(getResources().getColor(R.color.text_brown));
        showFragment(PersonalMessageFragment.newInstance());
        isRightFragmentShowing = false;
        headerView.setBtnRightVisible(View.GONE);
    }
}
