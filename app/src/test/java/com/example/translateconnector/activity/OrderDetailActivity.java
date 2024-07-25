package com.example.translateconnector.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.fragment.ChatFragment;
import com.imoktranslator.fragment.FragmentBox;
import com.imoktranslator.fragment.FragmentController;
import com.imoktranslator.fragment.ListPriceFragment;
import com.imoktranslator.fragment.OrderDetailFragment;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.PriceModel;
import com.imoktranslator.model.firebase.OfferPrice;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import butterknife.BindView;
import butterknife.OnClick;

public class OrderDetailActivity extends BaseActivity implements
        HeaderView.BackButtonClickListener, FragmentBox, ListPriceFragment.ListPriceClickListener {

    public static final String ORDER_KEY = "ORDER_KEY";
    public static final String RECEIVER_ID_KEY = "RECEIVER_ID_KEY";

    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.container_order_detail)
    FrameLayout containerOrderDetail;
    @BindView(R.id.layout_navigation)
    LinearLayout layoutNavigation;
    @BindView(R.id.txt_list_price)
    OpenSansSemiBoldTextView txtListPrice;
    @BindView(R.id.txt_order_detail)
    OpenSansSemiBoldTextView txtOrderDetail;

    private FragmentController fm;
    private boolean isRightFragmentShowing;
    private OrderModel orderModel;
    private PersonalInfo personalInfo;

    private BaseFragment leftFragment;
    private int receiverId;

    public static void startActivity(BaseActivity activity, OrderModel orderModel, int receiverId) {
        Intent intent = new Intent(activity, OrderDetailActivity.class);
        intent.putExtra(ORDER_KEY, orderModel);
        //receiverId available when start from Notification Screen only.
        intent.putExtra(RECEIVER_ID_KEY, receiverId);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void initViews() {
        header.setTittle(getString(R.string.MH23_001));
        header.setCallback(this);

        personalInfo = LocalSharedPreferences.getInstance(getApplicationContext()).getPersonalInfo();
        orderModel = getIntent().getParcelableExtra(ORDER_KEY);
        receiverId = getIntent().getIntExtra(RECEIVER_ID_KEY, -1);

        fm = new FragmentController(getSupportFragmentManager(), getContainerViewId());

        handleViewByUserType();
    }

    private void handleViewByUserType() {
        boolean isOwner = personalInfo.getId() == orderModel.getUserId();

        if (isShowChatForTrans() || isShowChatForUser()) {
            //Show chat
            txtListPrice.setText(getString(R.string.MH18_001));
            startChatFragment(isOwner ? orderModel.getAcceptedTransId() : personalInfo.getId(),
                    isShowChatForUser() ? orderModel.getAcceptedTransId() : orderModel.getUserId()
                    , false);
        } else if ((orderModel.getOrderStatus() == OrderModel.ORDER_STATUS.FINISHED_ORDER && isOwner)
                || isShowChatHistoryForTrans()) {
            //Show chat history
            txtListPrice.setText(getString(R.string.MH23_002));
            startChatFragment(isOwner ? orderModel.getAcceptedTransId() : personalInfo.getId(),
                    isOwner ? orderModel.getAcceptedTransId() : personalInfo.getId(), true);
        } else if (isOwner && orderModel.getOrderStatus() == OrderModel.ORDER_STATUS.SEARCHING_ORDER) {
            layoutNavigation.setVisibility(View.VISIBLE);
            if (receiverId > 0) {
                txtListPrice.setText(getString(R.string.MH18_001));
                startChatFragment(receiverId, receiverId, false);
            } else {
                txtListPrice.setText(getString(R.string.MH12_001));
                leftFragment = ListPriceFragment.newInstance(orderModel, this);
                showFragment(leftFragment);
            }
        } else {
            layoutNavigation.setVisibility(View.GONE);
            showFragment(OrderDetailFragment.newInstance(orderModel));
        }
    }

    private boolean isShowChatForUser() {
        return personalInfo.getId() == orderModel.getUserId() // isOwner
                && orderModel.getOrderStatus() == OrderModel.ORDER_STATUS.TRADING_ORDER;
    }

    private boolean isShowChatForTrans() {
        return personalInfo.getId() != orderModel.getUserId() && isOrderStatusForChat() &&
                orderModel.getStatusPriceForTrans() == OrderModel.ORDER_PRICE_STATUS.UPDATED_PRICE;
    }

    private boolean isOrderStatusForChat() {
        return orderModel.getOrderStatus() == OrderModel.ORDER_STATUS.SEARCHING_ORDER ||
                orderModel.getOrderStatus() == OrderModel.ORDER_STATUS.TRADING_ORDER;
    }

    private boolean isShowChatHistoryForTrans() {
        return personalInfo.getId() != orderModel.getUserId() && //isTranslator
                (orderModel.getOrderStatus() == OrderModel.ORDER_STATUS.FINISHED_ORDER &&
                        orderModel.getAcceptedTransId() == personalInfo.getId())
                || (orderModel.getStatusPriceForTrans() == OrderModel.ORDER_PRICE_STATUS.CANCELED_PRICE);
    }


    private void startChatFragment(int transId, int receiverId, boolean isViewOnly) {
        DialogUtils.showProgress(this);
        FireBaseDataUtils.getInstance().getOrderRoomById(orderModel.getOrderId(), new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    leftFragment = ChatFragment.newInstance("", true,
                            true, receiverId, orderModel.getOrderId());
                }
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    OfferPrice offerPrice = dsp.getValue(OfferPrice.class);
                    if (offerPrice.getTransID() == transId) {
                        String roomKey = offerPrice.getRoomKey();
                        leftFragment = ChatFragment.newInstance(roomKey, true, isViewOnly,
                                receiverId, orderModel.getOrderId());
                        showFragment(leftFragment);
                        break;
                    }
                }
                DialogUtils.hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                DialogUtils.hideProgress();
            }
        });
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
        txtListPrice.setTextColor(getResources().getColor(R.color.dark_sky_blue));
        txtListPrice.setBackgroundColor(getResources().getColor(R.color.white));
        txtOrderDetail.setTextColor(getResources().getColor(R.color.white));
        txtOrderDetail.setBackgroundColor(getResources().getColor(R.color.medium_blue));
        showFragment(OrderDetailFragment.newInstance(orderModel));
        isRightFragmentShowing = true;
    }

    private void switchToLeftFragment() {
        if (!isRightFragmentShowing)
            return;
        txtListPrice.setTextColor(getResources().getColor(R.color.white));
        txtListPrice.setBackgroundColor(getResources().getColor(R.color.medium_blue));
        txtOrderDetail.setTextColor(getResources().getColor(R.color.dark_sky_blue));
        txtOrderDetail.setBackgroundColor(getResources().getColor(R.color.white));
        showFragment(leftFragment);
        isRightFragmentShowing = false;
    }

    @Override
    public void backButtonClicked() {
        this.finish();
    }

    @Override
    public int getContainerViewId() {
        return R.id.container_order_detail;
    }

    @Override
    public void switchFragment(BaseFragment baseFragment, FragmentController.Option option) {
        if (fm != null && baseFragment != null && option != null) {
            fm.switchFragment(baseFragment, option);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // don't super to prevent
        // java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
    }

    @OnClick({R.id.txt_list_price, R.id.txt_order_detail})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_list_price:
                switchToLeftFragment();
                break;
            case R.id.txt_order_detail:
                switchToRightFragment();
                break;
        }
    }

    @Override
    public void onAcceptedPrice(PriceModel priceModel) {
        isRightFragmentShowing = false;
        orderModel.setOrderStatus(OrderModel.ORDER_STATUS.TRADING_ORDER);
        startChatFragment(priceModel.getUserId(), priceModel.getUserId(), false);
        txtListPrice.setText(getString(R.string.MH18_001));
    }

    @Override
    public void onGoChatScreen() {
        txtListPrice.setText(getString(R.string.MH18_001));
    }

    public void setLeftFragmentTitle(String title) {
        txtListPrice.setText(title);
    }
}
