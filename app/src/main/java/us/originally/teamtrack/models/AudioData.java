package us.originally.teamtrack.models;

/**
 * Created by VietHoa on 07/09/15.
 */
public class AudioData {

    public AudioData(byte[] bytes, int size) {
        this.bytes = bytes;
        this.size = size;
    }

    public byte[] bytes;
    public int size;
}
