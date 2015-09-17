package us.originally.teamtrack.controllers;

import android.os.Bundle;
import android.view.View;

import com.lorem_ipsum.managers.CacheManager;
import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.Constant;
import us.originally.teamtrack.R;
import us.originally.teamtrack.controllers.base.MapBaseActivity;
import us.originally.teamtrack.managers.GPSTrackerManager;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.dagger.callback.CallbackListener;
import us.originally.teamtrack.modules.dagger.managers.UserManager;

public class LandingActivity extends MapBaseActivity implements GPSTrackerManager.GPSListener {

    protected GPSTrackerManager mGpsTracker;
    private double lat, lng = 0;

    @InjectView(R.id.btn_add_team)
    View vAddNewTeam;

    @Inject
    UserManager userManager;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_landing);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialiseGPS();
        autoLoginLogic();
    }

    //----------------------------------------------------------------------------------------------
    //  Setup
    //----------------------------------------------------------------------------------------------

    protected void autoLoginLogic() {
        String teamName = CacheManager.getStringCacheData(Constant.CACHE_TEAM_KEY);
        String userName = CacheManager.getStringCacheData(Constant.CACHE_USER_NAME_KEY);
        String password = CacheManager.getStringCacheData(Constant.CACHE_USER_PASSWORD_KEY);
        if (StringUtils.isNull(teamName) || StringUtils.isNull(userName) || StringUtils.isNull(password))
            return;

        if (lat == 0 || lng == 0)
            return;

        String id = DeviceUtils.getDeviceUUID(this);
        long timeStamp = System.currentTimeMillis();
        final UserTeamModel user = new UserTeamModel(id, userName, lat, lng);
        final TeamModel teamModel = new TeamModel(teamName, password, timeStamp);

        showLoadingDialog();
        userManager.loginOrSubscribe(teamModel, user, new LoginOrSubscribeCallback(teamModel, user));
    }

    //----------------------------------------------------------------------------------------------
    //  Event
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.btn_add_team)
    protected void onAddNewTeamClicked() {
        startActivity(LoginActivity.getInstance(this, lat, lng));
    }

    //----------------------------------------------------------------------------------------------
    //  GPS Helper
    //----------------------------------------------------------------------------------------------

    protected void initialiseGPS() {
        mGpsTracker = new GPSTrackerManager(this);
        mGpsTracker.setOnGPSListener(this);
        if (!mGpsTracker.canGetLocation()) {
            mGpsTracker.showSettingsAlert(this);
        } else {
            lat = mGpsTracker.getLatitude();
            lng = mGpsTracker.getLongitude();
            showLocationWithCamera(lat, lng);
        }
    }

    @Override
    public void onLocationChange(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        showLocationWithCamera(lat, lng);
    }

    //----------------------------------------------------------------------------------------------
    //  Api Helper
    //----------------------------------------------------------------------------------------------

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
    }
}
