package us.originally.teamtrack.modules.dagger.callback;

/**
 * Created by VietHoa on 30/07/15.
 */
public abstract class CallbackListener<T> {

    public abstract void onDone(T response, Exception exception);
}
