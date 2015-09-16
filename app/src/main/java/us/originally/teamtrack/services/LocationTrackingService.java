package us.originally.teamtrack.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import us.originally.teamtrack.TeamTrackApplication;
import us.originally.teamtrack.controllers.MainActivity;
import us.originally.teamtrack.controllers.base.TeamBaseActivity;
import us.originally.teamtrack.managers.GPSTrackerManager;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;

/**
 * Created by VietHoa on 16/09/15.
 */
public class LocationTrackingService extends Service {

    private static final String TAG = "TrackingLocationService";
    private static final int TRACKING_TIME = 5000;

    private static double latitude = 0;
    private static double longitude = 0;
    private static TeamModel mTeam = null;
    private static UserTeamModel mUser = null;

    private static Context mContext;
    private static GPSTrackerManager mGpsTracker;

    @Override
    public void onCreate() {
        mContext = TeamTrackApplication.getAppContext();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mTeam == null && intent != null)
            mTeam = (TeamModel) intent.getSerializableExtra(MainActivity.EXTRACT_TEAM);
        if (mUser == null && intent != null)
            mUser = (UserTeamModel) intent.getSerializableExtra(MainActivity.EXTRACT_USER);

        startTracking();
        return START_STICKY;
    }

    protected void startTracking() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                trackingLocation();
                startTracking();
            }
        }, TRACKING_TIME);
    }

    protected void trackingLocation() {
        if (mTeam == null || mUser == null)
            return;

        Log.d(TAG, "Location service tracker");
        if (mGpsTracker == null)
            mGpsTracker = new GPSTrackerManager(mContext);
        if (!mGpsTracker.canGetLocation())
            return;

        double newLat = mGpsTracker.getLatitude();
        double newLon = mGpsTracker.getLongitude();
        double latDistance = newLat - latitude;
        double lonDistance = newLon - longitude;
        Log.d(TAG, String.valueOf(newLat));
        Log.d(TAG, String.valueOf(newLon));

        if (latDistance >= 0.001 || lonDistance >= 0.001) {
            latitude = newLat;
            longitude = newLon;
            TeamBaseActivity.onChangeUserInfoOrState(mContext, mTeam, mUser);
        }
    }
}
