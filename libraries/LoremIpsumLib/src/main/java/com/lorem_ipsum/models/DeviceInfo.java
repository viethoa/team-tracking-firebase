package com.lorem_ipsum.models;

import java.util.Date;

/**
 * Created by originally.us on 7/11/14.
 */
public class DeviceInfo {

    /*
    {
       "id": 9,
       “app_id”,                //normal app
       “client_id”,             //master app
       "os_type": 1,
       "os_version": 4,
       "uuid": "",
       "push_token": "",
       "app_version": "1",
       "created_datetime": "2014-07-09 13:24:54",
       "modified_datetime": "2014-07-09 13:24:54",
       "model": "",
       "manufacturer": "",
       "country_code": "SG",
       "user_id": -1
    }
    */

    public Number id;
    public Number app_id;
    public Number os_type;
    public String os_version;
    public String uuid;
    public String push_token;
    public String app_version;
    public Date created_datetime;
    public Date modified_datetime;
    public String model;
    public String manufacturer;
    public String country_code;
    public Number user_id;
    public String language;
}
