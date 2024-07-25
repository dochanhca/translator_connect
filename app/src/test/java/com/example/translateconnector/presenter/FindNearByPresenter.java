package com.example.translateconnector.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.model.NearByFriend;
import com.imoktranslator.model.NearBySettingItem;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.SearchFriend;
import com.imoktranslator.model.firebase.Invitation;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.FriendManager;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.NotificationHelper;
import com.imoktranslator.utils.Utils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FindNearByPresenter extends BasePresenter {

    private FindNearByView view;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private boolean mRequestingLocationUpdates;
    private Location mLocation;
    private String userKey;
    private PersonalInfo personalInfo;
    private User currentUser;

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            mLocation = locationResult.getLastLocation();
            updateUserLocation();
        }
    };

    public FindNearByPresenter(Context context, FindNearByView view) {
        super(context);
        this.view = view;
        currentUser = LocalSharedPreferences.getInstance(getContext()).getCurrentFirebaseUser();
        userKey = LocalSharedPreferences.getInstance(context).getKeyUser();
        personalInfo = LocalSharedPreferences.getInstance(context).getPersonalInfo();
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
                        checkLocationSetting(activity);

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    public void startLocationUpdates(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (!mRequestingLocationUpdates) {
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback,
                    null /* Looper */);
            mRequestingLocationUpdates = true;
        }
    }


    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mRequestingLocationUpdates = false;
    }

    private void checkLocationSetting(Activity activity) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        // Check the location settings of the user and create the callback to react to the different possibilities
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(60 * 1000);
        locationRequest.setMaxWaitTime(10 * 60 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(getContext()).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
                startLocationUpdates(activity);
                findNearByFriends();

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        view.requestLocation(exception);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    public void findNearByFriends() {
        DialogUtils.showProgress(getContext());

        NearBySettingItem settingItem = LocalSharedPreferences.getInstance(getContext()).getNearBySetting();

        if (TextUtils.isEmpty(currentUser.getCountryCode())) {
            updateUserLocation();
            currentUser.setCountryCode("VN");
        }
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                .orderByChild("city").equalTo(currentUser.getCity());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> nearByFriends = new ArrayList<>();
                if (dataSnapshot.getValue() == null) {
                    view.onGetNearByFriends(new ArrayList<>());
                    return;
                }
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    User user = dsp.getValue(User.class);
                    if (user.getCountryCode().equals(currentUser.getCountryCode()) && !user.getKey().equals(userKey)
                            && user.isTranslator() == personalInfo.isTranslator()) {
                        if (settingItem == null || (isGenderEqual(user.getGender(), settingItem)
                                && isAgeValid(user, settingItem))) {
                            nearByFriends.add(user);
                        }
                    }
                }
                removeAddedFriends(nearByFriends);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onGetNearByFriendsError();
            }
        });
    }

    private boolean isGenderEqual(int gender, NearBySettingItem settingItem) {
        return settingItem.getGender() == Constants.ANY || gender == settingItem.getGender();

    }

    private boolean isAgeValid(User user, NearBySettingItem item) {
        return TextUtils.isEmpty(user.getDob()) ||
                (Integer.valueOf(Utils.getAgeFromDob(user.getDob())) >= item.getFromAge()
                        && Integer.valueOf(Utils.getAgeFromDob(user.getDob())) <= item.getToAge());
    }

    private void removeAddedFriends(List<User> nearByFriends) {
        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                .child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //remove added friend
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    for (Iterator<User> iter = nearByFriends.listIterator(); iter.hasNext(); ) {
                        User item = iter.next();
                        if (item.getKey().equals(dsp.getKey())) {
                            iter.remove();
                            break;
                        }
                    }
                }

                if (nearByFriends.size() > 0) {
                    calculateMutualFriends(nearByFriends);
                } else {
                    view.onGetNearByFriends(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (nearByFriends.size() > 0) {
                    calculateMutualFriends(nearByFriends);
                } else {
                    view.onGetNearByFriendsError();
                }
            }
        });
    }

    private void calculateMutualFriends(List<User> users) {
        FriendManager.getInstance(getContext()).countMutualFriends(users,
                searchFriends -> {
                    List<NearByFriend> nearByFriends = getNearByFriends(searchFriends);
                    Collections.sort(nearByFriends);
                    view.onGetNearByFriends(nearByFriends);
                });
    }

    private List<NearByFriend> getNearByFriends(List<SearchFriend> searchFriends) {
        List<NearByFriend> nearByFriends = new ArrayList<>();
        for (SearchFriend searchFriend : searchFriends) {
            Location currentLocation = getLocationFromLatLng(currentUser.getLatitude(),
                    currentUser.getLongitude());
            Location destination = getLocationFromLatLng(searchFriend.getLatitude(),
                    searchFriend.getLongitude());

            float distance = currentLocation.distanceTo(destination);
            NearByFriend nearByFriend = new NearByFriend(searchFriend.getId(),
                    searchFriend.getAvatar(), searchFriend.getStatus(), searchFriend.getName(),
                    searchFriend.getLatitude(), searchFriend.getLongitude(), searchFriend.getCountryCode(),
                    searchFriend.getCity(), searchFriend.getStar(), searchFriend.getGender(),
                    searchFriend.getDob(), searchFriend.getPhone(), searchFriend.getMutualFriend(), distance);
            nearByFriend.setKey(searchFriend.getKey());
            nearByFriends.add(nearByFriend);
        }
        return nearByFriends;
    }

    private Location getLocationFromLatLng(double lat, double lng) {
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        return location;
    }

    public void updateUserLocation() {
        try {
            FireBaseDataUtils.getInstance().updateUserLocation(getContext(), mLocation == null
                            ? getLocationFromLatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG) : mLocation
                    , userKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFriendInvitation(NearByFriend item) {
        Invitation invitation = new Invitation(userKey
                , personalInfo.getId(), item.getMutualFriend());
        FireBaseDataUtils.getInstance().sendInvitation(invitation, item.getKey(), (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        FireBaseDataUtils.getInstance().getFirebaseReference().
                                child(FireBaseDataUtils.USERS_COLLECTION).orderByChild("key").equalTo(item.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                                        sendNotify(user.getId());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                }
        );
    }

    private void sendNotify(int receiverId) {
        Integer[] receiverIds = new Integer[1];
        receiverIds[0] = receiverId;

        Map<String, Object> params = new HashMap<>();
        params.put("receiver_ids", receiverIds);
        params.put("type", Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_ADD_FRIEND));
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

    public interface FindNearByView extends BaseView {
        void requestLocation(ApiException exception);

        void onGetNearByFriends(List<NearByFriend> nearByFriend);

        void onGetNearByFriendsError();
    }
}
