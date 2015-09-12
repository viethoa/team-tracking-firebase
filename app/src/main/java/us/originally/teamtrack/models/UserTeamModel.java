package us.originally.teamtrack.models;

import java.io.Serializable;

/**
 * Created by VietHoa on 10/09/15.
 */
public class UserTeamModel implements Serializable {

    public String device_uuid;
    public String name;
    public Float lat;
    public Float lng;

    public UserTeamModel(String id, String name, float lat, float lng) {
        this.device_uuid = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }
}
