package com.example.translateconnector.fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imoktranslator.R;
import com.imoktranslator.activity.ListPriceActivity;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SelectionView;
import com.imoktranslator.dialog.UpdatePriceDialog;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.network.param.UpdatePriceParam;
import com.imoktranslator.presenter.OrderDetailPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DateTimeUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

public class OrderDetailFragment extends BaseFragment implements
        UpdatePriceDialog.OnUpdatePriceDialogClickListener, OrderDetailPresenter.OrderDetailView {

    @BindView(R.id.txt_price)
    OpenSansSemiBoldTextView txtPrice;
    @BindView(R.id.layout_price)
    LinearLayout layoutPrice;
    @BindView(R.id.txt_order_name)
    OpenSansSemiBoldTextView txtOrderName;
    @BindView(R.id.txt_translation_type)
    OpenSansSemiBoldTextView txtTranslationType;
    @BindView(R.id.txt_address)
    OpenSansSemiBoldTextView txtAddress;
    @BindView(R.id.select_place)
    SelectionView selectPlace;
    @BindView(R.id.txt_from_label)
    OpenSansBoldTextView txtFromLabel;
    @BindView(R.id.txt_from_date)
    TextView txtFromDate;
    @BindView(R.id.txt_from_time)
    TextView txtFromTime;
    @BindView(R.id.txt_to_label)
    OpenSansBoldTextView txtToLabel;
    @BindView(R.id.txt_to_date)
    TextView txtToDate;
    @BindView(R.id.txt_to_time)
    TextView txtToTime;
    @BindView(R.id.txt_expiration_label)
    OpenSansBoldTextView txtExpirationLabel;
    @BindView(R.id.txt_expiration_date)
    TextView txtExpirationDate;
    @BindView(R.id.txt_expiration_time)
    TextView txtExpirationTime;
    @BindView(R.id.txt_experience_label)
    OpenSansBoldTextView txtExperienceLabel;
    @BindView(R.id.txt_experience)
    OpenSansBoldTextView txtExperience;
    @BindView(R.id.txt_gender_label)
    OpenSansBoldTextView txtGenderLabel;
    @BindView(R.id.txt_gender)
    OpenSansBoldTextView txtGender;
    @BindView(R.id.txt_quality_label)
    OpenSansBoldTextView txtQualityLabel;
    @BindView(R.id.rating_bar)
    AppCompatRatingBar ratingBar;
    @BindView(R.id.txt_detail)
    OpenSansTextView txtDetail;
    @BindView(R.id.txt_detail_label)
    OpenSansTextView txtDetailLabel;
    @BindView(R.id.btn_send_price)
    TextView btnSendPrice;
    @BindView(R.id.txt_language_name)
    TextView txtTransLang;
    @BindView(R.id.txt_view_price_history)
    TextView txtViewPriceHistory;

    private OrderModel orderModel;
    private PersonalInfo personalInfo;
    private String[] translateTypes;
    private String[] transLanguages;
    private String[] genders;
    private String[] experiences;
    private String[] currencies;

    private OrderDetailPresenter presenter;

    public static OrderDetailFragment newInstance(OrderModel order) {

        Bundle args = new Bundle();

        OrderDetailFragment fragment = new OrderDetailFragment();
        fragment.orderModel = order;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_order_detail;
    }

    @Override
    protected void initViews() {
        personalInfo = LocalSharedPreferences.getInstance(getActivity()).getPersonalInfo();
        presenter = new OrderDetailPresenter(getActivity(), this);

        translateTypes = getResources().getStringArray(R.array.arr_translation_type);
        genders = getResources().getStringArray(R.array.arr_gender);
        experiences = getResources().getStringArray(R.array.arr_experience);
        currencies = getResources().getStringArray(R.array.arr_currency);
        transLanguages = getResources().getStringArray(R.array.arr_language);
        initLabels();
        fillDataToViews();
    }

    private void fillDataToViews() {

        if (orderModel.getCurrency() != 0) {
            txtPrice.setText(Utils.formatCurrency(orderModel.getPrice()) + " " + currencies[orderModel.getCurrency() - 1]);
        }
        layoutPrice.setVisibility(orderModel.getCurrency() != 0 ? View.VISIBLE : View.GONE);
        txtOrderName.setText(orderModel.getName());
        txtTranslationType.setText(translateTypes[orderModel.getTranslationType() - 1]);
        txtTransLang.setText(transLanguages[orderModel.getTranslationLang() - 1]);
        txtAddress.setText(getAddress());

        txtFromDate.setText(getString(R.string.MH11_022) + ": " +
                DateTimeUtils.getDMYFormat(orderModel.getFromDate()));
        txtFromTime.setText(getString(R.string.MH11_023) + ": " +
                DateTimeUtils.getTimeFormat(orderModel.getFromDate()));

        txtToDate.setText(getString(R.string.MH11_022) + ": " +
                DateTimeUtils.getDMYFormat(orderModel.getToDate()));
        txtToTime.setText(getString(R.string.MH11_023) + ": " +
                DateTimeUtils.getTimeFormat(orderModel.getToDate()));

        txtExpirationDate.setText(getString(R.string.MH11_022) + ": " +
                DateTimeUtils.getDMYFormat(orderModel.getExpirationDate()));
        txtExpirationTime.setText(getString(R.string.MH11_023) + ": " +
                DateTimeUtils.getTimeFormat(orderModel.getExpirationDate()));

        txtExperience.setText(orderModel.getExperience() == 0 ? "" : experiences[orderModel.getExperience() - 1]);
        txtGender.setText(orderModel.getGender() == 0 ? "" : genders[orderModel.getGender() - 1]);
        ratingBar.setRating((float) orderModel.getQuality());
        ratingBar.setEnabled(false);
        txtDetail.setText(orderModel.getDescription());

        btnSendPrice.setVisibility(getCanUpdatePrice() ? View.VISIBLE : View.GONE);

        btnSendPrice.setText(orderModel.getStatusPriceForTrans() == OrderModel.ORDER_PRICE_STATUS.UPDATED_PRICE
                ? getString(R.string.MH22_023) : getString(R.string.MH22_021));

        // hide button open map for sync with iOS version
        selectPlace.setVisibility(View.GONE);

        if (orderModel.getUserId() == personalInfo.getId() &&
                (orderModel.getOrderStatus() == OrderModel.ORDER_STATUS.TRADING_ORDER
                        || orderModel.getOrderStatus() == OrderModel.ORDER_STATUS.FINISHED_ORDER)) {
            txtViewPriceHistory.setVisibility(View.VISIBLE);
        } else {
            txtViewPriceHistory.setVisibility(View.GONE);
        }
    }

    private boolean getCanUpdatePrice() {
        String currentDate = DateTimeUtils.getCurrentDate(DateTimeUtils.YMD_HMS_FORMAT);
        if (!personalInfo.getId().equals(orderModel.getUserId()) &&
                personalInfo.isTranslator() &&
                orderModel.getOrderStatus() == OrderModel.ORDER_STATUS.SEARCHING_ORDER &&
                orderModel.getStatusPriceForTrans() != OrderModel.ORDER_PRICE_STATUS.CANCELED_PRICE
                && DateTimeUtils.compareDate(currentDate, orderModel.getToDate(), DateTimeUtils.YMD_HMS_FORMAT)
                == DateTimeUtils.BEFORE_DATE) {
            return true;
        }

        return false;
    }

    private void initLabels() {
        txtFromLabel.append(":");
        txtToLabel.append(":");
        txtExpirationLabel.append(":");
        txtExperienceLabel.append(":");
        txtGenderLabel.append(":");
        txtQualityLabel.append(":");
        txtDetailLabel.append(":");
    }

    private String getAddress() {
        String address;
        if (orderModel.getAddressType() == Constants.ADDRESS_TYPE_FILTER) {
            address = TextUtils.isEmpty(orderModel.getCity()) ? orderModel.getCountry() :
                    orderModel.getCity() + ", " + orderModel.getCountry();
        } else {
            address = orderModel.getAddress();
        }
        return address;
    }

    private void openMap() {
        FragmentController.Option option = new FragmentController.Option.Builder()
                .useAnimation(false)
                .addToBackStack(true)
                .setType(FragmentController.Option.TYPE.ADD)
                .build();
        switchFragment(MapFragment.newInstance(orderModel.getLatitude(),
                orderModel.getLongitude(), orderModel.getAddress()), option);
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }

    @OnClick({R.id.btn_send_price, R.id.select_place, R.id.txt_view_price_history})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send_price:
                UpdatePriceDialog.showDialog(getFragmentManager(), this);
                break;
            case R.id.select_place:
                openMap();
                break;
            case R.id.txt_view_price_history:
                ListPriceActivity.startActivity(getBaseActivity(), orderModel);
                break;
        }
    }

    @Override
    public void onUpdatePriceClickListener(double price, int currency) {
        orderModel.setPrice(price);
        orderModel.setCurrency(currency);
        presenter.updatePrice(orderModel, orderModel.getStatusPriceForTrans()
                == OrderModel.ORDER_PRICE_STATUS.UPDATED_PRICE ? false : true, personalInfo);
    }

    @Override
    public void onUpdatePrice(UpdatePriceParam param) {
        this.orderModel.setCurrency(param.getCurrency());
        this.orderModel.setPrice(param.getPrice());
        fillDataToViews();
        btnSendPrice.setText(getString(R.string.MH22_023));
        Toast.makeText(getActivity(), getString(R.string.MH13_003), Toast.LENGTH_SHORT).show();
    }
}
