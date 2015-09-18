package us.originally.teamtrack.modules.dagger.managers.impl;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lorem_ipsum.managers.CacheManager;
import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;

import us.originally.teamtrack.Constant;
import us.originally.teamtrack.managers.FireBaseManager;
import us.originally.teamtrack.models.AudioModel;
import us.originally.teamtrack.models.Comment;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.dagger.callback.CallbackListener;
import us.originally.teamtrack.modules.dagger.managers.UserManager;

/**
 * Created by VietHoa on 16/09/15.
 */
public class UserManagerImpl implements UserManager {

    private static final String TAG = "UserManager";
    private FireBaseManager fireBaseManager;
    private Context mContext;

    public UserManagerImpl(Context context, FireBaseManager fireBaseManager) {
        this.mContext = context;
        this.fireBaseManager = fireBaseManager;
    }

    @Override
    public void loginOrSubscribe(TeamModel team, UserTeamModel user, CallbackListener<UserTeamModel> callback) {
        if (team == null || user == null || StringUtils.isNull(team.team_name)) {
            if (callback != null) {
                callback.onDone(null, new IllegalAccessException("Data syntax error"));
            }
            return;
        }

        Firebase ref = fireBaseManager.getFireBaseRef();
        if (ref == null) {
            if (callback != null) {
                callback.onDone(null, new IllegalAccessException("FireBase syntax error"));
            }
            return;
        }

        Firebase temRef = ref.child(Constant.TEAM_GROUP);
        temRef.addValueEventListener(new TeamValueListener(temRef, team, user, callback));
    }

