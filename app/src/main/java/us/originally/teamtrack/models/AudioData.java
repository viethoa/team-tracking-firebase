package us.originally.teamtrack.models;

/**
 * Created by VietHoa on 07/09/15.
 */
public class AudioData {

    public AudioData(byte[] bytes, int size) {
        this.buffer = bytes;
        this.size = size;
    }

    public byte[] buffer;
    public int size;
}
