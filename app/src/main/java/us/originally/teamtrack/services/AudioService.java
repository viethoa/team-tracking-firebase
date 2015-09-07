package us.originally.teamtrack.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import us.originally.teamtrack.TeamTrackApplication;
import us.originally.teamtrack.modules.firebase.FireBaseAction;

/**
 * Created by VietHoa on 07/09/15.
 */
public class AudioService extends Service {

    private static final String AUDIO_CHANNEL = "audio_channel";
    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = TeamTrackApplication.getAppContext();
        FireBaseAction.registerEventListener(mContext, AUDIO_CHANNEL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
