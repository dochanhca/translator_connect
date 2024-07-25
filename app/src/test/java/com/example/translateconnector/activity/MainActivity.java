package com.example.translateconnector.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.imoktranslator.R;
import com.imoktranslator.adapter.SideMenuAdapter;
import com.imoktranslator.customview.NavigationLayout;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.fragment.FragmentBox;
import com.imoktranslator.fragment.FragmentController;
import com.imoktranslator.fragment.HomeFragment;
import com.imoktranslator.fragment.NewsFeedFragment;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.UserNeedReview;
import com.imoktranslator.network.response.GeneralInfoResponse;
import com.imoktranslator.presenter.BaseProfileActivity;
import com.imoktranslator.presenter.MainPresenter;
import com.imoktranslator.service.SyncUserInfoService;
import com.imoktranslator.utils.AppAction;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.LocaleHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements MainPresenter.MainView, FragmentBox {
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.navigation_message)
    NavigationLayout navigationMessage;
    @BindView(R.id.navigation_order)
    NavigationLayout navigationOrder;
    @BindView(R.id.navigation_create_order)
    NavigationLayout navigationCreateOrder;
    @BindView(R.id.navigation_notify)
    NavigationLayout navigationNotify;
    @BindView(R.id.navigation_bar)
    LinearLayout navigationBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.rcv_side_menu)
    RecyclerView rcvSideMenu;
    @BindView(R.id.txt_user_name)
    TextView txtUserName;
    @BindView(R.id.img_user_avatar)
    CircleImageView imgUserAvatar;
    @BindView(R.id.main_container)
    FrameLayout mainContainer;

    private SideMenuAdapter sideMenuAdapter;
    private MainPresenter presenter;
    private PersonalInfo personalInfo;
    private List<OrderModel> listOrderNeededExtend;
    private int orderNeedExpandIndex;

    private FragmentController fm;

    private BroadcastReceiver unreadOrderNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int unread = Integer.parseInt(navigationOrder.getBoxValue());
            navigationOrder.showBoxValue(String.valueOf(++unread));
        }
    };


    private BroadcastReceiver unreadNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int number = Integer.parseInt(navigationNotify.getBoxValue());
            navigationNotify.showBoxValue(String.valueOf(++number));
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        presenter = new MainPresenter(this, this);
        initRecyclerSideMenu();

        presenter.countUnreadMessage();
        startService(new Intent(this, SyncUserInfoService.class));


        fm = new FragmentController(getSupportFragmentManager(), getContainerViewId());

        FragmentController.Option option = new FragmentController.Option.Builder()
                .useAnimation(false)
                .addToBackStack(false)
                .setType(FragmentController.Option.TYPE.REPLACE)
                .build();
        switchFragment(HomeFragment.newInstance(), option);

        presenter.getGeneralInfo();
        LocalBroadcastManager.getInstance(this).
                registerReceiver(unreadOrderNotificationReceiver,
                        new IntentFilter(AppAction.ACTION_UPDATE_UNREAD_ORDER_NOTIFICATION));

        LocalBroadcastManager.getInstance(this).
                registerReceiver(unreadNotificationReceiver,
                        new IntentFilter(AppAction.ACTION_UPDATE_UNREAD_NOTIFICATION));
    }

    @Override
    public int getContainerViewId() {
        return R.id.main_container;
    }

    @Override
    public void switchFragment(BaseFragment baseFragment, FragmentController.Option option) {
        if (fm != null && baseFragment != null && option != null) {
            fm.switchFragment(baseFragment, option);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.getGeneralInfo();
        LocalBroadcastManager.getInstance(this).
                registerReceiver(unreadOrderNotificationReceiver,
                        new IntentFilter(AppAction.ACTION_UPDATE_UNREAD_ORDER_NOTIFICATION));

        LocalBroadcastManager.getInstance(this).
                registerReceiver(unreadNotificationReceiver,
                        new IntentFilter(AppAction.ACTION_UPDATE_UNREAD_NOTIFICATION));
    }


    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(unreadOrderNotificationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(unreadNotificationReceiver);
    }

    private void initRecyclerSideMenu() {
        List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.menu_item));
        sideMenuAdapter = new SideMenuAdapter(getApplicationContext(), menuItems);
        sideMenuAdapter.setOnItemClickListener((view, position) -> onClickMenuItem(menuItems, position));
        rcvSideMenu.setAdapter(sideMenuAdapter);
    }

    public void onClickMenuItem(List<String> menuItems, int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        new Handler().postDelayed(() -> {
            if (menuItems.get(position).equals(getResources().getString(R.string.MH21_012))) {
                if (personalInfo.isTranslator()) {
                    openTranslatorProfile();
                } else {
                    openUserProfile();
                }
            } else if (menuItems.get(position).equals(getString(R.string.MH21_003))) {
                OrderManagementActivity.startActivity(this, OrderManagementActivity.ALL_ORDER);
            } else if (menuItems.get(position).equals(getString(R.string.MH21_013))) {
                showNewsFeed();
            } else if (menuItems.get(position).equals(getString(R.string.MH21_005))) {
                SearchFriendActivity.startActivity(this);
            } else if (menuItems.get(position).equals(getString(R.string.MH21_006))) {
                ContactActivity.startActivity(this);
            } else if (menuItems.get(position).equals(getString(R.string.MH21_007))) {
                AccountSettingActivity.startActivity(this, personalInfo.getSettingLanguage());
            } else if (menuItems.get(position).equals(getString(R.string.MH21_008))) {
                NotificationSettingActivity.startActivity(this);

            } else if (menuItems.get(position).equals(getString(R.string.MH21_011))) {
                logout();
            }
        }, 200);
        drawerLayout.closeDrawer(Gravity.END);
    }

    private void showNewsFeed() {
        NewsFeedFragment myFragment = (NewsFeedFragment)
                getSupportFragmentManager().findFragmentByTag(NewsFeedFragment.class.getSimpleName());
        if (myFragment != null && myFragment.isVisible()) {
            return;
        }

        FragmentController.Option option = new FragmentController.Option.Builder()
                .useAnimation(false)
                .addToBackStack(true)
                .setType(FragmentController.Option.TYPE.ADD)
                .build();
        switchFragment(NewsFeedFragment.newInstance(), option);
    }

    public void logout() {
        LocalSharedPreferences.getInstance(getApplicationContext()).clearAll();
        //log-out firebase chat
        FirebaseAuth.getInstance().signOut();
        //clear fcm token
        removeFCMToken();
        //reset local language setting
        LocaleHelper.clearSharedPreferences(getApplicationContext());
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void removeFCMToken() {
        new Thread((() -> {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
                FirebaseInstanceId.getInstance().getToken();
            } catch (IOException e) {
                Log.e("RemoveFCMTokenFailed: ", e.getMessage());
            }
        })).start();
    }


    @OnClick({R.id.navigation_order, R.id.navigation_create_order, R.id.img_open_drawer,
            R.id.navigation_notify, R.id.navigation_message})
    public void onViewClicked(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (view.getId()) {
            case R.id.navigation_message:
                MessageManagementActivity.startActivity(MainActivity.this);
                break;
            case R.id.navigation_order:
                startActivity(new Intent(MainActivity.this, OrderNotificationActivity.class));
                break;
            case R.id.navigation_create_order:
                checkInfoValid();
                break;
            case R.id.img_open_drawer:
                drawerLayout.openDrawer(Gravity.END);
                break;
            case R.id.navigation_notify:
                GeneralNotificationActivity.startActivity(this);
                break;
            default:
                break;
        }
    }

    private void checkInfoValid() {
        if (personalInfo.canCreateOrder()) {
            startActivity(new Intent(this, CreateOrderActivity.class));
        } else {
            showDialogConfirmUpdateProfile();
        }
    }

    private void showDialogConfirmUpdateProfile() {
        showNotifyDialog(getString(R.string.TB_1036), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                if (personalInfo.isTranslator()) {
                    openTranslatorProfile();
                } else {
                    openUserProfile();
                }
            }
        });
    }

    public void fillUserData(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
        if (personalInfo != null) {
            if (!TextUtils.isEmpty(personalInfo.getName())) {
                txtUserName.setText(personalInfo.getName());
            }

            if (!TextUtils.isEmpty(personalInfo.getAvatar())) {
                loadUserAvatar(personalInfo.getAvatar(), imgUserAvatar);
            } else {
                imgUserAvatar.setImageResource(R.drawable.img_default_avatar);
            }
        }
    }

    private void openTranslatorProfile() {
        Intent intent = new Intent(this, TranslatorProfileActivity.class);
        intent.putExtra(BaseProfileActivity.KEY_PERSONAL_INFO, personalInfo);
        startActivity(intent);
    }

    private void openUserProfile() {
        Intent intent = new Intent(this, MyProfileActivity.class);
        intent.putExtra(BaseProfileActivity.KEY_PERSONAL_INFO, personalInfo);
        startActivity(intent);
    }

    private void openVotePartnerScreen(UserNeedReview userNeedReview) {
        Intent intent = new Intent(this, VotePartnerActivity.class);
        intent.putExtra(VotePartnerActivity.USER_NEED_REVIEW, userNeedReview);
        startActivity(intent);
    }

    private void handleExpandOrder(List<OrderModel> listOrderNeededExtend) {
        this.listOrderNeededExtend = listOrderNeededExtend;
        this.orderNeedExpandIndex = 0;
        confirmExpandOrder();
    }

    private void confirmExpandOrder() {
        showNotifyDialog(String.format(getString(R.string.TB_1032), listOrderNeededExtend.get(orderNeedExpandIndex).getName())
                , new NotifyDialog.OnNotifyCallback() {
                    @Override
                    public void onCancel() {
                        presenter.cancelExpand(listOrderNeededExtend.get(orderNeedExpandIndex).getOrderId());
                    }

                    @Override
                    public void onOk(Object... obj) {
                        presenter.acceptExpand(listOrderNeededExtend.get(orderNeedExpandIndex).getOrderId());
                    }
                });
    }

    private void handleGeneralInfo(GeneralInfoResponse generalInfo) {
        navigationOrder.showBoxValue(String.valueOf(generalInfo.getGeneralInfo().getTotalOrderNotificationUnder()));
        navigationNotify.showBoxValue(String.valueOf(generalInfo.getGeneralInfo().getTotalNotificationUnread()));

        if (generalInfo.getGeneralInfo().getListUserNeedReview() != null &&
                generalInfo.getGeneralInfo().getListUserNeedReview().size() > 0) {
            openVotePartnerScreen(generalInfo.getGeneralInfo().getListUserNeedReview().get(0));
        } else if (generalInfo.getGeneralInfo().getListOrderNeededExtend() != null &&
                generalInfo.getGeneralInfo().getListOrderNeededExtend().size() > 0) {
            handleExpandOrder(generalInfo.getGeneralInfo().getListOrderNeededExtend());
        }

        List<OrderModel> orderExtendeds = generalInfo.getGeneralInfo().getListOrderExtended();
        if (orderExtendeds != null &&
                orderExtendeds.size() > 0) {

            StringBuilder orderNames = new StringBuilder();
            for (int i = 0; i < orderExtendeds.size(); i++) {
                OrderModel orderModel = orderExtendeds.get(i);
                orderNames.append(orderModel.getName());
                if (i < orderExtendeds.size() - 1) {
                    orderNames.append(", ");
                }
            }
            showNotifyDialog(String.format(getString(R.string.TB_1042), orderNames));
        }
    }

    @Override
    public void getGeneralInfo(GeneralInfoResponse generalInfoResponse) {
        handleGeneralInfo(generalInfoResponse);
    }

    @Override
    public void expandTimeOfNextOrder() {
        orderNeedExpandIndex++;
        if (orderNeedExpandIndex < listOrderNeededExtend.size()) {
            confirmExpandOrder();
        }
    }

    @Override
    public void onUnreadMessageCount(int number) {
        if (navigationMessage != null) {
            navigationMessage.showBoxValue(String.valueOf(number));
        }
    }
}
