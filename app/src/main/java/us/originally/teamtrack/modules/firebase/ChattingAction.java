package us.originally.teamtrack.modules.firebase;

import android.content.Context;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import us.originally.teamtrack.modules.chat.MessageModel;


/**
 * Created by VietHoa on 04/09/15.
 */
public class ChattingAction extends FireBaseAction {

    protected static String TAG = "ChattingAction";
    protected static ChildEventListener orderListener;

    public static void pushAudio(Context context, MessageModel message) {
        if (!message.isNotNull())
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
