package com.example.translateconnector.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.imoktranslator.R;
import com.imoktranslator.activity.ImageDetailActivity;
import com.imoktranslator.activity.MainActivity;
import com.imoktranslator.activity.OrderManagementActivity;
import com.imoktranslator.activity.PostDetailActivity;
import com.imoktranslator.activity.QRCodeActivity;
import com.imoktranslator.activity.RatingDetailActivity;
import com.imoktranslator.activity.SearchUserResultActivity;
import com.imoktranslator.activity.UserInfoActivity;
import com.imoktranslator.adapter.NewsFeedAdapter;
import com.imoktranslator.adapter.SuggestionContactAdapter;
import com.imoktranslator.bottomsheet.CustomBottomSheetFragment;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansEditText;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SuggestionView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.dialog.UpdateStatusDialog;
import com.imoktranslator.firebase.PostManager;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.presenter.TimeLinePresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public abstract class TimeLineFragment extends BaseFragment implements TimeLinePresenter.TimeLineView,
        NewsFeedAdapter.OnPostActionListener {

    @BindView(R.id.img_home_avatar)
    CircleImageView imgHomeAvatar;
    @BindView(R.id.txt_home_name)
    OpenSansBoldTextView txtHomeName;
    @BindView(R.id.txt_user_role)
    OpenSansTextView txtUserRole;
    @BindView(R.id.txt_order_statistic)
    OpenSansBoldTextView txtOrderStatistic;
    @BindView(R.id.txt_ordered_number)
    OpenSansSemiBoldTextView txtOrderedNumber;
    @BindView(R.id.txt_canceled_number)
    OpenSansSemiBoldTextView txtCanceledNumber;
    @BindView(R.id.layout_user_statistic)
    LinearLayout layoutUserStatistic;
    @BindView(R.id.txt_success_order)
    OpenSansSemiBoldTextView txtSuccessOrder;
    @BindView(R.id.txt_updated_price_order)
    OpenSansSemiBoldTextView txtUpdatedPriceOrder;
    @BindView(R.id.layout_translator_statistic)
    LinearLayout layoutTranslatorStatistic;
    @BindView(R.id.txt_quality_label)
    OpenSansBoldTextView txtQualityLabel;
    @BindView(R.id.rating_bar)
    AppCompatRatingBar ratingBar;
    @BindView(R.id.layout_rating)
    LinearLayout layoutRating;
    @BindView(R.id.edt_user_status)
    OpenSansEditText edtUserStatus;
    @BindView(R.id.img_indicator)
    ImageView imgIndicator;
    @BindView(R.id.rcv_timeline)
    RecyclerView rcvTimeLine;
    @BindView(R.id.suggestion_view)
    SuggestionView suggestionView;

    private boolean isHeaderExpanded;
    private PersonalInfo personalInfo;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLocation;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private NewsFeedAdapter adapter;
    private String currentUserKey;
    private User currentUserFirebase;
    private SuggestionContactAdapter suggestionContactAdapter;

    protected abstract TimeLinePresenter getPresenter();

    protected abstract String timelineMode();

    protected void showPostStatusFragment(Post post) {
        switchFragment(PostStatusFragment.newInstance(post),
                new FragmentController.Option.Builder()
                        .useAnimation(true)
                        .addToBackStack(true)
                        .setType(FragmentController.Option.TYPE.ADD)
                        .build());
    }

    @Override
    protected void initViews() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getBaseActivity());

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        setEventForSuggestionView();
        rcvTimeLine.setNestedScrollingEnabled(false);
    }

    private void loadDataForSuggestionView() {
        List<String> recentSearches = LocalSharedPreferences.getInstance(getContext()).getSearchUserSuggestions();
        suggestionContactAdapter = new SuggestionContactAdapter(getContext(), R.layout.search_contact_item, recentSearches);
        suggestionView.setAdapter(suggestionContactAdapter);
    }

    private void setEventForSuggestionView() {
        suggestionView.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!getTextSearch().isEmpty()) {
                    LocalSharedPreferences.getInstance(getContext()).saveSearchUserSuggestionsToLocal(getTextSearch());
                    SearchUserResultActivity.startActivity(getBaseActivity(), getTextSearch());
                }
                return true;
            }
            return false;
        });
    }

    private String getTextSearch() {
        return suggestionView.getText().toString().trim();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataForSuggestionView();
        getPresenter().getUserData();
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }

    @Override
    public void initWall() {

        if (adapter != null) {
            return;
        }
        currentUserKey = LocalSharedPreferences.getInstance(getActivity()).getKeyUser();
        currentUserFirebase = LocalSharedPreferences.getInstance(getActivity()).getCurrentFirebaseUser();

        adapter = new NewsFeedAdapter(getActivity(), new ArrayList<>(), this, currentUserKey);
        rcvTimeLine.setAdapter(adapter);

        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.TIME_LINE)
                .child(timelineMode())
                .child(currentUserKey)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        try {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (post.getAuthor().getKey().equals(currentUserKey) ||
                                    !Constants.PRIVATE_MODE.equals(post.getMode())) {
                                if (currentUserFirebase.isTranslator() == post.getAuthor().isTranslator()) {
                                    adapter.add(0, post);
                                }
                                if (rcvTimeLine != null) {
                                    rcvTimeLine.scrollToPosition(0);
                                }
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        try {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (!post.getAuthor().getKey().equals(currentUserKey) &&
                                    Constants.PRIVATE_MODE.equals(post.getMode())) {
                                adapter.remove(post.getId());
                            } else {
                                if (currentUserFirebase.isTranslator() != post.getAuthor().isTranslator()) {
                                    adapter.remove(post.getId());
                                } else {
                                    adapter.updateItem(post);
                                }
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            Post post = dataSnapshot.getValue(Post.class);
                            adapter.remove(post.getId());
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void getUserInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
        updateView();
        getMainActivity().fillUserData(personalInfo);
        getPresenter().requestLocationPermission(getMainActivity());
    }

    @Override
    public void onAccountLocked() {
        MainActivity activity = getMainActivity();
        activity.logout();
    }

    @OnClick({R.id.txt_ordered_number, R.id.txt_canceled_number, R.id.txt_success_order,
            R.id.txt_updated_price_order, R.id.layout_rating, R.id.edt_user_status, R.id.img_indicator,
            R.id.layout_post_status, R.id.img_qr})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_ordered_number:
                OrderManagementActivity.startActivity(getMainActivity(), OrderManagementActivity.ORDERED);
                break;
            case R.id.txt_canceled_number:
                OrderManagementActivity.startActivity(getMainActivity(),
                        OrderManagementActivity.CANCELED);
                break;
            case R.id.txt_updated_price_order:
                OrderManagementActivity.startActivity(getMainActivity(),
                        OrderManagementActivity.UPDATED_PRICE);
                break;
            case R.id.txt_success_order:
                OrderManagementActivity.startActivity(getMainActivity(),
                        OrderManagementActivity.SUCCESSFUL);
                break;
            case R.id.layout_rating:
                RatingDetailActivity.startActivity(getMainActivity(), personalInfo.getId());
                break;
            case R.id.img_indicator:
                expandHeader();
                break;
            case R.id.edt_user_status:
                showDialogUpdateStatus();
                break;
            case R.id.layout_post_status:
                showPostStatusFragment(null);
                break;
            case R.id.img_qr:
                QRCodeActivity.startActivity(getBaseActivity(), false);
                break;

        }
    }

    private void showDialogUpdateStatus() {
        UpdateStatusDialog.showDialog(getFragmentManager(),
                status -> {
                    //update Status
                    edtUserStatus.setText(status);
                    getPresenter().updateUserStatus(status);
                });
    }

    private void expandHeader() {
        isHeaderExpanded = !isHeaderExpanded;
        imgIndicator.setImageResource(isHeaderExpanded ? R.drawable.ic_indicator_top
                : R.drawable.ic_indicator_grey);
        layoutRating.setVisibility(isHeaderExpanded ? View.VISIBLE : View.GONE);
        edtUserStatus.setVisibility(isHeaderExpanded ? View.VISIBLE : View.GONE);
    }

    private void updateView() {
        txtHomeName.setText(personalInfo.getName());
        if (personalInfo.isTranslator()) {
            txtUserRole.setVisibility(View.VISIBLE);
            layoutTranslatorStatistic.setVisibility(View.VISIBLE);
            layoutUserStatistic.setVisibility(View.GONE);
            txtUpdatedPriceOrder.setText(getString(R.string.MH13_002) + ": " + personalInfo.getOrderUpdatedPrice());
            txtSuccessOrder.setText(getString(R.string.MH13_003) + ": " + personalInfo.getOrderSuccess());
        } else {
            txtUserRole.setVisibility(View.GONE);
            layoutTranslatorStatistic.setVisibility(View.GONE);
            layoutUserStatistic.setVisibility(View.VISIBLE);
            txtOrderedNumber.setText(personalInfo.getOrderCreated() + " " + getString(R.string.MH08_010));
            txtCanceledNumber.setText(personalInfo.getOrderCanceled() + " " + getString(R.string.MH08_011));
        }
        ratingBar.setRating((float) personalInfo.getScore());
        edtUserStatus.setText(personalInfo.getStatusMessage());
        edtUserStatus.setFocusable(false);
        ratingBar.setEnabled(false);
        getMainActivity().loadUserAvatar(personalInfo.getAvatar(), imgHomeAvatar);
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getBaseActivity();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getBaseActivity(), location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        mLocation = location;
                        Log.d("FireBase", "Lat: " + location.getLatitude() + " Lon: " + location.getLongitude());
                    } else {
                        Log.d("FireBase", "can not get location");
                    }
                });
    }

    @Override
    public Location getLocation() {
        return mLocation;
    }

    @Override
    public void onPermissionGranted() {
        getCurrentLocation();
    }

    @Override
    public void onLikeClicked(Post post) {
        getPresenter().updateLike(post);
    }

    @Override
    public void onEditPost(Post post) {
        showPostStatusFragment(post);
    }

    @Override
    public void onShareFbPost(Post post) {
        if (post.getFileModels().isEmpty()) return;
        ShareLinkContent.Builder builder = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(post.getFileModels().get(0).getUrlFile()));

        if (!TextUtils.isEmpty(post.getMessage())) {
            builder.setQuote(post.getMessage());
        }

        ShareLinkContent content = builder.build();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(content);
        }
    }

    @Override
    public void onDeletePost(Post post) {
        showNotifyDialog(getString(R.string.TB_1065), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                getPresenter().deletePost(post);
            }
        });
    }

    @Override
    public void onAvatarClick(Post post) {
        if (currentUserKey.equals(post.getAuthor().getKey())) {
            return;
        }
        UserInfoActivity.startActivity(getBaseActivity(), post.getAuthor().getKey(),
                post.getAuthor().getId(), true);
    }

    @Override
    public void onViewAllComments(Post post) {
        PostDetailActivity.startActivity(getBaseActivity(), post);
    }

    @Override
    public void onViewPostDetail(Post post) {
        PostDetailActivity.startActivity(getMainActivity(), post);
    }

    @Override
    public void onEditMode(Post post) {
        if (post.isCreateByCurrentUser(currentUserKey)) {
            int selectedPosition = getSelectedPosition(post);
            CustomBottomSheetFragment bottomSheetFragment = CustomBottomSheetFragment.
                    newInstance(getString(R.string.MH45_011));
            List<String> modeOptions = new ArrayList<>();
            modeOptions.add(getString(R.string.MH45_004));
            modeOptions.add(getString(R.string.MH45_005));
            modeOptions.add(getString(R.string.MH45_010));
            bottomSheetFragment.setOptions(modeOptions);
            bottomSheetFragment.setSelectedPosition(selectedPosition);
            bottomSheetFragment.setListener(position -> {
                post.setMode(getModeBy(position));
                new PostManager().updatePost(post, (databaseError, databaseReference) ->
                        Toast.makeText(getActivity(), getString(R.string.TB_1069),
                                Toast.LENGTH_SHORT).show());
            });
            bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
        }
    }

    private String getModeBy(int position) {
        if (position == 0) {
            return Constants.PUBLIC_MODE;
        } else if (position == 1) {
            return Constants.FRIENDS_MODE;
        } else {
            return Constants.PRIVATE_MODE;
        }
    }

    private int getSelectedPosition(Post post) {
        if (post.isPrivate()) {
            return 2;
        } else if (post.isVisibilityWithFriends()) {
            return 1;
        } else return 0;
    }

    @Override
    public void onViewImage(Post post, int selectedPos) {
        ImageDetailActivity.showActivity(getMainActivity(), post.getFileModels(), selectedPos);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
