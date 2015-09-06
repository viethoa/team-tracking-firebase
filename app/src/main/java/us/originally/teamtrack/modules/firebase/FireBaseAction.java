package us.originally.teamtrack.modules.firebase;

import android.content.Context;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.lorem_ipsum.utils.StringUtils;

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
    protected static ChildEventListener childEventListener;

    protected static Firebase getFirebaseRef(Context context) {
        if (firebaseRef != null)
            return firebaseRef;

        Firebase.setAndroidContext(context);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        String url = context.getString(R.string.firebase_ref);
        firebaseRef = new Firebase(url);

        return firebaseRef;
    }

    public static void initListener() {
        if (childEventListener != null)
            return;

        childEventListener = new ChildEventListener() {
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

    public static void registerMessageListener(Context context, String channelName) {
        Firebase ref = getFirebaseRef(context);
        if (ref == null)
            return;

        initListener();
        Query messageQuery = ref.child(channelName);
        messageQuery.addChildEventListener(childEventListener);
    }

    protected static void childAdded(DataSnapshot dataSnapshot) {
        System.out.println(dataSnapshot.getValue());
        if (eventBus == null)
            eventBus = new EventBus();

        MessageModel message = null;
        try {
            message = dataSnapshot.getValue(MessageModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null)
            return;

        Log.d(TAG, "post message to UI Thread");
        eventBus.getDefault().post(new MessageEvent(message));
    }

    public static void pushMessage(Context context, final MessageModel message) {
        if (!isMessageValid(message))
            return;

        Firebase ref = getFirebaseRef(context);
        if (ref == null)
            return;

        String channelName = message.channel_name;
        String messageId = "message_" + message.id.toString();
        ref.child(channelName).child(messageId).setValue(message, new Firebase.CompletionListener() {

            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.e(TAG, "FireBase message could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.e(TAG, "FireBase message saved successfully.");
                }
            }
        });
    }

    protected static boolean isMessageValid(MessageModel message) {
        if (message == null || message.id == null)
            return false;

        boolean isNotNull = StringUtils.isNotNull(message.message);
        if (!isNotNull) {
            isNotNull = (message.audio != null && message.audio.size() > 0);
        }

        return isNotNull;
    }
}
