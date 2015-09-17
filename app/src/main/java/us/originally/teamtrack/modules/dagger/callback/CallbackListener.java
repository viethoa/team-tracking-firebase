package us.originally.teamtrack.modules.dagger.callback;

/**
 * Created by VietHoa on 30/07/15.
 */
public interface CallbackListener<T> {

     void onDone(T response, Exception exception);
}
