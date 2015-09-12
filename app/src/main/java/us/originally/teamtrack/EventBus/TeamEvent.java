package us.originally.teamtrack.EventBus;

import us.originally.teamtrack.models.TeamModel;

/**
 * Created by VietHoa on 12/09/15.
 */
public class TeamEvent {

    public TeamModel team;

    public TeamEvent(TeamModel team) {
        this.team = team;
    }
}
