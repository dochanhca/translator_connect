package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.model.firebase.Message;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DateTimeUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ChattingAdapter extends FirebaseRecyclerAdapter<Message, ChattingAdapter.MyChatViewHolder> {

    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;
    private static final int RIGHT_MSG_AUDIO = 4;
    private static final int LEFT_MSG_AUDIO = 5;
    private static final int NOTIFY_MSG = 6;
    private final boolean isOrderChat;

    private ChatAdapterClickListener mChatAdapterClickListener;

    private String currentUserKey;
    private Context context;
    private String roomKey;

    public ChattingAdapter(String currentUserKey, FirebaseRecyclerOptions options,
                           ChatAdapterClickListener mChatAdapterClickListener, Context context,
                           boolean isOrderChat, String roomKey) {
        super(options);
        this.currentUserKey = currentUserKey;
        this.mChatAdapterClickListener = mChatAdapterClickListener;
        this.context = context;
        this.isOrderChat = isOrderChat;
        this.roomKey = roomKey;
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case RIGHT_MSG:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_right, parent, false);
                break;
            case RIGHT_MSG_IMG:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_right_img, parent, false);
                break;
            case RIGHT_MSG_AUDIO:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_right_audio, parent, false);
                break;
            case LEFT_MSG:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_left, parent, false);
                break;
            case LEFT_MSG_IMG:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_left_img, parent, false);
                break;
            case LEFT_MSG_AUDIO:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_left_audio, parent, false);
                break;
            case NOTIFY_MSG:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_notify, parent, false);
                break;
            default:
                view = LayoutInflater.from(context).inflate(R.layout.item_message_right, parent, false);
                break;

        }

        return new MyChatViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        Message model = getItem(position);
        if (model.getType() == FireBaseDataUtils.ROOM_TYPE_GROUP) {
            return NOTIFY_MSG;
        }
        if (model.getFile() != null) {
            if (model.getFile().getType().equals(FireBaseDataUtils.TYPE_IMAGE)) {
                return model.getUserKey().equals(currentUserKey) ? RIGHT_MSG_IMG : LEFT_MSG_IMG;
            } else {
                return model.getUserKey().equals(currentUserKey) ? RIGHT_MSG_AUDIO : LEFT_MSG_AUDIO;
            }
        } else if (model.getUserKey().equals(currentUserKey)) {
            return RIGHT_MSG;
        } else {
            return LEFT_MSG;
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull MyChatViewHolder holder, int position, @NonNull Message model) {
        if (isOrderChat) {
            displayMessage(model, holder, position);
        } else {
            //Display only Message has Time Stamp >= created Time Stamp of User Rooms
            FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USER_CHAT_ROOM_COLLECTION)
                    .child(currentUserKey)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                UserRoom userRoom = dsp.getValue(UserRoom.class);
                                if (userRoom.getRoomKey().equals(roomKey)) {
                                    if (userRoom.getCreatedTimeStamp() <= Long.parseLong(model.getTimeStamp())) {
                                        displayMessage(model, holder, position);
                                    } else {
                                        setLayoutParams(holder.rootView, 0, 0);
                                        holder.rootView.setVisibility(View.GONE);
                                    }
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void displayMessage(Message model, MyChatViewHolder holder, int position) {
        if (getItemViewType(position) == NOTIFY_MSG) {
            holder.txtNotify.setText(getNotifyMessage(model.getMessage()));
            return;
        }
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                .orderByChild("key").equalTo(model.getUserKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    User user = dsp.getValue(User.class);

                    if (holder.txtMessage != null && model.getMessage().length() > 0
                            && model.getMessage().equals(holder.txtMessage.getText())) {
                        holder.setTvTimestamp(model.getTimeStamp());
                        return;
                    }
                    holder.rootView.setVisibility(View.VISIBLE);
                    setLayoutParams(holder.rootView, ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    holder.setImgAvatar(user.getAvatar());
                    holder.setTvTimestamp(model.getTimeStamp());
                    holder.setUserName(isOrderChat ? Utils.hideStringWithStars(user.getName())
                            : user.getName());
                    holder.setTxtMessage(model.getMessage());

                    if (model.getFile() != null && model.getFile().getType().equals(FireBaseDataUtils.TYPE_IMAGE)) {
                        holder.setIvChatPhoto(model.getFile().getUrlFile());
                    } else if (model.getFile() != null && model.getFile().getType().equals(FireBaseDataUtils.TYPE_AUDIO)) {
                        holder.setTxtMessage(model.getFile().getNameFile());
                        holder.setAudioClick();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireBase", ">>> Error:" + "find onCancelled:" + databaseError);
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

    private void setLayoutParams(ViewGroup viewGroup, int w, int h) {
        ViewGroup.LayoutParams params = viewGroup.getLayoutParams();
        params.width = w;
        params.height = h;
        viewGroup.setLayoutParams(params);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();
    }

    public class MyChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Nullable
        @BindView(R.id.txt_user_name)
        TextView txtUserName;

        @Nullable
        @BindView(R.id.txt_time_stamp)
        TextView tvTimestamp;

        @Nullable
        @BindView(R.id.txt_message)
        EmojiconTextView txtMessage;

        @Nullable
        @BindView(R.id.img_avatar)
        CircleImageView imgAvatar;

        @Nullable
        @BindView(R.id.img_chat)
        ImageView ivChatPhoto;

        @Nullable
        @BindView(R.id.layout_audio)
        LinearLayout layoutAudio;

        @BindView(R.id.root_view)
        ViewGroup rootView;

        @Nullable
        @BindView(R.id.txt_notify)
        TextView txtNotify;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Message model = getItem(position);
            switch (view.getId()) {
                case R.id.img_chat:
                    mChatAdapterClickListener.onImageClick(model.getFile().getUrlFile());
                    break;
                case R.id.img_avatar:
                    mChatAdapterClickListener.onUserClick(model.getUserId());
                    break;
                case R.id.layout_audio:
                    mChatAdapterClickListener.onAudioClick(model.getFile(), position);
                    break;
                default:
                    break;
            }
        }

        public void setTxtMessage(String message) {
            if (txtMessage == null) return;
            txtMessage.setText(message);
        }

        public void setImgAvatar(String urlPhotoUser) {
            if (imgAvatar == null) return;
            Glide.with(context)
                    .load(urlPhotoUser)
                    .error(R.drawable.img_default_avatar)
                    .into(imgAvatar);
            imgAvatar.setOnClickListener(this);
        }

        public void setTvTimestamp(String timestamp) {
            if (tvTimestamp == null) return;
            tvTimestamp.setText(DateTimeUtils.convertTimestamp(context, timestamp));
        }

        public void setIvChatPhoto(String url) {
            if (ivChatPhoto == null) return;

            Glide.with(context).load(url)
                    .override(100, 100)
                    .fitCenter()
                    .into(ivChatPhoto);

            ivChatPhoto.setOnClickListener(this);
        }

        public void setUserName(String name) {
            if (txtUserName == null) return;
            txtUserName.setText(name);
        }

        public void setAudioClick() {
            if (layoutAudio == null) return;
            layoutAudio.setOnClickListener(this);
        }
    }

    public interface ChatAdapterClickListener {

        void onImageClick(String photoUrl);

        void onUserClick(int userId);

        void onAudioClick(FileModel file, int position);
    }
}
