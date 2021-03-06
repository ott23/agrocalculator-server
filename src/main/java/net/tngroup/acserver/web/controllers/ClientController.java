package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.acserver.databases.cassandra.models.Client;
import net.tngroup.acserver.databases.cassandra.services.ClientService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

import static net.tngroup.acserver.web.controllers.Responses.*;

@RestController
@Lazy
@RequestMapping("/client")
public class ClientController {

    private ClientService clientService;

    public ClientController(@Lazy ClientService clientService) {
        this.clientService = clientService;
    }

    @RequestMapping
    public ResponseEntity getList(HttpServletRequest request) {
        try {
            List<Client> clientList = clientService.getAll();
            return okResponse(clientList);
        } catch (JsonProcessingException e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/save")
    public ResponseEntity save(HttpServletRequest request, @RequestBody String jsonRequest) {
        try {
            Client client = new ObjectMapper().readValue(jsonRequest, Client.class);

            List<Client> clientList = clientService.getAllByName(client.getName());
            if (clientList.size() == 1 && !clientList.get(0).getId().equals(client.getId()) || clientList.size() > 1)
                return Responses.conflictResponse("name");

            if (client.getId() == null) client.setId(UUID.randomUUID());
            clientService.save(client);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(HttpServletRequest request, @PathVariable UUID id) {
        try {
            clientService.deleteById(id);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }
}
