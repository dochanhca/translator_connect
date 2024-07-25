package com.example.translateconnector.fragment;

import android.os.Bundle;

import com.imoktranslator.R;
import com.imoktranslator.adapter.NewsFeedAdapter;
import com.imoktranslator.presenter.HomePresenter;
import com.imoktranslator.presenter.TimeLinePresenter;
import com.imoktranslator.utils.FireBaseDataUtils;

public class HomeFragment extends TimeLineFragment implements HomePresenter.HomeView,
        NewsFeedAdapter.OnPostActionListener {

    private HomePresenter homePresenter;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected TimeLinePresenter getPresenter() {
        return homePresenter;
    }

    @Override
    protected String timelineMode() {
        return FireBaseDataUtils.WALL;
    }

    @Override
    protected void initViews() {
        super.initViews();
        homePresenter = new HomePresenter(getActivity(), this);
    }
}
