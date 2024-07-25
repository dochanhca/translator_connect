package com.example.translateconnector.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.imoktranslator.R;
import com.imoktranslator.activity.OrderManagementActivity;
import com.imoktranslator.activity.UserDetailActivity;
import com.imoktranslator.adapter.ListPriceAdapter;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.PriceModel;
import com.imoktranslator.presenter.ListPricePresenter;
import com.imoktranslator.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ListPriceFragment extends BaseFragment implements
        ListPricePresenter.ListPriceView, ListPriceAdapter.OnPriceClickListener {

    @BindView(R.id.img_sort_price)
    ImageView imgSortPrice;
    @BindView(R.id.layout_sort_by_price)
    LinearLayout layoutSortByPrice;
    @BindView(R.id.img_sort_time)
    ImageView imgSortTime;
    @BindView(R.id.layout_sort_by_time)
    LinearLayout layoutSortByTime;
    @BindView(R.id.img_sort_quality)
    ImageView imgSortQuality;
    @BindView(R.id.layout_sort_by_quality)
    LinearLayout layoutSortByQuality;
    @BindView(R.id.layout_sort_order)
    LinearLayout layoutSortOrder;
    @BindView(R.id.rcv_list_price)
    RecyclerView rcvListPrice;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private ListPricePresenter pricePresenter;
    private ListPriceAdapter listPriceAdapter;
    private ListPriceClickListener listPriceClickListener;
    private OrderModel orderModel;

    private int selectedPos;

    private OrderManagementActivity.SORT_BY sortBy;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = () -> {
        swipeRefreshLayout.setRefreshing(true);
        pricePresenter.getListPrice(orderModel.getOrderId(), sortBy.getValue(), sortBy.getType());
    };

    public static ListPriceFragment newInstance(OrderModel orderModel,
                                                ListPriceClickListener listPriceClickListener) {

        Bundle args = new Bundle();

        ListPriceFragment fragment = new ListPriceFragment();
        fragment.setArguments(args);
        fragment.orderModel = orderModel;
        fragment.listPriceClickListener = listPriceClickListener;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_price;
    }

    @Override
    protected void initViews() {
        pricePresenter = new ListPricePresenter(getActivity(), this);

        initListPrice();

        sortBy = OrderManagementActivity.SORT_BY.SORT_PRICE_DESC;
        pricePresenter.getListPrice(orderModel.getOrderId(), sortBy.getValue(), sortBy.getType());

        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

    }

    private void initListPrice() {
        listPriceAdapter = new ListPriceAdapter(getActivity().getApplicationContext(),
                new ArrayList<>(), orderModel, listPriceClickListener == null ? true : false);
        listPriceAdapter.setOnPriceClickListener(this);
        rcvListPrice.setAdapter(listPriceAdapter);
        rcvListPrice.addItemDecoration(new DividerItemDecoration(rcvListPrice.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }

    @OnClick({R.id.layout_sort_by_time, R.id.layout_sort_by_quality, R.id.layout_sort_by_price})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_sort_by_time:
                sortByTime();
                break;
            case R.id.layout_sort_by_quality:
                sortByQuality();
                break;
            case R.id.layout_sort_by_price:
                sortByPrice();
                break;
        }
    }

    private void sortByPrice() {
        sortBy = sortBy == OrderManagementActivity.SORT_BY.SORT_PRICE_DESC ?
                OrderManagementActivity.SORT_BY.SORT_PRICE_ASC : OrderManagementActivity.SORT_BY.SORT_PRICE_DESC;
        imgSortPrice.setImageResource(sortBy.getType().equals(Constants.SORT_ASC) ? R.drawable.ic_sort_asc
                : R.drawable.ic_sort_desc);
        updateSortBtnBg();
        pricePresenter.getListPrice(orderModel.getOrderId(), sortBy.getValue(),
                sortBy.getType());
    }

    private void sortByTime() {
        sortBy = sortBy == OrderManagementActivity.SORT_BY.SORT_DATE_DESC ?
                OrderManagementActivity.SORT_BY.SORT_DATE_ASC : OrderManagementActivity.SORT_BY.SORT_DATE_DESC;
        imgSortTime.setImageResource(sortBy.getType().equals(Constants.SORT_ASC) ? R.drawable.ic_sort_asc
                : R.drawable.ic_sort_desc);
        updateSortBtnBg();

        pricePresenter.getListPrice(orderModel.getOrderId(), sortBy.getValue(),
                sortBy.getType());
    }


    private void sortByQuality() {
        sortBy = sortBy == OrderManagementActivity.SORT_BY.SORT_SCORE_DESC ?
                OrderManagementActivity.SORT_BY.SORT_SCORE_ASC : OrderManagementActivity.SORT_BY.SORT_SCORE_DESC;
        imgSortQuality.setImageResource(sortBy.getType().equals(Constants.SORT_ASC) ? R.drawable.ic_sort_asc
                : R.drawable.ic_sort_desc);
        updateSortBtnBg();

        pricePresenter.getListPrice(orderModel.getOrderId(), sortBy.getValue(),
                sortBy.getType());
    }

    private void updateSortBtnBg() {
        switch (sortBy) {
            case SORT_PRICE_DESC:
                layoutSortByPrice.setBackgroundResource(R.drawable.bg_sort_selected);
                layoutSortByQuality.setBackgroundResource(R.drawable.bg_sort_normal);
                layoutSortByTime.setBackgroundResource(R.drawable.bg_sort_normal);
                break;
            case SORT_DATE_DESC:
                layoutSortByPrice.setBackgroundResource(R.drawable.bg_sort_normal);
                layoutSortByQuality.setBackgroundResource(R.drawable.bg_sort_normal);
                layoutSortByTime.setBackgroundResource(R.drawable.bg_sort_selected);
                break;
            case SORT_SCORE_DESC:
                layoutSortByPrice.setBackgroundResource(R.drawable.bg_sort_normal);
                layoutSortByQuality.setBackgroundResource(R.drawable.bg_sort_selected);
                layoutSortByTime.setBackgroundResource(R.drawable.bg_sort_normal);
                break;
        }
    }

    @Override
    public void onGetListPrices(List<PriceModel> priceModels) {
        swipeRefreshLayout.setRefreshing(false);
        listPriceAdapter.setData(priceModels);
    }

    @Override
    public void onGetListPricesError() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAcceptPrice(PriceModel priceModel) {
        if (listPriceClickListener != null) {
            listPriceClickListener.onAcceptedPrice(priceModel);
        }
    }

    @Override
    public void onDeletePrice() {
        listPriceAdapter.getData().remove(selectedPos);
        listPriceAdapter.notifyDataSetChanged();
    }

    @Override
    public void goChatScreen(String roomKey, int receiverId) {
        FragmentController.Option option = new FragmentController.Option.Builder()
                .useAnimation(false)
                .addToBackStack(true)
                .setType(FragmentController.Option.TYPE.ADD)
                .build();
        switchFragment(ChatFragment.newInstance(roomKey, true, false,
                receiverId, orderModel.getOrderId(), true)
                , option);
        listPriceClickListener.onGoChatScreen();
    }

    @Override
    public void onAcceptPriceClick(int pos) {
        PriceModel priceModel = listPriceAdapter.getData().get(pos);
        pricePresenter.acceptPrice(priceModel);
    }

    @Override
    public void onDeletePriceClick(int pos) {
        this.selectedPos = pos;
        PriceModel priceModel = listPriceAdapter.getData().get(pos);
        pricePresenter.deletePrice(priceModel.getOrderId(), priceModel.getId());
    }

    @Override
    public void onChatClick(int pos) {
        PriceModel priceModel = listPriceAdapter.getData().get(pos);
        pricePresenter.startOrderChat(priceModel.getUserId(),
                priceModel.getOrderId());
    }

    @Override
    public void onTranslatorClick(int pos) {
        PriceModel priceModel = listPriceAdapter.getData().get(pos);
        UserDetailActivity.startActivity(getBaseActivity(), priceModel.getUserId());
    }

    public interface ListPriceClickListener {
        void onAcceptedPrice(PriceModel priceModel);

        void onGoChatScreen();
    }
}
