package com.lorem_ipsum.models;

/**
 * Created by Originally.US on 2/7/14.
 */
public class Language {

    public Number id;
    public Number language_id;
    public String name;

    public Number getID() {
        if (this.id != null)
            return this.id;
        if (this.language_id != null)
            return this.language_id;
        return null;
    }
}
