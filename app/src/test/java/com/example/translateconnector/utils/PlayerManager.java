package com.example.translateconnector.utils;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.imoktranslator.R;

public class PlayerManager {

    private static PlayerManager playerManagerInstance;
    private SimpleExoPlayer player;
    private long contentPosition;

    //using to view video screen
    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;

    private PlayerView playerView;
    private ViewGroup viewGroup;

    public PlayerManager() {
    }

    public static synchronized PlayerManager getPlayerManagerInstance() {
        if (playerManagerInstance == null) {
            synchronized (PlayerManager.class) {
                if (playerManagerInstance == null) {
                    playerManagerInstance = new PlayerManager();
                }
            }

        }
        return playerManagerInstance;
    }

    public void init(Activity activity, PlayerView playerView, ViewGroup viewGroup,
                     String contentUrl, Uri uri) {
        // Create a default track selector.
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // Create a player instance.
        player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);

        // Bind the player to the view.
        playerView.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(activity,
                Util.getUserAgent(activity, activity.getString(R.string.app_name)));

        // This is the MediaSource representing the content media (i.e. not the ad).
        MediaSource contentMediaSource =
                new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri == null ? Uri.parse(contentUrl) : uri);

        this.playerView = playerView;

        //using to play video full screen
        initFullscreenButton(activity);
        initFullscreenDialog(activity, viewGroup);

        player.seekTo(contentPosition);
        player.prepare(contentMediaSource);
    }

    public void setPayWhenReady(boolean isPlay) {
        if (player != null) {
            player.setPlayWhenReady(isPlay);
        }
    }

    public void reset() {
        if (player != null) {
            contentPosition = player.getContentPosition();
            player.release();
        }
    }

    public void release() {
        if (player != null) {
            player.release();
            contentPosition = 0;
            player = null;
        }
    }

    private void initFullscreenDialog(Activity activity, ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
        mFullScreenDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog(activity);
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog(Activity activity) {
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mFullScreenDialog.addContentView(playerView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog(Activity activity) {
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        viewGroup.addView(playerView, 0);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_fullscreen_expand));
    }

    private void initFullscreenButton(Activity activity) {
        PlayerControlView controlView = playerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(v -> {
            if (!mExoPlayerFullscreen)
                openFullscreenDialog(activity);
            else
                closeFullscreenDialog(activity);
        });
    }
}
