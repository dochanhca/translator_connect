package com.example.translateconnector.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.firebase.PostManager;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.NotificationHelper;
import com.imoktranslator.utils.Utils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class TimeLinePresenter extends BasePresenter {

    private TimeLineView view;

    protected PersonalInfo personalInfo;

    public TimeLinePresenter(Context context, TimeLineView view) {
        super(context);
        this.view = view;
    }

    public void getUserData() {
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().fetchPersonalInfo(), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                if (response != null && response.getPersonalInfo() != null) {
                    personalInfo = response.getPersonalInfo();
                    if (personalInfo.getStatus() == Constants.STATUS_LOCKED) {
                        view.onAccountLocked();
                    } else {
                        LocalSharedPreferences.getInstance(getContext()).savePersonalInfo(personalInfo);
                        view.getUserInfo(personalInfo);
                        getKeyByUserId(personalInfo.getId());
                    }
                    DialogUtils.hideProgress();
                }
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    private void getKeyByUserId(int id) {
        //Check user already existed in FireBase
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference().
                child(FireBaseDataUtils.USERS_COLLECTION).orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                    String key = nodeDataSnapshot.getKey();
                    User user = nodeDataSnapshot.getValue(User.class);

                    updateFireBaseUser(key);
                    updateFireUser(user);
                    if (!TextUtils.isEmpty(key)) {
                        view.initWall();
                    }
                } catch (NoSuchElementException e) {
                    updateFireBaseUser(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireBase", ">>> Error:" + "find onCancelled:" + databaseError);
                updateFireBaseUser(null);
            }
        });
    }

    private void updateFireBaseUser(String key) {
        if (key != null && !key.isEmpty()) {
            //User already been added before, update location only
            LocalSharedPreferences.getInstance(getContext()).saveKeyUser(key);
            updateUserLocation(key);
        } else {
            //In first time login by this user, add new user to FireBase
            String status = "";
            double latitude = Constants.DEFAULT_LAT;
            double longitude = Constants.DEFAULT_LNG;
            Address address = null;
            if (view.getLocation() != null) {
                latitude = view.getLocation().getLatitude();
                longitude = view.getLocation().getLongitude();
            }
            try {
                address = Utils.getCurrentAddress(getContext(), latitude, longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }

            User user = new User(personalInfo.getId(), personalInfo.getAvatar(), status,
                    personalInfo.getName(), latitude, longitude,
                    address == null ? "" : address.getCountryCode(),
                    address == null ? "" : address.getAdminArea(), 0, personalInfo.getGender(),
                    personalInfo.getDob(), personalInfo.getPhone());
            user.setTranslator(personalInfo.isTranslator());

            FireBaseDataUtils.getInstance().addNewUser(getContext(), user);
            LocalSharedPreferences.getInstance(getContext()).saveCurrentFirebaseUser(user);
            view.initWall();
        }
    }

    /**
     * update Firebase User if Data changed
     *
     * @param user
     */
    private void updateFireUser(User user) {
        User savedUser = LocalSharedPreferences.getInstance(getContext()).getCurrentFirebaseUser();
        if (savedUser == null) {
            LocalSharedPreferences.getInstance(getContext()).saveCurrentFirebaseUser(user);
        }

        checkGenderChanged(personalInfo, user);
        checkDobChanged(personalInfo, user);
        checkAddressChanged(personalInfo, user);
        checkAvatarChanged(personalInfo, user);
        checkRoleChanged(personalInfo, user);
    }

    private void checkRoleChanged(PersonalInfo personalInfo, User user) {
        if (personalInfo.isTranslator() && !user.isTranslator()) {
            FireBaseDataUtils.getInstance().updateUserTranslator(getContext());
        }
    }

    private void checkAvatarChanged(PersonalInfo personalInfo, User user) {
        if (!TextUtils.isEmpty(personalInfo.getAvatar()) || !TextUtils.isEmpty(user.getAvatar())) {
            if (user.getAvatar() == null || !user.getAvatar().equals(personalInfo.getAvatar())) {
                FireBaseDataUtils.getInstance().updateUserAvatar(getContext(),
                        personalInfo.getAvatar());
            }
        }
    }

    private void checkGenderChanged(PersonalInfo personalInfo, User user) {
        if (personalInfo.getGender() != user.getGender()) {
            FireBaseDataUtils.getInstance().updateUserGender(getContext(), personalInfo.getGender());
        }
    }

    private void checkDobChanged(PersonalInfo personalInfo, User user) {
        if (!TextUtils.isEmpty(user.getDob()) || !TextUtils.isEmpty(personalInfo.getDob())) {
            if (user.getDob() == null || personalInfo.getDob() == null ||
                    !user.getDob().equals(personalInfo.getDob())) {
                FireBaseDataUtils.getInstance().updateUserDob(getContext(), personalInfo.getDob());
            }
        }
    }

    private void checkAddressChanged(PersonalInfo personalInfo, User user) {
        if (personalInfo.getAddressType() == Constants.ADDRESS_TYPE_FILTER) {
            if (!personalInfo.getCountry().equals(user.getRegisterCountry())) {
                FireBaseDataUtils.getInstance().updateUserAddress(getContext(), personalInfo.getCountry(),
                        personalInfo.getCity() == null ? "" : personalInfo.getCity());
            }
        } else if (personalInfo.getAddressType() == Constants.ADDRESS_TYPE_MAP
                && personalInfo.getLatitude() != null) {
            try {
                Address address = Utils.getCurrentAddress(getContext(), Double.valueOf(personalInfo.getLatitude()),
                        Double.valueOf(personalInfo.getLongitude()));
                if (!address.getCountryName().equals(user.getRegisterCountry()) ||
                        !address.getAdminArea().equals(user.getRegisterCity())) {
                    FireBaseDataUtils.getInstance().updateUserAddress(getContext(), address.getCountryName(),
                            address.getAdminArea());
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateUserLocation(String key) {
        try {
            FireBaseDataUtils.getInstance().updateUserLocation(getContext(), view.getLocation(), key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUserStatus(String status) {
        Map<String, String> updateStatus = new HashMap<>();
        updateStatus.put("status_message", status);
        requestAPI(getAPI().updateProfileInfo(updateStatus), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                FireBaseDataUtils.getInstance().updateUserStatus(getContext(), status);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {

            }
        });
    }

    public void requestLocationPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        view.onPermissionGranted();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    public void updateLike(Post post) {
        String userKeyLikePost = LocalSharedPreferences.getInstance(getContext()).getCurrentFirebaseUser().getKey();
        boolean isLikePost = !post.getLikes().containsKey(userKeyLikePost);
        new PostManager().updateLike(post, userKeyLikePost, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                if (!userKeyLikePost.equals(post.getAuthor().getKey())) {
                    if (isLikePost) {
                        sendNotify(post.getAuthor().getId(), post.getId());
                    }
                }
            }
        });
    }

    private void sendNotify(int receiverId, String postId) {
        Integer[] receiverIds = new Integer[1];
        receiverIds[0] = receiverId;

        Map<String, Object> params = new HashMap<>();
        params.put("receiver_ids", receiverIds);
        params.put("type", Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_LIKE));
        params.put("model_id", postId);
        requestAPI(getAPI().sendNotification(params), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                Log.d(TAG, "Send notification success to: " + receiverId);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {

            }
        });
    }

    public void deletePost(Post post) {
        view.showProgress();
        new PostManager().deletePost(post, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                view.notify(getContext().getString(R.string.TB_1053));
            } else {
            }
            view.hideProgress();
        });
    }

    public interface TimeLineView extends BaseView {
        void getUserInfo(PersonalInfo personalInfo);

        void onAccountLocked();

        void initWall();

        Location getLocation();

        void onPermissionGranted();
    }
}
