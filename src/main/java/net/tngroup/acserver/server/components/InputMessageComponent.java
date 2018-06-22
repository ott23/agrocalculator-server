package net.tngroup.acserver.server.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import net.tngroup.acserver.components.CipherComponent;
import net.tngroup.acserver.models.*;
import net.tngroup.acserver.services.CalculatorService;
import net.tngroup.acserver.services.CalculatorStatusService;
import net.tngroup.acserver.services.SettingService;
import net.tngroup.acserver.services.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InputMessageComponent {

    private Logger logger = LogManager.getFormatterLogger("ConsoleLogger");

    private CalculatorService calculatorService;
    private CalculatorStatusService calculatorStatusService;
    private TaskService taskService;
    private SettingService settingService;
    private StatusComponent statusComponent;
    private OutputMessageComponent outputMessageComponent;

    @Autowired
    public InputMessageComponent(CalculatorService calculatorService,
                                 CalculatorStatusService calculatorStatusService,
                                 TaskService taskService,
                                 SettingService settingService,
                                 StatusComponent statusComponent,
                                 OutputMessageComponent outputMessageComponent) {
        this.calculatorService = calculatorService;
        this.calculatorStatusService = calculatorStatusService;
        this.taskService = taskService;
        this.settingService = settingService;
        this.statusComponent = statusComponent;
        this.outputMessageComponent = outputMessageComponent;
    }

    /*
    Event of new message
    */
    private Map<Channel, String> messageCacheMap = new HashMap<>();

    public void readMessage(Channel channel, String msg) {
        try {
            if (messageCacheMap.containsKey(channel)) msg = messageCacheMap.remove(channel) + msg;

            Pattern p = Pattern.compile("-[0-9]+-");
            Matcher m = p.matcher(msg);

            while (m.find()) {
                String result_msg;
                String lengthString = msg.substring(m.start() + 1, m.end() - 1);
                int length = Integer.parseInt(lengthString);

                if (msg.length() == m.end() + length) {

                    result_msg = msg.substring(m.end(), m.end() + length);

                    SocketAddress address = channel.remoteAddress();
                    Message message = base64Message(result_msg);
                    if (message == null) {
                        message = decMessage(result_msg, address);
                        message.setEncoded(true);
                    }

                    messageHandler(message, address);
                } else messageCacheMap.put(channel, msg.substring(m.start(), msg.length()));

            }
        } catch (Exception e) {
            logger.error("Error during message reading: %s", e.getMessage());
            outputMessageComponent.sendMessageWrongMessage(channel);
        }
    }

    /*
    If key is needed handler
    */
    private Message base64Message(String msg) {
        try {
            msg = new String(Base64.getDecoder().decode(msg));
            return new Message(msg);
        } catch (Exception e) {
            return null;
        }
    }

    /*
    Message decoder
     */
    private Message decMessage(String msg, SocketAddress address) throws Exception {
        try {
            Calculator calculator = calculatorService.getByAddressAndActive(address, true);
            if (calculator == null) throw new Exception("Unexpected error: calculator not found");

            msg = CipherComponent.decodeDes(msg, calculator.getKey());
            return new Message(msg);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new Exception("Unexpected error: decoding error");
        } catch (IOException e) {
            throw new Exception("Unexpected error: json parsing error");
        } catch (Exception e) {
            throw new Exception("Unexpected error: wrong message");
        }
    }

    /*
    Message handler
     */
    private void messageHandler(Message message, SocketAddress address) throws Exception {
        logger.info("Message from client '%s': %s", address.toString(), message.getType());

        if (!message.isEncoded()) {
            switch (message.getType()) {
                case "key request":
                    keyRequestEvent(message, address);
                    return;
            }
        }

        if (message.isEncoded()) {
            switch (message.getType()) {
                case "status":
                    statusEvent(message, address);
                    return;
                case "properties request":
                    propertiesRequestEvent(message, address);
                    return;
                case "confirm":
                    confirmEvent(message);
                    return;
            }
        }

        throw new Exception("Message type not found");
    }

    private void keyRequestEvent(Message message, SocketAddress address) {
        Calculator calculator = statusComponent.checkCalculator(message.getValue(), address);
        calculatorService.updateKeyById(calculator.getId(), null);
    }

    private void propertiesRequestEvent(Message message, SocketAddress address) {
        try {
            Calculator calculator = statusComponent.checkCalculator(message.getValue(), address);
            List<Setting> settingList = settingService.getAllByCalculatorId(calculator.getId());
            String json = new ObjectMapper().writeValueAsString(settingList);
            taskService.add(new Task(calculator, "properties", json));
        } catch (JsonProcessingException e) {
            logger.error("Error during json forming: %s", e.getMessage());
        }
    }

    private void statusEvent(Message message, SocketAddress address) {
        Calculator calculator = statusComponent.checkCalculator(message.getValue(), address);
        calculatorStatusService.add(new CalculatorStatus(calculator, "CONNECTED", new Date()));
    }

    private void confirmEvent(Message message) {
        taskService.updateConfirmedById(message.getId(), true);
    }

}
