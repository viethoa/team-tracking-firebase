package com.lorem_ipsum.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lorem_ipsum.helper.SimpleCrypto;
import com.lorem_ipsum.models.User;
import com.lorem_ipsum.utils.AppUtils;
import com.lorem_ipsum.utils.CustomDateParser;
import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;

import java.util.Date;

/**
 * Created by Torin on 22/11/14.
 */
public class CredentialManager {

    private static final boolean ENABLE_ENCRYPTION = false;
    private static final boolean ENABLE_DYNAMIC_PASSWORD = true;

    private static final String CREDENTIAL_FACEBOOK_ID_KEY = "CredientialFacebookId";
    private static final String CREDENTIAL_FACEBOOK_TOKEN_KEY = "CredientialFacebookToken";
    private static final String CREDENTIAL_USERNAME_KEY = "CredientialUsername";
    private static final String CREDENTIAL_PASSWORD_KEY = "CredientialPassword";
    private static final String CREDENTIAL_TOKEN_KEY = "CredientialToken";

    private static String encryptionPassword = null;

    /*
     * Get shared preferences
     */
    private static SharedPreferences getSharedPreferences() {
        Context ctx = AppUtils.getAppContext();
        return ctx.getSharedPreferences("pref_user_credential_data", Context.MODE_PRIVATE);
    }

    /**
     * Helper method to clear all user-related cached data before logging out
     */
    public static boolean clearAllSavedUserData() {
        return getSharedPreferences().edit().clear().commit();
    }

    //*************************************************************************
    //
    //*************************************************************************

    //High level logic
    public static boolean hasValidUsernamePasswordLogin() {
        String username = getRememberedUsername();
        String password = getRememberedPassword();

        if (validatePassword(password) == false)
            return false;
        if (validateEmail(username) == false && validateUsername(username) == false)
            return false;

        return true;
    }

    public static boolean hasValidFacebookLogin() {
        String facebookToken = getRememberedFacebookToken();
        if (facebookToken == null || facebookToken.length() < 10)
            return false;
        return true;
    }

    public static boolean clearRememberedCredential() {
        return clearAllSavedUserData();
    }

    //Getters
    public static String getRememberedValueForKey(String key) {
        String result = getSharedPreferences().getString(key, null);
        if (result == null)
            return null;

        return decryptString(result);
    }

    public static String getRememberedFacebookId() {
        return getRememberedValueForKey(CREDENTIAL_FACEBOOK_ID_KEY);
    }

    public static String getRememberedFacebookToken() {
        return getRememberedValueForKey(CREDENTIAL_FACEBOOK_TOKEN_KEY);
    }

    public static String getRememberedUsername() {
        return getRememberedValueForKey(CREDENTIAL_USERNAME_KEY);
    }

    public static String getRememberedPassword() {
        return getRememberedValueForKey(CREDENTIAL_PASSWORD_KEY);
    }

    public static String getRememberedToken() {
        return getRememberedValueForKey(CREDENTIAL_TOKEN_KEY);
    }


    //*************************************************************************
    //
    //*************************************************************************

    //Delete
    public static boolean deleteValueForKey(String key) {
        if (key == null || key.length() <= 0)
            return false;
        return getSharedPreferences().edit().remove(key).commit();
    }

    //Setters
    public static boolean saveValue(String value, String key) {
        if (key == null || key.length() <= 0)
            return false;
        if (value == null)
            return deleteValueForKey(key);
        try {
            value = encryptString(value);
        } catch (Exception e) {
            return false;
        }
        return getSharedPreferences().edit().putString(key, value).commit();
    }

    public static boolean saveFacebookId(String value) {
        return saveValue(value, CREDENTIAL_FACEBOOK_ID_KEY);
    }

    public static boolean saveFacebookToken(String value) {
        return saveValue(value, CREDENTIAL_FACEBOOK_TOKEN_KEY);
    }

    public static boolean saveUsername(String value) {
        return saveValue(value, CREDENTIAL_USERNAME_KEY);
    }

    public static boolean savePassword(String value) {
        return saveValue(value, CREDENTIAL_PASSWORD_KEY);
    }

    public static boolean saveToken(String value) {
        return saveValue(value, CREDENTIAL_TOKEN_KEY);
    }


    //*************************************************************************
    //
    //*************************************************************************

    private static boolean validateUsername(String value) {
        if (value == null)
            return false;
        int minLength = 3;
        return value.length() >= minLength;
    }

    private static boolean validateFacebookId(String value) {
        if (value == null)
            return false;
        return value.length() > 3;
    }

    private static boolean validateEmail(String value) {
        return StringUtils.isValidEmail(value);
    }

    private static boolean validatePassword(String value) {
        if (value == null)
            return false;
        int minLength = 6;
        return value.length() >= minLength;
    }


    //*************************************************************************
    //
    //*************************************************************************

    //Offline login support
    public static boolean addOfflineUser(User user, String username, String password) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new CustomDateParser());
        Gson gson = gsonBuilder.create();
        String modelString = gson.toJson(user);

        String key = "offline_" + username + "_" + password;
        return saveValue(key, modelString);
    }

    public static User getOfflineUserWithUsername(String username, String password) {
        String key = "offline_" + username + "_" + password;
        String modelString = getRememberedValueForKey(key);
        if (modelString == null || modelString.length() <= 0)
            return null;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new CustomDateParser());
        Gson gson = gsonBuilder.create();
        User model = gson.fromJson(modelString, User.class);
        return model;
    }


    //*************************************************************************
    // Encryption
    //*************************************************************************

    private static String getEncryptionPassword() {
        if (ENABLE_DYNAMIC_PASSWORD == false)
            return "superSecret";

        if (encryptionPassword != null)
            return encryptionPassword;

        String uuid = DeviceUtils.getDeviceUUID(AppUtils.getAppContext());
        uuid = new StringBuilder(uuid).reverse().toString();
        int uuidLength = uuid.length();

        String packageName = AppUtils.getAppPackageName();
        int packageNameLength = packageName.length();

        String key = "";
        for (int i=0; i < uuidLength; i++) {
            key += uuid.charAt(i);
            key += packageName.charAt(i % packageNameLength);
        }

        encryptionPassword = new StringBuilder(key).reverse().toString();
        return encryptionPassword;
    }

    private static String decryptString(String input) {
        if (ENABLE_ENCRYPTION == false)
            return input;

        String returnString;
        try {
            returnString = SimpleCrypto.decrypt(getEncryptionPassword(), input);
        } catch (Exception e) {
            returnString = null;
            e.printStackTrace();
        }
        return returnString;
    }

    private static String encryptString(String input) {
        if (ENABLE_ENCRYPTION == false)
            return input;

        String returnString;
        try {
            returnString = SimpleCrypto.encrypt(getEncryptionPassword(), input);
        } catch (Exception e) {
            returnString = null;
            e.printStackTrace();
        }
        return returnString;
    }
}
