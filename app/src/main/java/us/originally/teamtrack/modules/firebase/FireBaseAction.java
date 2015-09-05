package us.originally.teamtrack.modules.firebase;

import android.content.Context;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import us.originally.teamtrack.R;

/**
 * Created by VietHoa on 04/09/15.
 */
public class FireBaseAction {

    protected static Firebase firebaseRef;
    protected static ChildEventListener orderListener;

    protected static Firebase getFirebaseRef(Context context) {
        if (firebaseRef != null)
            return firebaseRef;

        Firebase.setAndroidContext(context);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        String url = context.getString(R.string.firebase_ref);
        firebaseRef = new Firebase(url);

        return firebaseRef;
    }

    protected static void initListener() {
        if (orderListener != null)
            return;

        orderListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
    }

    protected void childAdded(DataSnapshot dataSnapshot) {
        //Todo override
    }

    protected void childChanged(DataSnapshot dataSnapshot) {
        //Todo override
    }

    protected void childRemoved(DataSnapshot dataSnapshot) {
        //Todo override
    }
}
