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
    If encodedKey is needed handler
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
            Calculator calculator = calculatorService.getByAddressAndConnection(address, true);
            if (calculator == null) throw new Exception("Unexpected error: calculator not found");

            msg = CipherComponent.decodeDes(msg, calculator.getEncodedKey());
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

        // Key request
        if (message.getType().equals("key request") && !message.isEncoded()) {
            keyRequestEvent(message, address);
            return;
        }
        // Status
        if (message.getType().equals("status") && message.isEncoded()) {
            statusEvent(message, address);
            return;
        }
        // Settings request
        if (message.getType().equals("settings request") && message.isEncoded()) {
            propertiesRequestEvent(message, address);
            return;
        }
        // Confirm
        if (message.getType().equals("confirm") && message.isEncoded()) {
            confirmEvent(message, address);
            return;
        }

        throw new Exception("Message type not found");
    }

    private void keyRequestEvent(Message message, SocketAddress address) {
        Calculator calculator = statusComponent.checkCalculator(message.getValue(), address);
        calculatorService.updateKeyById(calculator.getId(), false);
        calculatorService.updateEncodedKeyById(calculator.getId(), null);
        calculatorStatusService.add(new CalculatorStatus(calculator, "CONNECTED", new Date()));
    }

    private void propertiesRequestEvent(Message message, SocketAddress address) {
        try {
            Calculator calculator = statusComponent.checkCalculator(message.getValue(), address);
            List<Setting> settingList = settingService.getAllByCalculatorId(calculator.getId());
            String json = new ObjectMapper().writeValueAsString(settingList);
            taskService.add(new Task(calculator, "settings", json));
        } catch (JsonProcessingException e) {
            logger.error("Error during json forming: %s", e.getMessage());
        }
    }

    private void statusEvent(Message message, SocketAddress address) {
        Calculator calculator = statusComponent.checkCalculator(message.getValue(), address);
        if (message.getId().equals(1)) calculatorService.updateStatusById(calculator.getId(), true);
        else calculatorService.updateStatusById(calculator.getId(), false);
        calculatorStatusService.add(new CalculatorStatus(calculator, "CONNECTED", new Date()));
    }

    private void confirmEvent(Message message, SocketAddress address) throws Exception {
        taskService.updateConfirmedById(message.getId(), true);

        Task task = taskService.getById(message.getId());
        Calculator calculator = calculatorService.getByAddressAndConnection(address, true);

        // Start confirm event
        if (task.getValue().equals("start")) {
            calculatorService.updateStatusById(calculator.getId(), true);
            return;
        }
        // Stop confirm event
        if (task.getValue().equals("stop")) {
            calculatorService.updateStatusById(calculator.getId(), false);
            return;
        }
        // Destroy confirm event
        if (task.getValue().equals("destroy")) {
            calculatorService.updateArchiveById(calculator.getId(), true);
            return;
        }
        // Key confirm event
        if (message.getValue().equals("key")) {
            calculatorService.updateKeyById(calculator.getId(), true);
            return;
        }

        // Shutdown
        if (message.getValue().equals("shutdown")) {
            return;
        }

        // Settings
        if (message.getValue().equals("settings")) {
            return;
        }

        throw new Exception("Confirmation error");
    }

}
