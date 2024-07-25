package com.example.translateconnector.activity;

import android.content.Intent;

import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.fragment.FragmentBox;
import com.imoktranslator.fragment.FragmentController;
import com.imoktranslator.fragment.ListPriceFragment;
import com.imoktranslator.model.OrderModel;

import butterknife.BindView;

public class ListPriceActivity extends BaseActivity implements FragmentBox {

    private static final String ORDER_KEY = "ORDER_KEY";

    @BindView(R.id.header)
    HeaderView headerView;

    private FragmentController fm;
    private OrderModel orderModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_price;
    }

    @Override
    protected void initViews() {
        orderModel = getIntent().getParcelableExtra(ORDER_KEY);

        headerView.setCallback(() -> onBackPressed());

        fm = new FragmentController(getSupportFragmentManager(), getContainerViewId());

        FragmentController.Option option = new FragmentController.Option.Builder()
                .useAnimation(false)
                .addToBackStack(false)
                .setType(FragmentController.Option.TYPE.ADD)
                .build();
        switchFragment(ListPriceFragment.newInstance(orderModel, null), option);
    }

    @Override
    public int getContainerViewId() {
        return R.id.container_list_price;
    }

    @Override
    public void switchFragment(BaseFragment baseFragment, FragmentController.Option option) {
        if (fm != null && baseFragment != null && option != null) {
            fm.switchFragment(baseFragment, option);
        }
    }

    public static void startActivity(BaseActivity activity, OrderModel orderModel) {
        Intent intent = new Intent(activity, ListPriceActivity.class);
        intent.putExtra(ORDER_KEY, orderModel);
        activity.startActivity(intent);
    }
}
