package com.lorem_ipsum.models;

import com.lorem_ipsum.utils.AppUtils;

import java.util.Date;

/**
 * Created by Originally.US on 20/9/14.
 */
public class AppVersion {

    /*
    [
       {
           "app_id": 1,
           "client_id": 1,
           "os_type": 1,
           "latest_version": "1.1.1",
           "upgrade_message": "What an upgrade!",
           "disable_old_app": 0,
           "modified_datetime": "2014-09-16 00:00:00"
       }
    ]
     */

    public Number app_id;
    public Number client_id;
    public Number os_type;
    public String latest_version;
    public String android_download_url;
    public String upgrade_message;
    public Number disable_old_app;
    public Date modified_datetime;

    public boolean hasNewVersion() {
        if (this.latest_version == null)
            return false;
        String latestVersionString = "" + this.latest_version;

        String currentVersionString = AppUtils.getAppVersionName();
        if (currentVersionString == null || currentVersionString.length() <= 0)
            return false;

        boolean hasNewVersion = false;
        try {
            String[] current = currentVersionString.split("\\.");       //regex
            String[] latest = latestVersionString.split("\\.");         //regex

            int latestMajor = latest.length > 0 ? Integer.parseInt(latest[0]) : Integer.parseInt(latestVersionString);
            int latestMinor = latest.length > 1 ? Integer.parseInt(latest[1]) : 0;
            int latestPatch = latest.length > 2 ? Integer.parseInt(latest[2]) : 0;
            int currentMajor = current.length > 0 ? Integer.parseInt(current[0]) : Integer.parseInt(currentVersionString);
            int currentMinor = current.length > 1 ? Integer.parseInt(current[1]) : 0;
            int currentPatch = current.length > 2 ? Integer.parseInt(current[2]) : 0;

            if (latestMajor > currentMajor)
                hasNewVersion = true;
            else if (latestMajor == currentMajor && latestMinor > currentMinor)
                hasNewVersion = true;
            else if (latestMajor == currentMajor && latestMinor == currentMinor && latestPatch > currentPatch)
                hasNewVersion = true;

        } catch (Exception e) {
            hasNewVersion = false;
        }

        return hasNewVersion;
    }

    public boolean needForceUpdate() {
        if (this.disable_old_app == null)
            return false;
        return this.disable_old_app.intValue() != 0;
    }
}
