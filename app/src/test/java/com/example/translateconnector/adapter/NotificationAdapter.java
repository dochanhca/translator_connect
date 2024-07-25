package com.example.translateconnector.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
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
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SwipeLayout;
import com.imoktranslator.customview.SwipeViewButton;
import com.imoktranslator.model.NotificationStatus;
import com.imoktranslator.model.OrderNotificationModel;
import com.imoktranslator.utils.FontUtils;
import com.imoktranslator.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter implements SwipeLayout.SwipeListener {
    private static final int TYPE_WORKER_NOTIFICATION = 0;
    private static final int TYPE_OWNER_NOTIFICATION = 1;
    private static final int TYPE_ORDER_CANCELLED_BY_USER = 2;

    private List<OrderNotificationModel> notificationList;
    private Context context;
    private OnOrderNotificationClickListener listener;
    private SwipeLayout currentSwipeOpen;
    private int colorRead;
    private int colorUnread;

    public NotificationAdapter(Context context, List<OrderNotificationModel> notificationList,
                               OnOrderNotificationClickListener onOrderNotificationClickListener) {
        this.notificationList = notificationList;
        this.context = context;
        this.listener = onOrderNotificationClickListener;
        colorRead = ContextCompat.getColor(context, R.color.notification_read);
        colorUnread = ContextCompat.getColor(context, R.color.pale_grey_two);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case TYPE_WORKER_NOTIFICATION:
                view = inflater.inflate(R.layout.worker_notification_item, parent, false);
                return new WorkerNotificationViewHolder(view);

            case TYPE_OWNER_NOTIFICATION:
                view = inflater.inflate(R.layout.owner_notification_item, parent, false);
                return new OwnerNotificationViewHolder(view);

            case TYPE_ORDER_CANCELLED_BY_USER:
                view = inflater.inflate(R.layout.order_cancelled_by_user_notification_item, parent, false);
                return new OrderCancelledNotificationViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderNotificationModel notification = notificationList.get(position);
        if (notification.isNotificationForWorker()) {
            bindWorkerNotificationData((WorkerNotificationViewHolder) holder, notification);
        } else if (notification.isNotificationForOwner()) {
            bindOwnerNotificationData((OwnerNotificationViewHolder) holder, notification);
        } else if (notification.getType() == OrderNotificationModel.CANCELLED) {
            bindCancelledOrderNotificationData((OrderCancelledNotificationViewHolder) holder, notification);
        }

        ((NotificationViewHolder) holder).contentNotification.setOnClickListener(view -> listener.onNotificationClicked(notification));
        ((NotificationViewHolder) holder).ivBlock.setImageResource(notification.isBlocked(String.valueOf(notification.getSenderId())) ? R.drawable.ic_enable_notification : R.drawable.ic_disable_notification);
        ((NotificationViewHolder) holder).btBlock.setOnClickListener(view -> {
            ((NotificationViewHolder) holder).swipeLayout.close(true);
            new Handler().postDelayed(() -> listener.onBlockNotificationClicked(notification), 300);
        });
        ((NotificationViewHolder) holder).btDelete.setOnClickListener(view -> {
            ((NotificationViewHolder) holder).swipeLayout.close(true);
            new Handler().postDelayed(() -> listener.onDeleteNotificationClicked(notification), 300);
        });
        ((NotificationViewHolder) holder).swipeLayout.addSwipeListener(this);
    }

    private void bindOwnerNotificationData(OwnerNotificationViewHolder holder, OrderNotificationModel notification) {
        OwnerNotificationViewHolder viewHolder = holder;
        Glide.with(context)
                .load(notification.getSender().getAvatar())
                .error(R.drawable.img_default_avatar)
                .into(viewHolder.ivAvatar);

        String message = "";
        String hiddenName = Utils.hideStringWithStars(notification.getSender().getName());
        if (notification.getType() == OrderNotificationModel.NEW_BIDS) {
            message = String.format(context.getString(R.string.TB_1049),
                    hiddenName, notification.getOrder().getName());
        } else if (notification.getType() == OrderNotificationModel.NEW_MESSAGE) {
            message = String.format(context.getString(R.string.TB_1050),
                    hiddenName, notification.getOrder().getName());
        }

        SpannableString spannableString = new SpannableString(message);

        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                0, notification.getSender().getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                message.indexOf(notification.getOrder().getName()),
                message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.tvMessage.setText(spannableString);
        viewHolder.tvTime.setText(notification.getCreatedAt());
        holder.layoutNotificationContent.setBackgroundColor(notification.getStatus() == NotificationStatus.READ ? colorRead : colorUnread);
    }

    private void bindWorkerNotificationData(WorkerNotificationViewHolder holder, OrderNotificationModel notification) {
        String notify = context.getString(R.string.TB_1048);
        String message = "";
        if (notification.getType() == OrderNotificationModel.NEW_ORDER) {
            message = String.format(context.getString(R.string.TB_1045), notification.getOrder().getName());
        } else if (notification.getType() == OrderNotificationModel.ALLOW_PRICE) {
            message = String.format(context.getString(R.string.TB_1046), notification.getOrder().getName());
        } else if (notification.getType() == OrderNotificationModel.REFUSE_PRICE) {
            message = String.format(context.getString(R.string.TB_1047), notification.getOrder().getName());
        }

        SpannableString spannableString = new SpannableString(message);

        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                0, notify.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                message.indexOf(notification.getOrder().getName()),
                message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvMessage.setText(spannableString);
        holder.tvTime.setText(notification.getCreatedAt());
        holder.contentNotification.setBackgroundColor(notification.getStatus() == NotificationStatus.READ ? colorRead : colorUnread);
    }

    private void bindCancelledOrderNotificationData(OrderCancelledNotificationViewHolder holder, OrderNotificationModel notification) {
        String notices = context.getString(R.string.TB_1048);

        String message = String.format(context.getString(R.string.TB_1051), notification.getOrder().getName(),
                notification.getOrder().getReasonToCancel());
        SpannableString spannableString = new SpannableString(message);

        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                0, notices.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                message.indexOf(notification.getOrder().getName()),
                (message.indexOf(notification.getOrder().getName()) + notification.getOrder().getName().length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvMessage.setText(spannableString);
        holder.tvTime.setText(notification.getCreatedAt());
        holder.contentNotification.setBackgroundColor(notification.getStatus() == NotificationStatus.READ ? colorRead : colorUnread);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }


    @Override
    public int getItemViewType(int position) {
        OrderNotificationModel notification = notificationList.get(position);
        if (notification.getType() == OrderNotificationModel.NEW_ORDER ||
                notification.getType() == OrderNotificationModel.ALLOW_PRICE ||
                notification.getType() == OrderNotificationModel.REFUSE_PRICE) {
            return TYPE_WORKER_NOTIFICATION;
        } else if (notification.getType() == OrderNotificationModel.NEW_BIDS || notification.getType() == OrderNotificationModel.NEW_MESSAGE) {
            return TYPE_OWNER_NOTIFICATION;
        } else if (notification.getType() == OrderNotificationModel.CANCELLED) {
            return TYPE_ORDER_CANCELLED_BY_USER;
        } else {
            return -1;
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

    public class WorkerNotificationViewHolder extends NotificationViewHolder {
        @BindView(R.id.tv_message)
        OpenSansTextView tvMessage;
        @BindView(R.id.tv_time)
        OpenSansTextView tvTime;

        public WorkerNotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class OrderCancelledNotificationViewHolder extends NotificationViewHolder {
        @BindView(R.id.tv_message)
        OpenSansTextView tvMessage;
        @BindView(R.id.tv_time)
        OpenSansTextView tvTime;

        public OrderCancelledNotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class OwnerNotificationViewHolder extends NotificationViewHolder {
        @BindView(R.id.tv_message)
        OpenSansTextView tvMessage;
        @BindView(R.id.tv_time)
        OpenSansTextView tvTime;
        @BindView(R.id.iv_avatar)
        CircleImageView ivAvatar;
        @BindView(R.id.layout_notification_content)
        View layoutNotificationContent;

        public OwnerNotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.swipe_layout)
        SwipeLayout swipeLayout;
        @BindView(R.id.content_notification)
        View contentNotification;
        @BindView(R.id.bt_delete)
        SwipeViewButton btDelete;
        @BindView(R.id.bt_block)
        SwipeViewButton btBlock;
        @BindView(R.id.iv_block)
        ImageView ivBlock;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnOrderNotificationClickListener {
        void onNotificationClicked(OrderNotificationModel notification);

        void onDeleteNotificationClicked(OrderNotificationModel notification);

        void onBlockNotificationClicked(OrderNotificationModel notification);
    }

    //helper methods
    public void addAll(List<OrderNotificationModel> notifications) {
        for (OrderNotificationModel notification : notifications) {
            add(notification);
        }
    }

    public void add(OrderNotificationModel item) {
        notificationList.add(item);
        notifyItemInserted(notificationList.size() - 1);
    }

    public void clearAll() {
        notificationList.clear();
        notifyDataSetChanged();
    }

}
