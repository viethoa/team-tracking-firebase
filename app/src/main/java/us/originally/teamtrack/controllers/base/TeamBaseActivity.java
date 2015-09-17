package us.originally.teamtrack.controllers.base;

import android.os.Bundle;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.lorem_ipsum.utils.AppUtils;
import com.lorem_ipsum.utils.StringUtils;

import javax.inject.Inject;

import us.originally.teamtrack.Constant;
import us.originally.teamtrack.customviews.VisualizerView;
import us.originally.teamtrack.managers.FireBaseManager;
import us.originally.teamtrack.models.AudioModel;
import us.originally.teamtrack.models.Comment;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.chat.audio.AudioStreamManager;

/**
 * Created by VietHoa on 14/09/15.
 */
public abstract class TeamBaseActivity extends MapBaseActivity implements
        VisualizerView.VisualizerListener {

    protected UserTeamModel mUser;
    protected TeamModel mTeam;
    
    @Inject
    FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setAppState(false);

        extractTeamAndUserModel();
        initialiseTeamUser();
        initialiseTeamMessage();
        initialiseAudio();
    }

    /**
     * This is necessary to receive "TeamModel and UserTeamModel" from extract data.
     */
    protected abstract void extractTeamAndUserModel();

    //----------------------------------------------------------------------------------------------
    // Messages Section
    //----------------------------------------------------------------------------------------------

    protected abstract void onShowComment(Comment message);

    protected void initialiseTeamMessage() {
        if (mTeam == null || StringUtils.isNull(mTeam.team_name))
            return;

        Firebase ref = fireBaseManager.getFireBaseRef();
        if (ref == null)
            return;

        ref.child(Constant.TEAM_GROUP).child(mTeam.team_name).child(Constant.SLUG_MESSAGE)
                .addChildEventListener(new MessagesEventListener());
    }

    protected void onMessagePushed(DataSnapshot dataSnapshot) {
        Comment comment = null;
        try {
            comment = dataSnapshot.getValue(Comment.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (comment == null)
            return;

        onShowComment(comment);
    }

    public class MessagesEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            logDebug("User message: " + dataSnapshot.getValue());
            onMessagePushed(dataSnapshot);
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

    //----------------------------------------------------------------------------------------------
    // Users Section
    //----------------------------------------------------------------------------------------------

    protected abstract void onUserSubscribed(UserTeamModel user);
    protected abstract void onUserInfoChanged(UserTeamModel user);
    protected abstract void onUserMoveOut(UserTeamModel user);

    protected void initialiseTeamUser() {
        if (mTeam == null || StringUtils.isNull(mTeam.team_name))
            return;

        Firebase ref = fireBaseManager.getFireBaseRef();
        if (ref == null)
            return;

        ref.child(Constant.TEAM_GROUP).child(mTeam.team_name).child(Constant.SLUG_USERS)
                .addChildEventListener(new UsersEventListener());
    }

    protected void onUserSubscribed(DataSnapshot dataSnapshot) {
        UserTeamModel user = null;
        try {
            user = dataSnapshot.getValue(UserTeamModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user == null)
            return;

        onUserSubscribed(user);
    }

    protected void onUserInfoChanged(DataSnapshot dataSnapshot) {
        UserTeamModel user = null;
        try {
            user = dataSnapshot.getValue(UserTeamModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user == null)
            return;

        onUserInfoChanged(user);
    }

    protected void onUserMoveOut(DataSnapshot dataSnapshot) {
        UserTeamModel user = null;
        try {
            user = dataSnapshot.getValue(UserTeamModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user == null)
            return;

        onUserMoveOut(user);
    }

    public class UsersEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            logDebug("user add: " + dataSnapshot.getValue());
            onUserSubscribed(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            logDebug("user change: " + dataSnapshot.getValue());
            onUserInfoChanged(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            logDebug("user change: " + dataSnapshot.getValue());
            onUserMoveOut(dataSnapshot);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    //----------------------------------------------------------------------------------------------
    // Audio Section
    //----------------------------------------------------------------------------------------------

    protected void initialiseAudio() {
        if (mTeam == null || StringUtils.isNull(mTeam.team_name))
            return;

        Firebase ref = fireBaseManager.getFireBaseRef();
        if (ref == null)
            return;

        ref.child(Constant.TEAM_GROUP).child(mTeam.team_name).child(Constant.SLUG_AUDIOS)
                .addChildEventListener(new AudioEventListener());
    }

    protected boolean onReceiveAudio(DataSnapshot dataSnapshot) {
        AudioModel audio = null;
        try {
            audio = dataSnapshot.getValue(AudioModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (audio == null)
            return false;

        //Do not play audio by myself
        if (mUser != null && StringUtils.isNotNull(mUser.device_uuid) &&
                audio.user != null && StringUtils.isNotNull(audio.user.device_uuid) &&
                audio.user.device_uuid.equals(mUser.device_uuid))
            return true;

        AudioStreamManager.startPlaying(audio);
        return true;
    }

    public class AudioEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            logDebug("user taking: " + dataSnapshot.getValue());
            onReceiveAudio(dataSnapshot);
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
