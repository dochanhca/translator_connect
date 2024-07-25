package com.example.translateconnector.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.imoktranslator.R;
import com.imoktranslator.network.response.IntroduceResponse;
import com.imoktranslator.presenter.SplashPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.LocalSharedPreferences;

import static com.imoktranslator.utils.Constants.KEY_INTRO_DATA;

/**
 * Created by tontn on 3/24/18.
 */

public class SplashActivity extends BaseActivity implements SplashPresenter.SplashView {

    private SplashPresenter presenter;
    private SharedPreferences mPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {
        mPreferences = this.getSharedPreferences(Constants.INTRODUCING_PREFERENCES, MODE_PRIVATE);
        presenter = new SplashPresenter(this, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirstScreen();
    }

    private void initFirstScreen() {
        boolean introShowed = mPreferences.getBoolean(Constants.KEY_INTRO_SHOWED, false);

        boolean isUserLoggedIn = LocalSharedPreferences.getInstance(this).getBooleanData(Constants.IS_USER_LOGGED_IN);
        if (introShowed) {
            new Handler().postDelayed(() -> {
                if (isUserLoggedIn) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }, 500);
        } else {
            presenter.fetchIntroData();
        }
    }

    @Override
    public void openIntroduceScreen(IntroduceResponse introduceResponse) {
        Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
        intent.putExtra(KEY_INTRO_DATA, introduceResponse);
        startActivity(intent);
        finish();
    }
}
