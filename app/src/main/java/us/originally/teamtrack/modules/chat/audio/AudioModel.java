package us.originally.teamtrack.modules.chat.audio;

import java.io.Serializable;

/**
 * Created by VietHoa on 04/09/15.
 */
public class AudioModel implements Serializable {

    public String encode;
    public Integer size;
    public String timestamp;
    public String uuid;

    public AudioModel() {
    }

    public AudioModel(String encode_base_64, int size, String timestamp, String uuid) {
        this.encode = encode_base_64;
        this.timestamp = timestamp;
        this.size = size;
        this.uuid = uuid;
    }
}
