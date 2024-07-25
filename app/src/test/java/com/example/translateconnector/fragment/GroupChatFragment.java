package com.example.translateconnector.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.imoktranslator.R;
import com.imoktranslator.activity.ChatRoomActivity;
import com.imoktranslator.adapter.ConversationAdapter;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansEditText;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.presenter.GroupMessagePresenter;
import com.imoktranslator.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class GroupChatFragment extends BaseFragment implements GroupMessagePresenter.GroupMessageView,
        ConversationAdapter.OnMessageAdapterClickListener {

    @BindView(R.id.edt_search)
    OpenSansEditText edtSearch;
    @BindView(R.id.txt_message_label)
    OpenSansBoldTextView txtMessageLabel;
    @BindView(R.id.list_message)
    RecyclerView listMessage;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    private GroupMessagePresenter presenter;
    private ConversationAdapter adapter;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            adapter.filterChat(s.toString());
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshData = () -> {
        swipeLayout.setRefreshing(true);
        presenter.getUserRoomsFromFireBase();
    };

    public static GroupChatFragment newInstance() {

        Bundle args = new Bundle();

        GroupChatFragment fragment = new GroupChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_chat;
    }

    @Override
    protected void initViews() {
        presenter = new GroupMessagePresenter(getActivity(), this);
        txtMessageLabel.append(":");

        adapter = new ConversationAdapter(new ArrayList<>(), getActivity());
        adapter.setOnMessageAdapterClickListener(this);
        listMessage.setAdapter(adapter);

        edtSearch.addTextChangedListener(textWatcher);
        swipeLayout.setOnRefreshListener(onRefreshData);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.getUserRoomsFromFireBase();
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }

    @Override
    public void onGetListRoom(List<UserRoom> userRooms) {
        adapter.setListRoom(userRooms);
        try {
            swipeLayout.setRefreshing(false);
        } catch (NullPointerException ex) {
            //null when user room data changed from other screen
        }
        DialogUtils.hideProgress();
    }

    @Override
    public void onGetLisRoomError() {
        DialogUtils.hideProgress();
        try {
            swipeLayout.setRefreshing(false);
        } catch (NullPointerException ex) {
            //null when user room data changed from other screen
        }
    }

    @Override
    public void onDeleteChatSuccess(UserRoom userRoom) {
        adapter.getData().remove(userRoom);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMuteNotificationSuccess() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMuteNotificationClick(UserRoom userRoom) {
        presenter.muteNotification(userRoom);
    }

    @Override
    public void onDeleteChatClick(UserRoom userRoom) {
        showNotifyDialog(getString(R.string.TB_1070), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                presenter.deleteUserRoom(userRoom);
            }
        });
    }

    @Override
    public void onChatClick(UserRoom userRoom) {
        ChatRoomActivity.startActivity(getBaseActivity(), userRoom);
    }
}
