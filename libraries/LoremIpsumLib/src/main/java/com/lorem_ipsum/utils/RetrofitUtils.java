package com.lorem_ipsum.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lorem_ipsum.R;
import com.lorem_ipsum.configs.AppConfigs;
import com.lorem_ipsum.managers.CredentialManager;
import com.lorem_ipsum.managers.UserSessionDataManager;
import com.lorem_ipsum.requests.BaseRequest;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

/**
 * Created by DangTai on 3/1/14.
 */
public final class RetrofitUtils {

    private static final String LOG_TAG = "RetrofitUtils";

    private RetrofitUtils() {
    }

    public static RestAdapter getRestApdaterWithFullUrl(String url, RequestInterceptor requestInterceptor) {

        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new CustomDateParser());
        //builder.registerTypeAdapter(String.class, new CustomStringSerializer());
        builder.disableHtmlEscaping();
        final Gson gson = builder.create();

        RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder();
        restAdapterBuilder.setConverter(new GsonConverter(gson));
        restAdapterBuilder.setEndpoint(url);
        if (requestInterceptor != null)
            restAdapterBuilder.setRequestInterceptor(requestInterceptor);
        RestAdapter adapter = restAdapterBuilder.build();

        //Full log level on development mode
        if (AppConfigs.getAppEnvironment() == BaseRequest.DEVELOPMENT)
            adapter.setLogLevel(RestAdapter.LogLevel.FULL);

        return adapter;
    }

    public static RestAdapter getRestApdater(boolean withPrefix, RequestInterceptor requestInterceptor) {
        String baseUrl = BaseRequest.getBaseUrl(AppConfigs.getAppEnvironment(), withPrefix);

        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new CustomDateParser());
        //builder.registerTypeAdapter(String.class, new CustomStringSerializer());
        builder.disableHtmlEscaping();
        final Gson gson = builder.create();

        RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder();
        restAdapterBuilder.setConverter(new GsonConverter(gson));
        restAdapterBuilder.setEndpoint(baseUrl);
        if (requestInterceptor != null)
            restAdapterBuilder.setRequestInterceptor(requestInterceptor);
        RestAdapter adapter = restAdapterBuilder.build();

        //Full log level on development mode
