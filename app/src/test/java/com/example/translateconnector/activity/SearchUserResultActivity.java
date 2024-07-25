package com.example.translateconnector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.imoktranslator.R;
import com.imoktranslator.adapter.FriendResultAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.model.SearchFriend;
import com.imoktranslator.presenter.SearchUserResultPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchUserResultActivity extends BaseActivity implements
        SearchUserResultPresenter.SearchUserResultView, HeaderView.BackButtonClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TEXT_SEARCH = "text_search";
    @BindView(R.id.header)
    HeaderView headerView;
    @BindView(R.id.list_user_result)
    RecyclerView listUserResult;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private SearchUserResultPresenter presenter;
    private FriendResultAdapter friendResultAdapter;
    private List<String> friendKeys;
    private String textSearch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_user_result;
    }

    @Override
    protected void initViews() {
        headerView.setCallback(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        presenter = new SearchUserResultPresenter(this, this);
        initListUserResult();
    }

    private void initListUserResult() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listUserResult.setLayoutManager(linearLayoutManager);
        friendResultAdapter = new FriendResultAdapter(new ArrayList<>(), SearchUserResultActivity.this);
        friendResultAdapter.setOnClickListener(new FriendResultAdapter.OnClickListener() {
            @Override
            public void onAddFriendClick(SearchFriend searchFriend) {
                presenter.sendFriendInvitation(searchFriend);
            }

            @Override
            public void onOpenInfo(SearchFriend searchFriend) {
                Intent intent = new Intent(SearchUserResultActivity.this, UserInfoActivity.class);
                intent.putExtra(Constants.SEARCH_FRIEND, searchFriend);
                if (friendKeys != null && !friendKeys.isEmpty() && friendKeys.contains(searchFriend.getKey())) {
                    intent.putExtra(Constants.IS_FRIEND, true);
                }
                startActivity(intent);
            }
        });
        listUserResult.setAdapter(friendResultAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        textSearch = intent.getStringExtra(TEXT_SEARCH);
        doSearchUserByName();
    }

    private void doSearchUserByName() {
        presenter.searchMyFriendKeys();
    }

    public static void startActivity(BaseActivity activity, String text) {
        Intent intent = new Intent(activity, SearchUserResultActivity.class);
        intent.putExtra(TEXT_SEARCH, text);
        activity.startActivity(intent);
    }

    @Override
    public void onSearchUsersFail() {
        DialogUtils.hideProgress();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSearchFriends(List<SearchFriend> searchFriends) {
        DialogUtils.hideProgress();
        swipeRefreshLayout.setRefreshing(false);
        friendResultAdapter.setListFriend(searchFriends, friendKeys);
    }

    @Override
    public void searchMyFriendKeysDone(List<String> friendKeys) {
        this.friendKeys = friendKeys;
        presenter.searchUserByName(textSearch);
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        doSearchUserByName();
    }
}
