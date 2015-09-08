package us.originally.teamtrack.EventBus;

import us.originally.teamtrack.models.AudioData;

/**
 * Created by VietHoa on 07/09/15.
 */
public class AudioEvent {

    private AudioData audio;

    public AudioEvent(AudioData audio) {
        this.audio = audio;
    }

    public AudioData getVisualizer() {
        return audio;
    }

}
