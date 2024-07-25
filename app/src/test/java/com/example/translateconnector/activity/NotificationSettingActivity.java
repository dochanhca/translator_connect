package com.example.translateconnector.activity;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.model.NotificationSetting;
import com.imoktranslator.presenter.NotificationSettingPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class NotificationSettingActivity extends BaseActivity implements
        NotificationSettingPresenter.NotificationSettingView {

    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.switch_post)
    Switch switchPost;
    @BindView(R.id.switch_message)
    Switch switchMessage;
    @BindView(R.id.switch_price)
    Switch switchPrice;
    @BindView(R.id.switch_find_translator)
    Switch switchFindTranslator;
    @BindView(R.id.switch_register_translator)
    Switch switchRegisterTranslator;
    @BindView(R.id.switch_friend_request)
    Switch switchFriendRequest;
    @BindView(R.id.switch_best_friend)
    Switch switchBestFriend;
    @BindView(R.id.layout_setting)
    LinearLayout layoutSetting;
    @BindView(R.id.txt_setting)
    OpenSansBoldTextView txtSetting;

    private NotificationSettingPresenter presenter;
    private NotificationSetting notificationSetting;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notification_setting;
    }

    @Override
    protected void initViews() {
        header.setCallback(() -> onBackPressed());
        presenter = new NotificationSettingPresenter(this, this);
        presenter.getNotificationSetting();
    }

    @OnClick(R.id.txt_setting)
    public void onViewClicked() {
        updateSetting();
    }

    @Override
    public void onGetNotificationSetting(NotificationSetting notificationSetting) {
        this.notificationSetting = notificationSetting;
        fillSetting();
    }

    @Override
    public void onSettingNotification() {
// Show toast
    }

    private void updateSetting() {
        if (notificationSetting == null) {
            notificationSetting = new NotificationSetting();
        }
        notificationSetting.setNotifyPost(switchPost.isChecked() ? 1 : 0);
        notificationSetting.setNotifyMessage(switchMessage.isChecked() ? 1 : 0);
        notificationSetting.setNotifyUpdatePrice(switchPrice.isChecked() ? 1 : 0);
        notificationSetting.setNotifyFindTranslator(switchFindTranslator.isChecked() ? 1 : 0);
        notificationSetting.setNotifyRegisterTranslator(switchRegisterTranslator.isChecked() ? 1 : 0);
        notificationSetting.setNotifyAddFriend(switchFriendRequest.isChecked() ? 1 : 0);
        notificationSetting.setNotifyBestFriend(switchBestFriend.isChecked() ? 1 : 0);
        presenter.settingNotification(notificationSetting);
    }

    private void fillSetting() {
        if (notificationSetting == null) {
            return;
        }
        switchPost.setChecked(notificationSetting.getNotifyPost() == 1);
        switchMessage.setChecked(notificationSetting.getNotifyMessage() == 1);
        switchPrice.setChecked(notificationSetting.getNotifyUpdatePrice() == 1);
        switchFindTranslator.setChecked(notificationSetting.getNotifyFindTranslator() == 1);
        switchRegisterTranslator.setChecked(notificationSetting.getNotifyRegisterTranslator() == 1);
        switchFriendRequest.setChecked(notificationSetting.getNotifyAddFriend() == 1);
        switchBestFriend.setChecked(notificationSetting.getNotifyBestFriend() == 1);
    }

    public static void startActivity(BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, NotificationSettingActivity.class);
        baseActivity.startActivity(intent);
    }
}
