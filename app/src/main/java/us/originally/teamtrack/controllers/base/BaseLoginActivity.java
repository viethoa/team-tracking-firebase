package us.originally.teamtrack.controllers.base;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lorem_ipsum.activities.BaseActivity;
import com.lorem_ipsum.utils.StringUtils;

import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.firebase.FireBaseAction;

/**
 * Created by VietHoa on 09/09/15.
 */
public abstract class BaseLoginActivity extends BaseActivity {

    public static final String TEAM_GROUP = "Team_Group";
    protected TeamValueListener mTeamValueListener;

    protected abstract void goToNextScreen(TeamModel teamModel, UserTeamModel user);

    //----------------------------------------------------------------------------------------------
    //  Event
    //----------------------------------------------------------------------------------------------

    protected void takeLoginOrSubscribe(final TeamModel teamModel, final UserTeamModel user) {
        if (teamModel == null || user == null || StringUtils.isNull(teamModel.team_name)) {
            dismissLoadingDialog();
            return;
        }

        Firebase ref = FireBaseAction.getFirebaseRef(this);
        if (ref == null) {
            dismissLoadingDialog();
            return;
        }

        ref = ref.child(TEAM_GROUP).child(teamModel.team_name);
        mTeamValueListener = new TeamValueListener(ref, teamModel, user);
        ref.addValueEventListener(mTeamValueListener);
    }

    protected void createNewTeam(TeamModel teamModel, UserTeamModel user) {
        if (teamModel == null || user == null)
            return;

        FireBaseAction.addNewTeam(this, teamModel, user);
        goToNextScreen(teamModel, user);
        super.finish();
    }

    protected void subscribe(String password, TeamModel teamModel, UserTeamModel user) {
        if (teamModel == null || user == null)
            return;

        if (StringUtils.isNull(password) || StringUtils.isNull(teamModel.password) ||
                !teamModel.password.equals(password)) {
            showToastErrorMessage("Invalid password !");
            return;
        }

        FireBaseAction.addNewUserToTeam(this, teamModel, user);
        goToNextScreen(teamModel, user);
        super.finish();
    }

    protected void removeLoginValueListener(Firebase ref) {
        if (mTeamValueListener == null)
            return;

        ref.removeEventListener(mTeamValueListener);
        mTeamValueListener = null;
    }

    //----------------------------------------------------------------------------------------------
    //  Api helper
    //----------------------------------------------------------------------------------------------

    protected class TeamValueListener implements ValueEventListener {

        private Firebase ref;
        private TeamModel teamModel;
        private UserTeamModel user;

        public TeamValueListener(Firebase ref, TeamModel teamModel, UserTeamModel user) {
            this.teamModel = teamModel;
            this.user = user;
            this.ref = ref;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            logDebug("Team: " + dataSnapshot.toString());
            removeLoginValueListener(ref);
            dismissLoadingDialog();

            System.out.println(dataSnapshot.getValue());
            if (dataSnapshot.getValue() == null) {
                createNewTeam(teamModel, user);
                return;
            }

            DataSnapshot passwordSnapshot = dataSnapshot.child("password");
            String teamPassword = passwordSnapshot.getValue(String.class);
            logDebug("password: " + teamPassword);
            subscribe(teamPassword, teamModel, user);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            dismissLoadingDialog();
            logError("Team error: " + firebaseError.getMessage());
        }
    }
}
