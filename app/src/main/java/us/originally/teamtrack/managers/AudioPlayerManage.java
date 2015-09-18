package us.originally.teamtrack.managers;

import android.content.Context;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.lorem_ipsum.managers.CacheManager;
import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;

import us.originally.teamtrack.Constant;
import us.originally.teamtrack.models.AudioModel;
import us.originally.teamtrack.modules.chat.audio.AudioStreamManager;

/**
 * Created by VietHoa on 18/09/15.
 */
public class AudioPlayerManage {

    private static final String TAG = "AudioPlayerManage";
    private static final int LIMIT_TIME_TO_PLAY = 10;

    protected static ChildEventListener childEventListener;
    private static Context mContext;

    public AudioPlayerManage(Context context) {
        mContext = context;
    }

    public void registerEventListener(Firebase ref) {
        if (ref == null)
            return;

        String myTeamKey = CacheManager.getStringCacheData(Constant.TEAM_KEY_CACHE_KEY);
        if (StringUtils.isNull(myTeamKey)) {
            Log.d(TAG, "FireBase key null");
            return;
        }

        initialiseEventListener();
        ref.child(Constant.TEAM_GROUP).child(myTeamKey).child(Constant.SLUG_AUDIOS)
                .addChildEventListener(childEventListener);
    }

    protected void initialiseEventListener() {
        if (childEventListener != null)
            return;

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "user taking: " + dataSnapshot.getValue());
                onReceiveAudio(dataSnapshot);
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "user taking: " + dataSnapshot.getValue());
                onReceiveAudio(dataSnapshot);
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

    protected void onReceiveAudio(DataSnapshot dataSnapshot) {
        AudioModel audio = null;
        try {
            audio = dataSnapshot.getValue(AudioModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (audio == null)
            return;

        //Do not play audio by myself
        if (isAudioByMyself(audio))
            return;

        //Do not play in old time
        long currentTime = System.currentTimeMillis();
        int second = (int) (Math.abs(((currentTime - audio.time_stamp) / 1000) % 60));
        Log.d(TAG, "Second: " + second);
        if (second > LIMIT_TIME_TO_PLAY)
            return;

        AudioStreamManager.startPlaying(audio);
    }

    protected boolean isAudioByMyself(AudioModel audio) {
        if (audio == null || audio.user == null || StringUtils.isNull(audio.user.device_uuid))
            return false;

        String uuid = DeviceUtils.getDeviceUUID(mContext);
        if (StringUtils.isNull(uuid))
            return false;

        return uuid.equals(audio.user.device_uuid);
    }
}
