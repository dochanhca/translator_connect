package com.example.translateconnector.dialog;

import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.exoplayer2.ui.PlayerView;
import com.imoktranslator.R;
import com.imoktranslator.utils.PlayerManager;

import butterknife.BindView;

public class VideoDialog extends BaseDialog {

    @BindView(R.id.player_view)
    PlayerView playerView;

    private PlayerManager mPlayer;
    private String videoUrl;

    @Override
    protected int getDialogLayout() {
        return R.layout.dialog_video;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayer.reset();
    }

    private void initViews() {
        mPlayer = PlayerManager.getPlayerManagerInstance();
//        mPlayer.init(getContext(), playerView, videoUrl, null);
        mPlayer.setPayWhenReady(true);
    }

    public static void startDialog(FragmentManager fragmentManager, String videoPath) {
        VideoDialog videoDialog = new VideoDialog();
        videoDialog.videoUrl = videoPath;
        videoDialog.show(fragmentManager, VideoDialog.class.getSimpleName());
    }

}
