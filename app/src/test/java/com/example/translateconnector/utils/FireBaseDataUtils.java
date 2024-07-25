package com.example.translateconnector.utils;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.firebase.data.FirebaseUserData;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.firebase.Friend;
import com.imoktranslator.model.firebase.Invitation;
import com.imoktranslator.model.firebase.Message;
import com.imoktranslator.model.firebase.OfferPrice;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireBaseDataUtils {

    private static final String TRANS_ID = "transID";
    private static final String ORDER_ID = "orderId";
    public static String MESSAGES_COLECTION = "messages";
    public static String USERS_COLLECTION = "users";
    public static String OFFER_PRICE = "offer_price";
    public static String ROOM_MESSAGES_COLLECTION = "room_messages";
    public static String USER_FRIEND_COLLECTION = "user_friends";
    public static String FRIEND_INVITATION_COLLECTION = "friend_invitations";
    public static String USER_CHAT_ROOM_COLLECTION = "user_rooms";

    public static final String TIME_LINE = "time_line";
    public static final String COMMENTS = "comments";
    public static final String NEWS_FEED = "news_feed";
    public static final String TIMESTAMP_REVERSE = "timestampReverse";
    public static final String POSTS = "posts";
    public static final String AUTHOR_CHILD_NODE = "author";
    public static final String POST_STATS_CHILD_NODE = "postStats";
    public static final String COMMENT_STATS_CHILD_NODE = "commentStats";
    public static final String POST_ID_CHILD_NODE = "postId";
    public static final String COMMENT_IDS_CHILD_NODE = "commentIds";
    public static final String FRIENDS_CHILD_NODE = "friends";
    public static final String COMMENTATORS_CHILD_NODE = "commentators";
    public static final String COMMENTS_CHILD_NODE = "comments";
    public static final String WALL = "wall";

    public static int ROOM_TYPE_PERSONAL = 0;
    public static int ROOM_TYPE_GROUP = 1;
    public static int ROOM_TYPE_ORDER_INTERNAL = 2;

    public static int CHAT_TYPE_NORMAL = 0;
    public static int CHAT_TYPE_NOTIFY = 1;

    public static String DEFAULT_VALUE = "translator_connector_default";

    public static final String TYPE_IMAGE = "img";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_VIDEO = "video";

    public static final String URL_STORAGE_REFERENCE = "gs://translook-1523109512153.appspot.com";
    public static final String CHAT_STORAGE = "chat_storage";


    public static FireBaseDataUtils instance;
    public static DatabaseReference mFirebaseDatabaseReference;
    public static FirebaseAuth mAuth;

    public static FireBaseDataUtils getInstance() {
        if (instance == null) {
            instance = new FireBaseDataUtils();
        }
        return instance;
    }

    private FireBaseDataUtils() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getFirebaseReference() {
        return mFirebaseDatabaseReference;
    }

    public void createUserWithEmail(String email, String pass, OnCompleteListener onCompleteListener) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(onCompleteListener);

    }

    public void loginToFireBase(String email, String password, OnCompleteListener onCompleteListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(onCompleteListener);
    }

    public void addNewUser(Context context, User user) {
        String userKey = mFirebaseDatabaseReference.child(USERS_COLLECTION).push().getKey();

        LocalSharedPreferences.getInstance(context).saveKeyUser(userKey);
        user.setKey(userKey);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + USERS_COLLECTION + "/" + userKey, user);
        mFirebaseDatabaseReference.updateChildren(childUpdates, (databaseError, databaseReference) -> {
            if (databaseError == null) {

            }
        });
    }

    public void addMessage(Context context, String roomKey, Message message, int roomType) {
        DatabaseReference data = mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION)
                .child(roomKey).child(MESSAGES_COLECTION).push();
        message.setKey(data.getKey());
        data.setValue(message);

