package net.tngroup.acserver.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.tngroup.acserver.models.Calculator;
import net.tngroup.acserver.models.CalculatorStatus;
import net.tngroup.acserver.models.Message;
import net.tngroup.acserver.services.CalculatorService;
import net.tngroup.acserver.services.CalculatorStatusService;
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
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NodeComponent {

    private Logger logger = LogManager.getFormatterLogger("ConsoleLogger");

    private CalculatorService calculatorService;
    private CalculatorStatusService calculatorStatusService;
    private TaskService taskService;

    private ChannelGroup channels;
    private HashMap<SocketAddress, ChannelId> channelMap;

    @Autowired
    public NodeComponent(CalculatorService calculatorService,
                         CalculatorStatusService calculatorStatusService,
                         TaskService taskService) {
        this.calculatorService = calculatorService;
        this.calculatorStatusService = calculatorStatusService;
        this.taskService = taskService;

        setAllNotActiveOnInit();

        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelMap = new HashMap<>();
    }

    /*
    Make all calculator not active on init
     */
    private void setAllNotActiveOnInit() {
        calculatorService.updateAllActive(false);
    }

    /*
    Handler of tasks
     */
    public void handleTasks() {
        taskService.getAllByConfirmed(false).forEach(t -> {
            Calculator calculator = t.getCalculator();
            if (channelMap.containsKey(calculator.getAddress())) {
                Channel channel = channels.find(channelMap.get(calculator.getAddress()));

                logger.info("Sending a '%s' - '%s' to '%s'", t.getType(), t.getValue(), channel.remoteAddress().toString());

                String json = new Message(t).formJson();

                // Select message type by type
                if (t.getType().equals("key")) sendMessage(channel, json, null);
                else sendMessage(channel, json, calculator.getKey());
            }
        });
    }

    /*
    Event of connection
     */
    public void connected(Channel channel) {
        logger.info("Client with address '%s' connected", channel.remoteAddress().toString());

        // Add to channel arrays
        channels.add(channel);
        channelMap.put(channel.remoteAddress(), channel.id());
    }

    /*
    Event of disconnection
     */
    public void disconnected(Channel channel) {
        logger.info("Client with address '%s' disconnected", channel.remoteAddress().toString());

        // Remove from channel arrays
        channels.remove(channel);
        channelMap.remove(channel.remoteAddress());

        // Set status "Not active" if calculator exists
        Calculator calculator = calculatorService.getByAddressAndActive(channel.remoteAddress(), false);
        if (calculator != null) {
            calculatorService.updateActiveById(calculator.getId(), false);
            calculatorStatusService.add(new CalculatorStatus(calculator, "DISCONNECTED", new Date()));
        }

    }

    /*
    Handler of message sending
     */
    private void sendMessage(Channel channel, String msg, String keyString) {
        try {
            // Encode message if key exists
            if (keyString != null) msg = CipherComponent.encodeDes(msg, keyString);
            else msg = Base64.getEncoder().encodeToString(msg.getBytes());

            String result_msg = new StringBuilder().append("-").append(msg.length()).append("-").append(msg).toString();

            // Sending message
            channel.writeAndFlush(result_msg);
        } catch (Exception e) {
            logger.error("Error during message sending: %s", e.getMessage());
        }
    }

    /*
    Event of new message
     */
    public void readMessage(Channel channel, String msg) {
        try {
            Pattern p = Pattern.compile("-[0-9]+-");
            Matcher m = p.matcher(msg);

            while (m.find()) {
                String result_msg;
                String lengthString = msg.substring(m.start() + 1, m.end() - 1);
                int length = Integer.parseInt(lengthString);
                if (msg.length() < m.end() + length) throw new Exception("Length error");
                else result_msg = msg.substring(m.end(), m.end() + length);

                SocketAddress address = channel.remoteAddress();
                if (!base64Message(address, result_msg)) decMessage(address, result_msg);
            }
        } catch (Exception e) {
            logger.error("Error during message reading: %s", e.getMessage());
            // Answer about wrong message
            sendWrongMessage(channel);
        }
    }

    /*
    If key is needed handler
     */
    private boolean base64Message(SocketAddress address, String msg) {
        try {
            Calculator calculator;

            // Decoding by base64Message and parsing json
            msg = new String(Base64.getDecoder().decode(msg));
            Message message = new Message(msg);

            logger.info("Message from address '%s' received: %s", address.toString(), message.getType());

            switch (message.getType()) {
                case "status":
                    calculator = checkCalculator(message.getValue(), address);
                    calculatorStatusService.add(new CalculatorStatus(calculator, "CONNECTED", new Date()));
                    return true;
                case "key request":
                    calculator = checkCalculator(message.getValue(), address);
                    genKey(calculator.getId());
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /*
    Check name
     */
    private Calculator checkCalculator(String name, SocketAddress address) {

        // Make all calculator with the same address archived
        calculatorService.updateAllArchiveByAddress(address, true);

        Calculator calculator =calculatorService.getByName(name);

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

    /*
    Message decoder
     */
    private void decMessage(SocketAddress address, String msg) throws Exception {
        try {
            Calculator calculator = calculatorService.getByAddressAndActive(address, true);
            if (calculator == null) throw new Exception("Unexpected error: calculator not found");

            // Decoding
            msg = CipherComponent.decodeDes(msg, calculator.getKey());
            Message message = new Message(msg);

            logger.info("Message from address '%s' received: %s", calculator.getAddress().toString(), message.getType());

            // switch handler by type
            switch (message.getType()) {
                case "properties request":
                    // Alright!
                    break;
                case "confirm":
                    confirmationHandler(message.getId());
                    break;
                default:
                    throw new Exception("wrong message");
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            throw new Exception("Unexpected error: decoding error");
        } catch (IOException e) {
            throw new Exception("Unexpected error: json parsing error");
        } catch (Exception e) {
            throw new Exception("Unexpected error: " + e.getMessage());
        }
    }

    /*
    Confirmation handler
     */
    private void confirmationHandler(int id) {
        taskService.updateConfirmedById(id, true);
    }

    private void sendWrongMessage(Channel channel) {
        logger.info("Sending a wrong message to '%s'", channel.remoteAddress().toString());

        ObjectNode jsonOutput = new ObjectMapper().createObjectNode();
        jsonOutput.put("type", "wrong message");
        jsonOutput.put("value", "");
        jsonOutput.put("id", "");
        sendMessage(channel, jsonOutput.toString(), null);
    }

    private void genKey(int id) {
        calculatorService.updateKeyById(id,null);
    }

}