package com.example.translateconnector.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.activity.ChatRoomActivity;
import com.imoktranslator.activity.GeneralNotificationActivity;
import com.imoktranslator.activity.OrderDetailActivity;
import com.imoktranslator.activity.PostDetailActivity;
import com.imoktranslator.model.OrderNotificationModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.transform.CircleTransform;

import java.util.Random;

import static com.imoktranslator.activity.OrderDetailActivity.ORDER_KEY;

public class NotificationHelper {

    public static final String NOTIFICATION_TYPE_RELATE_ORDERS = "0";
    public static final String NOTIFICATION_TYPE_REVIEW_USER = "1";
    public static final String NOTIFICATION_TYPE_ACCEPT_SIGN_UP_TRANSLATOR = "2";
    public static final String NOTIFICATION_TYPE_REJECT_SIGN_UP_TRANSLATOR = "3";
    public static final String NOTIFICATION_TYPE_ACCEPT_TRANSLATOR_UPDATE_INFO = "4";
    public static final String NOTIFICATION_TYPE_REJECT_TRANSLATOR_UPDATE_INFO = "5";

    public static final String NOTIFICATION_TYPE_BEST_FRIEND = "6";
    public static final String NOTIFICATION_TYPE_ADD_FRIEND = "7";
    public static final String NOTIFICATION_TYPE_CHAT = "8";
    public static final String NOTIFICATION_TYPE_LIKE = "9";
    public static final String NOTIFICATION_TYPE_AUTHOR_COMMENT = "10";
    public static final String NOTIFICATION_TYPE_OTHER_USER_COMMENT = "12";

    private static final String CHANNEL_ID = "Translator";
    private static NotificationHelper instance;
    private Context context;
    private NotificationManager notificationManager;
    private Uri defaultSoundUri;
    private NotificationCompat.Builder builder;
    private Random random = new Random();
    private SimpleTarget<Bitmap> target;

