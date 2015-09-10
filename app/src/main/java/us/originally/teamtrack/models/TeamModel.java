package us.originally.teamtrack.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by VietHoa on 10/09/15.
 */
public class TeamModel implements Serializable {

    public String device_uuid;
    public String team_name;
    public String password;
    public List<TeamUser> users;

    public TeamModel() {
    }

    public TeamModel(String id, String team, String password) {
        this.device_uuid = id;
        this.team_name = team;
        this.password = password;
    }
}
