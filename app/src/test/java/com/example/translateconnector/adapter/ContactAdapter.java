package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SwipeLayout;
import com.imoktranslator.customview.SwipeViewButton;
import com.imoktranslator.model.UserFriend;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends BaseRecyclerAdapter<ContactAdapter.ViewHolder> implements SwipeLayout.SwipeListener {

    private Context context;
    private List<UserFriend> listContact;
    private SwipeLayout currentSwipeOpen;
    private OnContactClickListener listener;

    public ContactAdapter(List<UserFriend> listContact, Context context, OnContactClickListener listener) {
        this.listContact = new ArrayList<>();
        this.listContact.addAll(listContact);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        UserFriend friend = listContact.get(position);
        Glide.with(context)
                .load(friend.getUser().getAvatar())
                .error(R.drawable.img_default_avatar)
                .into(holder.avatar);
        holder.name.setText(friend.getUser().getName());
        if (!TextUtils.isEmpty(friend.getUser().getStatus())) {
            holder.status.setText("\"" + friend.getUser().getStatus() + "\"");
        }

        holder.swipeLayout.addSwipeListener(this);
        holder.contactRow.setOnClickListener(view -> {
            //TODO open personal page
            holder.swipeLayout.close(true);
            listener.openUserProfile(friend);

        });
        holder.btDelete.setOnClickListener(view -> {
            holder.swipeLayout.close(true);
            listener.deleteFriend(friend);
        });

        holder.btChat.setOnClickListener(view -> {
            holder.swipeLayout.close(true);
            listener.openChat(friend.getUser().getKey(), friend.getUser().getName());
        });
    }

    @Override
    public int getItemCount() {
        return listContact.size();
    }


    public void setListContact(List<UserFriend> listFriend) {
        this.listContact.clear();
        this.listContact.addAll(listFriend);
        notifyDataSetChanged();
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

    public void removeItem(UserFriend friend) {
        listContact.remove(friend);
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contact_avatar)
        CircleImageView avatar;
        @BindView(R.id.contact_name)
        OpenSansBoldTextView name;
        @BindView(R.id.personal_status)
        OpenSansTextView status;
        @BindView(R.id.swipe_layout)
        SwipeLayout swipeLayout;
        @BindView(R.id.contact_row)
        RelativeLayout contactRow;
        @BindView(R.id.bt_delete)
        SwipeViewButton btDelete;
        @BindView(R.id.bt_chat)
        SwipeViewButton btChat;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnContactClickListener {
        void openUserProfile(UserFriend friend);

        void deleteFriend(UserFriend friend);

        void openChat(String firebaseFriendID, String friendName);
    }
}
