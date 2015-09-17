package us.originally.teamtrack.managers;

import android.content.Context;

import com.firebase.client.Firebase;

import us.originally.teamtrack.R;

/**
 * Created by VietHoa on 17/09/15.
 */
public class FireBaseManager {

    private Context mContext;
    private static Firebase fireBaseRef;

    public FireBaseManager(Context context) {
        this.mContext = context;
    }

    public Firebase getFireBaseRef() {
        if (fireBaseRef != null)
            return fireBaseRef;

        if (mContext == null)
            return null;

        Firebase.setAndroidContext(mContext);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        String url = mContext.getString(R.string.firebase_ref);
        fireBaseRef = new Firebase(url);

        return fireBaseRef;
    }
}
