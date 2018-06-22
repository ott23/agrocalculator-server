package net.tngroup.acserver.server.components;

import io.netty.channel.Channel;
import net.tngroup.acserver.components.CipherComponent;
import net.tngroup.acserver.models.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class OutputMessageComponent {

    private Logger logger = LogManager.getFormatterLogger("ConsoleLogger");

    private OutputMessageComponent() {}

    /*
    Handler of message sending
    */
    public void sendMessage(Message message, Channel channel, String key) {
        try {
            logger.info("Sending a '%s' message to '%s'", message.getType(), channel.remoteAddress().toString());

            String msg = message.formJson();
            if (key != null) msg = CipherComponent.encodeDes(msg, key);
            else msg = Base64.getEncoder().encodeToString(msg.getBytes());
            String result_msg = "-" + msg.length() + "-" + msg;

            channel.writeAndFlush(result_msg);
        } catch (Exception e) {
            logger.error("Error during message sending: %s", e.getMessage());
        }
    }

    void sendMessageWrongMessage(Channel channel) {
        sendMessage(new Message("wrong message", "wrong message", null), channel, null);
    }

}
