package com.example.translateconnector.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.imoktranslator.R;
import com.imoktranslator.adapter.ContactToCreateGroupChatAdapter;
import com.imoktranslator.customview.CustomFlexBoxLayout;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansEditText;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.presenter.NewMessagesPresenter;
import com.imoktranslator.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NewGroupChatActivity extends BaseActivity implements NewMessagesPresenter.NewMessagesView,
        ContactToCreateGroupChatAdapter.OnContactClickListener, HeaderView.BackButtonClickListener {

    @BindView(R.id.list_friend)
    RecyclerView listFriend;
    @BindView(R.id.flexBox)
    CustomFlexBoxLayout flexBox;
    @BindView(R.id.header)
    HeaderView headerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_add_friend)
    TextView txtAddFriend;

    private NewMessagesPresenter presenter;

    private ContactToCreateGroupChatAdapter contactAdapter;
    private List<User> selectedUsers = new ArrayList<>();

    private Room room;
    private User partner;

    private SwipeRefreshLayout.OnRefreshListener onRefreshData = () -> {
        swipeRefreshLayout.setRefreshing(true);
        presenter.getFriends(room);
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_messages;
    }

    @Override
    protected void initViews() {

        headerView.setCallback(this);

        room = getIntent().getParcelableExtra(Constants.ROOM_KEY);
        partner = getIntent().getParcelableExtra(Constants.USER_KEY);
        if (room != null) {
            txtAddFriend.setVisibility(View.VISIBLE);
        } else {
            headerView.setTvRightValue(R.string.MH29_003);
            headerView.setTvRightOnClick(v -> {
                if (selectedUsers.size() < 2) {
                    Toast.makeText(NewGroupChatActivity.this, getString(R.string.TB_1058),
                            Toast.LENGTH_SHORT).show();
                } else {
                    presenter.addNewGroup(selectedUsers, null);
                }
            });
        }

        presenter = new NewMessagesPresenter(this, this);
        contactAdapter = new ContactToCreateGroupChatAdapter(getApplicationContext(),
                new ArrayList<>(), selectedUsers, this);

        listFriend.setAdapter(contactAdapter);
        presenter.getFriends(room);

        flexBox.addDataSetChangeListener(new CustomFlexBoxLayout.DataSetChangeListener() {
            @Override
            public void onItemAdded(User item) {
                selectedUsers.add(item);
                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRemoved(User item) {
                selectedUsers.remove(item);
                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void query(String text) {
                presenter.query(text);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(onRefreshData);
    }

    @Override
    public void onUserClicked(User user) {
        if (flexBox.isContainThisItem(user)) {
            flexBox.deleteFlexBoxItem(user);
        } else {
            flexBox.addNewUser(user);
        }
    }

    @Override
    public void filterResult(List<User> result, String query) {
        contactAdapter.setListContact(result);
    }

    @Override
    public void fireBaseError() {
        swipeRefreshLayout.setRefreshing(false);
        showNotifyDialog(getString(R.string.TB_1053));
    }

    @Override
    public void getDataSuccessful(List<User> userList) {
        swipeRefreshLayout.setRefreshing(false);
        contactAdapter.setListContact(userList);

        String textSearch = flexBox.getTextSearch();
        if (!TextUtils.isEmpty(textSearch)) {
            presenter.query(textSearch);
        }
    }

    @Override
    public void onCreateRoomChat(UserRoom userRoom, String roomName) {
        ChatRoomActivity.startActivity(this, userRoom, roomName);
        finish();
    }

    @Override
    public void addUserSuccess() {
        Intent intent = new Intent();
        intent.putExtra(Constants.ROOM_KEY, room);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @OnClick(R.id.txt_add_friend)
    public void onAddUserToRoom() {
        if (selectedUsers.size() == 0) {
            return;
        }

        if (partner != null) {
            presenter.addNewGroup(selectedUsers,partner);
        } else {
            presenter.addUserToRoom(selectedUsers, room);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && view instanceof OpenSansEditText) {
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            Log.d("KeyEvent", "catch");
        }
        return super.dispatchKeyEvent(event);
    }


    public static void startActivity(BaseActivity activity) {
        Intent intent = new Intent(activity, NewGroupChatActivity.class);
        activity.startActivity(intent);
    }

    public static void startActivityForResult(BaseFragment baseFragment, Room room,
                                              @Nullable User partner, int requestCode) {
        Intent intent = new Intent(baseFragment.getActivity(), NewGroupChatActivity.class);
        intent.putExtra(Constants.ROOM_KEY, room);
        intent.putExtra(Constants.USER_KEY, partner);
        baseFragment.startActivityForResult(intent, requestCode);
    }
}
