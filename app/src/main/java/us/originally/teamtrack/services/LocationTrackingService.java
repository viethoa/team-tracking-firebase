package us.originally.teamtrack.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import javax.inject.Inject;

import dagger.ObjectGraph;
import us.originally.teamtrack.TeamTrackApplication;
import us.originally.teamtrack.controllers.MainActivity;
import us.originally.teamtrack.managers.GPSTrackerManager;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.dagger.RequestModule;
import us.originally.teamtrack.modules.dagger.managers.UserManager;

/**
 * Created by VietHoa on 16/09/15.
 */
public class LocationTrackingService extends Service {

    private static final String TAG = "TrackingLocationService";
    private static final int TRACKING_TIME = 5000;
    private static final double DISTANCE_TRACKING = 1f;

    private static TeamModel mTeam = null;
    private static UserTeamModel mUser = null;

    @Inject
    UserManager userManager;

    @Override
    public void onCreate() {
        TeamTrackApplication application = (TeamTrackApplication) getApplication();
        if (application != null) {
            ObjectGraph graph = application.extendScope(new RequestModule());
            graph.inject(this);
        }
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
        GPSTrackerManager mGpsTracker = new GPSTrackerManager(getApplicationContext());
        if (!mGpsTracker.canGetLocation())
            return;

        double newLat = mGpsTracker.getLatitude();
        double newLon = mGpsTracker.getLongitude();
        float[] results = new float[1];

        Location.distanceBetween(mUser.lat, mUser.lng, newLat, newLon, results);
        double distance = results[0];

        Log.d(TAG, String.valueOf(distance));
        if (distance > DISTANCE_TRACKING) {
            Log.d(TAG, String.valueOf(newLat));
            Log.d(TAG, String.valueOf(newLon));

            mUser.lat = newLat;
            mUser.lng = newLon;
            userManager.updateUser(mTeam, mUser);
        }
    }
}
