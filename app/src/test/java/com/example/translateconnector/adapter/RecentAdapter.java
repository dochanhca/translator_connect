package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecentAdapter extends BaseRecyclerAdapter<RecentAdapter.ViewHolder> {

    private List<UserRoom> listRoom;
    private Context context;
    private String currentUserKey;

    public RecentAdapter(List<UserRoom> listRoom, Context context) {
        this.listRoom = new ArrayList<>();
        this.listRoom.addAll(listRoom);
        this.context = context;
        currentUserKey = LocalSharedPreferences.getInstance(context).getKeyUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recent_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        UserRoom userRoom = listRoom.get(position);
        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.ROOM_MESSAGES_COLLECTION).orderByChild("key").equalTo(userRoom.getRoomKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Room room = dsp.getValue(Room.class);
                            if (room.getType() == FireBaseDataUtils.ROOM_TYPE_GROUP) {
                                loadGroupAvatar(room, holder);
                            } else {
                                List<String> listVisitor = Arrays.asList(room.getVisitor().split("\\s*,\\s*"));
                                String keyUser = !room.getOwner().equals(currentUserKey)
                                        ? room.getOwner() : listVisitor.get(listVisitor.size() - 1);
                                Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                                        .orderByChild("key").equalTo(keyUser);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                            User user = dsp.getValue(User.class);
                                            Glide.with(context)
                                                    .load(user.getAvatar())
                                                    .error(R.drawable.img_default_avatar)
                                                    .into(holder.avatar);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("FireBase", ">>> Error:" + "find onCancelled:" + databaseError);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void loadGroupAvatar(Room room, ViewHolder holder) {
        if (!TextUtils.isEmpty(room.getAvatar())) {
            Glide.with(context)
                    .load(room.getAvatar())
                    .error(R.drawable.img_avatar_default)
                    .into(holder.avatar);
            return;
        }
        List<String> userKeys = new ArrayList<>();
        userKeys.add(room.getOwner());
        userKeys.addAll(Arrays.asList(room.getVisitor().split("\\s*,\\s*")));
        final String[] avatar = {""};
        for (int i = 0; i < 2; i++) {
            Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                    .orderByChild("key").equalTo(userKeys.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                    avatar[0] += user.getName().substring(0, 1);
                    if (avatar[0].length() == 2) {
                        TextDrawable drawable = TextDrawable.builder()
                                .beginConfig()
                                .width(120).height(120)
                                .fontSize(context.getResources().getDimensionPixelSize(R.dimen.text_common))
                                .endConfig()
                                .buildRound(avatar[0], context.getResources().getColor(R.color.colorPrimary));
                        holder.avatar.setImageDrawable(drawable);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FireBase", ">>> Error:" + "find onCancelled:" + databaseError);
                }
            });
        }
    }


    public void setListRoom(List<UserRoom> rooms) {
        listRoom.clear();
        listRoom.addAll(rooms);
        notifyDataSetChanged();
    }

    public List<UserRoom> getData() {
        return this.listRoom;
    }

    @Override
    public int getItemCount() {
        return listRoom.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recent_avatar)
        CircleImageView avatar;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
