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

    void logout(TeamModel team, UserTeamModel user);

    void updateUser(TeamModel team, UserTeamModel user);

    void pushComment(Comment comment, TeamModel team);

    void pushAudio(AudioModel audioModel, TeamModel mTeam);
}
