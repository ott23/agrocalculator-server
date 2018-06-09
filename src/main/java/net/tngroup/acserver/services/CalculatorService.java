package net.tngroup.acserver.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.tngroup.acserver.models.Calculator;
import net.tngroup.acserver.models.CalculatorStatus;
import net.tngroup.acserver.models.Task;
import net.tngroup.acserver.repositories.CalculatorRepository;
import net.tngroup.acserver.repositories.CalculatorStatusRepository;
import net.tngroup.acserver.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service
public class CalculatorService {

    private CalculatorRepository calculatorRepository;
    private CalculatorStatusRepository calculatorStatusRepository;
    private TaskRepository taskRepository;

    private ChannelGroup channels;
    private HashMap<SocketAddress, ChannelId> channelMap;

    @Autowired
    public CalculatorService(CalculatorRepository calculatorRepository, CalculatorStatusRepository calculatorStatusRepository, TaskRepository taskRepository) {
        this.calculatorRepository = calculatorRepository;
        this.calculatorStatusRepository = calculatorStatusRepository;
        this.taskRepository = taskRepository;

        setAllNotActiveOnInit();

        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelMap = new HashMap<>();
    }

    /*
    Make all calculator not active on init
     */
    private void setAllNotActiveOnInit() {
        calculatorRepository.findAllByActive(true).forEach((calculator -> {
            calculator.setActive(false);
            calculatorRepository.save(calculator);
        }));
    }

    /*
    Handler of tasks
     */
    public void handleTasks() {
        taskRepository.findAll().forEach((task) -> {
            Calculator calculator = task.getCalculator();
            if (channelMap.containsKey(calculator.getAddress())) {
                Channel channel = channels.find(channelMap.get(calculator.getAddress()));

                // Forming json
                ObjectNode json = new ObjectMapper().createObjectNode();
                json.put("id", task.getId());
                json.put("type", task.getType());
                json.put("value", task.getValue());

                // Select message type by type
                if (task.getType().equals("key")) {
                    sendMessage(channel, json.toString(), null);
                } else {
                    sendMessage(channel, json.toString(), calculator.getKey());
                }
            }
        });
    }

    /*
    Event of connection
     */
    public void connected(Channel channel) {
        // Add to channel arrays
        channels.add(channel);
        channelMap.put(channel.remoteAddress(), channel.id());

        // Create new calculator if does not exist
        Calculator calculator = calculatorRepository.findClientByAddress(channel.remoteAddress());
        if (calculator == null) {
            calculator = new Calculator();
            calculator.setAddress(channel.remoteAddress());
        }

        // Set status "Active"
        calculator.setActive(true);
        calculatorRepository.save(calculator);

        CalculatorStatus calculatorStatus = new CalculatorStatus(calculator, "CONNECTED", new Date());
        calculatorStatusRepository.save(calculatorStatus);
    }

    /*
    Event of disconnection
     */
    public void disconnected(Channel channel) {
        // Remove from channel arrays
        channels.remove(channel);
        channelMap.remove(channel.remoteAddress());

        // Set status "Not active" if calculator exists
        Calculator calculator = calculatorRepository.findClientByAddress(channel.remoteAddress());
        if (calculator != null) {
            calculator.setActive(false);
            calculatorRepository.save(calculator);

            CalculatorStatus calculatorStatus = new CalculatorStatus(calculator, "DISCONNECTED", new Date());
            calculatorStatusRepository.save(calculatorStatus);
        }
    }

    /*
    Handler of message sending
     */
    private void sendMessage(Channel channel, String msg, String keyString) {
        try {
            // Encode message if key exists
            if (keyString != null) {
                byte[] decodedKey = Base64.getDecoder().decode(keyString);
                SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                msg = CipherService.aes256(msg, key, true);
            }
            // Sending message
            channel.writeAndFlush(msg);
        } catch (Exception e) {
            // Логгирование
        }
    }

    /*
    Event of new message
     */
    public void readMessage(Channel channel, String msg) {
        try {
            if (!needKeyMessage(channel, msg)) {
                decodeMessage(channel, msg);
            }
        } catch (Exception e) {
            // Логгирование
        }
    }

    /*
    If key is needed handler
     */
    private boolean needKeyMessage(Channel channel, String msg) throws Exception {
        try {
            // Decoding by base64 and parsing json
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonInput = objectMapper.readTree(Base64.getDecoder().decode(msg));
            String name = jsonInput.get("name").asText();

            Calculator calculator = calculatorRepository.findClientByAddress(channel.remoteAddress());
            if (calculator == null) throw new Exception("Unexpected error: calculator not found");

            // If new name, create new calculator
            if (!name.equals(calculator.getName())) {
                calculator.setArchive(true);
                calculatorRepository.save(calculator);
                calculator = new Calculator();
                calculator.setAddress(channel.remoteAddress());
                calculator.setName(name);
            }

            // Set needKey property
            calculator.setNeedKey(true);
            calculator.setKey(null);
            calculatorRepository.save(calculator);
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    /*
    Message decoder
     */
    private void decodeMessage(Channel channel, String msg) throws Exception {
        try {
            // Decoding
            Calculator calculator = calculatorRepository.findClientByAddress(channel.remoteAddress());
            byte[] decodedKey = Base64.getDecoder().decode(calculator.getKey());
            SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            msg = CipherService.aes256(msg, key, false);

            // Parsing json
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonInput = objectMapper.readTree(msg);
            String type = jsonInput.get("type").asText();
            String value = jsonInput.get("value").asText();

            // switch handler by type
            switch (type) {
                case "status":
                    // Alright!
                    break;
                case "confirm":
                    confirmationHandler(Integer.parseInt(value));
                    break;
                default:
                    throw new Exception("wrong message");
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new Exception("Unexpected error: decoding error");
        } catch (IOException e) {
            throw new Exception("Unexpected error: json parsing error");
        }
    }

    /*
    Confirmation handler
     */
    private void confirmationHandler(int id) {
        if (taskRepository.findById(id).isPresent()) {
            Task task = taskRepository.findById(id).get();
            task.setConfirmed(true);
            taskRepository.save(task);
        }
    }

}