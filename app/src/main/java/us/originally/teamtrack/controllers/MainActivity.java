package us.originally.teamtrack.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    protected static final int DURATION = 300;
    protected static int CommentId = 0;

    @InjectView(R.id.tv_app_title)
    TextView tvTitle;
    @InjectView(R.id.visualizer)
    VisualizerView mVisualiser;
    @InjectView(R.id.chatting_box)
    ChattingView mChattingBox;
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

    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.btn_open_chat_box)
    protected void onBtnOpenChatBoxClicked() {
        float footerHeight = mFooter.getHeight();
        mFooter.animate()
                .translationY(footerHeight)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mVisualiser.setVisibility(View.GONE);
                    }
                })
                .setDuration(DURATION).start();

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
    }

    @Override
    public void onLocationChange(double lat, double lng) {
        showLocationWithCamera(lat, lng);
    }

    //----------------------------------------------------------------------------------------------
    // Team User section
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onTeamUserJoined(UserTeamModel user) {
        showLocationNoneCamera(user);
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
}
