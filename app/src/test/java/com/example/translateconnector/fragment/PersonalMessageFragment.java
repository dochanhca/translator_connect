package com.example.translateconnector.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.imoktranslator.R;
import com.imoktranslator.activity.ChatRoomActivity;
import com.imoktranslator.adapter.ConversationAdapter;
import com.imoktranslator.adapter.RecentAdapter;
import com.imoktranslator.adapter.RecyclerViewClickListener;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.presenter.PersonalMessagePresenter;
import com.imoktranslator.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class PersonalMessageFragment extends BaseFragment implements
        PersonalMessagePresenter.PersonalMessageView,
        RecyclerViewClickListener.OnItemClickListener, ConversationAdapter.OnMessageAdapterClickListener {

    @BindView(R.id.txt_recent_label)
    OpenSansBoldTextView txtRecentLabel;
    @BindView(R.id.list_recent)
    RecyclerView listRecent;
    @BindView(R.id.txt_message_label)
    OpenSansBoldTextView txtMessageLabel;
    @BindView(R.id.list_message)
    RecyclerView listMessage;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private RecentAdapter recentAdapter;
    private ConversationAdapter conversationAdapter;

    private PersonalMessagePresenter personalMessagePresenter;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            conversationAdapter.filterChat(s.toString());
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshData = () -> {
        swipeRefreshLayout.setRefreshing(true);
        personalMessagePresenter.getUserRoomsFromFireBase();
    };

    public static PersonalMessageFragment newInstance() {

        Bundle args = new Bundle();

        PersonalMessageFragment fragment = new PersonalMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal_message;
    }

    @Override
    protected void initViews() {
        personalMessagePresenter = new PersonalMessagePresenter(getActivity(), this);
        txtRecentLabel.append(":");
        txtMessageLabel.append(":");

        recentAdapter = new RecentAdapter(new ArrayList<>(), getActivity().getApplicationContext());
        recentAdapter.setOnItemClickListener(this::OnItemClick);
        listRecent.setAdapter(recentAdapter);

        conversationAdapter = new ConversationAdapter(new ArrayList<>(), getActivity());
        conversationAdapter.setOnMessageAdapterClickListener(this);
        listMessage.setAdapter(conversationAdapter);

        edtSearch.addTextChangedListener(textWatcher);
        swipeRefreshLayout.setOnRefreshListener(onRefreshData);
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }

    @Override
    public void onGetListRoom(List<UserRoom> userRooms, List<UserRoom> personalRooms) {
        recentAdapter.setListRoom(userRooms);
        conversationAdapter.setListRoom(personalRooms);
        try {
            swipeRefreshLayout.setRefreshing(false);
        } catch (NullPointerException ex) {
            //null when user room data changed from other screen
        }
        DialogUtils.hideProgress();
    }

    @Override
    public void onGetLisRoomError() {
        DialogUtils.hideProgress();
        try {
            swipeRefreshLayout.setRefreshing(false);
        } catch (NullPointerException ex) {
            //null when user room data changed from other screen
        }
    }

    @Override
    public void onDeleteChatSuccess(UserRoom userRoom) {
        conversationAdapter.getData().remove(userRoom);
        recentAdapter.getData().remove(userRoom);
        recentAdapter.notifyDataSetChanged();
        conversationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMuteNotificationSuccess() {
        conversationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        personalMessagePresenter.getUserRoomsFromFireBase();
    }

    @Override
    public void OnItemClick(View view, int position) {
        UserRoom userRoom = recentAdapter.getData().get(position);
        ChatRoomActivity.startActivity(getBaseActivity(), userRoom);
    }

    @Override
    public void onMuteNotificationClick(UserRoom userRoom) {
        personalMessagePresenter.muteNotification(userRoom);
    }

    @Override
    public void onDeleteChatClick(UserRoom userRoom) {
        showNotifyDialog(getString(R.string.TB_1070), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                personalMessagePresenter.deleteUserRoom(userRoom);
            }
        });
    }

    @Override
    public void onChatClick(UserRoom userRoom) {
        ChatRoomActivity.startActivity(getBaseActivity(), userRoom);
    }
}
