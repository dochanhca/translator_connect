package com.example.translateconnector.adapter;

import static com.imoktranslator.model.OrderModel.ORDER_TYPE.CANCELED_ORDER;
import static com.imoktranslator.model.OrderModel.ORDER_TYPE.EXPIRED_ORDER;
import static com.imoktranslator.model.OrderModel.ORDER_TYPE.FINISHED_ORDER;
import static com.imoktranslator.model.OrderModel.ORDER_TYPE.NEW_ORDER;
import static com.imoktranslator.model.OrderModel.ORDER_TYPE.OWNER_TRADING_ORDER;
import static com.imoktranslator.model.OrderModel.ORDER_TYPE.REJECTED_ORDER;
import static com.imoktranslator.model.OrderModel.ORDER_TYPE.SEARCHING_ORDER;
import static com.imoktranslator.model.OrderModel.ORDER_TYPE.TRANS_TRADING_ORDER;
import static com.imoktranslator.model.OrderModel.ORDER_TYPE.UPDATED_PRICE_ORDER;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.adapter.viewholder.BaseOrderViewHolder;
import com.imoktranslator.adapter.viewholder.FinishedOrderViewHolder;
import com.imoktranslator.adapter.viewholder.NewOrderViewHolder;
import com.imoktranslator.adapter.viewholder.SearchingOrderViewHolder;
import com.imoktranslator.adapter.viewholder.TradingOrderViewHolder;
import com.imoktranslator.customview.SwipeLayout;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DateTimeUtils;
import com.imoktranslator.utils.Utils;

import java.util.List;


public class OrderAdapter extends BaseRecyclerAdapter<BaseOrderViewHolder> implements SwipeLayout.SwipeListener {

    private Context context;
    private List<OrderModel> orderModels;
    private PersonalInfo personalInfo;

    private String[] translationLanguage;
    private String[] currency;
    private SwipeLayout currentSwipeOpen;

    private OnOrderClickListener onOrderClickListener;

