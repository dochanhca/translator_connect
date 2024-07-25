package com.example.translateconnector.activity;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.imoktranslator.R;
import com.imoktranslator.adapter.ChatMemberAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.SerialUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class ChatMembersActivity extends BaseActivity implements HeaderView.BackButtonClickListener, ChatMemberAdapter.ClickListener {

    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.rcv_member)
    RecyclerView rcvMember;

    private Room room;
    private ChatMemberAdapter adapter;
    private String currentUserKey;
    private boolean isRoomRemoved;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_member;
    }

    @Override
    protected void initViews() {
        header.setCallback(this);

        room = getIntent().getParcelableExtra(Constants.ROOM_KEY);
        currentUserKey = LocalSharedPreferences.getInstance(this).getKeyUser();

        adapter = new ChatMemberAdapter(getApplicationContext(), getUserKeys(),
                currentUserKey.equals(room.getOwner()));
        adapter.setListener(this);
        rcvMember.setAdapter(adapter);
    }

    private List<String> getUserKeys() {
        List<String> keys = new ArrayList<>();
        keys.addAll(Arrays.asList(room.getVisitor().split(",")));
        keys.add(room.getOwner());

        return keys;
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.ROOM_KEY, room);
        intent.putExtra(Constants.IS_ROOM_REMOVED, isRoomRemoved);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRemoveMember(User user) {
        DialogUtils.showProgress(this);
        //clone object to keep original data if update room failed
        Room clone = (Room) SerialUtils.cloneObject(room);
        FireBaseDataUtils.getInstance().removeUserInRoom(this, user.getKey(), user.getId(),
                user.getName(), room, (databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        Toast.makeText(this, getString(R.string.TB_1053), Toast.LENGTH_SHORT).show();
                        room.setOwner(clone.getOwner());
                        room.setVisitor(clone.getVisitor());
                    } else {
                        adapter.removeItem(user.getKey());
                        if (adapter.getItemCount() == 0) {
                            isRoomRemoved = true;
                            onBackPressed();
                        }
                    }
                    DialogUtils.hideProgress();
                });
    }

    public static void startActivityForResult(BaseFragment baseFragment, Room room, int requestCode) {
        Intent intent = new Intent(baseFragment.getActivity(), ChatMembersActivity.class);
        intent.putExtra(Constants.ROOM_KEY, room);
        baseFragment.startActivityForResult(intent, requestCode);
    }
}
