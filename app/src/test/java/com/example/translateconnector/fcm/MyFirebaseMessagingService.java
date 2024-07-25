package com.example.translateconnector.fcm;


import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.imoktranslator.TranlookApplication;
import com.imoktranslator.activity.VotePartnerActivity;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.OrderNotificationModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.UserNeedReview;
import com.imoktranslator.utils.AppAction;
import com.imoktranslator.utils.NotificationHelper;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            processDataPayload(remoteMessage);

            // Handle message within 10 seconds
            handleNow();
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //sendNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void processDataPayload(RemoteMessage remoteMessage) {
        Intent intent;
        String notificationType = remoteMessage.getData().get("notification_type");
        switch (notificationType) {
            case NotificationHelper.NOTIFICATION_TYPE_REVIEW_USER:
                try {
                    UserNeedReview userNeedReview = new UserNeedReview();
                    userNeedReview.setOrderId(Integer.parseInt(remoteMessage.getData().get("order_id")));
                    userNeedReview.setUserId(Integer.parseInt(remoteMessage.getData().get("user_id")));
                    userNeedReview.setAcceptedTranslatorId(Integer.parseInt(remoteMessage.getData().get("accepted_translator_id")));

                    //send broadcast to update notification unread
                    intent = new Intent(AppAction.ACTION_VOTE_USER);
                    intent.putExtra(VotePartnerActivity.USER_NEED_REVIEW, userNeedReview);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                } catch (Exception e) {

                }
                break;

            case NotificationHelper.NOTIFICATION_TYPE_RELATE_ORDERS:
                OrderNotificationModel orderNotificationModel = genOrderNotificationFrom(remoteMessage.getData());
                NotificationHelper.getInstance(this).showOrderNotification(orderNotificationModel);
                break;

            case NotificationHelper.NOTIFICATION_TYPE_REJECT_SIGN_UP_TRANSLATOR:
                intent = new Intent(AppAction.ACTION_REJECT_SIGN_UP_TRANSLATOR);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;

            case NotificationHelper.NOTIFICATION_TYPE_ACCEPT_SIGN_UP_TRANSLATOR:
                intent = new Intent(AppAction.ACTION_ACCEPT_SIGN_UP_TRANSLATOR);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;

            case NotificationHelper.NOTIFICATION_TYPE_ACCEPT_TRANSLATOR_UPDATE_INFO:
                intent = new Intent(AppAction.ACTION_ACCEPT_TRANSLATOR_UPDATE_INFO);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;

            case NotificationHelper.NOTIFICATION_TYPE_REJECT_TRANSLATOR_UPDATE_INFO:
                intent = new Intent(AppAction.ACTION_REJECT_TRANSLATOR_UPDATE_INFO);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;
            case NotificationHelper.NOTIFICATION_TYPE_CHAT:
                NotificationHelper.getInstance(this).showChatNotification(getSender(remoteMessage.getData()),
                        getRoomKey(remoteMessage.getData()));
                break;
            case NotificationHelper.NOTIFICATION_TYPE_BEST_FRIEND:
                NotificationHelper.getInstance(this).showNotificationBestFriendPostStatus(getSender(remoteMessage.getData()),
                        getPostId(remoteMessage.getData()));
                break;
            case NotificationHelper.NOTIFICATION_TYPE_ADD_FRIEND:
                NotificationHelper.getInstance(this).showNotificationFriendRequest(getSender(remoteMessage.getData()));
                break;
            case NotificationHelper.NOTIFICATION_TYPE_LIKE:
                String postId = getPostId(remoteMessage.getData());
                NotificationHelper.getInstance(this).showNotificationLikePost(getSender(remoteMessage.getData()), postId);
                break;

            case NotificationHelper.NOTIFICATION_TYPE_AUTHOR_COMMENT:
                String postId1 = getPostId(remoteMessage.getData());
                NotificationHelper.getInstance(this).showNotificationAuthorComment(getSender(remoteMessage.getData()), postId1);
                break;
            case NotificationHelper.NOTIFICATION_TYPE_OTHER_USER_COMMENT:
                int ownerId = getOwnerId(remoteMessage.getData());
                if (ownerId != 0) {
                    NotificationHelper.getInstance(this).showNotificationForAuthorWhenAUserComment(getSender(remoteMessage.getData()), getPostId(remoteMessage.getData()));
                } else {
                    NotificationHelper.getInstance(this).showNotificationForOtherUserWhenAUserComment(getSender(remoteMessage.getData()), getPostId(remoteMessage.getData()), getOwner(remoteMessage.getData()));
                }
                break;
        }
    }

    private int getOwnerId(Map<String, String> data) {
        String ownerIdStr = data.get("owner_id");
        try {
            return Integer.parseInt(ownerIdStr);
        } catch (Exception e) {
            return 0;
        }
    }

    private String getPostId(Map<String, String> data) {
        return data.get("model_id");
    }

    private PersonalInfo getSender(Map<String, String> data) {
        String receiver = data.get("sender");
        PersonalInfo personalInfo = TranlookApplication.getGson().fromJson(receiver, PersonalInfo.class);
        return personalInfo;
    }

    private PersonalInfo getOwner(Map<String, String> data) {
        String receiver = data.get("owner");
        PersonalInfo personalInfo = TranlookApplication.getGson().fromJson(receiver, PersonalInfo.class);
        return personalInfo;
    }

    private String getRoomKey(Map<String, String> data) {
        String roomKey = data.get("room_key");
        return roomKey;
    }

    private OrderNotificationModel genOrderNotificationFrom(Map<String, String> data) {
        OrderNotificationModel orderNotificationModel = new OrderNotificationModel();

        int type = Integer.parseInt(data.get("type"));
        orderNotificationModel.setType(type);

        orderNotificationModel.setCreatedAt("dummy");

        String orderJson = data.get("order");
        orderNotificationModel.setOrder(new Gson().fromJson(orderJson, OrderModel.class));

        String senderJson = data.get("sender");
        orderNotificationModel.setSender(new Gson().fromJson(senderJson, PersonalInfo.class));

        orderNotificationModel.setStatus(0);

        return orderNotificationModel;
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }
}
