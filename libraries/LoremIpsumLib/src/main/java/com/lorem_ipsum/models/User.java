package com.lorem_ipsum.models;

import com.lorem_ipsum.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by originally.us on 5/10/14.
 */
public class User implements Serializable {


//    "id":13,
//    "email":"",
//    "last_login_timestamp":1429848485,
//    "created_timestamp":1429848485,
//    "secret":"52a39820798e7b92e27faceed51a2ffbb960f142",
//    "secret_expiry_timestamp":1429855685,
//    "reset_password":null,
//    "verified_account":null,
//    "device_token":"1_1234",
//    "banned":null,
//    "modified_timestamp":1429848485,
//    "first_name":null,
//    "last_name":null,
//    "display_name":"User"

    public Number id;
    public String email;
    public Date last_login_timestamp;
    public Date created_timestamp;
    public String secret;
    public Date secret_expiry_timestamp;
    public String reset_password;
    public String verified_account;
    public String device_token;
    public String banned;
    public Date modified_timestamp;
    public String first_name;
    public String last_name;
    public String display_name;

    public String response;

    public boolean isVerified() {
        return StringUtils.isNotNull(secret);
    }

}