//        String lastMessage;
//        if (message.getRecord() != null) {
//            lastMessage = "đã gửi tin nhắn ghi âm";
//        } else if (message.getFile() != null) {
//            lastMessage = "đã gửi hình ảnh";
//        } else {
//            lastMessage = message.getMessage();
//        }

        //update Room Message
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("lastTimeActive", Long.parseLong(message.getTimeStamp()));
        if (!TextUtils.isEmpty(message.getMessage())) {
            postValues.put("lastMessage", message.getMessage());
        }
        postValues.put("lastSender", message.getUserKey());
        mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION)
                .child(roomKey).updateChildren(postValues);

        mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION).orderByChild("key").equalTo(roomKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Room room = dsp.getValue(Room.class);
                            updateUserRoom(context, room.getOwner(), roomKey, roomType, Long.parseLong(message.getTimeStamp()));
                            List<String> listVisitor = Arrays.asList(room.getVisitor().split(","));
                            for (String keyUser : listVisitor) {
                                updateUserRoom(context, keyUser, roomKey, roomType, Long.parseLong(message.getTimeStamp()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Update lasTimeActive if contain a user_rooms with userKey, then create new user_rooms
     * (user_rooms deleted from user)
     *
     * @param userKey
     * @param roomKey
     * @param type
     * @param timeStamp
     */
    private void updateUserRoom(Context context, String userKey, String roomKey, int type, long timeStamp) {
        String currentUserKey = LocalSharedPreferences.getInstance(context).getKeyUser();

        mFirebaseDatabaseReference.child(USER_CHAT_ROOM_COLLECTION).child(userKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            addNewUserChatRoom(userKey, roomKey, type == ROOM_TYPE_GROUP, timeStamp);
                        } else {
                            Map<String, Object> postValues = new HashMap<>();
                            postValues.put("lastTimeActive", timeStamp);
                            if (!currentUserKey.equals(userKey)) {
                                postValues.put("hasUnreadMessage", true);
                            }
                            postValues.put("deleted", false);
                            mFirebaseDatabaseReference.child(USER_CHAT_ROOM_COLLECTION).child(userKey).child(roomKey)
                                    .updateChildren(postValues);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public void addNewUserChatRoom(String userKey, String roomKey, boolean isGroup, long timeStamp) {
        UserRoom userRoom = new UserRoom(roomKey, userKey, roomKey, timeStamp,
                timeStamp > 0 ? timeStamp : System.currentTimeMillis());
        if (isGroup) {
            userRoom.setType(ROOM_TYPE_GROUP);
        } else {
            userRoom.setType(ROOM_TYPE_PERSONAL);
        }
        mFirebaseDatabaseReference.child(USER_CHAT_ROOM_COLLECTION).child(userKey).child(roomKey).setValue(userRoom);
    }

    public String addNewChatRoom(Room room) {
        DatabaseReference data = mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION).push();
        room.setKey(data.getKey());
        data.setValue(room);
        return data.getKey();
    }

    /**
     * @param nameOfGroup
     * @param ownerKey
     * @param visitorKeys
     */
    public String addNewGroupChat(Context context, String nameOfGroup, String ownerKey,
                                  List<String> visitorKeys, int userId, String userName) {
        Room room = new Room(nameOfGroup, ROOM_TYPE_GROUP, ownerKey, DEFAULT_VALUE,
                ownerKey, DEFAULT_VALUE, System.currentTimeMillis());
        String roomKey = addNewChatRoom(room);

        //add New User Chat Room for owner
        addNewUserChatRoom(ownerKey, roomKey, true, System.currentTimeMillis());

        //Add new notify message
        String notify = String.format("%1s đã tạo nhóm chat", userName);
        Message message = new Message(ownerKey, notify, String.valueOf(System.currentTimeMillis())
                , null, null, userId);
        message.setType(CHAT_TYPE_NOTIFY);
        addMessage(context, roomKey, message, ROOM_TYPE_GROUP);

        String visitorGroup = "";
        for (String visitorKey : visitorKeys) {
            addNewUserChatRoom(visitorKey, roomKey, true, System.currentTimeMillis());
            visitorGroup += visitorKey + ",";
        }
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("visitor", visitorGroup.substring(0, visitorGroup.length() - 1));
        mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION).child(roomKey)
                .updateChildren(postValues);
        return roomKey;
    }

    public void addUserToGroup(Context context, Room room, List<User> users, String visitors,
                               OnCompleteListener completionListener) {
        for (User user : users) {
            //add new User Room
            addNewUserChatRoom(user.getKey(), room.getKey(), true, System.currentTimeMillis());

            //Add new notify message
            String notify = String.format("%1s đã được thêm vào nhóm chat", user.getName());
            Message message = new Message(user.getKey(), notify, String.valueOf(System.currentTimeMillis())
                    , null, null, user.getId());
            message.setType(CHAT_TYPE_NOTIFY);
            addMessage(context, room.getKey(), message, ROOM_TYPE_GROUP);
        }
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("visitor", visitors);
        postValues.put("type", ROOM_TYPE_GROUP);

        mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION).child(room.getKey())
                .updateChildren(postValues)
                .addOnCompleteListener(completionListener);
    }

    public void addNewFriend(String owner, String visitor) {
        //remove partner friend invitation
        ignoreInvitation(visitor, owner);
        Room room = new Room(DEFAULT_VALUE, ROOM_TYPE_PERSONAL, owner, DEFAULT_VALUE, owner, visitor, 0);
        String roomKey = addNewChatRoom(room);
        Friend ownerFriend = new Friend(visitor, roomKey);
        mFirebaseDatabaseReference.child(USER_FRIEND_COLLECTION).child(owner).child(visitor).setValue(ownerFriend);

        Friend visitorFriend = new Friend(owner, roomKey);
        mFirebaseDatabaseReference.child(USER_FRIEND_COLLECTION).child(visitor).child(owner).setValue(visitorFriend);

        //add corresponding chat room to each user
        addNewUserChatRoom(owner, roomKey, false, 0);
        addNewUserChatRoom(visitor, roomKey, false, 0);
        mFirebaseDatabaseReference.child(USERS_COLLECTION).child(owner).child(FRIENDS_CHILD_NODE).child(visitor).setValue(true);
        mFirebaseDatabaseReference.child(USERS_COLLECTION).child(visitor).child(FRIENDS_CHILD_NODE).child(owner).setValue(true);
    }

    private void removeFriend(String userKey, String friendKey) {
        removeFriend(userKey, friendKey, null);
    }

    public void removeFriend(String userId, String friendId, DatabaseReference.CompletionListener listener) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + USER_FRIEND_COLLECTION + "/" + userId + "/" + friendId, null);
        childUpdates.put("/" + USER_FRIEND_COLLECTION + "/" + friendId + "/" + userId, null);

        childUpdates.put("/" + USERS_COLLECTION + "/" + userId + "/" + FRIENDS_CHILD_NODE + "/" + friendId, null);
        childUpdates.put("/" + USERS_COLLECTION + "/" + friendId + "/" + FRIENDS_CHILD_NODE + "/" + userId, null);

        if (listener != null) {
            mFirebaseDatabaseReference.updateChildren(childUpdates, listener);
        } else {
            mFirebaseDatabaseReference.updateChildren(childUpdates);
        }
    }

    public void sendInvitation(Invitation invitation, String friendKey,
                               DatabaseReference.CompletionListener completionListener) {
        mFirebaseDatabaseReference
                .child(FRIEND_INVITATION_COLLECTION)
                .child(friendKey)
                .child(invitation.getSenderKey())
                .setValue(invitation, completionListener);
    }

    public void updateUserLocation(Context context, Location location, String key) throws IOException {
        if (context == null || location == null || TextUtils.isEmpty(key)) {
            return;
        }
        Address address = Utils.getCurrentAddress(context, location.getLatitude(), location.getLongitude());
        Map<String, Object> postValues = new HashMap<String, Object>();
        postValues.put("latitude", location.getLatitude());
        postValues.put("longitude", location.getLongitude());
        postValues.put("countryCode", address.getCountryCode());
        postValues.put("city", address.getAdminArea());
        mFirebaseDatabaseReference.child(USERS_COLLECTION).child(key).updateChildren(postValues);

        User user = LocalSharedPreferences.getInstance(context).getCurrentFirebaseUser();
        if (user != null) {
            user.setLatitude(location.getLatitude());
            user.setLongitude(location.getLongitude());
            user.setCountryCode(address.getCountryCode());
            user.setCity(address.getAdminArea());
            LocalSharedPreferences.getInstance(context).saveCurrentFirebaseUser(user);
        }
    }

    public void updateUserAvatar(Context context, String avatar) {
        String key = LocalSharedPreferences.getInstance(context).getKeyUser();

        mFirebaseDatabaseReference.child(USERS_COLLECTION).child(key).child("avatar")
                .setValue(avatar);
        User user = LocalSharedPreferences.getInstance(context).getCurrentFirebaseUser();
        user.setAvatar(avatar);
        LocalSharedPreferences.getInstance(context).saveCurrentFirebaseUser(user);
    }

    public void updateUserGender(Context context, int gender) {
        String key = LocalSharedPreferences.getInstance(context).getKeyUser();

        mFirebaseDatabaseReference.child(USERS_COLLECTION).child(key).child("gender")
                .setValue(gender);
        User user = LocalSharedPreferences.getInstance(context).getCurrentFirebaseUser();
        user.setGender(gender);
        LocalSharedPreferences.getInstance(context).saveCurrentFirebaseUser(user);
    }

    public void updateUserDob(Context context, String dob) {
        String key = LocalSharedPreferences.getInstance(context).getKeyUser();

        mFirebaseDatabaseReference.child(USERS_COLLECTION).child(key).child("dob")
                .setValue(dob);
        User user = LocalSharedPreferences.getInstance(context).getCurrentFirebaseUser();
        user.setDob(dob);
        LocalSharedPreferences.getInstance(context).saveCurrentFirebaseUser(user);
    }

    public void updateUserAddress(Context context, String registerCountry, String registerCity) {
        String key = LocalSharedPreferences.getInstance(context).getKeyUser();
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("registerCountry", registerCountry);
        userInfo.put("registerCity", registerCity);
        mFirebaseDatabaseReference.child(USERS_COLLECTION).child(key).updateChildren(userInfo);
        User user = LocalSharedPreferences.getInstance(context).getCurrentFirebaseUser();
        user.setRegisterCountry(registerCountry);
        user.setRegisterCity(registerCity);
        LocalSharedPreferences.getInstance(context).saveCurrentFirebaseUser(user);
    }

    public void updateUserTranslator(Context context) {
        String key = LocalSharedPreferences.getInstance(context).getKeyUser();

        mFirebaseDatabaseReference.child(USERS_COLLECTION).child(key).child("translator")
                .setValue(true);
        User user = LocalSharedPreferences.getInstance(context).getCurrentFirebaseUser();
        user.setTranslator(true);
        LocalSharedPreferences.getInstance(context).saveCurrentFirebaseUser(user);
        //remove all friend aren't translator
        removeFriends(key);
        //leave all group chat
        leaveAllGroupChats(key, context);

        //sync user translator info
        FirebaseUserData.getInstance().checkAndSyncUserInfo(context);
    }

    private void leaveAllGroupChats(String userKey, Context context) {
        mFirebaseDatabaseReference.child(FireBaseDataUtils.USER_CHAT_ROOM_COLLECTION)
                .child(userKey).orderByChild("lastTimeActive")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            UserRoom userRoom = dsp.getValue(UserRoom.class);
                            if (!userRoom.isDeleted() &&
                                    userRoom.getType() == FireBaseDataUtils.ROOM_TYPE_GROUP
                                    && userRoom.getLastTimeActive() > 0) {
                                removeUserInRoom(context, userRoom, null);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //
                    }
                });
    }

    private void removeFriends(String key) {
        mFirebaseDatabaseReference.child(USER_FRIEND_COLLECTION)
                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Friend friend = dsp.getValue(Friend.class);

                    mFirebaseDatabaseReference.child(USERS_COLLECTION).
                            orderByChild("key").equalTo(friend.getUserKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                                    if (!user.isTranslator()) {
                                        removeFriend(user.getKey(), key);
                                        disableRoomChat(friend.getRoomKey(), (databaseError, databaseReference) -> {
                                            //
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateUserStatus(Context context, String status) {
        String key = LocalSharedPreferences.getInstance(context).getKeyUser();

        mFirebaseDatabaseReference.child(USERS_COLLECTION).child(key).child("status")
                .setValue(status);
        User user = LocalSharedPreferences.getInstance(context).getCurrentFirebaseUser();
        user.setStatus(status);
        LocalSharedPreferences.getInstance(context).saveCurrentFirebaseUser(user);
    }

    /**
     * When Owner Accept an Offer. create an active order in FireBase and corresponding room
     *
     * @param context activity context
     * @param orderID ID of order
     */
    public String addNewOffer(Context context, int orderID, int transId) {
        // add new chat room for order
        String currentKey = LocalSharedPreferences.getInstance(context).getKeyUser();
        Room room = new Room(DEFAULT_VALUE, ROOM_TYPE_ORDER_INTERNAL, DEFAULT_VALUE, DEFAULT_VALUE, currentKey, DEFAULT_VALUE, 0);
        String roomKey = addNewChatRoom(room);

        //add new active order to user's order list
        DatabaseReference data = mFirebaseDatabaseReference.child(OFFER_PRICE).push();
        String orderKey = data.getKey();
        OfferPrice offerPrice = new OfferPrice(orderKey, orderID, roomKey, transId);
        data.setValue(offerPrice);
        return roomKey;
    }

    /**
     * When owner or translator send an message in chat order. Create message under corresponding room
     *
     * @param roomKey key of room
     * @param message message object
     */
    public void addMessageInChatOrder(String roomKey, Message message) {
        DatabaseReference data = mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION).child(roomKey).child(MESSAGES_COLECTION).push();
        message.setKey(data.getKey());
        data.setValue(message);

        String lastMessage;
        if (message.getRecord() != null) {
            lastMessage = "đã gửi tin nhăn ghi âm";
        } else if (message.getFile() != null) {
            lastMessage = "đã gửi hình ảnh";
        } else {
            lastMessage = message.getMessage();
        }
        //update Room Message
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("lastTimeActive", Long.parseLong(message.getTimeStamp()));
        postValues.put("lastMessage", lastMessage);
        postValues.put("lastSender", message.getUserKey());
        mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION)
                .child(roomKey).updateChildren(postValues);
    }

    public void getOrderRoomById(int orderID, ValueEventListener listener) {
        mFirebaseDatabaseReference.child(FireBaseDataUtils.OFFER_PRICE).orderByChild(ORDER_ID).equalTo(orderID).
                addListenerForSingleValueEvent(listener);
    }


    public void ignoreInvitation(String userKey, String senderKey) {
        mFirebaseDatabaseReference
                .child(FireBaseDataUtils.FRIEND_INVITATION_COLLECTION)
                .child(userKey)
                .child(senderKey)
                .removeValue((databaseError, databaseReference) -> {
                });
    }

    public void deleteUserRoom(Context context, UserRoom userRoom, DatabaseReference.CompletionListener completionListener) {
        Map<String, Object> postValues = new HashMap<>();
        postValues.put("createdTimeStamp", System.currentTimeMillis());
        postValues.put("deleted", true);
        DatabaseReference databaseReference = mFirebaseDatabaseReference.child(FireBaseDataUtils.USER_CHAT_ROOM_COLLECTION)
                .child(userRoom.getUserKey())
                .child(userRoom.getRoomKey());
        if (userRoom.getType() == ROOM_TYPE_PERSONAL) {
            databaseReference.updateChildren(postValues, completionListener);
        } else {
            databaseReference.updateChildren(postValues);
            removeUserInRoom(context, userRoom, completionListener);
        }
    }

    public void removeUserInRoom(Context context, UserRoom userRoom, DatabaseReference.CompletionListener completionListener) {
        mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION).orderByChild("key").equalTo(userRoom.getRoomKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Room room = dsp.getValue(Room.class);
                            PersonalInfo personalInfo = LocalSharedPreferences.getInstance(context)
                                    .getPersonalInfo();
                            removeUserInRoom(context, userRoom.getUserKey(), personalInfo.getId(),
                                    personalInfo.getName(), room, completionListener);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void removeUserInRoom(Context context, String userKey, int userId, String userName, Room room,
                                 DatabaseReference.CompletionListener completionListener) {
        Map<String, Object> postValues = new HashMap<>();
        List<String> listVisitor = new ArrayList<>();
        listVisitor.addAll(Arrays.asList(room.getVisitor().split(",")));
        if (listVisitor.contains(userKey)) {
            listVisitor.remove(userKey);
            String visitorGroup = "";
            for (String visitorKey : listVisitor) {
                String append = visitorKey + ",";
                visitorGroup += append;
            }
            String visitor = visitorGroup.length() > 0
                    ? visitorGroup.substring(0, visitorGroup.length() - 1) : "";
            postValues.put("visitor", visitor);
            room.setVisitor(visitor);
        } else if (userKey.equals(room.getOwner())) {
            postValues.put("owner", listVisitor.get(0));
            room.setOwner(listVisitor.get(0));
            listVisitor.remove(0);
            String visitorGroup = "";
            for (String visitor : listVisitor) {
                visitorGroup += visitor + ",";
            }
            postValues.put("visitor", visitorGroup.substring(0, visitorGroup.length() - 1));
            room.setVisitor(visitorGroup.substring(0, visitorGroup.length() - 1));
        }

        //remove user rooms & update room message
        mFirebaseDatabaseReference.child(USER_CHAT_ROOM_COLLECTION).child(userKey).child(room.getKey()).removeValue();
        if (listVisitor.size() == 0) {
            //remove room message if there is only one user
            mFirebaseDatabaseReference.child(USER_CHAT_ROOM_COLLECTION).child(room.getOwner()).child(room.getKey()).removeValue();
            mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION).child(room.getKey()).removeValue(completionListener);
        } else {
            //create new notify message
            String currentKey = LocalSharedPreferences.getInstance(context).getKeyUser();
            boolean selfLeavingGroup = currentKey.equals(userKey);
            String notify = String.format(selfLeavingGroup ? "%1s đã rời khỏi nhóm"
                    : "%1s đã bị mời khỏi nhóm", userName);

            Message message = new Message(currentKey, notify, String.valueOf(System.currentTimeMillis())
                    , null, null, userId);
            message.setType(CHAT_TYPE_NOTIFY);

            addMessage(context, room.getKey(), message, room.getType());

            mFirebaseDatabaseReference.child(ROOM_MESSAGES_COLLECTION).child(room.getKey()).
                    updateChildren(postValues, completionListener);
        }
    }

    public void muteNotification(UserRoom userRoom, DatabaseReference.CompletionListener completionListener) {
        mFirebaseDatabaseReference
                .child(USER_CHAT_ROOM_COLLECTION)
                .child(userRoom.getUserKey())
                .child(userRoom.getRoomKey())
                .child("muteNotification")
                .setValue(userRoom.isMuteNotification(), completionListener);
    }

    public void renameGroupChat(Room room, DatabaseReference.CompletionListener completionListener) {
        mFirebaseDatabaseReference
                .child(ROOM_MESSAGES_COLLECTION)
                .child(room.getKey())
                .child("chatRoomName")
                .setValue(room.getChatRoomName(), completionListener);

    }

    public void addMessageChangeGroupName(Context context, Room room) {
        User currentUser = LocalSharedPreferences.getInstance(context).getCurrentFirebaseUser();
        String notify = String.format("%1s đã đổi tên nhóm chat thành %2s", currentUser.getName(),
                room.getChatRoomName());
        Message message = new Message(currentUser.getKey(), notify, String.valueOf(System.currentTimeMillis())
                , null, null, currentUser.getId());
        message.setType(CHAT_TYPE_NOTIFY);

        addMessage(context, room.getKey(), message, room.getType());
    }

    public void disableRoomChat(String roomKey, DatabaseReference.CompletionListener completionListener) {
        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.ROOM_MESSAGES_COLLECTION)
                .child(roomKey)
                .child("readOnly").setValue(true, completionListener);
    }

    public void setRoomChatAvatar(String roomKey, String avatarUrl,
                                  DatabaseReference.CompletionListener completionListener) {
        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.ROOM_MESSAGES_COLLECTION)
                .child(roomKey)
                .child("avatar").setValue(avatarUrl, completionListener);
    }
}

