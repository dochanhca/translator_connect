package com.example.translateconnector.fragment;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.database.DatabaseError;
import com.imoktranslator.R;
import com.imoktranslator.adapter.SelectedImageAdapter;
import com.imoktranslator.bottomsheet.CustomBottomSheetFragment;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansEditText;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.presenter.PostStatusPresenter;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.PlayerManager;
import com.imoktranslator.utils.PopupMenuUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.imoktranslator.utils.Constants.FRIENDS_MODE;
import static com.imoktranslator.utils.Constants.MAX_AUDIO_DURATION;
import static com.imoktranslator.utils.Constants.MAX_VIDEO_DURATION;
import static com.imoktranslator.utils.Constants.PRIVATE_MODE;
import static com.imoktranslator.utils.Constants.PUBLIC_MODE;

public class PostStatusFragment extends BaseFragment implements PostStatusPresenter.PostStatusView {

    private static final int IMAGE_CAMERA_REQUEST = 11;
    private static final int VIDEO_CAMERA_REQUEST = 12;
    private static final int VIDEO_GALLERY_REQUEST = 13;

    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.contact_avatar)
    CircleImageView contactAvatar;
    @BindView(R.id.contact_name)
    OpenSansBoldTextView contactName;
    @BindView(R.id.contact_row)
    RelativeLayout contactRow;
    @BindView(R.id.edtContent)
    OpenSansEditText edtContent;
    @BindView(R.id.txt_attack_image)
    OpenSansBoldTextView txtAttackImage;
    @BindView(R.id.txt_attack_record)
    OpenSansBoldTextView txtAttackRecord;
    @BindView(R.id.divider_content)
    View dividerContent;
    @BindView(R.id.rcv_selected_image)
    RecyclerView rcvSelectedImage;
    @BindView(R.id.player_view)
    PlayerView playerView;
    @BindView(R.id.layout_selected_video)
    RelativeLayout layoutSelectedVideo;
    @BindView(R.id.img_record_audio)
    ImageView imgRecordAudio;
    @BindView(R.id.layout_select_priority)
    ViewGroup layoutSelectPriority;
    @BindView(R.id.txt_priority)
    TextView txtPriority;
    @BindView(R.id.ic_priority)
    ImageView icPriority;

    private User currentFirebaseUser;
    private PostStatusPresenter presenter;

    private SelectedImageAdapter adapter;
    private List<Image> selectedImages = new ArrayList<>();
    private String currentUserId;
    private List<String> imageOptions;
    private Uri selectedUri;
    private File selectedFile;

    private String selectedType;

    //using for Exo player
    private PlayerManager playerManager;

    //
    private boolean isRecording;
    private MediaRecorder recorder;

    private String priorityMode = PUBLIC_MODE;

    private Post post;

    public static PostStatusFragment newInstance(Post post) {

        Bundle args = new Bundle();

        PostStatusFragment fragment = new PostStatusFragment();
        fragment.setArguments(args);
        fragment.post = post;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_post_status;
    }

    @Override
    protected void initViews() {
        currentFirebaseUser = LocalSharedPreferences.getInstance(getContext()).getCurrentFirebaseUser();

        if (currentFirebaseUser == null) {
            showDialogLoadUserData();
        }

        currentUserId = LocalSharedPreferences.getInstance(getActivity()).getKeyUser();
        presenter = new PostStatusPresenter(getActivity(), this);


        imageOptions = Arrays.asList(getResources().getStringArray(R.array.arr_image_options));

        initHeader();
        displayUserInfo();

        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setPostBtnEnabled();
            }
        });
        fillData();
    }

    private void showDialogLoadUserData() {
        showNotifyDialog(getString(R.string.TB_1084), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {
                getChildFragmentManager().popBackStackImmediate();
            }

            @Override
            public void onOk(Object... obj) {
                initViews();
            }
        });
    }

    private void fillData() {
        if (post != null) {
            edtContent.setText(post.getMessage());
            setPriority(post.getMode());
            loadMediaContent();
        } else {
            User user = LocalSharedPreferences.getInstance(getActivity()).getCurrentFirebaseUser();
            String priorityMode = TextUtils.isEmpty(user.getPrioritySetting())
                    ? com.imoktranslator.utils.Constants.PUBLIC_MODE : user.getPrioritySetting();
            setPriority(priorityMode);
        }
    }

    private void loadMediaContent() {
        if (post.getFileModels() != null && post.getFileModels().size() > 0) {
            String mediaType = post.getFileModels().get(0).getType();
            if (mediaType.equals(FireBaseDataUtils.TYPE_IMAGE)) {
                setImageFromFileModels(post.getFileModels());
                loadSelectedImages();
            } else {
                selectedType = mediaType;
                loadMedia();
            }
        }
    }

    private void setPriority(String priority) {
        priorityMode = priority;
        if (priority.equals(FRIENDS_MODE)) {
            updatePriorityLayout(getString(R.string.MH45_005), R.drawable.ic_friends);
        } else if (priority.equals(PRIVATE_MODE)) {
            updatePriorityLayout(getString(R.string.MH45_010), R.drawable.ic_only_me);
        }
    }

    private void setImageFromFileModels(List<FileModel> fileModels) {
        for (FileModel fileModel : fileModels) {
            Image image = new Image(-1, fileModel.getNameFile(), fileModel.getUrlFile(), false);
            selectedImages.add(image);
        }
    }

    private void initHeader() {
        header.setTittle(getString(post == null ? R.string.MH28_011 : R.string.MH28_018));
        header.setTvRightValue(post == null ? R.string.MH28_003 : R.string.MH04_013);
        header.setTvRightOnClick(v -> postStatus());
        header.getTvRight().setEnabled(false);
        header.setCallback(() -> getActivity().onBackPressed());
    }

    private void postStatus() {
        if (getUnUploadedImages().size() > 0) {
            presenter.uploadImage(getUnUploadedImages());
        } else if (selectedUri != null) {
            presenter.uploadMediaFile(selectedUri, selectedType);
        } else {
            presenter.post(post, edtContent.getText().toString(), null, priorityMode);
        }
    }

    private void loadSelectedImages() {
        if (adapter == null) {
            adapter = new SelectedImageAdapter(getActivity().getApplicationContext(), selectedImages);
            rcvSelectedImage.setAdapter(adapter);
            adapter.setOnItemClickListener(pos -> updateContentView());
        } else {
            adapter.notifyDataSetChanged();
        }
        updateContentView();
    }

    private List<Image> getUnUploadedImages() {
        List<Image> unUploadedImages = new ArrayList<>();
        for (Image image : selectedImages) {
            if (image.id > 0) {
                unUploadedImages.add(image);
            }
        }
        return unUploadedImages;
    }

    private void updateContentView() {
        rcvSelectedImage.setVisibility(selectedImages.size() == 0 ? View.GONE : View.VISIBLE);
        dividerContent.setVisibility(selectedImages.size() == 0 ? View.GONE : View.VISIBLE);
        if (selectedImages.size() == 0) {
            selectedType = null;
        }
        setPostBtnEnabled();
    }

    private void setPostBtnEnabled() {
        if (!edtContent.getText().toString().isEmpty() || selectedImages.size() > 0
                || selectedUri != null || hasPostMediaFile()) {
            header.getTvRight().setEnabled(true);
        } else {
            header.getTvRight().setEnabled(false);
        }
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }

    @OnClick({R.id.txt_attack_image, R.id.layout_record_audio, R.id.img_delete_video, R.id.layout_select_priority})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_attack_image:
                showSelectMediaPopup();
                break;
            case R.id.layout_record_audio:
                setAudioRecording();
                break;
            case R.id.img_delete_video:
                removeMedia();
                break;
            case R.id.layout_select_priority:
                showPopUpMenu(layoutSelectPriority);
                break;
        }
    }

    private void showPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.menu_select_priority);
        // ...
        PopupMenuUtils.setForceShowIcon(popupMenu);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.priority_public:
                    priorityMode = PUBLIC_MODE;
                    updatePriorityLayout(getString(R.string.MH45_004), R.drawable.ic_public);
                    break;
                case R.id.priority_friend:
                    priorityMode = FRIENDS_MODE;
                    updatePriorityLayout(getString(R.string.MH45_005), R.drawable.ic_friends);
                    break;
                case R.id.priority_private:
                    priorityMode = PRIVATE_MODE;
                    updatePriorityLayout(getString(R.string.MH45_010), R.drawable.ic_only_me);
                    break;
            }
            return false;
        });
    }

    private void updatePriorityLayout(String name, int resourceId) {
        txtPriority.setText(name);
        icPriority.setImageResource(resourceId);
    }

    private void setAudioRecording() {
        if (!isRecording) {
            if (!isMediaSelected(FireBaseDataUtils.TYPE_AUDIO)) {
                presenter.requestRecordAudioPermission(getActivity());
            }
        } else {
            stopRecordAudio();
        }
    }

    public void selectPhotos() {
        if (selectedImages.size() >= 9) {
            Toast.makeText(getActivity(), getString(R.string.TB_1063), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 9);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void displayUserInfo() {
        Glide.with(this)
                .load(currentFirebaseUser.getAvatar())
                .error(R.drawable.img_default_avatar)
                .into(contactAvatar);

        contactName.setText(currentFirebaseUser.getName());
    }

    @Override
    public void uploadImagesSuccess(List<FileModel> uploadedFile) {
        if (post != null) {
            if (post.getFileModels() == null) {
                post.setFileModels(uploadedFile);
            } else {
                post.getFileModels().addAll(uploadedFile);
            }
        }
        presenter.post(post, edtContent.getText().toString(), uploadedFile, priorityMode);
    }

    @Override
    public void postStatusSuccess() {
        selectedImages.clear();
        getActivity().onBackPressed();
    }

    @Override
    public void openCamera() {
        selectedFile = createFile();
        selectedUri = FileProvider.getUriForFile(getActivity(),
                com.imoktranslator.utils.Constants.IMAGE_CAMERA_AUTHORITY,
                selectedFile);

        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, selectedUri);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_GALLERY_REQUEST);
    }

    @Override
    public void onLoadBitmap(byte[] bytes) {
        //not implement
    }

    @Override
    public void firebaseError(DatabaseError databaseError) {
        showNotifyDialog(getString(R.string.TB_1053));
    }

    @Override
    public void recordVideo() {
        File selectedFile = createFile();

        selectedUri = FileProvider.getUriForFile(getActivity(),
                com.imoktranslator.utils.Constants.IMAGE_CAMERA_AUTHORITY,
                selectedFile);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, VIDEO_CAMERA_REQUEST);
    }

    @Override
    public void uploadMediaSuccess(FileModel fileModel) {
        List<FileModel> fileModels = new ArrayList<>();
        fileModels.add(fileModel);
        if (post != null) {
            post.setFileModels(fileModels);
        }
        presenter.post(post, edtContent.getText().toString(), fileModels, priorityMode);
    }

    @Override
    public void startRecordAudio() {
        isRecording = true;
        imgRecordAudio.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.flickering_animation));
        txtAttackRecord.setText(getString(R.string.MH22_018));

        selectedFile = createFile();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(selectedFile.getPath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setMaxDuration(MAX_AUDIO_DURATION);
        recorder.setOnInfoListener((mr, what, extra) -> {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                stopRecordAudio();
            }
        });

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Chat Fragment", "prepare() failed");
        }
        recorder.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected fileModels
            List<Image> selectedList = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            for (Image image : selectedList) {
                if (selectedImages.size() < 9) {
                    selectedImages.add(image);
                }
            }
            loadSelectedImages();
            selectedType = FireBaseDataUtils.TYPE_IMAGE;
        }
        if (requestCode == IMAGE_CAMERA_REQUEST && resultCode == RESULT_OK) {
            loadImageFromCamera();
            selectedType = FireBaseDataUtils.TYPE_IMAGE;
        }
        if (requestCode == VIDEO_CAMERA_REQUEST && resultCode == RESULT_OK) {
            selectedType = FireBaseDataUtils.TYPE_VIDEO;
            loadMedia();
        }
        if (requestCode == VIDEO_GALLERY_REQUEST && resultCode == RESULT_OK) {
            selectedUri = data.getData();
            selectedType = FireBaseDataUtils.TYPE_VIDEO;
            loadMedia();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            stopRecordAudio();
        }
    }

    private void loadImageFromCamera() {
        Image image = new Image(System.currentTimeMillis(), selectedFile.getName(), selectedFile.getPath(), false);
        selectedImages.add(image);
        loadSelectedImages();
    }

    private void loadMedia() {
        if (playerManager == null) {
            playerManager = PlayerManager.getPlayerManagerInstance();
        }

        adjustPlayView();
        layoutSelectedVideo.setVisibility(View.VISIBLE);
        dividerContent.setVisibility(View.VISIBLE);

        if (post == null || post.getFileModels() == null || post.getFileModels().size() == 0) {
            playerManager.init(getActivity(), playerView, layoutSelectedVideo,
                    selectedUri.getPath(), selectedUri);
        } else {
            playerManager.init(getActivity(), playerView, layoutSelectedVideo,
                    post.getFileModels().get(0).getUrlFile(), null);
        }
        playerManager.setPayWhenReady(false);

        if (selectedType.equals(FireBaseDataUtils.TYPE_VIDEO)) {
            playerView.getPlayer().addListener(new Player.DefaultEventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    super.onPlayerStateChanged(playWhenReady, playbackState);
                    if (playbackState == Player.STATE_READY) {
                        long duration = playerView.getPlayer().getDuration();
                        checkVideoDuration(duration);
                    }
                }
            });
        }

        setPostBtnEnabled();
    }

    private void checkVideoDuration(long duration) {
        if (duration / 1000 > MAX_VIDEO_DURATION) {
            showNotifyDialog(getString(R.string.TB_1072));
            removeMedia();
            return;
        }
    }

    private void removeMedia() {
        playerManager.release();
        layoutSelectedVideo.setVisibility(View.GONE);
        dividerContent.setVisibility(View.GONE);
        if (hasPostMediaFile()) {
            post.getFileModels().clear();
        } else {
            selectedUri = null;
            selectedFile = null;
        }
        selectedType = null;
        setPostBtnEnabled();
    }

    private void showSelectMediaPopup() {
        CustomBottomSheetFragment bottomSheetFragment = CustomBottomSheetFragment.newInstance(getString(R.string.MH28_006));
        bottomSheetFragment.setOptions(imageOptions);
        bottomSheetFragment.setSelectedPosition(-1);
        bottomSheetFragment.setListener(position -> onUploadImageSelect(position));
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void onUploadImageSelect(int position) {
        switch (position) {
            case 0:
                if (!isMediaSelected(FireBaseDataUtils.TYPE_IMAGE)) {
                    presenter.requestCameraPermission(getActivity());
                }
                break;
            case 1:
                if (!isMediaSelected(FireBaseDataUtils.TYPE_IMAGE)) {
                    selectPhotos();
                }
                break;
            case 2:
                if (!isMediaSelected(FireBaseDataUtils.TYPE_VIDEO)) {
                    presenter.requestRecordVideo(getActivity());
                }
                break;
            case 3:
                if (!isMediaSelected(FireBaseDataUtils.TYPE_VIDEO)) {
                    presenter.requestReadSDCardPermission(getActivity());
                }
                break;
        }
    }

    private boolean isMediaSelected(String type) {
        if (selectedType != null && !selectedType.equals(type)) {
            showNotifyDialog(getString(R.string.TB_1064));
            return true;
        }
        return false;
    }

    private File createFile() {
        String photoName = String.valueOf(System.currentTimeMillis()) + "_" + currentUserId;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                photoName + "camera.jpg");

        return file;
    }

    private void stopRecordAudio() {
        selectedUri = Uri.fromFile(selectedFile);
        isRecording = false;
        imgRecordAudio.clearAnimation();
        txtAttackRecord.setText(getString(R.string.MH28_007));

        recorder.stop();
        recorder.release();
        recorder = null;
        selectedType = FireBaseDataUtils.TYPE_AUDIO;
        loadMedia();
    }

    private void adjustPlayView() {
        int pixels = (int) getResources().getDimension(selectedType.equals(FireBaseDataUtils.TYPE_VIDEO)
                ? R.dimen.video_view_h : R.dimen.audio_view_h);

        ViewGroup.LayoutParams params = layoutSelectedVideo.getLayoutParams();
        params.height = pixels;
        layoutSelectedVideo.setLayoutParams(params);
    }

    private boolean hasPostMediaFile() {
        return post != null && post.getFileModels() != null && post.getFileModels().size() > 0;
    }
}
