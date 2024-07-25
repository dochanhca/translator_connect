package com.example.translateconnector.fragment;

import android.os.Bundle;

import com.imoktranslator.R;

public class ChatHistoryFragment extends BaseFragment {

    public static ChatHistoryFragment newInstance() {
        Bundle args = new Bundle();

        ChatHistoryFragment fragment = new ChatHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_history;
    }

    @Override
    protected void initViews() {

    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }
}
