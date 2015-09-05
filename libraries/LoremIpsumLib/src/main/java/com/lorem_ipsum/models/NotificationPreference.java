package com.lorem_ipsum.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Originally.US on 8/10/14.
 */
public class NotificationPreference implements Serializable {
    /*
        "id": 20,
        "pick_up": 1,
        "in_transit": 1,
        "delays_problems": 1,
        "service_request_confirmed": 1,
        "delivered": 1,
        "delivery_today": 1,
        "delivery_tomorrow": 1,
        "delivery_this_week": 1,
        "subscribed_emails": "15,2",
        "modified_timestamp": 1412327939
     */

    public Number id;           //this is the same as user_id
    public Number pick_up;
    public Number in_transit;
    public Number delays_problems;
    public Number service_request_confirmed;
    public Number delivered;
    public Number delivery_today;
    public Number delivery_tomorrow;
    public Number delivery_this_week;
    public String subscribed_emails;
    public Date modified_timestamp;

    public boolean hasSubscribeEmail(int emailId) {
        if (this.subscribed_emails == null || subscribed_emails.length() <= 0)
            return false;
        if (emailId <= 0)
            return false;

        String testString = "" + emailId;

        String[] parts = this.subscribed_emails.split(",");
        for (String subscribedId : parts) {
            if (subscribedId == null || subscribedId.length() <= 0)
                continue;
            if (testString.equalsIgnoreCase(subscribedId))
                return true;
        }

        return false;
    }

}
