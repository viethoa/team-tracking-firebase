package us.originally.teamtrack.modules.chat.audio;

import java.io.Serializable;

/**
 * Created by VietHoa on 04/09/15.
 */
public class AudioModel implements Serializable {

    public String encode;
    public Integer size;

    public AudioModel() {
    }

    public AudioModel(String encode_base_64, int size) {
        this.encode = encode_base_64;
        this.size = size;
    }
}
