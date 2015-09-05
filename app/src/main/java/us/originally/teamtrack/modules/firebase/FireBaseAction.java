package us.originally.teamtrack.modules.firebase;

import android.content.Context;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import de.greenrobot.event.EventBus;
import us.originally.teamtrack.EventBus.MessageEvent;
import us.originally.teamtrack.R;
import us.originally.teamtrack.modules.chat.MessageModel;


/**
 * Created by VietHoa on 04/09/15.
 */
public class FireBaseAction {

    protected static String TAG = "ChattingAction";

    protected static EventBus eventBus;
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
                childAdded(dataSnapshot);
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

    protected static void childAdded(DataSnapshot dataSnapshot) {
        if (eventBus == null)
            eventBus = new EventBus();

        MessageModel message = null;
        try {
            message = dataSnapshot.getValue(MessageModel.class);
        } catch(Exception e) {
            e.fillInStackTrace();
        }
        if (message == null)
            return;

        eventBus.getDefault().post(new MessageEvent(message));
    }


    public static void pushMessage(Context context, MessageModel message) {
        if (!message.isValid())
            return;

        //Send to FireBase
        Firebase ref = getFirebaseRef(context);
        if (ref == null)
            return;

        String channelName = message.channel_name;
        String messageId = "message_" + message.id.toString();

        ref.child(channelName).child(messageId).setValue(message, new Firebase.CompletionListener() {

            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.e(TAG, "FireBase data message could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.e(TAG, "FireBase data message saved successfully.");
                }
            }
        });
    }
}
