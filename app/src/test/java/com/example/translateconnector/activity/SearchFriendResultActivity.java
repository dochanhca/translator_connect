package com.example.translateconnector.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.imoktranslator.R;
import com.imoktranslator.adapter.FriendResultAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.model.SearchFriend;
import com.imoktranslator.presenter.SearchFriendResultPresenter;
import com.imoktranslator.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class SearchFriendResultActivity extends BaseActivity implements
        HeaderView.BackButtonClickListener, SwipeRefreshLayout.OnRefreshListener, SearchFriendResultPresenter.View {

    private static final String PHONE_NUMBER = "phone_number";
    private static final String COUNTRY = "country";
    private static final String CITY = "city";

    @BindView(R.id.list_friend_result)
    RecyclerView listFriendResult;
    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private SearchFriendResultPresenter presenter;
    private FriendResultAdapter friendResultAdapter;

    private String phoneNumber;
    private String country;
    private String city;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_friends_result;
    }

    @Override
    protected void initViews() {
        phoneNumber = getIntent().getStringExtra(PHONE_NUMBER);
        country = getIntent().getStringExtra(COUNTRY);
        city = getIntent().getStringExtra(CITY);

        presenter = new SearchFriendResultPresenter(this, this);

        header.setCallback(this);
        initListFriendResult();

        swipeRefreshLayout.setOnRefreshListener(this);

        searchFriend();
    }

    private void searchFriend() {
        DialogUtils.showProgress(this);
        if (TextUtils.isEmpty(phoneNumber)) {
            presenter.getFriendByCountryAndCity(country, city);
        } else {
            presenter.getFriendByPhoneNumber(phoneNumber);
        }
    }

    private void initListFriendResult() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listFriendResult.setLayoutManager(linearLayoutManager);
        friendResultAdapter = new FriendResultAdapter(new ArrayList<>(), SearchFriendResultActivity.this);
        friendResultAdapter.setOnClickListener(new FriendResultAdapter.OnClickListener() {
            @Override
            public void onAddFriendClick(SearchFriend searchFriend) {
                presenter.sendFriendInvitation(searchFriend);
            }

            @Override
            public void onOpenInfo(SearchFriend searchFriend) {
                UserInfoActivity.startActivity(SearchFriendResultActivity.this, searchFriend);
            }
        });
        listFriendResult.setAdapter(friendResultAdapter);
    }

    @Override
    public void backButtonClicked() {
        this.finish();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        searchFriend();
    }

    @Override
    public void onSearchFriends(List<SearchFriend> users) {
        DialogUtils.hideProgress();
        friendResultAdapter.setListFriend(users);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSearchFriendsFail() {
        DialogUtils.hideProgress();
        swipeRefreshLayout.setRefreshing(false);
    }

    public static void startActivity(BaseActivity activity, String phoneNumber) {
        Intent intent = new Intent(activity, SearchFriendResultActivity.class);
        intent.putExtra(PHONE_NUMBER, phoneNumber);
        activity.startActivity(intent);
    }

    public static void startActivity(BaseActivity activity, String country, String city) {
        Intent intent = new Intent(activity, SearchFriendResultActivity.class);
        intent.putExtra(COUNTRY, country);
        intent.putExtra(CITY, city);
        activity.startActivity(intent);
    }
}
