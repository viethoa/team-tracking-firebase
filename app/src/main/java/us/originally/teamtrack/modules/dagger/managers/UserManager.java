package us.originally.teamtrack.modules.dagger.managers;

import us.originally.teamtrack.models.AudioModel;
import us.originally.teamtrack.models.Comment;
import us.originally.teamtrack.models.TeamModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.dagger.callback.CallbackListener;

/**
 * Created by VietHoa on 16/09/15.
 */
public interface UserManager {

    void loginOrSubscribe(TeamModel team, UserTeamModel user, CallbackListener<UserTeamModel> listener);

    void logout(UserTeamModel user);

    void updateUser(UserTeamModel user);

    void pushComment(Comment comment);

    void pushAudio(AudioModel audioModel);
}