//        if (AppConfigs.getAppEnvironment() == BaseRequest.DEVELOPMENT)
//            adapter.setLogLevel(RestAdapter.LogLevel.FULL);

        return adapter;
    }

    private static void showErrorMessage(Context thisContext, String message) {
        if (message == null || message.isEmpty())
            return;

        if (thisContext != null && thisContext instanceof Activity)
            SuperActivityToast.create((Activity) thisContext, message, SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
        else
            Toast.makeText(thisContext, message, Toast.LENGTH_LONG).show();
    }


    /**
     * print error log on UI and LogCat
     */
    public static void printRetrofitError(Context thisContext, RetrofitError retrofitError) {
        // use in debugging
        if (AppConfigs.isDebug() == false) {
            return;
        }

        String logTag = thisContext.getClass().getSimpleName();
        if (retrofitError == null) {
            //Toast.makeText(thisContext, logTag + " Request failure: [RetrofitError null]", Toast.LENGTH_SHORT).show();
            return;
        }
        if (retrofitError.getResponse() == null) {
            String str = " Request failure: [RESPONSE null] [MESSAGE " + retrofitError.getMessage() + "]";
            showErrorMessage(thisContext, logTag + str);
            return;
        }

        // show toast on UI
        String errorMessage = getStringError(retrofitError);
        showErrorMessage(thisContext, logTag + errorMessage);

        // print log to LogCat
        Log.w(logTag, errorMessage);
        Log.w(logTag, "URL " + retrofitError.getUrl());
    }

    public static String getStringError(RetrofitError retrofitError) {
        if (retrofitError == null || retrofitError.getResponse() == null)
            return "";

        StringBuilder builder = new StringBuilder();
        builder.append(" Request failure: [MESSAGE ");
        builder.append(retrofitError.getMessage());
        builder.append("] [STATUS ");
        builder.append(retrofitError.getResponse().getStatus());
        builder.append("] [REASON ");
        builder.append(retrofitError.getResponse().getReason());
        builder.append("]");
        return builder.toString();
    }

    private static boolean isUnauthorizedError(RetrofitError error) {
        if (error == null)
            return false;

        retrofit.client.Response response = error.getResponse();
        if (response == null) {
            String tempMessage = error.getMessage();
            if (StringUtils.isNotNull(tempMessage)) {

                //Unauthorized
                if (tempMessage.toLowerCase().contains("no authentication challenges found"))
                    return true;
            }
            return false;
        }

        //Unauthorized
        int statusCode = response.getStatus();
        if (statusCode == 401)
            return true;

        //Unauthorized
        String reason = response.getReason();
        if (StringUtils.isNotNull(reason) && reason.toLowerCase().contains("no authentication challenges found"))
            return true;

        return false;
    }

    private static String getFriendlyStandardErrorMessage(RetrofitError error) throws Exception {
        if (error == null)
            return null;

        retrofit.client.Response response = error.getResponse();
        if (response == null) {
            String tempMessage = error.getMessage();
            if (StringUtils.isNotNull(tempMessage)) {

                //Unauthorized
                if (tempMessage.toLowerCase().contains("no authentication challenges found"))
                    return "Invalid username or password";
            }

            //Really check if it's network error
            ConnectivityManager cm = (ConnectivityManager) AppUtils.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (!isConnected)
                return "Please check your Internet connection";

            if (error.isNetworkError()) {
                if (error.getCause() instanceof SocketTimeoutException) {
                    tempMessage = error.getLocalizedMessage();
                    Log.e(LOG_TAG, tempMessage);
                    return "Network Timeout";
                } else if (error.getCause() instanceof ConnectException) {
                    tempMessage = error.getLocalizedMessage();
                    Log.e(LOG_TAG, tempMessage);
                    return "Please check your network connection";
                } else {
                    //This happens too often for no reason (looks like Retrofit is reconnecting)
                    //return "Please check your Internet connection";
                    return null;
                }
            }
            return tempMessage;
        }

        //Unauthorized
        int statusCode = response.getStatus();
        if (statusCode == 401)
            return "Invalid username or password";

        //Unauthorized
        String reason = response.getReason();
        if (StringUtils.isNotNull(reason) && reason.toLowerCase().contains("no authentication challenges found"))
            return "Invalid username or password";

        JsonElement rawErrorJsonElement = (JsonElement) error.getBodyAs(JsonElement.class);
        if (rawErrorJsonElement == null)
            return null;
        if (rawErrorJsonElement.isJsonObject() == false)
            return null;

        //Look for error signature
        JsonObject jsonObject = rawErrorJsonElement.getAsJsonObject();

        JsonElement errorElement = jsonObject.get("error");
        if (errorElement == null)
            errorElement = jsonObject.get("errors");
        if (errorElement == null)
            errorElement = jsonObject.get("message");
        if (errorElement == null)
            errorElement = jsonObject.get("messages");

        //Another nested level, if any
        if (errorElement.isJsonObject()) {
            JsonObject errorObject = errorElement.getAsJsonObject();
            JsonElement tempElement = errorObject.get("message");
            if (tempElement == null)
                tempElement = errorObject.get("messages");
            if (tempElement != null && (tempElement.isJsonObject() || tempElement.isJsonArray()))
                errorElement = tempElement;
        }

        //If there's only 1 element inside it, use it
        if (errorElement.isJsonObject()) {
            JsonObject errorObject = errorElement.getAsJsonObject();
            int count = errorObject.entrySet().size();
            if (count == 1) {
                for (Map.Entry<String, JsonElement> entry : errorObject.entrySet()) {
                    JsonElement anotherTempElement = entry.getValue();
                    if (anotherTempElement != null && anotherTempElement instanceof JsonElement)
                        errorElement = anotherTempElement;
                }
            }
        }
        if (errorElement.isJsonArray()) {
            JsonArray errorArray = errorElement.getAsJsonArray();
            int count = errorArray.size();
            if (count == 1) {
                JsonElement anotherTempElement = errorArray.get(0);
                if (anotherTempElement != null && anotherTempElement instanceof JsonElement)
                    errorElement = anotherTempElement;
            }
        }

        //Can't find the error element
        if (errorElement == null || errorElement.isJsonNull())
            return null;

        //Simple error string or type
        if (errorElement.isJsonPrimitive()) {
            String simpleErrorElement = errorElement.getAsString();
            if (StringUtils.isNotNull(simpleErrorElement) && simpleErrorElement.toLowerCase().contains("invalid_resource_owner"))
                return "Invalid username or password";
            if (StringUtils.isNotNull(simpleErrorElement))
                return simpleErrorElement;

            return errorElement.toString();
        }

        //Array of error strings
        if (errorElement.isJsonArray()) {
            JsonArray array = errorElement.getAsJsonArray();
            if (array.size() <= 0)
                return null;

            //Convert array to a nice formatted string
            StringBuilder builder = new StringBuilder();
            for (JsonElement element : array) {
                String elementString = element.toString();
                if (StringUtils.isNull(element.toString()))
                    continue;

                if (elementString.toLowerCase().contains("invalid_resource_owner"))
                    elementString = "Invalid username or password";
                elementString = elementString.trim();

                builder.append(elementString);
                builder.append("\n");
            }
            return builder.toString().trim();
        }

        return errorElement.toString();
    }


    public static int handleRetrofitError(Activity activity, final RetrofitError retrofitError, String message, boolean isToastMessage) {

        //Extract status code
        int statusCode = 0;
        retrofit.client.Response response = null;
        if (retrofitError != null)
            response = retrofitError.getResponse();
        if (response != null) {
            statusCode = response.getStatus();
        }

        //401 - Handle token expire
        boolean authError = isUnauthorizedError(retrofitError);
        if (authError) {
            UserSessionDataManager.clearAllSavedUserData();
            CredentialManager.clearAllSavedUserData();
            String myClass = "mydelivery.shippin.com.mydelivery.controllers.registration.LandingActivity";
            Class<?> clazz;
            try {
                clazz = Class.forName(myClass);
            } catch (ClassNotFoundException e) {
                clazz = null;
                Log.e(LOG_TAG, "Unable to find class " + myClass + ". Did you include it in AndroidManifest? or use full package path?");
            }
            if (clazz != null) {
                String errorMsg = activity.getString(R.string.session_expired_error_string);
                Intent intent = new Intent(activity, clazz);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_left_to_right);
                activity.finish();
                SuperToast.create(AppUtils.getAppContext(), errorMsg, SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
                return statusCode;
            }
        }

        //Handle error gracefully
        String errorMessage;
        try {
            errorMessage = getFriendlyStandardErrorMessage(retrofitError);
        } catch (Exception e) {
            errorMessage = null;
        }
        if (StringUtils.isNotNull(errorMessage))
            message = errorMessage;
        if (StringUtils.isNull(message))
            message = "Unknown Error";

        //Toast it
        if (isToastMessage)
            SuperActivityToast.create(activity, message, SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();

        //Logcat
        Log.e(LOG_TAG, message);
        AppUtils.writeToLogFile(LOG_TAG, message);

        if (retrofitError != null) {
            Log.e(LOG_TAG, retrofitError.getUrl());
            AppUtils.writeToLogFile(LOG_TAG, retrofitError.getUrl());
            AppUtils.writeToLogFile(LOG_TAG, retrofitError.getLocalizedMessage());
        }

        return statusCode;
    }

}
