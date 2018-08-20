package net.tngroup.acserver.nodeserver.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import net.tngroup.acserver.web.components.CipherComponent;
import net.tngroup.acserver.databases.h2.models.*;
import net.tngroup.acserver.databases.h2.services.NodeService;
import net.tngroup.acserver.databases.h2.services.NodeStatusService;
import net.tngroup.acserver.databases.h2.services.SettingService;
import net.tngroup.acserver.databases.h2.services.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class InputMessageComponent {

    private Logger logger = LogManager.getFormatterLogger("CommonLogger");

    private NodeService nodeService;
    private NodeStatusService nodeStatusService;
    private TaskService taskService;
    private SettingService settingService;
    private StatusComponent statusComponent;
    private OutputMessageComponent outputMessageComponent;
    private CipherComponent cipherComponent;

    @Autowired
    public InputMessageComponent(NodeService nodeService,
                                 NodeStatusService nodeStatusService,
                                 TaskService taskService,
                                 SettingService settingService,
                                 StatusComponent statusComponent,
                                 OutputMessageComponent outputMessageComponent,
                                 CipherComponent cipherComponent) {
        this.nodeService = nodeService;
        this.nodeStatusService = nodeStatusService;
        this.taskService = taskService;
        this.settingService = settingService;
        this.statusComponent = statusComponent;
        this.outputMessageComponent = outputMessageComponent;
        this.cipherComponent = cipherComponent;
    }

    /*
    Event of new message
    */
    private Map<Channel, String> messageCacheMap = new HashMap<>();

    public Message readMessage(Channel channel, String msg) throws Exception {
        try {
            if (messageCacheMap.containsKey(channel)) msg = messageCacheMap.remove(channel) + msg;

            Pattern p = Pattern.compile("-[0-9]+-");
            Matcher m = p.matcher(msg);

            Message message = null;

            while (m.find()) {
                String result_msg;
                String lengthString = msg.substring(m.start() + 1, m.end() - 1);
                int length = Integer.parseInt(lengthString);

                if (msg.length() == m.end() + length) {

                    result_msg = msg.substring(m.end(), m.end() + length);

                    InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
                    message = base64Message(result_msg);
                    if (message == null) {
                        message = decMessage(result_msg, address);
                        message.setEncoded(true);
                    }

                    messageHandler(message, address);
                } else messageCacheMap.put(channel, msg.substring(m.start(), msg.length()));
            }

            return message;

        } catch (Exception e) {
            outputMessageComponent.sendMessageWrongMessage(channel);
            throw e;
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
    private Message decMessage(String msg, InetSocketAddress address) throws Exception {
        try {
            Node node = nodeService.getByAddressAndConnection(address, true);
            if (node == null) {
                List<Node> nodes = nodeService.getAllByAddress(address);
                for (Node n : nodes) {
                    try {
                        msg = cipherComponent.decodeDes(msg, n.getKey());
                        return new Message(msg);
                    } catch (Exception e) {
                        // Key is not correct
                    }
                }
                throw new Exception("node not found");
            } else {
                msg = cipherComponent.decodeDes(msg, node.getKey());
                return new Message(msg);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new Exception("Unexpected error: decoding error");
        } catch (IOException e) {
            throw new Exception("Unexpected error: json parsing error");
        } catch (Exception e) {
            throw new Exception("Unexpected error: " + e.getMessage());
        }
    }

    /*
    Message handler
     */
    private void messageHandler(Message message, InetSocketAddress address) throws Exception {

        // Key request
        if (message.getType().equals("key request") && !message.isEncoded()) {
            keyRequestEvent(message, address);
        }
        // Status
        else if (message.getType().equals("status") && message.isEncoded()) {
            statusEvent(message, address);
        }
        // Settings request
        else if (message.getType().equals("settings request") && message.isEncoded()) {
            propertiesRequestEvent(message, address);
        }
        // Confirm
        else if (message.getType().equals("confirm") && message.isEncoded()) {
            confirmEvent(message, address);
        } else throw new Exception("Message type not found");
    }

    private void keyRequestEvent(Message message, InetSocketAddress address) {
        Node node = statusComponent.checkNode(message.getCode(), message.getValue(), address);
        nodeService.updateKeyById(node.getId(), null);
        nodeStatusService.save(new NodeStatus("CONNECTED", new Date(), node));
    }

    private void propertiesRequestEvent(Message message, InetSocketAddress address) throws Exception {
        Node node = statusComponent.checkNode(message.getCode(), message.getValue(), address);
        List<Setting> settingList = settingService.getAllByCalculatorId(node.getId());
        settingList = settingList.stream().peek(setting -> setting.setNode(null)).collect(Collectors.toList());
        String json = new ObjectMapper().writeValueAsString(settingList);
        taskService.save(new Task(node, "settings", json));
    }

    private void statusEvent(Message message, InetSocketAddress address) {
        Node node = statusComponent.checkNode(message.getCode(), message.getValue(), address);
        if (message.getId().equals(1)) nodeService.updateStatusById(node.getId(), true);
        else nodeService.updateStatusById(node.getId(), false);
        nodeStatusService.save(new NodeStatus("CONNECTED", new Date(), node));
    }

    private void confirmEvent(Message message, InetSocketAddress address) throws Exception {
        taskService.updateConfirmedById(message.getId(), true);

        Task task = taskService.getById(message.getId());
        Node node = nodeService.getByAddressAndConnection(address, true);

        // Start confirm event
        if (task.getValue().equals("start")) {
            nodeService.updateStatusById(node.getId(), true);
            return;
        }
        // Stop confirm event
        if (task.getValue().equals("stop")) {
            nodeService.updateStatusById(node.getId(), false);
            return;
        }
        // Destroy confirm event
        if (task.getValue().equals("destroy")) {
            nodeService.updateArchiveById(node.getId(), true);
            return;
        }
        // Key confirm event
        if (task.getType().equals("key")) {
            return;
        }

        // Shutdown
        if (task.getValue().equals("shutdown")) {
            return;
        }

        // Settings
        if (task.getType().equals("settings")) {
            return;
        }

        throw new Exception("Confirmation error");
    }

}
