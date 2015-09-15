package us.originally.teamtrack.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;

import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.R;
import us.originally.teamtrack.controllers.base.TeamBaseActivity;
import us.originally.teamtrack.customviews.ChattingView;
import us.originally.teamtrack.customviews.VisualizerView;
import us.originally.teamtrack.managers.GPSTrackerManager;
import us.originally.teamtrack.models.Comment;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;

public class MainActivity extends TeamBaseActivity implements
        GPSTrackerManager.GPSListener, ChattingView.ChattingViewListener {

    private static final String EXTRACT_TEAM = "extrac-team";
    private static final String EXTRACT_USER = "extrac-user";
    protected static final int NOTIFY_COMMENT_DELAY = 3000;
    protected static final int DURATION = 300;
    protected static int CommentId = 0;

    @InjectView(R.id.tv_app_title)
    TextView tvTitle;
    @InjectView(R.id.visualizer)
    VisualizerView mVisualiser;
    @InjectView(R.id.chatting_box)
    ChattingView mChattingBox;
    @InjectView(R.id.ll_comment_notify)
    View mNotifyCommentBox;
    @InjectView(R.id.tv_user_name)
    TextView tvUserName;
    @InjectView(R.id.tv_user_comment)
    TextView tvUserComment;
    @InjectView(R.id.ll_footer)
    View mFooter;

    public static Intent getInstance(Context context, TeamModel teamModel, UserTeamModel user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRACT_TEAM, teamModel);
        intent.putExtra(EXTRACT_USER, user);
        return intent;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialiseData();
        initialiseUI();
    }

    @Override
    public void onResume() {
        if (mUser != null) {
            mUser.state = true;
            onChangeUserInfoOrState(mUser);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mUser != null) {
            mUser.state = false;
            onChangeUserInfoOrState(mUser);
        }
        super.onBackPressed();
    }

    //----------------------------------------------------------------------------------------------
    // Setup
    //----------------------------------------------------------------------------------------------

    @Override
    protected void initialiseTeamData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;

        mUser = (UserTeamModel) bundle.getSerializable(EXTRACT_USER);
        mTeam = (TeamModel) bundle.getSerializable(EXTRACT_TEAM);
    }

    protected void initialiseData() {
        if (mUser != null) {
            showLocationWithCamera(mUser.lat, mUser.lng);
        }

        if (mTeam != null) {
            tvTitle.setText(mTeam.team_name);
        }
    }

    protected void initialiseUI() {

        //Chatting box
        mChattingBox.setOnChattingListener(this);
        float height = DeviceUtils.getDeviceScreenHeight(this);
        mChattingBox.setTranslationY(height);

        //Notify message box
        float width = DeviceUtils.getDeviceScreenWidth(this);
        mNotifyCommentBox.setTranslationX(width);
        mNotifyCommentBox.setAlpha(0f);
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.ll_comment_notify)
    protected void onNotifyCommentBoxClicked() {
        onBtnOpenChatBoxClicked();
    }

    @OnClick(R.id.btn_open_chat_box)
    protected void onBtnOpenChatBoxClicked() {
        float footerHeight = mFooter.getHeight();
        mFooter.animate()
                .translationY(footerHeight)
                .setDuration(DURATION).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mVisualiser.setVisibility(View.GONE);
            }
        }, DURATION);

        mChattingBox.animate()
                .translationY(0)
                .setDuration(DURATION).start();
    }

    @Override
    public void onCloseChatBox() {
        mVisualiser.setVisibility(View.VISIBLE);
        mFooter.animate()
                .translationY(0)
                .setDuration(DURATION).start();

        float height = DeviceUtils.getDeviceScreenHeight(this);
        mChattingBox.animate()
                .translationY(height)
                .setDuration(DURATION).start();

        DeviceUtils.hideKeyboard(this);
    }

    @Override
    public void onLocationChange(double lat, double lng) {
        showLocationWithCamera(lat, lng);
    }

    //----------------------------------------------------------------------------------------------
    // Team User section
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onUserSubscribed(UserTeamModel user) {
        if (user == null)
            return;

        //Do not do by myself
        if (mUser != null && StringUtils.isNotNull(mUser.device_uuid) && StringUtils.isNotNull(user.device_uuid)
                && mUser.device_uuid.equals(user.device_uuid))
            return;

        addUserToMapWithNoneCamera(user);
    }

    @Override
    protected void onUserUnSubscribed(UserTeamModel user) {
        if (user == null)
            return;

        takeUserOnMapOfflineOrOnline(user);
    }

    //----------------------------------------------------------------------------------------------
    // Team Message section
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onShowComment(Comment comment) {
        if (comment == null || StringUtils.isNull(comment.message))
            return;

        if (comment.id > CommentId) {
            CommentId = comment.id;
        }
        mChattingBox.pushComment(comment);

        //Notify comment in chat box closed
        float translateY = mChattingBox.getTranslationY();
        if (translateY == 0)
            return;
        takeNotifyComment(comment);
    }

    @Override
    public void onPushComment(String comment) {
        if (StringUtils.isNull(comment) || mUser == null)
            return;

        CommentId += 1;
        long timeStamp = System.currentTimeMillis();
        Comment userComment = new Comment(CommentId, timeStamp, comment, mUser);
        pushComment(userComment);
    }

    protected void takeNotifyComment(Comment comment) {
        if (comment == null)
            return;

        if (comment.user != null && StringUtils.isNotNull(comment.user.name))
            tvUserName.setText(comment.user.name + ":");

        if (comment.user != null && StringUtils.isNotNull(comment.message))
            tvUserComment.setText(comment.message);

        final float screenWidth = DeviceUtils.getDeviceScreenWidth(this);
        mNotifyCommentBox.animate().translationX(0f).start();
        mNotifyCommentBox.animate().alpha(1f).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNotifyCommentBox.animate().translationX(screenWidth).start();
                mNotifyCommentBox.animate().alpha(0f).start();
            }
        }, NOTIFY_COMMENT_DELAY);
    }
}
