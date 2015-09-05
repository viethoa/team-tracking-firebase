package com.lorem_ipsum.requests;

import retrofit.RetrofitError;

/**
 * Created by originally.us on 4/5/14.
 */

public interface MyDataCallback <T> {

    void success(T t);

    void failure(RetrofitError retrofitError);
}