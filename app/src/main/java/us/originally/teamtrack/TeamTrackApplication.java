package us.originally.teamtrack;

import com.lorem_ipsum.utils.AppUtils;
import com.lorem_ipsum.utils.DeviceUtils;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by VietHoa on 04/09/15.
 */
public class TeamTrackApplication extends AppUtils {

    @Override
    public void onCreate() {
        super.onCreate();

        initialiseParse();
    }

    protected void initialiseParse() {
        try {
            final String device_uuid = DeviceUtils.getDeviceUUID(getAppContext());
            Parse.enableLocalDatastore(this);
            ParseInstallation currentInstall = ParseInstallation.getCurrentInstallation();
            currentInstall.put("device_uuid", device_uuid);
            currentInstall.saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
