package us.originally.teamtrack.controllers.base;

import android.os.Bundle;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.lorem_ipsum.utils.StringUtils;

import us.originally.teamtrack.Constant;
import us.originally.teamtrack.models.Comment;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.firebase.FireBaseAction;

/**
 * Created by VietHoa on 14/09/15.
 */
public abstract class TeamBaseActivity extends MapBaseActivity {

    protected UserTeamModel mUser;
    protected TeamModel mTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialiseTeamData();
        initialiseTeamUser();
        initialiseTeamMessage();
    }

    protected abstract void initialiseTeamData();

    //----------------------------------------------------------------------------------------------
    // Messages helper
    //----------------------------------------------------------------------------------------------

    protected abstract void onShowComment(Comment message);

    protected void initialiseTeamMessage() {
        if (mTeam == null || StringUtils.isNull(mTeam.team_name))
            return;

        Firebase ref = FireBaseAction.getFirebaseRef(this);
        if (ref == null)
            return;

        ref.child(BaseLoginActivity.TEAM_GROUP).child(mTeam.team_name).child(Constant.SLUG_MESSAGE)
                .addChildEventListener(new MessagesEventListener());
    }

    protected void pushComment(Comment comment) {
        if (comment == null || mTeam == null || StringUtils.isNull(mTeam.team_name))
            return;

        Firebase ref = FireBaseAction.getFirebaseRef(this);
        if (ref == null)
            return;

        ref.child(BaseLoginActivity.TEAM_GROUP).child(mTeam.team_name).child(Constant.SLUG_MESSAGE)
                .child(comment.user.device_uuid).setValue(comment);
    }

    protected void onMessagePushed(DataSnapshot dataSnapshot) {
        Comment comment = null;
        try {
            comment = dataSnapshot.getValue(Comment.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if (comment == null)
            return;

        onShowComment(comment);
    }

    public class MessagesEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            logDebug("message: " + dataSnapshot.getValue());
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
    // Users helper
    //----------------------------------------------------------------------------------------------

    protected abstract void onTeamUserJoined(UserTeamModel user);

    protected void initialiseTeamUser() {
        if (mTeam == null || StringUtils.isNull(mTeam.team_name))
            return;

        Firebase ref = FireBaseAction.getFirebaseRef(this);
        if (ref == null)
            return;

        ref.child(BaseLoginActivity.TEAM_GROUP).child(mTeam.team_name).child(Constant.SLUG_USERS)
                .addChildEventListener(new UsersEventListener());
    }

    protected void onUserSubscribed(DataSnapshot dataSnapshot) {
        UserTeamModel user = null;
        try {
            user = dataSnapshot.getValue(UserTeamModel.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if (user == null)
            return;

        onTeamUserJoined(user);
    }

    public class UsersEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            logDebug("team: " + dataSnapshot.getValue());
            onUserSubscribed(dataSnapshot);
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
