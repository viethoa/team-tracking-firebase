package us.originally.teamtrack.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;

import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.Constant;
import us.originally.teamtrack.R;
import us.originally.teamtrack.controllers.base.BaseLoginActivity;
import us.originally.teamtrack.controllers.base.MapBaseActivity;
import us.originally.teamtrack.customviews.ChattingView;
import us.originally.teamtrack.customviews.VisualizerView;
import us.originally.teamtrack.managers.GPSTrackerManager;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.firebase.FireBaseAction;

public class MainActivity extends MapBaseActivity implements GPSTrackerManager.GPSListener,
        ChattingView.ChattingViewListener {

    private static final String EXTRACT_TEAM = "extrac-team";
    private static final String EXTRACT_USER = "extrac-user";

    private static final int DURATION = 300;
    private UserTeamModel mUser;
    private TeamModel mTeam;

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

    protected void initialiseData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;

        //Show my location
        mUser = (UserTeamModel) bundle.getSerializable(EXTRACT_USER);
        if (mUser == null)
            return;
        showLocationWithCamera(mUser.lat, mUser.lng);

        //Show my team location
        mTeam = (TeamModel) bundle.getSerializable(EXTRACT_TEAM);
        if (mTeam == null || StringUtils.isNull(mTeam.team_name))
            return;

        Firebase ref = FireBaseAction.getFirebaseRef(this);
        if (ref == null)
            return;

        ref.child(BaseLoginActivity.TEAM_GROUP).child(mTeam.team_name).child(Constant.SLUG_USERS)
                .addChildEventListener(new TeamChildListener());
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
        mVisualiser.setVisibility(View.GONE);

        float footerHeight = mFooter.getHeight();
        mFooter.animate()
                .translationY(footerHeight)
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
    // FireBase helper
    //----------------------------------------------------------------------------------------------

    protected class TeamChildListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            System.out.println(dataSnapshot.getValue());
            UserTeamModel user = null;
            try {
                user = dataSnapshot.getValue(UserTeamModel.class);
            } catch(Exception e) {
                e.printStackTrace();
            }
            if (user == null)
                return;

            showLocationNoneComaera(user.lat, user.lng);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }
}
