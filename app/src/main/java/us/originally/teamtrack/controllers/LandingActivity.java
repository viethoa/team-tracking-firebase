package us.originally.teamtrack.controllers;

import android.os.Bundle;
import android.view.View;

import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.R;
import us.originally.teamtrack.controllers.base.MapBaseActivity;
import us.originally.teamtrack.managers.GPSTrackerManager;

public class LandingActivity extends MapBaseActivity implements GPSTrackerManager.GPSListener {

    protected GPSTrackerManager mGpsTracker;

    @InjectView(R.id.btn_add_team)
    View vAddNewTeam;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_landing);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialiseGPS();
    }

    @OnClick(R.id.btn_add_team)
    protected void onAddNewTeamClicked() {
        startActivity(LoginActivity.getInstance(this));
    }

    //----------------------------------------------------------------------------------------------
    //  GPS Helper
    //----------------------------------------------------------------------------------------------

    protected void initialiseGPS() {
        mGpsTracker = new GPSTrackerManager(this, getFragmentManager());
        if (!mGpsTracker.canGetLocation()) {
            mGpsTracker.showSettingsAlert();
        } else {
            double lat = mGpsTracker.getLatitude();
            double lng = mGpsTracker.getLongitude();
            takeLocationWithCamera(lat, lng);
        }
    }

    @Override
    public void onLocationChange(double lat, double lng) {
        takeLocationWithCamera(lat, lng);
    }
}
