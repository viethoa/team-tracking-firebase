package us.originally.teamtrack.modules.chat.audio;

import java.io.Serializable;

import us.originally.teamtrack.models.UserTeamModel;

/**
 * Created by VietHoa on 04/09/15.
 */
public class AudioModel implements Serializable {

    public String encode;
    public Integer size;
    public String timestamp;
    public UserTeamModel user;

    public AudioModel() {
    }

    public AudioModel(String encode_base_64, int size, String timestamp, UserTeamModel user) {
        this.encode = encode_base_64;
        this.timestamp = timestamp;
        this.size = size;
        this.user = user;
    }
}
