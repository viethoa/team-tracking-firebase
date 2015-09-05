package com.lorem_ipsum.requests;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lorem_ipsum.utils.CustomDateParser;
import com.lorem_ipsum.utils.LogUtils;

import java.lang.reflect.Type;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class JsonHelper {

    /**
     * @param context
     * @param myCallback
     * @param type
     * @param dataWrapperElement ("data" is default)
     * @param logTagClass
     * @param logTagMethod
     */
    public static Callback<JsonElement> callbackJsonElement(final Context context,
                                                            final MyDataCallback myCallback,
                                                            final Type type,
                                                            final String dataWrapperElement,
                                                            final String logTagClass,
                                                            final String logTagMethod) {
        Callback<JsonElement> callbackJsonElement = new Callback<JsonElement>() {

            @Override
            public void success(final JsonElement jsonElement, Response response) {
                // wrong data, callback null
                if (jsonElement == null) {
                    if (myCallback != null)
                        myCallback.success(null);

                    return;
                }

                // parse json and callback result
                if (jsonElement.isJsonObject() || jsonElement.isJsonArray()) {
                    if (type == null) {
                        if (myCallback != null)
                            myCallback.success(jsonElement);

                    } else
                        JsonHelper.jsonParsingHelper(context, jsonElement, myCallback, type, dataWrapperElement, logTagClass, logTagMethod);
                    return;
                }

                // wrong data, callback null
                if (myCallback != null)
                    myCallback.success(null);

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                // callback error on UI
                if (myCallback != null)
                    myCallback.failure(retrofitError);
            }
        };

        return callbackJsonElement;
    }

    private static void jsonParsingHelper(final Context context,
                                          final JsonElement jsonElement,
                                          final MyDataCallback myCallback,
                                          final Type type,
                                          final String dataWrapperElement,
                                          final String logTagClass,
                                          final String logTagMethod) {

        // run in background thread
        new Thread(new Runnable() {
            @Override
            public void run() {

                // init gson
                final GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(Date.class, new CustomDateParser());
                final Gson gson = builder.create();

                Object object = null;

                try {
                    JsonElement theJsonElement = jsonElement;

                    //Check if our data model is wrapped inside dataWrapperElement or "data" element
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();

                        // try to get dataWrapperElement, "data" is default
                        String finalDataElement;
                        if (dataWrapperElement == null || dataWrapperElement.isEmpty() || dataWrapperElement.toUpperCase().equals("NULL"))
                            finalDataElement = "data";
                        else
                            finalDataElement = dataWrapperElement;

                        JsonElement dataJsonElement = jsonObject.get(finalDataElement);
                        if (dataJsonElement != null)
                            theJsonElement = dataJsonElement;
                    }

                    object = gson.fromJson(theJsonElement, type);

                } catch (Exception e) {
                    String message = e.getMessage();
                    LogUtils.logInDebug(logTagClass, logTagMethod + " error: " + message);
                    Log.e(logTagClass, logTagMethod + " error: " + message);
                    object = null;

                } finally {

                    //Sanity checks
                    if (myCallback == null)
                        return;
                    if (object == null) {
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myCallback.success(null);

                                }
                            });
                        } else {
                            Log.e(logTagClass, "Context passed to API call is not Activity class");
                        }
                    } else {

                        final Object finalObject = object;
                        final Class finalClass = object.getClass();

                        // callback on UI thread
                        if (context != null && context instanceof Activity) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myCallback.success(finalClass.cast(finalObject));

                                }
                            });
                        }
                        //Or current thread
                        else {
                            myCallback.success(finalClass.cast(finalObject));

                        }
                    }
                }

            }
        }).start();
    }

}
