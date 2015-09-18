package us.originally.teamtrack.models;

import java.io.Serializable;

/**
 * Created by VietHoa on 04/09/15.
 */
public class AudioModel implements Serializable {

    public String encode;
    public Integer size;
    public Integer id;
    public UserTeamModel user;
    public long time_stamp;

    public AudioModel() {
    }

    public AudioModel(String encode_base_64, int size, int id, long time_stamp, UserTeamModel user) {
        this.encode = encode_base_64;
        this.time_stamp = time_stamp;
        this.size = size;
        this.user = user;
        this.id = id;
    }
}