    public static NotificationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationHelper(context);
        }
        return instance;
    }

    private NotificationHelper() {
    }

    private NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        initBuilder();
        createNotificationChanel();
    }

    private void initBuilder() {
        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH);
    }

    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CHANEL NAME";
            String description = "CHANEL DESCRIPTION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showOrderNotification(OrderNotificationModel notificationModel) {
        if (!Validator.validNotificationInfo(notificationModel)) {
            return;
        }

        //send broadcast to update notification unread
        Intent intent = new Intent(AppAction.ACTION_UPDATE_UNREAD_ORDER_NOTIFICATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        boolean chatting = LocalSharedPreferences.getInstance(context).getBooleanData(Constants.KEY_CHATTING);
        if (chatting && notificationModel.getType() == OrderNotificationModel.NEW_MESSAGE) {
            return;
        }
        if (notificationModel.isNotificationForWorker()) {
            showNotificationForWorker(notificationModel);
        } else if (notificationModel.isNotificationForOwner()) {
            showNotificationForOwner(notificationModel);
        } else if (notificationModel.getType() == OrderNotificationModel.CANCELLED) {
            showCancelledOrderNotification(notificationModel);
        }
    }

    private void showCancelledOrderNotification(OrderNotificationModel notificationModel) {
        int notificationId = random.nextInt(1000);

        //create PendingIntent
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(ORDER_KEY, notificationModel.getOrder());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //custom notification view
        String notices = context.getString(R.string.TB_1048);

        String message = String.format(context.getString(R.string.TB_1051),
                notificationModel.getOrder().getName(), notificationModel.getOrder().getReasonToCancel());

        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, notices.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                message.indexOf(notificationModel.getOrder().getName()),
                (message.indexOf(notificationModel.getOrder().getName()) + notificationModel.getOrder().getName().length()),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_order_notification_for_worker);
        remoteViews.setTextViewText(R.id.tv_message, spannableString);
        remoteViews.setTextViewText(R.id.tv_time, notificationModel.getCreatedAt());

        //builder
        builder.setCustomHeadsUpContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, builder.build());
    }

    private void showNotificationForOwner(OrderNotificationModel notificationModel) {
        int notificationId = random.nextInt(1000);
        //create PendingIntent
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(ORDER_KEY, notificationModel.getOrder());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //custom notification view
        String message = "";

        if (notificationModel.getType() == OrderNotificationModel.NEW_BIDS) {
            message = String.format(context.getString(R.string.TB_1049),
                    Utils.hideStringWithStars(notificationModel.getSender().getName()),
                    notificationModel.getOrder().getName());
        } else if (notificationModel.getType() == OrderNotificationModel.NEW_MESSAGE) {
            message = String.format(context.getString(R.string.TB_1050),
                    Utils.hideStringWithStars(notificationModel.getSender().getName()),
                    notificationModel.getOrder().getName());
        }
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                0,
                notificationModel.getSender().getName().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                message.indexOf(notificationModel.getOrder().getName()),
                message.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_order_notification_for_owner);
        remoteViews.setTextViewText(R.id.tv_message, spannableString);

        //builder
        builder.setCustomHeadsUpContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();

        if (notificationModel.getSender().getAvatar() != null) {
            loadUserAvatar(notificationModel.getSender().getAvatar(), remoteViews, notificationId, notification);
        } else {
            notificationManager.notify(notificationId, notification);
        }
    }

    private void loadUserAvatar(String avatar, RemoteViews remoteViews,
                                int notificationId, Notification notification) {
        target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                remoteViews.setImageViewBitmap(R.id.iv_avatar, resource);
                notificationManager.notify(notificationId, notification);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                notificationManager.notify(notificationId, notification);
            }
        };

        new Handler(Looper.getMainLooper()).post(() -> Glide.with(context.getApplicationContext())
                .load(avatar)
                .asBitmap()
                .transform(new CircleTransform(context))
                .override(100, 100)
                .into(target));
    }

    private void showNotificationForWorker(OrderNotificationModel notificationModel) {
        int notificationId = random.nextInt(1000);

        //create PendingIntent
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(ORDER_KEY, notificationModel.getOrder());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //custom notification view
        String thongBao = context.getString(R.string.TB_1048);
        String message = "";
        if (notificationModel.getType() == OrderNotificationModel.NEW_ORDER) {
            message = String.format(context.getString(R.string.TB_1045),
                    notificationModel.getOrder().getName());
        } else if (notificationModel.getType() == OrderNotificationModel.ALLOW_PRICE) {
            message = String.format(context.getString(R.string.TB_1046),
                    notificationModel.getOrder().getName());
        } else if (notificationModel.getType() == OrderNotificationModel.REFUSE_PRICE) {
            message = String.format(context.getString(R.string.TB_1047),
                    notificationModel.getOrder().getName());
        }

        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, thongBao.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                message.indexOf(notificationModel.getOrder().getName()),
                message.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_order_notification_for_worker);
        remoteViews.setTextViewText(R.id.tv_message, spannableString);
        //remoteViews.setTextViewText(R.id.tv_time, notificationModel.getCreatedAt());

        //builder
        builder.setCustomHeadsUpContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, builder.build());
    }

    public void showChatNotification(PersonalInfo personalInfo, String roomKey) {
        boolean chatting = LocalSharedPreferences.getInstance(context).getBooleanData(Constants.KEY_CHATTING);
        if (chatting) {
            return;
        }

        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                .orderByChild("id").equalTo(personalInfo.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USER_CHAT_ROOM_COLLECTION)
                        .child(user.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            UserRoom userRoom = dsp.getValue(UserRoom.class);
                            if (userRoom != null && userRoom.getKey().equals(roomKey)) {
                                pushNotificationChat(userRoom, personalInfo);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showNotificationFriendRequest(PersonalInfo personalInfo) {
        //create PendingIntent
        Intent intent = new Intent(context, GeneralNotificationActivity.class);
        intent.putExtra(GeneralNotificationActivity.OPEN_ADD_FRIEND, true);

        String message = String.format(context.getString(R.string.TB_1062), personalInfo.getName());
        showCommonNotification(intent, message, personalInfo);
    }

    private void pushNotificationChat(UserRoom userRoom, PersonalInfo personalInfo) {
        //create PendingIntent
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(ChatRoomActivity.INTENT_ROOM, userRoom);

        String message = String.format(context.getString(R.string.TB_1061), personalInfo.getName());
        showCommonNotification(intent, message, personalInfo);
    }

    private void showCommonNotification(Intent intent, String message, PersonalInfo personalInfo) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, personalInfo.getName().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_order_notification_for_owner);
        remoteViews.setTextViewText(R.id.tv_message, spannableString);

        //builder
        builder.setCustomHeadsUpContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent);
        int notificationId = (int) System.currentTimeMillis();
        Notification notification = builder.build();

        if (!TextUtils.isEmpty(personalInfo.getAvatar())) {
            loadUserAvatar(personalInfo.getAvatar(), remoteViews, notificationId, notification);
        } else {
            notificationManager.notify(notificationId, notification);
        }
    }

    public void showNotificationBestFriendPostStatus(PersonalInfo personalInfo, String postId) {
        //create PendingIntent
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra(Constants.POST_ID_KEY, postId);
        String message = String.format(context.getString(R.string.TB_1081), personalInfo.getName());

        Intent broadsCastIntent = new Intent(AppAction.ACTION_UPDATE_UNREAD_NOTIFICATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadsCastIntent);

        showCommonNotification(intent, message, personalInfo);
    }

    public void showNotificationLikePost(PersonalInfo personalInfo, String postId) {
        //create PendingIntent
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra(Constants.POST_ID_KEY, postId);

        String message = String.format(context.getString(R.string.TB_1068), personalInfo.getName());

        Intent broadsCastIntent = new Intent(AppAction.ACTION_UPDATE_UNREAD_NOTIFICATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadsCastIntent);
        showCommonNotification(intent, message, personalInfo);
    }

    public void showNotificationAuthorComment(PersonalInfo personalInfo, String postId) {
        //create PendingIntent
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra(Constants.POST_ID_KEY, postId);

        String message = String.format(context.getString(R.string.TB_1076), personalInfo.getName());

        Intent broadsCastIntent = new Intent(AppAction.ACTION_UPDATE_UNREAD_NOTIFICATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadsCastIntent);
        showCommonNotification(intent, message, personalInfo);
    }

    public void showNotificationForAuthorWhenAUserComment(PersonalInfo sender, String postId) {
        //create PendingIntent
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra(Constants.POST_ID_KEY, postId);

        String message = String.format(context.getString(R.string.TB_1077), sender.getName());

        Intent broadsCastIntent = new Intent(AppAction.ACTION_UPDATE_UNREAD_NOTIFICATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadsCastIntent);
        showCommonNotification(intent, message, sender);
    }

    public void showNotificationForOtherUserWhenAUserComment(PersonalInfo sender, String postId, PersonalInfo owner) {
        //create PendingIntent
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra(Constants.POST_ID_KEY, postId);

        String message = String.format(context.getString(R.string.TB_1078), sender.getName(), owner.getName());

        Intent broadsCastIntent = new Intent(AppAction.ACTION_UPDATE_UNREAD_NOTIFICATION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadsCastIntent);
        showCommonNotification(intent, message, sender);
    }
}
