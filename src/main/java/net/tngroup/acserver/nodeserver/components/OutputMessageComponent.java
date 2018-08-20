package net.tngroup.acserver.nodeserver.components;

import io.netty.channel.Channel;
import net.tngroup.acserver.web.components.CipherComponent;
import net.tngroup.acserver.databases.h2.models.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class OutputMessageComponent {

    private Logger logger = LogManager.getFormatterLogger("CommonLogger");

    private CipherComponent cipherComponent;

    @Autowired
    public OutputMessageComponent(CipherComponent cipherComponent) {
        this.cipherComponent = cipherComponent;
    }

    /*
    Handler of message sending
    */
    public void sendMessage(Channel channel, String key, Message message) {
        try {
            logger.info("Sending message to  '%s': %s", channel.remoteAddress().toString(), message.getType());

            String msg = message.formJson();
            if (key != null) msg = cipherComponent.encodeDes(msg, key);
            else msg = Base64.getEncoder().encodeToString(msg.getBytes());
            String result_msg = "-" + msg.length() + "-" + msg;

            channel.writeAndFlush(result_msg);
        } catch (Exception e) {
            logger.error("Error during message sending: %s", e.getMessage());
        }
    }

    void sendMessageWrongMessage(Channel channel) {
        sendMessage(channel, null, new Message(null, "wrong message", null, null));
    }

}
