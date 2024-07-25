package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMemberAdapter extends BaseRecyclerAdapter<ChatMemberAdapter.ViewHolder> {

    private List<String> userKeys;
    private Context context;
    private ClickListener listener;
    private boolean isOwner;
    private String currentUserKey;

    public ChatMemberAdapter(Context context, List<String> userKeys, boolean isOwner) {
        this.userKeys = userKeys;
        this.context = context;
        this.isOwner = isOwner;
        this.currentUserKey = LocalSharedPreferences.getInstance(context).getKeyUser();
    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_member, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String userKey = userKeys.get(position);

        holder.imgDeleteUser.setVisibility((isOwner && !currentUserKey.equals(userKey)) ? View.VISIBLE : View.GONE);

        Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                .orderByChild("key").equalTo(userKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    User user = dsp.getValue(User.class);
                    Glide.with(context)
                            .load(user.getAvatar())
                            .error(R.drawable.img_default_avatar)
                            .into(holder.imgAvatar);
                    holder.txtName.setText(user.getName());
                    holder.imgDeleteUser.setOnClickListener(v -> {
                        listener.onRemoveMember(user);
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireBase", ">>> Error:" + "find onCancelled:" + databaseError);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userKeys.size();
    }

    public void removeItem(String key) {
        userKeys.remove(key);
        notifyDataSetChanged();
    }

    public

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_avatar)
        CircleImageView imgAvatar;
        @BindView(R.id.txt_name)
        OpenSansBoldTextView txtName;
        @BindView(R.id.img_delete_user)
        ImageView imgDeleteUser;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface ClickListener {
        void onRemoveMember(User user);
    }
}
