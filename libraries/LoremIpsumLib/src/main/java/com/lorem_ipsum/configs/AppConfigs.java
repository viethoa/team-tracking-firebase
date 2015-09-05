package com.lorem_ipsum.configs;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.lorem_ipsum.requests.BaseRequest;
import com.lorem_ipsum.utils.AppUtils;

/**
 * 
 * @author Originally.US
 * 
 */
public class AppConfigs {

    private static boolean firstTimeDebuggable = true;
    private static boolean cachedDebuggableFlag;

    public static int getAppEnvironment() {
		if (isDebug())
            return BaseRequest.DEVELOPMENT;

        return BaseRequest.PRODUCTION;
	}

    public static boolean isDebug() {
        if (firstTimeDebuggable) {
            cachedDebuggableFlag = isDebuggable();
            firstTimeDebuggable = false;
        }
		return cachedDebuggableFlag;
	}


    /**
     * This returns the 'debuggable' flag in AndroidManifest.xml
     */
    private static boolean isDebuggable()
    {
        Context ctx = AppUtils.getAppContext();
        if (ctx == null)
            return false;

        boolean debuggable;
        PackageManager pm = ctx.getPackageManager();
        try
        {
            ApplicationInfo appInfo = pm.getApplicationInfo(ctx.getPackageName(), 0);
            debuggable = (0 != (appInfo.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        }
        catch(Exception e)
        {
            debuggable = false;
        }

        return debuggable;
    }

}
