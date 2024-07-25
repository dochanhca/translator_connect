package com.example.translateconnector.presenter;

import android.content.Context;

public class HomePresenter extends TimeLinePresenter {
    private HomeView view;

    public HomePresenter(Context context, HomeView view) {
        super(context, view);
        this.view = view;
    }

    public interface HomeView extends TimeLineView {

    }
}
