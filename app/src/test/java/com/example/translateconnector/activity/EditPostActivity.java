package com.example.translateconnector.activity;

import android.content.Intent;

import com.imoktranslator.R;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.fragment.FragmentBox;
import com.imoktranslator.fragment.FragmentController;
import com.imoktranslator.fragment.PostStatusFragment;
import com.imoktranslator.utils.Constants;

public class EditPostActivity extends BaseActivity implements FragmentBox {

    private FragmentController fm;
    private Post post;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_post;
    }

    @Override
    protected void initViews() {
        post = getIntent().getParcelableExtra(Constants.POST_KEY);

        fm = new FragmentController(getSupportFragmentManager(), getContainerViewId());
        FragmentController.Option option = new FragmentController.Option.Builder()
                .useAnimation(false)
                .addToBackStack(false)
                .setType(FragmentController.Option.TYPE.REPLACE)
                .build();
        switchFragment(PostStatusFragment.newInstance(post), option);
    }

    @Override
    public int getContainerViewId() {
        return R.id.container_edit_post;
    }

    @Override
    public void switchFragment(BaseFragment baseFragment, FragmentController.Option option) {
        if (fm != null && baseFragment != null && option != null) {
            fm.switchFragment(baseFragment, option);
        }
    }

    public static void  startActivity(BaseActivity baseActivity, Post post) {
        Intent intent = new Intent(baseActivity, EditPostActivity.class);
        intent.putExtra(Constants.POST_KEY, post);
        baseActivity.startActivity(intent);
    }
}
