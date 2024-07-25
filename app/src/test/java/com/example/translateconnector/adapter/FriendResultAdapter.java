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
import com.imoktranslator.model.SearchFriend;
import com.imoktranslator.model.firebase.Invitation;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendResultAdapter extends BaseRecyclerAdapter<FriendResultAdapter.ViewHolder> {

    private Context context;
    private List<SearchFriend> listFriend;
    private String[] genders;

    private String userKey;

    private OnClickListener onClickListener;
    private List<String> myFriendKeys;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public FriendResultAdapter(List<SearchFriend> listFriend, Context context) {
        this.listFriend = new ArrayList<>();
        this.listFriend.addAll(listFriend);
        this.context = context;
        genders = context.getResources().getStringArray(R.array.arr_gender);
        userKey = LocalSharedPreferences.getInstance(context).getKeyUser();
    }

    public void setListFriend(List<SearchFriend> listFriend) {
        this.listFriend.clear();
        this.listFriend.addAll(listFriend);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_result, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        SearchFriend item = listFriend.get(position);

        loadAvatar(context, item.getAvatar(), holder.imgAvatar);
        holder.txtUserName.setText(item.getName());
        holder.txtGender.setText(item.getGender() == 0 ? "" :
                genders[item.getGender() - 1]);
        holder.txtGender.setTextColor(context.getResources().getColor(
                item.getGender() == Constants.MALE ? R.color.dark_sky_blue : R.color.salmon_pink));
        holder.txtAge.setText(item.getDob() == null ? "" :
                Utils.getAgeFromDob(item.getDob()) + " " + context.getString(R.string.MH41_002));

        holder.txtMutualFriend.setVisibility(item.getMutualFriend() == 0 ? View.GONE : View.VISIBLE);
        holder.txtMutualFriend.setText(String.format(context.getString(R.string.MH41_007), item.getMutualFriend()));

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
            holder.txtAddFriend.setVisibility(View.GONE);
            holder.txtSent.setVisibility(View.VISIBLE);
        });

        holder.imgAvatar.setOnClickListener(v -> onClickListener.onOpenInfo(item));

        holder.txtUserName.setOnClickListener(v -> onClickListener.onOpenInfo(item));

        if (myFriendKeys != null && !myFriendKeys.isEmpty()) {
            if (myFriendKeys.contains(item.getKey())) {
                holder.txtAddFriend.setVisibility(View.GONE);
                holder.txtSent.setVisibility(View.VISIBLE);
                holder.txtSent.setText(R.string.MH48_009);
            } else {
                holder.txtAddFriend.setVisibility(View.VISIBLE);
                holder.txtSent.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public int getItemCount() {
        return listFriend.size();
    }

    public void setListFriend(List<SearchFriend> searchFriends, List<String> friendKeys) {
        this.listFriend.clear();
        this.listFriend.addAll(searchFriends);
        this.myFriendKeys = friendKeys;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_avatar)
        CircleImageView imgAvatar;
        @BindView(R.id.txt_user_name)
        OpenSansBoldTextView txtUserName;
        @BindView(R.id.txt_mutual_friend)
        OpenSansTextView txtMutualFriend;
        @BindView(R.id.txt_gender)
        OpenSansTextView txtGender;
        @BindView(R.id.txt_age)
        OpenSansTextView txtAge;
        @BindView(R.id.txt_add_friend)
        OpenSansSemiBoldTextView txtAddFriend;
        @BindView(R.id.txt_sent)
        OpenSansSemiBoldTextView txtSent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnClickListener {
        void onAddFriendClick(SearchFriend searchFriend);

        void onOpenInfo(SearchFriend searchFriend);
    }
}
