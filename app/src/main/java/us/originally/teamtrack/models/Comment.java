package us.originally.teamtrack.models;

import java.io.Serializable;

/**
 * Created by VietHoa on 14/09/15.
 */
public class Comment implements Serializable {

    public Integer id;
    public String message;
    public UserTeamModel user;

    public Comment() {
    }

    public Comment(String message, UserTeamModel user) {
        this.message = message;
        this.user = user;
    }
}
