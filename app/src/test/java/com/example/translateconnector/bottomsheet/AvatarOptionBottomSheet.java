package com.example.translateconnector.bottomsheet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ton on 3/30/18.
 */

public class AvatarOptionBottomSheet extends BottomSheetDialogFragment {
    private View.OnClickListener onOptionClickListener;
    private boolean isAllowShowAvatar = true;
    @BindView(R.id.tv_display_avatar)
    OpenSansBoldTextView optionDisplayAvatar;
    @BindView(R.id.divider_line)
    View line;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bottom_sheet_avatar_option, container, false);
        ButterKnife.bind(this, rootView);


        if (!isAllowShowAvatar) {
            optionDisplayAvatar.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
        return rootView;
    }

    public void setOnOptionClickListener(View.OnClickListener onOptionClickListener) {
        this.onOptionClickListener = onOptionClickListener;
    }

    @OnClick({R.id.tv_display_avatar, R.id.tv_capture_avatar, R.id.tv_gallery, R.id.tv_delete_avatar, R.id.tv_cancel})
    public void onOptionClicked(View view) {
        onOptionClickListener.onClick(view);
        dismiss();
    }

    public void setAllowShowAvatar(boolean isAllowShowAvatar) {
        this.isAllowShowAvatar = isAllowShowAvatar;
    }
}
