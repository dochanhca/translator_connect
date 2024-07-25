package com.example.translateconnector.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.imoktranslator.R;
import com.imoktranslator.activity.GeneralNotificationActivity;
import com.imoktranslator.activity.UserInfoActivity;
import com.imoktranslator.adapter.FriendInvitationAdapter;
import com.imoktranslator.model.FriendInvitationModel;
import com.imoktranslator.presenter.FriendInvitationPresenter;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AddFriendFragment extends BaseFragment implements
        FriendInvitationPresenter.FriendInvitationView, FriendInvitationAdapter.OnInvitationClickListener {

    @BindView(R.id.rv_invitations)
    RecyclerView rvInvitations;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private FriendInvitationPresenter presenter;
    private FriendInvitationAdapter adapter;
    private List<FriendInvitationModel> invitationModelList = new ArrayList<>();

    private SwipeRefreshLayout.OnRefreshListener onRefreshData = () -> {
        swipeRefreshLayout.setRefreshing(true);
        presenter.getFriendInvitations();
    };


    public static AddFriendFragment newInstance() {
        return new AddFriendFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_friend;
    }

    @Override
    protected void initViews() {
        swipeRefreshLayout.setOnRefreshListener(onRefreshData);
        adapter = new FriendInvitationAdapter(getContext(), invitationModelList, this);
        rvInvitations.setAdapter(adapter);

        presenter = new FriendInvitationPresenter(getContext(), this);
        presenter.getFriendInvitations();
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }

    @Override
    public String getFireBaseUserId() {
        return LocalSharedPreferences.getInstance(getContext()).getKeyUser();
    }

    @Override
    public void fireBaseError() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void getDataSuccessful(List<FriendInvitationModel> data) {
        swipeRefreshLayout.setRefreshing(false);
        invitationModelList.clear();
        invitationModelList.addAll(data);
        adapter.notifyDataSetChanged();
        getNotificationActivity().setFriendRequestCount(adapter.getItemCount());
    }

    @Override
    public void emptyData() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        invitationModelList.clear();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onAllow(FriendInvitationModel invitation) {
        presenter.acceptFriendInvitation(invitation);
    }

    @Override
    public void onAddFriend(FriendInvitationModel invitationModel) {
        String msg = String.format(getString(R.string.MH19_006), invitationModel.getSenderName());
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        //remove invitations
        onCancel(invitationModel);
    }

    @Override
    public void onRemoveFriendRequest(FriendInvitationModel invitationModel) {
        onCancel(invitationModel);
    }

    @Override
    public void onCancel(FriendInvitationModel invitation) {
        //tu choi ket ban
        FireBaseDataUtils.getInstance().ignoreInvitation(getFireBaseUserId(), invitation.getSenderKey());
        invitationModelList.remove(invitation);
        adapter.notifyDataSetChanged();
        getNotificationActivity().setFriendRequestCount(adapter.getItemCount());

    }

    @Override
    public void onOpenInfo(FriendInvitationModel invitationModel) {
        UserInfoActivity.startActivity(getBaseActivity(), invitationModel.getSenderKey(),
                invitationModel.getSenderId(), false);
    }

    public GeneralNotificationActivity getNotificationActivity() {
        return (GeneralNotificationActivity) getBaseActivity();
    }
}
