package us.originally.teamtrack.modules.chat;

import com.lorem_ipsum.utils.StringUtils;

import java.io.Serializable;
import java.util.List;

import us.originally.teamtrack.modules.chat.audio.AudioModel;

/**
 * Created by VietHoa on 04/09/15.
 */
public class MessageModel implements Serializable {

    public String channel_name;

    public Integer id;
    public String message;
    public List<AudioModel> audio;

    public MessageModel() {
    }

    public MessageModel(String channel_name, int id, String message, List<AudioModel> audio) {
        this.id = id;
        this.message = message;
        this.audio = audio;
        this.channel_name = channel_name;
    }

    public boolean isValid() {
        if (id == null)
            return false;

        boolean isNotNull = StringUtils.isNotNull(message);
        if (!isNotNull) {
            isNotNull = (audio != null && audio.size() > 0);
        }

        return isNotNull;
    }
}