    public OrderAdapter(Context context, List<OrderModel> orderModels, PersonalInfo personalInfo) {
        this.context = context;
        this.orderModels = orderModels;
        this.personalInfo = personalInfo;
        translationLanguage = context.getResources().getStringArray(R.array.arr_language);
        currency = context.getResources().getStringArray(R.array.arr_currency);
    }

    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        this.onOrderClickListener = onOrderClickListener;
    }

    @Override
    public BaseOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        final BaseOrderViewHolder viewHolder;
        switch (viewType) {
            case NEW_ORDER:
                view = LayoutInflater.from(context).inflate(R.layout.item_new_order, parent, false);
                viewHolder = new NewOrderViewHolder(view, onOrderClickListener);
                break;
            case SEARCHING_ORDER:
                view = LayoutInflater.from(context).inflate(R.layout.item_searching_order, parent, false);
                viewHolder = new SearchingOrderViewHolder(view, onOrderClickListener);
                break;
            case TRANS_TRADING_ORDER:
                view = LayoutInflater.from(context).inflate(R.layout.item_order_trans_none, parent, false);
                viewHolder = new BaseOrderViewHolder(view, onOrderClickListener);
                break;
            case OWNER_TRADING_ORDER:
                view = LayoutInflater.from(context).inflate(R.layout.item_trading_order, parent, false);
                viewHolder = new TradingOrderViewHolder(view, onOrderClickListener);
                break;
            case UPDATED_PRICE_ORDER:
                view = LayoutInflater.from(context).inflate(R.layout.item_order_trans_none, parent, false);
                viewHolder = new BaseOrderViewHolder(view, onOrderClickListener);
                break;
            case REJECTED_ORDER:
            case CANCELED_ORDER:
            case FINISHED_ORDER:
            case EXPIRED_ORDER:
                view = LayoutInflater.from(context).inflate(R.layout.item_expired_order, parent, false);
                viewHolder = new FinishedOrderViewHolder(view, onOrderClickListener);
                break;
            default:
                view = LayoutInflater.from(context).inflate(R.layout.item_searching_order, parent, false);
                viewHolder = new SearchingOrderViewHolder(view, onOrderClickListener);
                break;
        }

        viewHolder.swipeLayout.addSwipeListener(this);
        viewHolder.layoutOrderInfo.setOnClickListener(v ->
                onOrderClickListener.onOrderItemClick(viewHolder.getAdapterPosition()));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseOrderViewHolder holder, int position) {
        OrderModel item = orderModels.get(position);
        holder.txtOrderName.setText(item.getName());

        holder.txtLanguageLabel.setText(context.getString(R.string.MH22_008) + ": ");
        holder.txtLanguage.setText(translationLanguage[item.getTranslationLang() - 1]);

        holder.txtDateCreated.setText(context.getString(R.string.MH22_005)
                + ": " + DateTimeUtils.getDMYFormat(item.getDateCreated()));

        holder.txtPlaceLabel.setText(context.getString(R.string.MH22_007) + ": ");
        holder.txtPlace.setText(getAddress(item));

        if (item.getPrice() > 0 && item.getCurrency() != 0 && getItemViewType(position) != EXPIRED_ORDER) {
            holder.txtPriceLabel.setText(context.getString(R.string.MH22_006) + ": ");
            holder.txtPrice.setText(Utils.formatCurrency(item.getPrice()) + " " + currency[item.getCurrency() - 1]);
            holder.txtPrice.setVisibility(View.VISIBLE);
            holder.txtPriceLabel.setVisibility(View.VISIBLE);
        } else {
            holder.txtPrice.setVisibility(View.GONE);
            holder.txtPriceLabel.setVisibility(View.GONE);
        }

        setOrderStatus(holder, position);
    }

    private void setOrderStatus(BaseOrderViewHolder holder, int position) {
        String orderStatus;
        Drawable orderBg;
        switch (getItemViewType(position)) {
            case NEW_ORDER:
                orderStatus = context.getString(R.string.MH22_019);
                orderBg = context.getDrawable(R.drawable.bg_order_status_blue);
                break;
            case SEARCHING_ORDER:
                orderStatus = context.getString(R.string.MH22_011);
                orderBg = context.getDrawable(R.drawable.bg_order_status_blue);
                break;
            case TRANS_TRADING_ORDER:
            case OWNER_TRADING_ORDER:
                orderStatus = context.getString(R.string.MH22_010);
                orderBg = context.getDrawable(R.drawable.bg_order_status_yellow);
                break;
            case UPDATED_PRICE_ORDER:
                orderStatus = context.getString(R.string.MH17_003);
                orderBg = context.getDrawable(R.drawable.bg_order_status_yellow);
                break;
            case REJECTED_ORDER:
                orderStatus = context.getString(R.string.MH22_022);
                orderBg = context.getDrawable(R.drawable.bg_order_status_grey);
                break;
            case CANCELED_ORDER:
                orderStatus = context.getString(R.string.MH22_014);
                orderBg = context.getDrawable(R.drawable.bg_order_status_grey);
                break;
            case FINISHED_ORDER:
                orderStatus = context.getString(R.string.MH22_020);
                orderBg = context.getDrawable(R.drawable.bg_order_status_grey);
                break;
            case EXPIRED_ORDER:
                orderStatus = context.getString(R.string.MH22_028);
                orderBg = context.getDrawable(R.drawable.bg_order_status_grey);
                break;
            default:
                orderStatus = context.getString(R.string.MH22_019);
                orderBg = context.getDrawable(R.drawable.bg_order_status_blue);
                break;
        }
        holder.txtOrderStatus.setText(orderStatus);
        holder.txtOrderStatus.setBackground(orderBg);
    }

    private String getAddress(OrderModel item) {
        String address;
        if (item.getAddressType() == Constants.ADDRESS_TYPE_FILTER) {
            address = TextUtils.isEmpty(item.getCity()) ? item.getCountry() :
                    item.getCity() + ", " + item.getCountry();
        } else {
            address = item.getAddress();
        }
        return address;
    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getOrderType(orderModels.get(position));
    }

    private int getOrderType(OrderModel orderModel) {
        boolean isOwner = orderModel.getUserId() == personalInfo.getId();
        if (!isOwner && orderModel.getStatusPriceForTrans() == OrderModel.ORDER_PRICE_STATUS.CANCELED_PRICE) {
            return REJECTED_ORDER;
        }
        switch (orderModel.getOrderStatus()) {
            case OrderModel.ORDER_STATUS.SEARCHING_ORDER:
                return getNewOrderType(isOwner, orderModel.getStatusPriceForTrans(), orderModel.getToDate());
            case OrderModel.ORDER_STATUS.TRADING_ORDER:
                return isOwner ? OWNER_TRADING_ORDER : getTradingStatusOrder(orderModel);
            case OrderModel.ORDER_STATUS.CANCELED_ORDER:
                return (!isOwner && personalInfo.getId() != orderModel.getAcceptedTransId())
                        ? EXPIRED_ORDER : CANCELED_ORDER;
            case OrderModel.ORDER_STATUS.FINISHED_ORDER:
                return (!isOwner && personalInfo.getId() != orderModel.getAcceptedTransId())
                        ? EXPIRED_ORDER : FINISHED_ORDER;
            default:
                return SEARCHING_ORDER;

        }
    }

    private int getTradingStatusOrder(OrderModel orderModel) {
        if (orderModel.getAcceptedTransId() == personalInfo.getId()) {
            return TRANS_TRADING_ORDER;
        }
        return EXPIRED_ORDER;
    }

    private int getNewOrderType(boolean isOwner, int priceStatus, String endDate) {
        if (isOwner) {
            return OrderModel.ORDER_TYPE.SEARCHING_ORDER;
        } else {
            switch (priceStatus) {
                case OrderModel.ORDER_PRICE_STATUS.NOTHING:
                    String currentDate = DateTimeUtils.getCurrentDate(DateTimeUtils.YMD_HMS_FORMAT);
                    return DateTimeUtils.compareDate(currentDate, endDate, DateTimeUtils.YMD_HMS_FORMAT)
                            == DateTimeUtils.AFTER_DATE ? EXPIRED_ORDER : NEW_ORDER;
                case OrderModel.ORDER_PRICE_STATUS.UPDATED_PRICE:
                    return UPDATED_PRICE_ORDER;
                default:
                    return NEW_ORDER;
            }
        }
    }

    @Override
    public void onStartOpen(SwipeLayout layout) {

    }

    @Override
    public void onOpen(SwipeLayout layout) {
        if (currentSwipeOpen == null) {
            currentSwipeOpen = layout;
        } else if (!currentSwipeOpen.equals(layout)) {
            currentSwipeOpen.close(true);
            currentSwipeOpen = layout;
        }
    }

    @Override
    public void onStartClose(SwipeLayout layout) {

    }

    @Override
    public void onClose(SwipeLayout layout) {

    }

    @Override
    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

    }

    @Override
    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

    }

    public List<OrderModel> getData() {
        return this.orderModels;
    }

    public void setData(List<OrderModel> orderModels) {
        this.orderModels.addAll(orderModels);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.orderModels.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int postion) {
        orderModels.remove(postion);
        notifyDataSetChanged();
    }

    public interface OnOrderClickListener {
        void onOrderItemClick(int position);

        void onOrderCancelClick(int position);

        void onOrderDeleteClick(int position);

        void onOrderEndEarlyClick(int position);

        void onHideOrderClick(int position);
    }
}
