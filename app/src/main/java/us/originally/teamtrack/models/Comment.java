package us.originally.teamtrack.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by VietHoa on 14/09/15.
 */
public class Comment implements Serializable, Comparable<Comment> {

    public Integer id;
    public String message;
    public long time_stamp;
    public UserTeamModel user;
    public List<AudioModel> audios;

    public Comment() {
    }

    public Comment(int id, long time_stamp, String message, List<AudioModel> audios, UserTeamModel user) {
        this.time_stamp = time_stamp;
        this.message = message;
        this.audios = audios;
        this.user = user;
        this.id = id;
    }

    @Override
    public int compareTo(Comment comment) {
        int compareQuantity = comment.id;

        //ascending order
        return this.id - compareQuantity;

        //descending order
        //return compareQuantity - this.id.intValue();
    }
}
