package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.database.cassandra.models.Client;
import net.tngroup.acserver.database.cassandra.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/client")
public class ClientController {

    private ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    private ResponseEntity okResponse(Object o) throws JsonProcessingException {
        String response = new ObjectMapper().writeValueAsString(o);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity successResponse() {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        jsonResponse.put("response", "Success");
        String response = jsonResponse.toString();
        return ResponseEntity.ok(response);
    }

    private ResponseEntity badResponse(Exception e) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        jsonResponse.put("response", "Server error: " + e.getMessage());
        String response = jsonResponse.toString();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity conflictResponse() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @RequestMapping
    public ResponseEntity getList() {
        try {
            List<Client> clientList = clientService.getAll();
            return okResponse(clientList);
        } catch (JsonProcessingException e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/save")
    public ResponseEntity save(@RequestBody String jsonRequest) {
        try {
            Client client = new ObjectMapper().readValue(jsonRequest, Client.class);

            List<Client> clientList = clientService.getAllByName(client.getName());
            if (clientList.size() == 1 && !clientList.get(0).getId().equals(client.getId()) || clientList.size() > 1) return conflictResponse();

            if (client.getId() == null) client.setId(UUID.randomUUID());
            clientService.save(client);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable UUID id) {
        try {
            clientService.deleteById(id);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }
}