    @Override
    public void logout(UserTeamModel user) {
        String myTeamKey = CacheManager.getStringCacheData(Constant.TEAM_KEY_CACHE_KEY);
        if (user == null || StringUtils.isNull(myTeamKey))
            return;

        Firebase ref = fireBaseManager.getFireBaseRef();
        if (ref == null)
            return;

        ref.child(Constant.TEAM_GROUP).child(myTeamKey).child(Constant.SLUG_USERS).child(user.device_uuid)
                .removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Log.d(TAG, "Logout error: " + firebaseError.getMessage());
                        } else {
                            Log.d(TAG, "Logout successfully.");
                        }
                    }
                });
    }

    @Override
    public void updateUser(UserTeamModel user) {
        String myTeamKey = CacheManager.getStringCacheData(Constant.TEAM_KEY_CACHE_KEY);
        if (user == null || StringUtils.isNull(myTeamKey))
            return;

        Firebase ref = fireBaseManager.getFireBaseRef();
        if (ref == null)
            return;

        ref.child(Constant.TEAM_GROUP).child(myTeamKey).child(Constant.SLUG_USERS)
                .child(user.device_uuid).setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(TAG, "Update user error: " + firebaseError.getMessage());
                } else {
                    Log.d(TAG, "Update user successfully.");
                }
            }
        });
    }

    @Override
    public void pushComment(Comment comment) {
        String myTeamKey = CacheManager.getStringCacheData(Constant.TEAM_KEY_CACHE_KEY);
        if (comment == null || StringUtils.isNull(myTeamKey))
            return;

        Firebase ref = fireBaseManager.getFireBaseRef();
        if (ref == null)
            return;

        String commentId = comment.time_stamp + "_" + comment.user.device_uuid;
        ref.child(Constant.TEAM_GROUP).child(myTeamKey).child(Constant.SLUG_MESSAGE)
                .child(commentId).setValue(comment, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(TAG, "Push comment error: " + firebaseError.getMessage());
                } else {
                    Log.d(TAG, "Push comment successfully.");
                }
            }
        });
    }

    @Override
    public void pushAudio(AudioModel audioModel) {
        String myTeamKey = CacheManager.getStringCacheData(Constant.TEAM_KEY_CACHE_KEY);
        if (!isAudioValid(audioModel) || StringUtils.isNull(myTeamKey))
            return;

        Firebase ref = fireBaseManager.getFireBaseRef();
        if (ref == null)
            return;

        String audioId = "audio_" + audioModel.id;
        ref.child(Constant.TEAM_GROUP).child(myTeamKey).child(Constant.SLUG_AUDIOS)
                .child(audioId).setValue(audioModel, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(TAG, "Push Audio error: " + firebaseError.getMessage());
                } else {
                    Log.d(TAG, "Push Audio successfully.");
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //  Event helper
    //----------------------------------------------------------------------------------------------

    protected void createNewTeam(TeamModel teamModel, UserTeamModel user, Firebase ref) {
        if (ref == null || teamModel == null || user == null)
            return;

        String myTeamKey = user.device_uuid + "_" + System.currentTimeMillis();
        ref.child(myTeamKey).setValue(teamModel, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d(TAG, "Create Team error: " + firebaseError.getMessage());
                } else {
                    Log.d(TAG, "Create Team successfully");
                }
            }
        });

        addNewUserToTeam(user, ref);
    }

    protected void addNewUserToTeam(UserTeamModel user, Firebase ref) {
        if (ref == null || user == null)
            return;

        if (StringUtils.isNull(user.device_uuid)) {
            user.device_uuid = DeviceUtils.getDeviceUUID(mContext);
        }

        ref.child(Constant.SLUG_USERS).child(user.device_uuid)
                .setValue(user, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Log.d(TAG, "User Could not be saved. " + firebaseError.getMessage());
                        } else {
                            Log.d(TAG, "User saved successfully");
                        }
                    }
                });
    }

    protected static boolean isAudioValid(AudioModel audio) {
        return (audio != null && audio.size != null && audio.size > 0 && StringUtils.isNotNull(audio.encode));
    }

    //----------------------------------------------------------------------------------------------
    //  FireBase Api helper
    //----------------------------------------------------------------------------------------------

    protected class TeamValueListener implements ValueEventListener {

        private Firebase ref;
        private TeamModel teamModel;
        private UserTeamModel user;
        private CallbackListener<UserTeamModel> callback;

        public TeamValueListener(Firebase ref, TeamModel teamModel, UserTeamModel user, CallbackListener<UserTeamModel> callback) {
            this.teamModel = teamModel;
            this.callback = callback;
            this.user = user;
            this.ref = ref;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, dataSnapshot.toString());
            ref.removeEventListener(this);

            //Team has been created
            if (teamHasBeenCreated(dataSnapshot))
                return;

            //Create New Team
            createNewTeam(teamModel, user, ref);
            if (callback != null) {
                callback.onDone(user, null);
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.d(TAG, firebaseError.getMessage());
            if (callback != null) {
                callback.onDone(null, new IllegalAccessException(firebaseError.getMessage()));
            }
        }

        protected boolean teamHasBeenCreated(DataSnapshot usersSnapShot) {
            if (usersSnapShot == null || usersSnapShot.getValue() == null)
                return false;

            for (DataSnapshot userSnapshot : usersSnapShot.getChildren()) {
                //Check Team Name
                DataSnapshot userNameSnapshot = userSnapshot.child("team_name");
                String userName = userNameSnapshot.getValue(String.class);
                Log.d(TAG, "userName: " + userName);
                if (StringUtils.isNull(userName) || !teamModel.team_name.equals(userName))
                    continue;

                //Check Password
                DataSnapshot passwordSnapshot = userSnapshot.child("password");
                String password = passwordSnapshot.getValue(String.class);
                Log.d(TAG, "password: " + password);
                if (StringUtils.isNull(password) || !teamModel.password.equals(password)) {
                    if (callback != null) {
                        callback.onDone(null, new IllegalAccessException("Invalid password !"));
                    }
                    return true;
                }

                //Take subscribe
                String key = userSnapshot.getKey();
                Log.d(TAG, "key: " + key);
                CacheManager.saveStringCacheData(Constant.TEAM_KEY_CACHE_KEY, key);
                Firebase teamNode = ref.child(key);
                addNewUserToTeam(user, teamNode);
                if (callback != null) {
                    callback.onDone(user, null);
                }
                return true;
            }

            return false;
        }
    }
}
