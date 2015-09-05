package com.lorem_ipsum.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.lorem_ipsum.models.User;
import com.lorem_ipsum.utils.AppUtils;
import com.lorem_ipsum.utils.LogUtils;
import com.lorem_ipsum.utils.StringUtils;

/**
 * Created by originally.us on 5/10/14.
 */
public class UserSessionDataManager {

    private static final String LOG_TAG = "UserSessionDataManager";

    private static final String PREF_KEY_ACCESS_TOKEN_DATA = "access_token_data";
    private static final String PREF_KEY_USER_INFO = "key_user_info";

    // memory cache
    private static User cacheUser;
    private static String cacheAccessToken;

    /*
     * Get share preferences
     */
    private static SharedPreferences getSharedPreferences() {
        Context ctx = AppUtils.getAppContext();
        return ctx.getSharedPreferences("pref_user_session_data", Context.MODE_PRIVATE);
    }

    /**
     * Helper method to clear all user-related cached data before logging out
     */
    public static void clearAllSavedUserData() {
        cacheUser = null;
        cacheAccessToken = null;
        getSharedPreferences().edit().clear().commit();
    }

    //*************************************************************************
    // Authorization token (access token for all authenticated APIs)
    //*************************************************************************

    /**
     * Get authorization token (not facebook token)
     */
    public static String getCurrentAccessToken() {
        if (StringUtils.isNotNull(cacheAccessToken))
            return cacheAccessToken;

        String accessToken = null;
        User user = getCurrentUser();
        if (user != null)
            accessToken = user.secret;
        if (accessToken != null)
            cacheAccessToken = accessToken;

        return cacheAccessToken;
    }

    //*************************************************************************
    // GCM
    //*************************************************************************

    /**
     * Helper method to cache GCM token
     *
     * @param gcmDeviceId GCM Device ID (or token)
     */
    public static void saveGCMToken(String gcmDeviceId) {
        String appVersion = AppUtils.getAppVersionName();
        String cacheKey = "gcm_reg_id_" + appVersion;

        getSharedPreferences().edit().putString(cacheKey, gcmDeviceId);
        Log.i(LOG_TAG, "Caching GCM token for app version " + appVersion);
    }

    /**
     * Helper method to get cached GCM token
     */
    public static String getGCMToken() {
        String appVersion = AppUtils.getAppVersionName();
        String cacheKey = "gcm_reg_id_" + appVersion;
        return getSharedPreferences().getString(cacheKey, null);
    }

    //*************************************************************************
    // User info
    //*************************************************************************

    /**
     * Save user info
     */
    public static void saveCurrentUser(User user) {
        if (user == null)
            return;

        // memory cache
        cacheUser = user;
        cacheAccessToken = user.secret;

        String json = (new Gson()).toJson(user);
        if (StringUtils.isNull(json))
            return;

        getSharedPreferences().edit().putString(PREF_KEY_USER_INFO, json).commit();
    }

    /**
     * Get user info from cache
     * <p/>
     * Remember to handle NULL case
     */
    public static User getCurrentUser() {
        if (cacheUser != null)
            return cacheUser;

        User user;
        String jsonData = getSharedPreferences().getString(PREF_KEY_USER_INFO, "");

        //Convert back to User data model
        try {
            user = (new Gson()).fromJson(jsonData, User.class);
        } catch (Exception e) {
            String message = "null";
            if (e != null) {
                message = e.getMessage();
            }
            LogUtils.logInDebug(LOG_TAG, "getCurrentUserInfo error: " + message);
            user = null;
        }

        cacheUser = user;

        if (user != null)
            cacheAccessToken = user.secret;

        return cacheUser;
    }

    /**
     * Get user id from cache
     * <p/>
     * Remember to handle NULL case
     */
    public static Number getCurrentUserID() {
        User currentUser = getCurrentUser();
        if (currentUser != null)
            return currentUser.id;
        return null;
    }

    //*************************************************************************
    // App preference
    //*************************************************************************

    public static void saveLanguagePreference(final int langId) {
        if (langId <= 0)
            getSharedPreferences().edit().putInt("langId", 1).commit();
        else
            getSharedPreferences().edit().putInt("langId", langId).commit();
    }

    public static int getLanguagePreference() {
        return getSharedPreferences().getInt("langId", 1);
    }
}
