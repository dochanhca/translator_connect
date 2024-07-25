package com.example.translateconnector.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.rd.PageIndicatorView;
import com.imoktranslator.R;
import com.imoktranslator.adapter.IntroPagerAdapter;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.fragment.IntroPageFragment;
import com.imoktranslator.model.IntroPageItem;
import com.imoktranslator.network.response.IntroduceResponse;
import com.imoktranslator.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by tontn on 3/24/18.
 */

public class IntroActivity extends BaseActivity {
    @BindView(R.id.viewPager)
    AutoScrollViewPager viewPager;

    @BindView(R.id.pageIndicatorView)
    PageIndicatorView pageIndicatorView;

    @BindView(R.id.tvTitle)
    OpenSansBoldTextView tvTitle;

    @BindView(R.id.tvDescription)
    OpenSansTextView tvDescription;

    @BindView(R.id.btStart)
    OpenSansBoldTextView btStart;


    private List<IntroPageItem> introData;
    private SharedPreferences mPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_introduce;
    }

    @Override
    protected void initViews() {
        mPreferences = this.getSharedPreferences(Constants.INTRODUCING_PREFERENCES, MODE_PRIVATE);

        initData();
        initPageIndicator();
        initViewPager();
    }

    private void initViewPager() {
        IntroPagerAdapter adapter = new IntroPagerAdapter(getSupportFragmentManager());
        for (IntroPageItem item : introData) {
            adapter.addPage(IntroPageFragment.newInstance(item.getImage()));
        }

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageIndicatorView.setSelection(position);
                tvTitle.setText(introData.get(position).getTitle());
                tvDescription.setText(introData.get(position).getDescription());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
    }

    private void initPageIndicator() {
        pageIndicatorView.setCount(introData.size()); // specify total count of indicators

        if (introData.size() == 0)
            return;
        pageIndicatorView.setSelection(0);
        if (!TextUtils.isEmpty(introData.get(0).getTitle())) {
            tvTitle.setText(introData.get(0).getTitle());
        }
        if (!TextUtils.isEmpty(introData.get(0).getDescription())) {
            tvDescription.setText(introData.get(0).getDescription());
        }

        pageIndicatorView.setRadius(4);
        pageIndicatorView.setPadding(5);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initData() {
        IntroduceResponse introduceResponse = getIntent().getParcelableExtra(Constants.KEY_INTRO_DATA);
        if (introduceResponse != null && introduceResponse.getData() != null) {
            introData = introduceResponse.getData();
        } else {
            introData = new ArrayList<>();
        }
    }

    @OnClick({R.id.btStart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btStart:
                saveFirstTimeStartAppKey();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void saveFirstTimeStartAppKey() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(Constants.KEY_INTRO_SHOWED, true);
        editor.commit();
    }
}
