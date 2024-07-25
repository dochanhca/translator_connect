package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.customview.CustomTypefaceSpan;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SwipeLayout;
import com.imoktranslator.customview.SwipeViewButton;
import com.imoktranslator.model.NotificationStatus;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.SocialNotificationModel;
import com.imoktranslator.utils.DateTimeUtils;
import com.imoktranslator.utils.FontUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SocialNotificationAdapter extends RecyclerView.Adapter<SocialNotificationAdapter.SocialNotificationViewHolder> implements SwipeLayout.SwipeListener {
    private Context context;
    private List<SocialNotificationModel> notificationList;
    private OnSocialNotificationClickListener listener;
    private int colorRead;
    private int colorUnread;
    private SwipeLayout currentSwipeOpen;

    public SocialNotificationAdapter(Context context, List<SocialNotificationModel> notificationList, OnSocialNotificationClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
        colorRead = ContextCompat.getColor(context, R.color.notification_read);
        colorUnread = ContextCompat.getColor(context, R.color.pale_grey_two);
    }

    @NonNull
    @Override
    public SocialNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_notification_item, parent, false);
        return new SocialNotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialNotificationViewHolder holder, int position) {
        SocialNotificationModel notification = notificationList.get(position);
        Glide.with(context)
                .load(notification.getSender().getAvatar())
                .error(R.drawable.img_default_avatar)
                .into(holder.ivAvatar);
        holder.contentNotification.setBackgroundColor(notification.getStatus() == NotificationStatus.READ ? colorRead : colorUnread);
        holder.tvTime.setText(DateTimeUtils.convertSocialNotificationDate(context, notification.getCreatedAt()));
        bindRelativeTimeLabel(holder, position);

        if (notification.typeLikePost()) {
            bindViewLikePost(notification, holder);
        } else if (notification.typeAuthorComment()) {
            bindViewAuthorComment(notification, holder);
        } else if (notification.typeOtherUserComment()) {
            bindViewOtherUserComment(notification, holder);
        } else if (notification.typeBestFriend()) {
            bindViewBestFriend(notification, holder);
        }

        holder.contentNotification.setOnClickListener(v -> listener.onNotificationClicked(notification, position));
        holder.btDelete.setOnClickListener(v -> {
            holder.swipeLayout.close(true);
            listener.onDeleteNotification(notification);
        });
        holder.swipeLayout.addSwipeListener(this);
    }

    private void bindViewOtherUserComment(SocialNotificationModel notification, SocialNotificationViewHolder holder) {
        int userId = LocalSharedPreferences.getInstance(context).getCurrentFirebaseUser().getId();
        int postOwnerId = notification.getOwnerId();
        if (userId == postOwnerId) {
            PersonalInfo sender = notification.getSender();
            String message = String.format(context.getString(R.string.TB_1077), sender.getName());
            SpannableString spannableString = new SpannableString(message);
            spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                    0, sender.getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.tvMessage.setText(spannableString);
        } else {
            PersonalInfo sender = notification.getSender();
            PersonalInfo owner = notification.getOwner();
            String message = String.format(context.getString(R.string.TB_1078), sender.getName(), owner.getName());
            SpannableString spannableString = new SpannableString(message);
            spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                    0, sender.getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                    message.indexOf(owner.getName()), message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.tvMessage.setText(spannableString);
        }
    }

    private void bindViewAuthorComment(SocialNotificationModel notification, SocialNotificationViewHolder holder) {
        PersonalInfo sender = notification.getSender();
        String message = String.format(context.getString(R.string.TB_1076), sender.getName());
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                0, sender.getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvMessage.setText(spannableString);
    }

    private void bindRelativeTimeLabel(@NonNull SocialNotificationViewHolder holder, int position) {
        if (position == 0 || position == 5) {
            holder.tvRelativeTime.setVisibility(View.VISIBLE);
            if (position == 0) {
                holder.tvRelativeTime.setText(context.getString(R.string.MH19_008));
            }
            if (position == 5) {
                holder.tvRelativeTime.setText(context.getString(R.string.MH19_009));
            }
        } else {
            holder.tvRelativeTime.setVisibility(View.GONE);
        }
    }

    private void bindViewLikePost(SocialNotificationModel notification, SocialNotificationViewHolder holder) {
        PersonalInfo sender = notification.getSender();
        String message = String.format(context.getString(R.string.TB_1068), sender.getName());
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                0, sender.getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvMessage.setText(spannableString);
    }

    private void bindViewBestFriend(SocialNotificationModel notification, SocialNotificationViewHolder holder) {
        PersonalInfo sender = notification.getSender();
        String message = String.format(context.getString(R.string.TB_1081), sender.getName());
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                0, sender.getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvMessage.setText(spannableString);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
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

    public void remove(SocialNotificationModel notificationToDelete) {
        int index = notificationList.indexOf(notificationToDelete);
        notificationList.remove(notificationToDelete);
        notifyItemRemoved(index);
    }

    public void itemChange(SocialNotificationModel notification, int position) {
        notificationList.set(position, notification);
        notifyItemChanged(position);
    }

    class SocialNotificationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar)
        CircleImageView ivAvatar;
        @BindView(R.id.tv_message)
        OpenSansTextView tvMessage;
        @BindView(R.id.layout_notification_content)
        View contentNotification;
        @BindView(R.id.tv_time)
        OpenSansTextView tvTime;
        @BindView(R.id.tv_relative_time)
        OpenSansBoldTextView tvRelativeTime;
        @BindView(R.id.swipe_layout)
        SwipeLayout swipeLayout;
        @BindView(R.id.bt_delete)
        SwipeViewButton btDelete;

        SocialNotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addAll(List<SocialNotificationModel> notifications) {
        for (SocialNotificationModel notification : notifications) {
            add(notification);
        }
    }

    public void add(SocialNotificationModel item) {
        notificationList.add(item);
        notifyItemInserted(notificationList.size() - 1);
    }

    public void clearData() {
        this.notificationList.clear();
        notifyDataSetChanged();
    }

    public interface OnSocialNotificationClickListener {
        void onNotificationClicked(SocialNotificationModel notification, int position);

        void onDeleteNotification(SocialNotificationModel notification);
    }
}
