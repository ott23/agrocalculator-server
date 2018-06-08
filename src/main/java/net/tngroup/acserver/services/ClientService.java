package net.tngroup.acserver.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.tngroup.acserver.models.Client;
import net.tngroup.acserver.models.ClientStatus;
import net.tngroup.acserver.models.Task;
import net.tngroup.acserver.repositories.ClientRepository;
import net.tngroup.acserver.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    TaskRepository taskRepository;

    private ChannelGroup channels;
    private HashMap<String, ChannelId> channelMap;

    public ClientService() {
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelMap = new HashMap<>();
    }

    private Set<Client> reactivatedClientList = new HashSet<>();

    public void performTasks() {
        List<Task> taskList = taskRepository.findAll();
        for (Task task : taskList) {
            Client client = task.getClient();
            if (channelMap.containsKey(client.getAddress())) {
                Channel channel = channels.find(channelMap.get(client.getAddress()));

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode json = mapper.createObjectNode();
                json.put("type", task.getType());
                json.put("value", task.getValue());

                sendMessage(channel, client.getKey(), json.toString());
            }
        }
    }

    public void sendMessage(Channel channel, SecretKey key, String msg) {
        try {
            String encodedMsg = CipherService.aes256(msg, key, true);
            channel.writeAndFlush(encodedMsg);
        } catch (Exception e) {
            // Логгирование
        }
    }

    public void newActiveClient(Channel channel) {
        String address = channel.remoteAddress().toString();
        Client client = clientRepository.findClientByAddress(address);
        if (client == null) {
            client = new Client(address);
            client.setAccepted(false);
            clientRepository.save(client);
        } else {
            reactivatedClientList.add(client);
        }
        channels.add(channel);
        channelMap.put(channel.remoteAddress().toString(), channel.id());
    }

    public void newMessage(Channel channel, String msg) {
        try {
            String address = channel.remoteAddress().toString();
            Client client = clientRepository.findClientByAddress(address);
            if (client != null) {
                String decodedMsg = CipherService.aes256(msg, client.getKey(), false);
                JsonNode json = new ObjectMapper().readTree(decodedMsg);
                String message = json.get("type").asText();

                switch (message) {
                    case "status":
                        if (reactivatedClientList.contains(client)) {
                            reactivatedClientList.remove(client);
                            client.getStatuses().add(new ClientStatus("CONNECTED", new Date()));
                        }
                        client.setLastStatus(new Date());
                        clientRepository.save(client);
                        break;
                    case "confirm":
                        String value = json.get("value").asText();
                        if (value.equals("accepted")) {
                            client.getStatuses().add(new ClientStatus("CONNECTED", new Date()));
                        }
                        int taskId = json.get("id").asInt();
                        taskRepository.deleteById(taskId);
                        client.setConfirmed(true);
                        clientRepository.save(client);
                        break;
                    default:
                        // Логгирование
                        throw new Exception("wrong message");
                }
            }
        } catch (Exception e) {
            channel.writeAndFlush("wrong request");
        }
    }

    public void newInactiveClient(Channel channel) {
        String address = channel.remoteAddress().toString();
        Client client = clientRepository.findClientByAddress(address);
        if (client != null && client.isAccepted()) {
            client.getStatuses().add(new ClientStatus("DISCONNECTED", new Date()));
            clientRepository.save(client);
        }
        channels.remove(channel);
        channelMap.remove(channel.remoteAddress().toString());
    }
}