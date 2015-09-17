package us.originally.teamtrack.controllers.base;

import com.lorem_ipsum.managers.CacheManager;

import us.originally.teamtrack.Constant;
import us.originally.teamtrack.controllers.MainActivity;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.dagger.callback.CallbackListener;

/**
 * Created by VietHoa on 09/09/15.
 */
public abstract class BaseLoginActivity extends BaseGraphActivity {

    protected class LoginOrSubscribeCallback implements CallbackListener<UserTeamModel> {

        private TeamModel teamModel;
        private UserTeamModel user;

        public LoginOrSubscribeCallback(TeamModel teamModel, UserTeamModel user) {
            this.teamModel = teamModel;
            this.user = user;
        }

        @Override
        public void onDone(UserTeamModel response, Exception exception) {
            dismissLoadingDialog();
            if (exception != null) {
                showToastErrorMessage(exception.getMessage());
                return;
            }

            goToNextScreen(teamModel, user);
        }
    }

    protected void goToNextScreen(TeamModel teamModel, UserTeamModel user) {
        if (teamModel == null || user == null)
            return;

        CacheManager.saveStringCacheData(Constant.CACHE_TEAM_KEY, teamModel.team_name);
        CacheManager.saveStringCacheData(Constant.CACHE_USER_NAME_KEY, user.name);
        CacheManager.saveStringCacheData(Constant.CACHE_USER_PASSWORD_KEY, teamModel.password);
        startActivity(MainActivity.getInstance(this, teamModel, user));
        finish();
    }
}
