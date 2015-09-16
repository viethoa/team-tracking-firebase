package us.originally.teamtrack.controllers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;
import com.viethoa.DialogUtils;

import java.io.IOException;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import us.originally.teamtrack.EventBus.VisualizeEvent;
import us.originally.teamtrack.R;
import us.originally.teamtrack.controllers.base.TeamBaseActivity;
import us.originally.teamtrack.customviews.ChattingView;
import us.originally.teamtrack.customviews.VisualizerView;
import us.originally.teamtrack.managers.GPSTrackerManager;
import us.originally.teamtrack.models.Comment;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.chat.audio.AudioStreamManager;
import us.originally.teamtrack.services.LocationTrackingService;

public class MainActivity extends TeamBaseActivity implements
        GPSTrackerManager.GPSListener, ChattingView.ChattingViewListener {

    public static final String EXTRACT_TEAM = "extrac-team";
    public static final String EXTRACT_USER = "extrac-user";
    protected static final int NOTIFY_COMMENT_DELAY = 3000;
    protected static final int DURATION = 300;
    protected static int CommentId = 0;

    protected GPSTrackerManager mGpsTracker;
    protected EventBus eventBus;

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
    protected void onStart() {
        super.onStart();
        eventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        requestLogOut();
    }

    //----------------------------------------------------------------------------------------------
    // Setup
    //----------------------------------------------------------------------------------------------

    @Override
    protected void extractTeamAndUserModel() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;

        //Extract data
        mUser = (UserTeamModel) bundle.getSerializable(EXTRACT_USER);
        mTeam = (TeamModel) bundle.getSerializable(EXTRACT_TEAM);
        if (mUser == null || mTeam == null) {
            finish();
            return;
        }

        //Set user position again
        mGpsTracker = new GPSTrackerManager(this);
        mGpsTracker.setOnGPSListener(this);
        if (mGpsTracker.canGetLocation()) {
            mUser.lat = mGpsTracker.getLatitude();
            mUser.lng = mGpsTracker.getLongitude();
        }

        //Start tracking location service
        Intent intent = new Intent(this, LocationTrackingService.class);
        intent.putExtra(EXTRACT_TEAM, mTeam);
        intent.putExtra(EXTRACT_USER, mUser);
        startService(intent);
    }

    protected void initialiseData() {
        if (mUser != null)
            showLocationWithCamera(mUser.lat, mUser.lng);
        if (mTeam != null)
            tvTitle.setText(mTeam.team_name);
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

        //Waveform
        mVisualiser.setOnSpeakListener(this);
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
        if (mUser == null || mTeam == null)
            return;

        mUser.lat = lat;
        mUser.lng = lng;
    }

    protected void requestLogOut() {
        String title = getString(R.string.str_logout_request);
        String message = getString(R.string.str_logout_question);
        String negativeButton = getString(R.string.dialog_cancel);
        String positiveButton = getString(R.string.dialog_ok);
        final Dialog gpsSettingDialog = DialogUtils.createDialogMessage(this, title,
                message, negativeButton, positiveButton, false, new DialogUtils.DialogListener() {
                    @Override
                    public void onPositiveButton() {
                        performLogout();
                        finish();
                    }

                    @Override
                    public void onNegativeButton() {

                    }
                });

        if (gpsSettingDialog != null && !gpsSettingDialog.isShowing()) {
            gpsSettingDialog.show();
        }
    }

    protected void performLogout() {
        removeUserFromTeam(mUser);
    }

    protected boolean validUser(UserTeamModel user) {
        if (user == null || StringUtils.isNull(user.device_uuid))
            return false;

        if (mUser == null || StringUtils.isNull(mUser.device_uuid))
            return false;

        return mUser.device_uuid.equals(user.device_uuid);
    }

    //----------------------------------------------------------------------------------------------
    // Team User section
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onUserSubscribed(UserTeamModel user) {
        if (user == null || validUser(user))
            return;

        addUserToMapWithNoneCamera(user);
    }

    @Override
    protected void onUserInfoChanged(UserTeamModel user) {
        if (user == null || validUser(user))
            return;

        changeUserLocation(user);
    }

    @Override
    protected void onUserMoveOut(UserTeamModel user) {
        if (user == null || validUser(user))
            return;

        removeUserFromMap(user);
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
        if (mChattingBox.getTranslationY() == 0f || validUser(comment.user))
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

        //Take sound
        try {
            AssetFileDescriptor afd = getAssets().openFd("sounds/comment_notify_sound.mp3");
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Take notify box
        final float screenWidth = DeviceUtils.getDeviceScreenWidth(this);
        mNotifyCommentBox.animate().translationX(0f).start();
        mNotifyCommentBox.animate().alpha(1f).start();

        //Hidden notify box
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNotifyCommentBox.animate().translationX(screenWidth).start();
                mNotifyCommentBox.animate().alpha(0f).start();
            }
        }, NOTIFY_COMMENT_DELAY);
    }

    //----------------------------------------------------------------------------------------------
    // Team audio section
    //----------------------------------------------------------------------------------------------

    public void onEventMainThread(VisualizeEvent event) {
        if (event == null)
            return;

        logDebug("sound value: " + event.getSoudValue());
        mVisualiser.OnSpeaking(event.getSoudValue());
    }

    @Override
    public void onEntered() {
        if (AudioStreamManager.isRecording() || mTeam == null || mUser == null)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                AudioStreamManager.startRecording(MainActivity.this, mTeam, mUser);
            }
        }).start();
    }

    @Override
    public void onLeaved() {
        AudioStreamManager.stopRecording();
    }
}
