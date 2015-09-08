package us.originally.teamtrack.EventBus;

/**
 * Created by VietHoa on 08/09/15.
 */
public class VisualizeEvent {

    public VisualizeEvent(float soudValue) {
        this.soudValue = soudValue;
    }

    //Value ranges from 0 to 1
    public float getSoudValue() {
        return soudValue;
    }

    public void setSoudValue(float soudValue) {
        this.soudValue = soudValue;
    }

    private float soudValue;
}
