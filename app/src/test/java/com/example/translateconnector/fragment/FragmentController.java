package com.example.translateconnector.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.imoktranslator.R;

/**
 * Created by tvoer on 4/5/18.
 */

public class FragmentController {
    private FragmentManager fm;
    private int container;

    public FragmentController(FragmentManager fm, int container) {
        this.fm = fm;
        this.container = container;
    }

    public void switchFragment(BaseFragment baseFragment, Option option) {
        FragmentTransaction transaction = fm.beginTransaction();
        if (option.useAnimation) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        }

        if (option.type == Option.TYPE.REPLACE) {
            transaction.replace(container, baseFragment, baseFragment.getClass().getSimpleName());
        } else {
            transaction.add(container, baseFragment, baseFragment.getClass().getSimpleName());
        }

        if (option.addToBackStack) {
            transaction.addToBackStack(baseFragment.getClass().getSimpleName());
        }

        transaction.commit();

    }

    public static class Option {
        public enum TYPE {
            REPLACE, ADD
        }

        private boolean useAnimation = true;
        private boolean addToBackStack = true;
        private TYPE type = TYPE.REPLACE;

        private Option() {
        }

        public static class Builder {
            private final Option option;

            public Builder() {
                this.option = new Option();
            }

            public Builder useAnimation(boolean isUseAnimation) {
                this.option.useAnimation = isUseAnimation;
                return this;
            }

            public Builder addToBackStack(boolean isAddToBackStack) {
                this.option.addToBackStack = isAddToBackStack;
                return this;
            }

            public Builder setType(TYPE type) {
                this.option.type = type;
                return this;
            }

            public Option build() {
                return this.option;
            }
        }
    }
}
