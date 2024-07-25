package com.example.translateconnector.fragment;

import android.os.Bundle;

import com.imoktranslator.R;
import com.imoktranslator.presenter.NewsFeedPresenter;
import com.imoktranslator.presenter.TimeLinePresenter;
import com.imoktranslator.utils.FireBaseDataUtils;

public class NewsFeedFragment extends TimeLineFragment implements NewsFeedPresenter.NewsFeedView {

    private NewsFeedPresenter presenter;

    public static NewsFeedFragment newInstance() {

        Bundle args = new Bundle();

        NewsFeedFragment fragment = new NewsFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected TimeLinePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected String timelineMode() {
        return FireBaseDataUtils.NEWS_FEED;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initViews() {
        super.initViews();
        presenter = new NewsFeedPresenter(getActivity(), this);
    }
}
