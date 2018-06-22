package net.tngroup.acserver.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.tngroup.acserver.models.Calculator;
import net.tngroup.acserver.models.CalculatorStatus;
import net.tngroup.acserver.models.Message;
import net.tngroup.acserver.models.Setting;
import net.tngroup.acserver.services.CalculatorService;
import net.tngroup.acserver.services.CalculatorStatusService;
import net.tngroup.acserver.services.SettingService;
import net.tngroup.acserver.services.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class NodeComponent {

    private Logger logger = LogManager.getFormatterLogger("ConsoleLogger");

    private CalculatorService calculatorService;
    private CalculatorStatusService calculatorStatusService;
    private TaskService taskService;
    private SettingService settingService;

    private ChannelGroup channels;
    private HashMap<SocketAddress, ChannelId> channelMap;

    @Autowired
    public NodeComponent(CalculatorService calculatorService,
                         CalculatorStatusService calculatorStatusService,
                         TaskService taskService,
                         SettingService settingService) {
        this.calculatorService = calculatorService;
        this.calculatorStatusService = calculatorStatusService;
        this.taskService = taskService;
        this.settingService = settingService;

        // Make all calculator not active on init
        calculatorService.updateAllActive(false);

        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelMap = new HashMap<>();
    }

    /*
    Handler of tasks
     */
    public void handleTasks() {
        taskService.getAllByConfirmed(false).forEach(t -> {
            Calculator calculator = t.getCalculator();
            if (channelMap.containsKey(calculator.getAddress())) {
                Channel channel = channels.find(channelMap.get(calculator.getAddress()));

                // Select message type by type
                if (t.getType().equals("key")) sendMessage(new Message(t), channel, null);
                else sendMessage(new Message(t), channel, calculator.getKey());
            }
        });
    }

    /*
    Event of connection
     */
    public void connected(Channel channel) {
        logger.info("Client with address '%s': connected", channel.remoteAddress().toString());

        // Add to channel arrays
        channels.add(channel);
        channelMap.put(channel.remoteAddress(), channel.id());
    }

    /*
    Event of disconnection
     */
    public void disconnected(Channel channel) {
        logger.info("Client with address '%s': disconnected", channel.remoteAddress().toString());

        // Remove from channel arrays
        channels.remove(channel);
        channelMap.remove(channel.remoteAddress());

        // Set status "Not active" if calculator exists
        Calculator calculator = calculatorService.getByAddressAndActive(channel.remoteAddress(), true);
        if (calculator != null) {
            calculatorService.updateActiveById(calculator.getId(), false);
            calculatorStatusService.add(new CalculatorStatus(calculator, "DISCONNECTED", new Date()));
        }

    }

    /*
    Handler of message sending
     */
    private void sendMessage(Message message, Channel channel, String key) {
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

    private void sendMessageWrongMessage(Channel channel) {
        sendMessage(new Message("wrong message", "wrong message", null), channel, null);
    }

    private void sendMessageProperties(Channel channel, Calculator calculator) {
        try {
            List<Setting> settingList = settingService.getAllByCalculatorId(calculator.getId());
            String json = new ObjectMapper().writeValueAsString(settingList);
            sendMessage(new Message("properties", json, null), channel, calculator.getKey());
        } catch (JsonProcessingException e) {
            logger.error("Error during json forming: %s", e.getMessage());
        }

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
            sendMessageWrongMessage(channel);
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
    private void messageHandler(Message message, SocketAddress address) {
        logger.info("Message from client '%s': %s", address.toString(), message.getType());

        Calculator calculator;

        if (!message.isEncoded()) {
            switch (message.getType()) {
                case "key request":
                    calculator = checkCalculator(message.getValue(), address);
                    calculatorService.updateKeyById(calculator.getId(), null);
                    return;
            }
        }

        if (message.isEncoded()) {
            switch (message.getType()) {
                case "status":
                    calculator = checkCalculator(message.getValue(), address);
                    calculatorStatusService.add(new CalculatorStatus(calculator, "CONNECTED", new Date()));
                    return;
                case "properties request":
                    calculator = checkCalculator(message.getValue(), address);
                    if (channelMap.containsKey(address)) {
                        Channel channel = channels.find(channelMap.get(address));
                        sendMessageProperties(channel, calculator);
                    }
                    return;
                case "confirm":
                    taskService.updateConfirmedById(message.getId(), true);
                    return;
            }
        }

        if (channelMap.containsKey(address)) {
            Channel channel = channels.find(channelMap.get(address));
            sendMessageWrongMessage(channel);
        }
    }

    /*
    Check name
     */
    private Calculator checkCalculator(String name, SocketAddress address) {

        // Make all calculator with the same address archived
        calculatorService.updateAllArchiveByAddress(address, true);

        Calculator calculator = calculatorService.getByName(name);

        if (calculator != null) {
            if (!calculator.getAddress().equals(address)) calculator.setAddress(address);
        } else {
            calculator = new Calculator();
            calculator.setName(name);
            calculator.setAddress(address);
        }

        calculator.setActive(true);
        calculator.setArchive(false);

        calculatorService.addOrUpdate(calculator);

        return calculator;
    }

}