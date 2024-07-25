package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SwipeLayout;
import com.imoktranslator.customview.SwipeViewButton;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DateTimeUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends BaseRecyclerAdapter<ConversationAdapter.ViewHolder>
        implements SwipeLayout.SwipeListener {

    private List<UserRoom> listRoom;
    private List<UserRoom> localListRoom;
    private Context context;
    private String currentUserKey;
    private SwipeLayout currentSwipeOpen;
    private OnMessageAdapterClickListener onMessageAdapterClickListener;

    public void setOnMessageAdapterClickListener(OnMessageAdapterClickListener onMessageAdapterClickListener) {
        this.onMessageAdapterClickListener = onMessageAdapterClickListener;
    }

    public ConversationAdapter(List<UserRoom> listRoom, Context context) {
        this.listRoom = new ArrayList<>();
        this.listRoom.addAll(listRoom);
        this.localListRoom = new ArrayList<>();
        this.localListRoom.addAll(listRoom);
        this.context = context;
        currentUserKey = LocalSharedPreferences.getInstance(context).getKeyUser();
    }

    public void filterChat(String text) {
        listRoom.clear();
        if (!text.isEmpty()) {
            for (UserRoom item : localListRoom) {
                if (item.getUserName().toLowerCase().contains(text.toLowerCase())) {
                    listRoom.add(item);
                }
            }
        } else {
            listRoom.addAll(localListRoom);
        }

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.swipeLayout.addSwipeListener(this);
        viewHolder.btBlock.setOnClickListener(v -> {
            int pos = viewHolder.getAdapterPosition();
            onMessageAdapterClickListener.onMuteNotificationClick(listRoom.get(pos));
            viewHolder.swipeLayout.close();
        });
        viewHolder.btDelete.setOnClickListener(v -> {
            int pos = viewHolder.getAdapterPosition();
            onMessageAdapterClickListener.onDeleteChatClick(listRoom.get(pos));
            viewHolder.swipeLayout.close();
        });
        viewHolder.rootView.setOnClickListener(v -> {
            int pos = viewHolder.getAdapterPosition();
            onMessageAdapterClickListener.onChatClick(listRoom.get(pos));
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        UserRoom userRoom = listRoom.get(position);

        holder.swipeLayout.setBackgroundResource(userRoom.isHasUnreadMessage() ? R.color.pale_grey_two
                : R.color.transparent);
        holder.ivBlock.setImageResource(userRoom.isMuteNotification()
                ? R.drawable.ic_enable_notification : R.drawable.ic_mute_notification);

        FireBaseDataUtils.getInstance().getFirebaseReference().
                child(FireBaseDataUtils.ROOM_MESSAGES_COLLECTION).orderByChild("key").equalTo(userRoom.getRoomKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Room room = dataSnapshot.getChildren().iterator().next().getValue(Room.class);
                            holder.timeStamp.setText(DateTimeUtils.convertTimestamp(context,
                                    String.valueOf(room.getLastTimeActive())));
                            if (room.getType() == FireBaseDataUtils.ROOM_TYPE_GROUP) {
                                holder.lastMessage.setText(getNotifyMessage(room.getLastMessage()));
                                holder.name.setText(room.getChatRoomName());
                                //set UserName of UserRoom for filter room
                                userRoom.setUserName(room.getChatRoomName());

                                if (currentUserKey.equals(room.getLastSender())) {
                                    holder.sender.setText(context.getString(R.string.MH18_008));
                                } else {
                                    Query query = FireBaseDataUtils.getInstance().
                                            getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                                            .orderByChild("key").equalTo(room.getLastSender());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                                User user = dsp.getValue(User.class);
                                                holder.sender.setText(user.getName() + ":");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                                //load avatar for group chat
                                loadGroupAvatar(room, holder);

                            } else {
                                holder.lastMessage.setText(room.getLastMessage());
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
                                                Glide.with(context.getApplicationContext())
                                                        .load(user.getAvatar())
                                                        .error(R.drawable.img_default_avatar)
                                                        .into(holder.personalAvatar);
                                            holder.name.setText(user.getName());
                                            //set UserName of UserRoom for filter room
                                            userRoom.setUserName(user.getName());

                                            if (currentUserKey.equals(room.getLastSender())) {
                                                holder.sender.setText(context.getString(R.string.MH18_008));
                                            } else {
                                                holder.sender.setText(user.getName() + ":");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("FireBase", ">>> Error:" + "find onCancelled:" + databaseError);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private String getNotifyMessage(String message) {
        String result;
        if (message.contains(Constants.MESS_1054)) {
            result = message.replace(Constants.MESS_1054, context.getString(R.string.TB_1054));
            return result;
        }
        if (message.contains(Constants.MESS_1055)) {
            result = message.replace(Constants.MESS_1055, context.getString(R.string.TB_1055));
            return result;
        }
        if (message.contains(Constants.MESS_1056)) {
            result = message.replace(Constants.MESS_1056, context.getString(R.string.TB_1056));
            return result;
        }
        if (message.contains(Constants.MESS_1059)) {
            result = message.replace(Constants.MESS_1059, context.getString(R.string.TB_1059));
            return result;
        }
        if (message.contains(Constants.MESS_1071)) {
            String[] dynamicName = message.split(Constants.MESS_1071);
            result = String.format(context.getString(R.string.TB_1071), dynamicName[0], dynamicName[1]);
            return result;
        }
        return message;
    }

    private void loadGroupAvatar(Room room, ViewHolder holder) {
        if (!TextUtils.isEmpty(room.getAvatar())) {
            Glide.with(context)
                    .load(room.getAvatar())
                    .error(R.drawable.img_avatar_default)
                    .into(holder.personalAvatar);
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
                        holder.personalAvatar.setImageDrawable(drawable);
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
        localListRoom.clear();
        localListRoom.addAll(rooms);
        notifyDataSetChanged();
    }

    public List<UserRoom> getData() {
        return this.listRoom;
    }

    @Override
    public int getItemCount() {
        return listRoom.size();
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        //        @BindView(R.id.group_avatar)
//        MultiImageView groupAvatar;
        @BindView(R.id.personal_avatar)
        CircleImageView personalAvatar;
        @BindView(R.id.message_name)
        OpenSansBoldTextView name;
        @BindView(R.id.message_sender)
        OpenSansTextView sender;
        @BindView(R.id.last_message)
        OpenSansTextView lastMessage;
        @BindView(R.id.message_timestamp)
        OpenSansTextView timeStamp;
        @BindView(R.id.swipe_layout)
        SwipeLayout swipeLayout;
        @BindView(R.id.bt_delete)
        SwipeViewButton btDelete;
        @BindView(R.id.bt_block)
        SwipeViewButton btBlock;
        @BindView(R.id.iv_block)
        ImageView ivBlock;
        @BindView(R.id.root_view)
        ViewGroup rootView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnMessageAdapterClickListener {

        void onMuteNotificationClick(UserRoom userRoom);

        void onDeleteChatClick(UserRoom userRoom);

        void onChatClick(UserRoom userRoom);
    }
}

