package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.customview.CustomTypefaceSpan;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SwipeLayout;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.PriceModel;
import com.imoktranslator.utils.FontUtils;
import com.imoktranslator.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ListPriceAdapter extends BaseRecyclerAdapter<ListPriceAdapter.ViewHolder>
        implements SwipeLayout.SwipeListener {

    private Context context;
    private List<PriceModel> priceModels;
    private OrderModel orderModel;

    private SwipeLayout currentSwipeOpen;
    private String[] currencies;
    private boolean isViewOnly;

    private OnPriceClickListener onPriceClickListener;

    public void setOnPriceClickListener(OnPriceClickListener onPriceClickListener) {
        this.onPriceClickListener = onPriceClickListener;
    }

    public ListPriceAdapter(Context context, List<PriceModel> priceModels, OrderModel orderModel,
                            boolean isViewOnly) {
        this.context = context;
        this.priceModels = priceModels;
        this.orderModel = orderModel;
        this.isViewOnly = isViewOnly;
        currencies = context.getResources().getStringArray(R.array.arr_currency);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_price, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.txtPriceLabel.append(":");

        if (isViewOnly) {
            return holder;
        }

        holder.swipeLayout.addSwipeListener(this);

        holder.layoutDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            onPriceClickListener.onDeletePriceClick(pos);
        });

        holder.txtAccept.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            onPriceClickListener.onAcceptPriceClick(pos);
        });

        holder.txtChat.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            onPriceClickListener.onChatClick(pos);
        });

        holder.imgAvatar.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            onPriceClickListener.onTranslatorClick(pos);
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        PriceModel item = priceModels.get(position);

        String userName = Utils.hideStringWithStars(item.getName());
        String des = context.getString(R.string.MH12_004);
        String orderName = orderModel.getName();

        SpannableString spannableString = new SpannableString(userName + " " + des + " " + orderName);

        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSans()),
                userName.length(), userName.length() + des.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                userName.length() + des.length() + 1,
                userName.length() + des.length() + orderName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.txtDescription.setText(spannableString);
        holder.txtPrice.setText(Utils.formatCurrency(item.getPrice()) + " " +
                currencies[item.getCurrency() - 1]);

        holder.txtAccept.setVisibility(isViewOnly ? View.GONE : View.VISIBLE);
        holder.txtChat.setVisibility(isViewOnly ? View.GONE : View.VISIBLE);
        holder.swipeLayout.setSwipeEnabled(isViewOnly ? false : true);

        Glide.with(context)
                .load(item.getAvatar())
                .error(R.drawable.img_default_avatar)
                .centerCrop()
                .into(holder.imgAvatar);
        holder.swipeLayout.close();
    }

    @Override
    public int getItemCount() {
        return priceModels.size();
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

    public void setData(List<PriceModel> priceModels) {
        this.priceModels.clear();
        this.priceModels.addAll(priceModels);
        notifyDataSetChanged();
    }

    public List<PriceModel> getData() {
        return priceModels;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_delete)
        ViewGroup layoutDelete;
        @BindView(R.id.img_avatar)
        CircleImageView imgAvatar;
        @BindView(R.id.txt_description)
        OpenSansTextView txtDescription;
        @BindView(R.id.txt_price_label)
        OpenSansTextView txtPriceLabel;
        @BindView(R.id.txt_price)
        OpenSansBoldTextView txtPrice;
        @BindView(R.id.txt_accept)
        OpenSansSemiBoldTextView txtAccept;
        @BindView(R.id.txt_chat)
        OpenSansSemiBoldTextView txtChat;
        @BindView(R.id.swipe_layout)
        SwipeLayout swipeLayout;
        @BindView(R.id.img_delete)
        ImageView imgDelete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnPriceClickListener {
        void onAcceptPriceClick(int pos);

        void onDeletePriceClick(int pos);

        void onChatClick(int pos);

        void onTranslatorClick(int pos);
    }
}
