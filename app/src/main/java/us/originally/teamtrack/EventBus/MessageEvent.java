package us.originally.teamtrack.EventBus;

import us.originally.teamtrack.modules.chat.MessageModel;

/**
 * Created by VietHoa on 05/09/15.
 */
public class MessageEvent {

    private MessageModel messageModel;

    public MessageEvent(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }
}
