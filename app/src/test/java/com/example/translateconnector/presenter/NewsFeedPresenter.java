package com.example.translateconnector.presenter;

import android.content.Context;

public class NewsFeedPresenter extends TimeLinePresenter {

    private NewsFeedView view;

    public NewsFeedPresenter(Context context, NewsFeedView view) {
        super(context, view);
        this.view = view;
    }

    public interface NewsFeedView extends TimeLineView {

    }
}
