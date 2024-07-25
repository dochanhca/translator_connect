package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.model.firebase.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactToCreateGroupChatAdapter extends BaseRecyclerAdapter<ContactToCreateGroupChatAdapter.ViewHolder> {
    private Context context;
    private List<User> listContact;
    private List<User> selectedUsers;
    private OnContactClickListener listener;

    public ContactToCreateGroupChatAdapter(Context context, List<User> listContact, List<User> selectedUsers,
                                           OnContactClickListener listener) {
        this.context = context;
        this.listContact = listContact;
        this.selectedUsers = selectedUsers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item_create_group_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        User user = listContact.get(position);
        Glide.with(context)
                .load(user.getAvatar())
                .error(R.drawable.img_default_avatar)
                .into(holder.avatar);
        holder.name.setText(user.getName());
        if (!TextUtils.isEmpty(user.getStatus())) {
            holder.status.setText("\"" + user.getStatus() + "\"");
        }

        holder.contactRow.setOnClickListener(view -> listener.onUserClicked(user));
        holder.ivSelected.setVisibility(isSelected(user) ? View.VISIBLE : View.GONE);
    }

    private boolean isSelected(User currentUser) {
        for (User user : selectedUsers) {
            if (user.getId() == currentUser.getId()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return listContact.size();
    }

    public void setListContact(List<User> listFriend) {
        this.listContact.clear();
        this.listContact.addAll(listFriend);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contact_avatar)
        CircleImageView avatar;
        @BindView(R.id.contact_name)
        OpenSansBoldTextView name;
        @BindView(R.id.personal_status)
        OpenSansTextView status;
        @BindView(R.id.contact_row)
        RelativeLayout contactRow;
        @BindView(R.id.iv_selected)
        ImageView ivSelected;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnContactClickListener {
        void onUserClicked(User user);
    }
}
