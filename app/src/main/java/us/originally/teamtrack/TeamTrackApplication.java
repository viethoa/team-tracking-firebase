package us.originally.teamtrack;

import com.lorem_ipsum.utils.AppUtils;
import com.lorem_ipsum.utils.DeviceUtils;
import com.parse.Parse;
import com.parse.ParseInstallation;

import dagger.ObjectGraph;
import us.originally.teamtrack.modules.dagger.AppModule;

/**
 * Created by VietHoa on 04/09/15.
 */
public class TeamTrackApplication extends AppUtils {

    protected ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        initialiseParse();
        initialiseGraph();
    }

    protected void initialiseParse() {
        try {
            final String device_uuid = DeviceUtils.getDeviceUUID(getAppContext());
            Parse.enableLocalDatastore(this);
            Parse.initialize(getAppContext(), getString(R.string.parse_app_id), getString(R.string.parse_client_key));
            ParseInstallation currentInstall = ParseInstallation.getCurrentInstallation();
            currentInstall.put("device_uuid", device_uuid);
            currentInstall.saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initialiseGraph() {
        objectGraph = ObjectGraph.create(new AppModule(this));
    }

    public ObjectGraph extendScope(Object... modules) {
        if (modules != null) {
            return objectGraph.plus(modules);
        } else {
            return objectGraph;
        }
    }


}
