package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.model.NearByFriend;
import com.imoktranslator.model.firebase.Invitation;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NearByFriendAdapter extends BaseRecyclerAdapter<NearByFriendAdapter.ViewHolder> {

    private List<NearByFriend> nearByFriends;
    private Context context;
    private String[] genders;
    private String userKey;

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public NearByFriendAdapter(List<NearByFriend> nearByFriends, Context context) {
        this.nearByFriends = nearByFriends;
        this.context = context;
        genders = context.getResources().getStringArray(R.array.arr_gender);
        userKey = LocalSharedPreferences.getInstance(context).getKeyUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_near_by, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        NearByFriend item = nearByFriends.get(position);

        holder.txtUserName.setText(item.getName());
        holder.txtGender.setText(item.getGender() == 0 ? "" :
                genders[item.getGender() - 1]);
        holder.txtGender.setTextColor(context.getResources().getColor(
                item.getGender() == Constants.MALE ? R.color.dark_sky_blue : R.color.salmon_pink));
        holder.txtAge.setText(item.getDob() == null ? "" :
                Utils.getAgeFromDob(item.getDob()) + " " + context.getString(R.string.MH41_002));
        holder.txtDistance.setText(Utils.getDistance(item.getDistance()));

        loadAvatar(context, item.getAvatar(), holder.imgAvatar);

        FireBaseDataUtils.getInstance().getFirebaseReference().
                child(FireBaseDataUtils.FRIEND_INVITATION_COLLECTION)
                .child(item.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Invitation invitation = dsp.getValue(Invitation.class);
                    if (invitation.getSenderKey().equals(userKey)) {
                        holder.txtAddFriend.setVisibility(View.GONE);
                        holder.txtSent.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.txtAddFriend.setOnClickListener(view -> {
            onClickListener.onAddFriendClick(item);
            //TODO call API send invitation with receiverId
            holder.txtAddFriend.setVisibility(View.GONE);
            holder.txtSent.setVisibility(View.VISIBLE);
        });
        holder.userInfoGroup.setOnClickListener(v -> onClickListener.onOpenInfo(item));
    }

    @Override
    public int getItemCount() {
        return nearByFriends.size();
    }

    public void setData(List<NearByFriend> nearByFriends) {
        this.nearByFriends.clear();
        this.nearByFriends.addAll(nearByFriends);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_avatar)
        CircleImageView imgAvatar;
        @BindView(R.id.txt_user_name)
        OpenSansBoldTextView txtUserName;
        @BindView(R.id.txt_distance)
        OpenSansTextView txtDistance;
        @BindView(R.id.txt_gender)
        OpenSansTextView txtGender;
        @BindView(R.id.txt_age)
        OpenSansTextView txtAge;
        @BindView(R.id.txt_add_friend)
        OpenSansSemiBoldTextView txtAddFriend;
        @BindView(R.id.txt_sent)
        OpenSansSemiBoldTextView txtSent;
        @BindView(R.id.user_info_group)
        ViewGroup userInfoGroup;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnClickListener {
        void onAddFriendClick(NearByFriend item);

        void onOpenInfo(NearByFriend item);
    }
}
