package com.example.translateconnector.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.imoktranslator.R;
import com.imoktranslator.adapter.ContactAdapter;
import com.imoktranslator.adapter.SuggestionContactAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.SuggestionView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.UserFriend;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.presenter.ContactPresenter;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ContactActivity extends BaseActivity implements HeaderView.BackButtonClickListener,
        ContactAdapter.OnContactClickListener, ContactPresenter.ContactView {

    @BindView(R.id.list_contact)
    RecyclerView listContact;
    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.suggestion_view)
    SuggestionView suggestionView;

    private SuggestionContactAdapter suggestionContactAdapter;
    private List<String> recentSearches;
    private ContactAdapter contactAdapter;

    private ContactPresenter presenter;
    private UserFriend friend;

    private SwipeRefreshLayout.OnRefreshListener onRefreshData = () -> {
        swipeRefreshLayout.setRefreshing(true);
        presenter.getFriends();
    };
    private boolean isResultFromSearchAction;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact;
    }

    @Override
    protected void initViews() {
        header.setCallback(this);
        presenter = new ContactPresenter(this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listContact.setLayoutManager(linearLayoutManager);
        contactAdapter = new ContactAdapter(new ArrayList<>(), ContactActivity.this, this);
        listContact.setAdapter(contactAdapter);
        presenter.getFriends();
        swipeRefreshLayout.setOnRefreshListener(onRefreshData);

        setEventForSuggestionView();
        loadDataForSuggestionView();
    }

    private void loadDataForSuggestionView() {
        recentSearches = getRecentSearches();
        suggestionContactAdapter = new SuggestionContactAdapter(this, R.layout.search_contact_item, recentSearches);
        suggestionView.setAdapter(suggestionContactAdapter);
    }

    private List<String> getRecentSearches() {
        List<User> recentSearchContacts = LocalSharedPreferences.getInstance(this).getSearchContacts();
        List<String> recentSearches = new ArrayList<>();
        for (User user : recentSearchContacts) {
            recentSearches.add(user.getName());
        }
        return recentSearches;
    }

    private String getTextSearch() {
        return suggestionView.getText().toString().trim();
    }

    private void setEventForSuggestionView() {
        suggestionView.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Your piece of code on keyboard search click
                presenter.filterFriend(getTextSearch());
                listContact.requestFocus();
                hideKeyboard();
                return true;
            }
            return false;
        });

        suggestionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.filterFriend(getTextSearch());
            }
        });
    }

    @Override
    public void backButtonClicked() {
        this.finish();
    }

    public static void startActivity(BaseActivity activity) {
        Intent intent = new Intent(activity, ContactActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void openUserProfile(UserFriend friend) {
        if (isResultFromSearchAction) {
            LocalSharedPreferences.getInstance(this).saveToSearchContacts(friend.getUser());
            loadDataForSuggestionView();
        }
        UserInfoActivity.startActivity(this, friend.getUser().getKey(),
                friend.getUser().getId(), true);
    }

    @Override
    public void deleteFriend(UserFriend friend) {
        this.friend = friend;

        showNotifyDialog(String.format(getString(R.string.TB_1066),
                friend.getUser().getName()), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                presenter.deleteFriend(friend);
            }
        });
    }

    @Override
    public void openChat(String fireBaseFriendID, String friendName) {
        presenter.findRoomKey(fireBaseFriendID, friendName);
    }

    @Override
    public void deleteFriendSuccessful() {
        contactAdapter.removeItem(friend);
    }

    @Override
    public void fireBaseError() {
        swipeRefreshLayout.setRefreshing(false);
        showNotifyDialog(getString(R.string.TB_1053));
    }

    @Override
    public void getUserRoomSuccess(UserRoom userRoom, String friendName) {
        ChatRoomActivity.startActivity(ContactActivity.this, userRoom, friendName);
    }

    @Override
    public void getDataSuccessful(List<UserFriend> userList) {
        swipeRefreshLayout.setRefreshing(false);
        contactAdapter.setListContact(userList);
    }

    @Override
    public void filterResult(List<UserFriend> result, String query) {
        contactAdapter.setListContact(result);
        this.isResultFromSearchAction = !TextUtils.isEmpty(query);
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
}
