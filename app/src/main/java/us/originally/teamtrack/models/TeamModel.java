package us.originally.teamtrack.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by VietHoa on 10/09/15.
 */
public class TeamModel implements Serializable {

    public String team_name;
    public String password;
    public List<UserTeamModel> users;
    public List<Comment> messages;
    public long update_timestamp;

    public TeamModel() {
    }

    public TeamModel(String team, String password, long update_timestamp) {
        this.team_name = team;
        this.password = password;
        this.update_timestamp = update_timestamp;
    }
}
