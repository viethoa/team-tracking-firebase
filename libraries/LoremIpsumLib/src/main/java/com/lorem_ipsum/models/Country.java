package com.lorem_ipsum.models;

import java.io.Serializable;

/**
 * Created by Torin on 12/11/14.
 */
public class Country implements Serializable {

    public Number id;
    public String country_code;     //SG
    public String name;             //Singapore
    public Number phone_code;       //65

    public Country(Number id, String code, String name, Number phone_code) {
        this.id = id;
        this.country_code = code;
        this.name = name;
        this.phone_code = phone_code;
    }
}
