package com.example.translateconnector.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.database.Query;
import com.imoktranslator.R;
import com.imoktranslator.adapter.CommentAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.ImageBoxLayout;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SquareImageView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.firebase.model.Comment;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.presenter.PostDetailPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DateTimeUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.ImagesManager;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.PlayerManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class PostDetailActivity extends BaseActivity implements HeaderView.BackButtonClickListener,
        PostDetailPresenter.PostDetailView {

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;

    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.contact_avatar)
    CircleImageView contactAvatar;
    @BindView(R.id.contact_name)
    OpenSansBoldTextView contactName;
    @BindView(R.id.tv_date)
    OpenSansTextView tvDate;
    @BindView(R.id.img_more_actions)
    ImageView imgMoreActions;
    @BindView(R.id.contact_row)
    RelativeLayout contactRow;
    @BindView(R.id.image_box)
    ImageBoxLayout imageBox;
    @BindView(R.id.tv_message)
    OpenSansTextView tvMessage;
    @BindView(R.id.txt_view_more)
    OpenSansTextView txtViewMore;
    @BindView(R.id.rv_comments)
    RecyclerView rvComments;
    @BindView(R.id.divider_line)
    View dividerLine;
    @BindView(R.id.img_select_image)
    ImageView imgSelectImage;
    @BindView(R.id.img_emoji)
    ImageView imgEmoji;
    @BindView(R.id.img_send_message)
    ImageView imgSendMessage;
    @BindView(R.id.contentRoot)
    ViewGroup contentRoot;
    @BindView(R.id.edt_comment)
    EmojiconEditText edtComment;
    @BindView(R.id.img_selected)
    SquareImageView imgSelected;
    @BindView(R.id.img_delete_image)
    ImageView imgDeleteImage;
    @BindView(R.id.layout_selected_image)
    RelativeLayout layoutSelectedImage;
    @BindView(R.id.divider_comment)
    View dividerComment;
    @BindView(R.id.layout_post_comment)
    LinearLayout layoutPostComment;
    @BindView(R.id.layout_player_view)
    ViewGroup layoutPlayerView;
    @BindView(R.id.player_view)
    PlayerView playerView;
    @BindView(R.id.iv_post_mode)
    ImageView imgPostMode;

    private Post post;
    private CommentAdapter adapter;
    private PostDetailPresenter presenter;
    private String currentUserKey;

    private EmojIconActions emojIcon;

    private File selectedFile;
    private byte[] bytes;
    private PlayerManager playerManager;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private String postId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void initViews() {
        currentUserKey = LocalSharedPreferences.getInstance(this).getKeyUser();
        post = getIntent().getParcelableExtra(Constants.POST_KEY);
        presenter = new PostDetailPresenter(this, this);
        if (post == null) {
            postId = getIntent().getStringExtra(Constants.POST_ID_KEY);
            presenter.getPostBy(postId);
        } else {
            initCommentList();
            fillPostDetail();
        }

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        header.setCallback(this);
        header.setTittle(getString(R.string.MH99_006));
        emojIcon = new EmojIconActions(this, contentRoot, edtComment, imgEmoji);
        emojIcon.ShowEmojIcon();
        edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (selectedFile == null) {
                    setSendButtonVisible();
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void fillPostDetail() {
        Glide.with(this)
                .load(post.getAuthor().getAvatar())
                .error(R.drawable.img_default_avatar)
                .into(contactAvatar);

        contactName.setText(post.getAuthor().getName());
        tvMessage.setText(post.getMessage());
        tvDate.setText(DateTimeUtils.convertTimestamp(this, String.valueOf(post.getTimestamp())));
        txtViewMore.setVisibility(View.GONE);
        tvMessage.setMaxLines(Integer.MAX_VALUE);
        tvMessage.setEllipsize(null);
        bindPostPriority();

        if (post.getFileModels().size() == 0) {
            return;
        }

        if (post.getFileModels().get(0).getType().equals(FireBaseDataUtils.TYPE_IMAGE)) {
            imageBox.setVisibility(View.VISIBLE);
            imageBox.setUrls(getUrls(post.getFileModels()), selectedPos -> {
                // Show BaseImageUploadView images screen
                ImageDetailActivity.showActivity(this, post.getFileModels(), selectedPos);
            });
        } else {
            layoutPlayerView.setVisibility(View.VISIBLE);
            loadMedia();
        }
    }

    private void bindPostPriority() {
        if (Constants.PUBLIC_MODE.equals(post.getMode())) {
            imgPostMode.setImageResource(R.drawable.ic_public);
        } else if (Constants.FRIENDS_MODE.equals(post.getMode())) {
            imgPostMode.setImageResource(R.drawable.ic_friends);
        } else if (Constants.PRIVATE_MODE.equals(post.getMode())) {
            imgPostMode.setImageResource(R.drawable.ic_only_me);
        }
    }

    private void initCommentList() {
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.TIME_LINE)
                .child(FireBaseDataUtils.COMMENTS)
                .child(post.getId());

        FirebaseRecyclerOptions<Comment> options =
                new FirebaseRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment.class)
                        .build();

        adapter = new CommentAdapter(options, this);
        rvComments.setAdapter(adapter);
        rvComments.setNestedScrollingEnabled(false);
        adapter.startListening();
        adapter.setOnCommentClickListener(comment -> {
            List<FileModel> fileModels = new ArrayList<>();
            fileModels.add(comment.getFile());
            ImageDetailActivity.showActivity(this, fileModels, 0);
        });
    }

    private List<String> getUrls(List<FileModel> images) {
        List<String> urls = new ArrayList<>();
        for (FileModel file : images) {
            urls.add(file.getUrlFile());
        }
        return urls;
    }

    @OnClick({R.id.img_more_actions, R.id.img_select_image, R.id.img_send_message,
            R.id.img_delete_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_more_actions:
                showPopup(imgMoreActions);
                break;
            case R.id.img_select_image:
                presenter.requestReadSDCardPermission(this);
                break;
            case R.id.img_send_message:
                sendComment();
                break;
            case R.id.img_delete_image:
                bytes = null;
                selectedFile = null;
                updateCommentView(false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_GALLERY_REQUEST) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selectedFile = ImagesManager.getFileFromUri(this,
                        selectedImageUri);
                presenter.loadBitmapFormFile(selectedFile);
            }
        }
    }

    private void sendComment() {
        hideKeyboard();
        if (selectedFile != null) {
            presenter.uploadImageToFirebase(bytes, selectedFile);
        } else {
            presenter.comment(post, edtComment.getText().toString(), null);
        }
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @Override
    public void openCamera() {
        String photoName = String.valueOf(System.currentTimeMillis()) + "_" + currentUserKey;
        selectedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), photoName + "camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(this,
                Constants.IMAGE_CAMERA_AUTHORITY,
                selectedFile);
        it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Get Photo"), IMAGE_GALLERY_REQUEST);
    }

    @Override
    public void uploadFileSuccess(FileModel file) {
        presenter.comment(post, edtComment.getText().toString(), file);
    }

    @Override
    public void onLoadBitmap(byte[] bytes) {
        this.bytes = bytes;
        updateCommentView(true);
        setSendButtonVisible();
        loadSelectedImage();
    }

    @Override
    public void postCommentSuccess() {
        clearCommentView();
        setSendButtonVisible();
    }

    @Override
    public void onGetPostByIdSuccessful(Post post) {
        this.post = post;
        initCommentList();
        fillPostDetail();
    }

    @Override
    public void notAuthorizedPost() {
        showNotifyDialog(getString(R.string.TB_1083), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onOk(Object... obj) {
                finish();
            }
        });
    }

    private void loadSelectedImage() {
        Glide.with(this)
                .load(bytes)
                .asBitmap()
                .error(R.drawable.img_loading_default)
                .placeholder(R.drawable.img_loading_default)
                .dontAnimate()
                .into(imgSelected);
    }

    private void updateCommentView(boolean hasImage) {
        dividerComment.setVisibility(hasImage ? View.VISIBLE : View.GONE);
        layoutSelectedImage.setVisibility(hasImage ? View.VISIBLE : View.GONE);
        imgSelectImage.setEnabled(hasImage ? false : true);
    }

    private void setSendButtonVisible() {
        if (!edtComment.getText().toString().isEmpty() || selectedFile != null) {
            imgSendMessage.setVisibility(View.VISIBLE);
        } else {
            imgSendMessage.setVisibility(View.GONE);
        }
    }

    private void clearCommentView() {
        selectedFile = null;
        bytes = null;
        updateCommentView(false);
        edtComment.setText(null);
    }

    private void loadMedia() {
        layoutPlayerView.setVisibility(View.VISIBLE);
        if (playerManager == null) {
            playerManager = PlayerManager.getPlayerManagerInstance();
        }
        adjustPlayView(post.getFileModels().get(0).getType());
        playerManager.init(this, playerView, layoutPlayerView,
                post.getFileModels().get(0).getUrlFile(), null);
        playerManager.setPayWhenReady(false);
    }

    private void adjustPlayView(String type) {
        int pixels = (int) getResources().getDimension(type.equals(FireBaseDataUtils.TYPE_VIDEO)
                ? R.dimen.video_view_h : R.dimen.audio_view_h);

        ViewGroup.LayoutParams params = layoutPlayerView.getLayoutParams();
        params.height = pixels;
        layoutPlayerView.setLayoutParams(params);
    }

    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(post.getAuthor().getKey().equals(currentUserKey) ?
                R.menu.menu_post_options_owner : R.menu.menu_post_options_visitor);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.post_edit:
                    onEditPost();
                    break;
                case R.id.post_share_fb:
                    onShareFbPost();
                    break;
                case R.id.post_delete:
                    onDeletePost();
                    break;
            }
            return false;
        });
    }

    private void onEditPost() {
        EditPostActivity.startActivity(this, post);
    }

    private void onShareFbPost() {
        ShareLinkContent.Builder builder = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(post.getFileModels().get(0).getUrlFile()));

        if (!TextUtils.isEmpty(post.getMessage())) {
            builder.setQuote(post.getMessage());
        }

        ShareLinkContent content = builder.build();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(content);
        }
    }

    private void onDeletePost() {
        showNotifyDialog(getString(R.string.TB_1065), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                presenter.deletePost(post);
            }
        });
    }


    public static void startActivity(BaseActivity activity, Post post) {
        Intent intent = new Intent(activity, PostDetailActivity.class);
        intent.putExtra(Constants.POST_KEY, post);
        activity.startActivity(intent);
    }
}
