package us.originally.teamtrack.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.firebase.client.Firebase;

import javax.inject.Inject;

import dagger.ObjectGraph;
import us.originally.teamtrack.TeamTrackApplication;
import us.originally.teamtrack.managers.AudioPlayerManage;
import us.originally.teamtrack.managers.FireBaseManager;
import us.originally.teamtrack.modules.dagger.RequestModule;

/**
 * Created by VietHoa on 07/09/15.
 */
public class AudioService extends Service {

    @Inject
    FireBaseManager fireBaseManager;
    @Inject
    AudioPlayerManage audioPlayerManage;

    @Override
    public void onCreate() {
        TeamTrackApplication application = (TeamTrackApplication) getApplication();
        if (application != null) {
            ObjectGraph graph = application.extendScope(new RequestModule());
            graph.inject(this);
        }

        Firebase ref = fireBaseManager.getFireBaseRef();
        if (ref == null)
            return;

        audioPlayerManage.registerEventListener(ref);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
