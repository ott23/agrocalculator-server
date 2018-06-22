package net.tngroup.acserver.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.io.IOException;

@Data
public class Message {

    private String type;

    private String value;

    private Integer id;

    private boolean encoded = false;

    public Message(Task task) {
        type = task.getType();
        value = task.getValue();
        id = task.getId();
    }

    public Message(String type, String value, Integer id) {
        this.type = type;
        this.value = value;
        this.id = id;
    }

    public Message(String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonInput = objectMapper.readTree(message);
        type = jsonInput.get("type").asText();
        value = jsonInput.get("value").asText();
        id = jsonInput.get("id").asInt();
        encoded = jsonInput.get("encoded").asBoolean();
    }

    public String formJson() {
        ObjectNode json = new ObjectMapper().createObjectNode();
        json.put("id", id);
        json.put("type", type);
        json.put("value", value);
        json.put("encoded", encoded);
        return  json.toString();
    }
}
