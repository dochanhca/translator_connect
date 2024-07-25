package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.customview.CustomTypefaceSpan;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.model.FriendInvitationModel;
import com.imoktranslator.utils.FontUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendInvitationAdapter extends RecyclerView.Adapter<FriendInvitationAdapter.InvitationViewHolder> {
    private Context context;
    private List<FriendInvitationModel> invitationList;
    private OnInvitationClickListener listener;

    public FriendInvitationAdapter(Context context, List<FriendInvitationModel> invitationList, OnInvitationClickListener listener) {
        this.context = context;
        this.invitationList = invitationList;
        this.listener = listener;
    }

    @Override
    public InvitationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.friend_invitation_item, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InvitationViewHolder holder, int position) {
        FriendInvitationModel invitation = invitationList.get(position);

        String message = invitation.getSenderName() + " " + context.getString(R.string.MH19_005);
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new CustomTypefaceSpan("", FontUtils.getInstance().getOpenSanBold()),
                0, invitation.getSenderName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.message.setText(spannableString);

        holder.commonFriends.setText(invitation.getCommonFriends() + " " + context.getString(R.string.MH19_004));

        Glide.with(context)
                .load(invitation.getSenderAvatar())
                .error(R.drawable.img_default_avatar)
                .placeholder(R.drawable.img_default_avatar)
                .dontAnimate()
                .into(holder.ivAvatar);

        holder.btAllow.setOnClickListener(view -> listener.onAllow(invitation));

        holder.btCancel.setOnClickListener(view -> listener.onCancel(invitation));

        holder.ivAvatar.setOnClickListener(v -> listener.onOpenInfo(invitation));

        holder.message.setOnClickListener(v -> listener.onOpenInfo(invitation));
    }

    @Override
    public int getItemCount() {
        return invitationList.size();
    }

    public class InvitationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_message)
        OpenSansTextView message;
        @BindView(R.id.common_friends)
        OpenSansTextView commonFriends;
        @BindView(R.id.iv_avatar)
        CircleImageView ivAvatar;
        @BindView(R.id.bt_allow)
        OpenSansTextView btAllow;
        @BindView(R.id.bt_cancel)
        OpenSansTextView btCancel;

        public InvitationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnInvitationClickListener {
        void onAllow(FriendInvitationModel invitation);

        void onCancel(FriendInvitationModel invitation);

        void onOpenInfo(FriendInvitationModel invitationModel);
    }
}
