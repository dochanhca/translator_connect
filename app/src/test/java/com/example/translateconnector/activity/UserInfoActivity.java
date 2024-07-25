package com.example.translateconnector.activity;


import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.imoktranslator.R;
import com.imoktranslator.adapter.ListCertificateAdapter;
import com.imoktranslator.adapter.NewsFeedAdapter;
import com.imoktranslator.adapter.SuggestionContactAdapter;
import com.imoktranslator.bottomsheet.CustomBottomSheetFragment;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SuggestionView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.SearchFriend;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.presenter.UserInfoPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseActivity implements NewsFeedAdapter.OnPostActionListener,
        UserInfoPresenter.View {

    @BindView(R.id.img_user_avatar)
    CircleImageView imgUserAvatar;
    @BindView(R.id.txt_user_name)
    OpenSansBoldTextView txtUserName;
    @BindView(R.id.txt_user_role)
    OpenSansTextView txtUserRole;
    @BindView(R.id.img_indicator)
    ImageView imgIndicator;
    @BindView(R.id.layout_user_info)
    ViewGroup layoutUserInfo;
    @BindView(R.id.txt_name)
    OpenSansBoldTextView txtName;
    @BindView(R.id.layout_name)
    LinearLayout layoutName;
    @BindView(R.id.txt_address)
    OpenSansBoldTextView txtAddress;
    @BindView(R.id.layout_address)
    LinearLayout layoutAddress;
    @BindView(R.id.txt_dob)
    OpenSansBoldTextView txtDob;
    @BindView(R.id.layout_dob)
    LinearLayout layoutDob;
    @BindView(R.id.layout_translator_detail)
    LinearLayout layoutTranslatorDetail;
    @BindView(R.id.txt_primary_lang)
    OpenSansBoldTextView txtPrimaryLang;
    @BindView(R.id.layout_primary_lang)
    LinearLayout layoutPrimaryLang;
    @BindView(R.id.txt_translation_lang)
    OpenSansBoldTextView txtTranslationLang;
    @BindView(R.id.layout_translation_lang)
    LinearLayout layoutTranslationLang;
    @BindView(R.id.txt_university)
    OpenSansBoldTextView txtUniversity;
    @BindView(R.id.layout_university)
    LinearLayout layoutUniversity;
    @BindView(R.id.txt_gradation_year)
    OpenSansBoldTextView txtGradationYear;
    @BindView(R.id.layout_gradation_year)
    LinearLayout layoutGradationYear;
    @BindView(R.id.txt_certificate_label)
    OpenSansBoldTextView txtCertificateLabel;
    @BindView(R.id.rcv_certificate)
    RecyclerView rcvCertificate;
    @BindView(R.id.txt_description_label)
    OpenSansBoldTextView txtDescriptionLabel;
    @BindView(R.id.txt_description)
    OpenSansTextView txtDescription;
    @BindView(R.id.rv_newsfeed)
    RecyclerView rvNewsfeed;
    @BindView(R.id.txt_add_friend)
    TextView txtAddFriend;
    @BindView(R.id.txt_sent)
    TextView txtSent;

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
    @BindView(R.id.suggestion_view)
    SuggestionView suggestionView;
    @BindView(R.id.img_qr)
    ImageView imgQR;

    private String userKey;
    private int userId;
    private SearchFriend searchFriend;
    private PersonalInfo personalInfo;

    private UserInfoPresenter presenter;
    private ListCertificateAdapter listCertificateAdapter;

    private String[] languages;
    private List<String> avatarOptions;

    private boolean isShowUserInfo;

    private NewsFeedAdapter adapter;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private boolean isBestFriend;
    private boolean isFriend;

    private SuggestionContactAdapter suggestionContactAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initViews() {
        presenter = new UserInfoPresenter(this, this);

        searchFriend = getIntent().getParcelableExtra(Constants.SEARCH_FRIEND);
        if (searchFriend != null) {
            userId = searchFriend.getId();
            userKey = searchFriend.getKey();
        } else {
            userId = getIntent().getIntExtra(Constants.USER_ID, -1);
            userKey = getIntent().getStringExtra(Constants.USER_KEY);
        }
        isFriend = getIntent().getBooleanExtra(Constants.IS_FRIEND, false);
        presenter.getUserDetail(userId, userKey, isFriend);

        languages = getResources().getStringArray(R.array.arr_language);

        initRecyclerCertificate();

        initNewsfeed();

        setEventForSuggestionView();
        loadDataForSuggestionView();
    }

    private void initNewsfeed() {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        adapter = new NewsFeedAdapter(this, new ArrayList<>(), this, userKey);
        rvNewsfeed.setAdapter(adapter);

        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.TIME_LINE)
                .child(FireBaseDataUtils.WALL)
                .child(userKey)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        if (post.getMode().equals(Constants.PUBLIC_MODE) || (isFriend && post.isVisibilityWithFriends())) {
                            adapter.add(0, post);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        if (post.isCreateByCurrentUser(userKey)) {
                            adapter.updateItem(post);
                        } else {
                            if (adapter.isContainThisPost(post.getId())) {
                                if (Constants.PRIVATE_MODE.equals(post.getMode())) {
                                    adapter.remove(post.getId());
                                } else {
                                    adapter.updateItem(post);
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);
                        adapter.remove(post.getId());
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @OnClick({R.id.img_indicator, R.id.txt_add_friend, R.id.img_user_avatar, R.id.img_qr})
    public void onViewClicked(View view) {
        if (view == imgIndicator) {
            setUserInfoVisible();
        }
        if (view == txtAddFriend) {
            presenter.sendFriendInvitation(searchFriend);
            txtAddFriend.setVisibility(View.GONE);
            txtSent.setVisibility(View.VISIBLE);
        }
        if (view == imgUserAvatar && isFriend) {
            showAvatarOptions();
        }
        if (view == imgQR) {
            QRCodeActivity.startActivity(this, false);
        }
    }

    @Override
    public void onGetUserDetail(PersonalInfo personalInfo, boolean isFriendRequestSent,
                                boolean isBestFriend, SearchFriend searchFriend) {
        this.isBestFriend = isBestFriend;
        this.personalInfo = personalInfo;
        avatarOptions = Arrays.asList(getResources().getStringArray(isBestFriend ? R.array.arr_best_friend_options
                : R.array.arr_friend_options));
        if (!isFriend) {
            txtAddFriend.setVisibility(isFriendRequestSent ? View.GONE : View.VISIBLE);
            txtSent.setVisibility(isFriendRequestSent ? View.VISIBLE : View.GONE);
            if (searchFriend != null) {
                this.searchFriend = searchFriend;
            }
        }

        fillDataToViews(personalInfo);
    }

    @Override
    public void onSetBestFriend(boolean isBestFriend) {
        this.isBestFriend = isBestFriend;
        avatarOptions = Arrays.asList(getResources().getStringArray(isBestFriend ? R.array.arr_best_friend_options
                : R.array.arr_friend_options));
        Toast.makeText(this, getString(isBestFriend ? R.string.TB_1073 : R.string.TB_1074),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteFriend() {
        isFriend = false;
        Toast.makeText(this, getString(R.string.TB_1075), Toast.LENGTH_SHORT).show();
    }

    private void setUserInfoVisible() {
        layoutUserInfo.setVisibility(isShowUserInfo ? View.GONE : View.VISIBLE);
        imgIndicator.setImageResource(isShowUserInfo ? R.drawable.ic_indicator_grey
                : R.drawable.ic_indicator_top);
        rvNewsfeed.setVisibility(isShowUserInfo ? View.VISIBLE : View.GONE);
        isShowUserInfo = !isShowUserInfo;
    }

    private void fillDataToViews(PersonalInfo personalInfo) {
        Glide.with(this)
                .load(personalInfo.getAvatar())
                .error(R.drawable.img_avatar_default)
                .into(imgUserAvatar);

        txtUserName.setText(personalInfo.getName());
        txtName.setText(personalInfo.getName());
        txtAddress.setText(getAddress(personalInfo));
        txtDob.setText(personalInfo.getDob());

        layoutTranslatorDetail.setVisibility(personalInfo.isTranslator() ? View.VISIBLE : View.GONE);
        txtUserRole.setVisibility(personalInfo.isTranslator() ? View.VISIBLE : View.GONE);
        if (personalInfo.isTranslator()) {
            fillDataForTranslator(personalInfo);
        }
        if (isFriend) {
            txtOrderStatistic.setVisibility(View.VISIBLE);
            fillOrderStatistic();
        }
    }

    private void fillOrderStatistic() {
        if (personalInfo.isTranslator()) {
            layoutTranslatorStatistic.setVisibility(View.VISIBLE);
            layoutUserStatistic.setVisibility(View.GONE);
            txtUpdatedPriceOrder.setText(getString(R.string.MH13_002) + ": " + personalInfo.getOrderUpdatedPrice());
            txtSuccessOrder.setText(getString(R.string.MH13_003) + ": " + personalInfo.getOrderSuccess());
        } else {
            layoutTranslatorStatistic.setVisibility(View.GONE);
            layoutUserStatistic.setVisibility(View.VISIBLE);
            txtOrderedNumber.setText(personalInfo.getOrderCreated() + " " + getString(R.string.MH08_010));
            txtCanceledNumber.setText(personalInfo.getOrderCanceled() + " " + getString(R.string.MH08_011));
        }
    }

    private void fillDataForTranslator(PersonalInfo personalInfo) {
        if (personalInfo.getCertificateName() != null) {
            listCertificateAdapter.setData(personalInfo.getCertificateName());
        }
        txtPrimaryLang.setText(languages[personalInfo.getForeignLanguages() - 1]);
        StringBuilder transLanguage = new StringBuilder();
        for (String lang : personalInfo.getTranslateLanguages()) {
            transLanguage.append(languages[Integer.parseInt(lang) - 1]).append(", ");
        }
        txtTranslationLang.setText(transLanguage.toString());
        txtUniversity.setText(personalInfo.getUniversity());
        txtGradationYear.setText(personalInfo.getYearOfGraduation());
        txtDescription.setText(personalInfo.getOtherInfo());

        txtCertificateLabel.append(":");
        txtDescriptionLabel.append(":");
    }

    private String getAddress(PersonalInfo personalInfo) {
        String address;
        if (personalInfo.getAddressType() == Constants.ADDRESS_TYPE_FILTER) {
            address = TextUtils.isEmpty(personalInfo.getCity()) ? personalInfo.getCountry() :
                    personalInfo.getCity() + ", " + personalInfo.getCountry();
        } else {
            address = personalInfo.getAddress();
        }
        return address;
    }

    private void initRecyclerCertificate() {
        listCertificateAdapter = new ListCertificateAdapter(new ArrayList<>(), getApplicationContext());
        rcvCertificate.setAdapter(listCertificateAdapter);
    }

    private void showAvatarOptions() {
        CustomBottomSheetFragment bottomSheetFragment = CustomBottomSheetFragment.
                newInstance("");

        bottomSheetFragment.setOptions(avatarOptions);
        bottomSheetFragment.setSelectedPosition(-1);
        bottomSheetFragment.setListener(position -> {
            switch (position) {
                case 0:
                    viewAvatarDetail();
                    break;
                case 1:
                    presenter.setBestFriend(userKey, !isBestFriend);
                    break;
                case 2:
                    confirmDeleteFriend();
                    break;
                default:
                    break;

            }
        });
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void confirmDeleteFriend() {
        showNotifyDialog(String.format(getString(R.string.TB_1066),
                personalInfo.getName()), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                presenter.deleteFriend(userKey);
            }
        });
    }

    private void viewAvatarDetail() {
        List<FileModel> fileModels = new ArrayList<>();
        fileModels.add(new FileModel(personalInfo.getAvatar()));
        ImageDetailActivity.showActivity(UserInfoActivity.this, fileModels, 0);
    }

    private void loadDataForSuggestionView() {
        List<String> recentSearches = LocalSharedPreferences.getInstance(this).getSearchUserSuggestions();
        suggestionContactAdapter = new SuggestionContactAdapter(this, R.layout.search_contact_item, recentSearches);
        suggestionView.setAdapter(suggestionContactAdapter);
    }

    private void setEventForSuggestionView() {
        suggestionView.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!getTextSearch().isEmpty()) {
                    LocalSharedPreferences.getInstance(this).saveSearchUserSuggestionsToLocal(getTextSearch());
                    SearchUserResultActivity.startActivity(this, getTextSearch());
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
    public void onLikeClicked(Post post) {
        presenter.updateLike(post);
    }

    @Override
    public void onEditPost(Post post) {
        EditPostActivity.startActivity(this, post);
    }

    @Override
    public void onShareFbPost(Post post) {
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
        // Just implement for Post's owner
    }

    @Override
    public void onViewAllComments(Post post) {
        PostDetailActivity.startActivity(this, post);
    }

    @Override
    public void onViewPostDetail(Post post) {
        PostDetailActivity.startActivity(this, post);
    }

    @Override
    public void onEditMode(Post post) {
        // Just implement for Post's owner
    }

    @Override
    public void onViewImage(Post post, int selectedPos) {
        ImageDetailActivity.showActivity(this, post.getFileModels(), selectedPos);
    }

    @Override
    public void onAvatarClick(Post post) {
        //
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && view instanceof SuggestionView) {
                Rect r = new Rect();
                view.getGlobalVisibleRect(r);
                int rawX = (int) ev.getRawX();
                int rawY = (int) ev.getRawY();
                if (!r.contains(rawX, rawY)) {
                    view.clearFocus();
                    hideKeyboard();
                }
            }
        }
        return super.dispatchTouchEvent(ev);

    }

    /**
     * @param activity start from Friend requests activity
     * @param userKey
     * @param userId
     */
    public static void startActivity(BaseActivity activity, String userKey, int userId, boolean isFriend) {
        Intent intent = new Intent(activity, UserInfoActivity.class);
        intent.putExtra(Constants.USER_KEY, userKey);
        intent.putExtra(Constants.USER_ID, userId);
        intent.putExtra(Constants.IS_FRIEND, isFriend);
        activity.startActivity(intent);
    }

    /**
     * @param activity     start from Add friend activity
     * @param searchFriend
     */
    public static void startActivity(BaseActivity activity, @NonNull SearchFriend searchFriend) {
        Intent intent = new Intent(activity, UserInfoActivity.class);
        intent.putExtra(Constants.SEARCH_FRIEND, searchFriend);
        activity.startActivity(intent);
    }
}
