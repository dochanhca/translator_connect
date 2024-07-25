package com.example.translateconnector.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.imoktranslator.R;
import com.imoktranslator.adapter.NearByFriendAdapter;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.dialog.NearBySettingDialog;
import com.imoktranslator.model.NearByFriend;
import com.imoktranslator.presenter.FindNearByPresenter;
import com.imoktranslator.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FindNearByActivity extends BaseActivity implements FindNearByPresenter.FindNearByView,
        SwipeRefreshLayout.OnRefreshListener {

    public static final int REQUEST_LOCATION = 1;
    @BindView(R.id.btBack)
    ImageView btBack;
    @BindView(R.id.txt_title)
    OpenSansBoldTextView title;
    @BindView(R.id.img_setting)
    ImageView imgSetting;
    @BindView(R.id.rcv_near_by)
    RecyclerView rcvNearBy;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    private FindNearByPresenter presenter;
    private NearByFriendAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_near_by;
    }

    @Override
    protected void initViews() {
        initListFriends();
        presenter = new FindNearByPresenter(this, this);
        presenter.requestLocationPermission(this);
        swipeLayout.setOnRefreshListener(this);
    }

    private void initListFriends() {
        adapter = new NearByFriendAdapter(new ArrayList<>(), getApplicationContext());
        adapter.setOnClickListener(new NearByFriendAdapter.OnClickListener() {
            @Override
            public void onAddFriendClick(NearByFriend item) {
                presenter.sendFriendInvitation(item);
            }

            @Override
            public void onOpenInfo(NearByFriend item) {
                UserInfoActivity.startActivity(FindNearByActivity.this, item);
            }
        });
        rcvNearBy.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startLocationUpdates(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopLocationUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    presenter.startLocationUpdates(this);
                    presenter.findNearByFriends();
                }
                break;
        }
    }

    @Override
    public void requestLocation(ApiException exception) {
        try {
            // Cast to a resolvable exception.
            ResolvableApiException resolvable = (ResolvableApiException) exception;
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            resolvable.startResolutionForResult(
                    FindNearByActivity.this,
                    REQUEST_LOCATION);
        } catch (IntentSender.SendIntentException e) {
            // Ignore the error.
        } catch (ClassCastException e) {
            // Ignore, should be an impossible error.
        }

    }

    @Override
    public void onGetNearByFriends(List<NearByFriend> nearByFriend) {
        adapter.setData(nearByFriend);
        swipeLayout.setRefreshing(false);
        DialogUtils.hideProgress();
    }

    @Override
    public void onGetNearByFriendsError() {
        swipeLayout.setRefreshing(false);
        DialogUtils.hideProgress();
    }

    @OnClick({R.id.btBack, R.id.img_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btBack:
                onBackPressed();
                break;
            case R.id.img_setting:
                showSettingDialog();
                break;
        }
    }

    private void showSettingDialog() {
        NearBySettingDialog.showDialog(getSupportFragmentManager(), new NearBySettingDialog.NearBySettingDialogClickListener() {
            @Override
            public void onOkClickListener() {
                presenter.findNearByFriends();
            }

            @Override
            public void onCancelClickListener() {

            }
        });
    }

    public static void startActivity(BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, FindNearByActivity.class);
        baseActivity.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        presenter.findNearByFriends();
    }
}
