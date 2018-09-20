package net.tngroup.acserver.web.controllers;

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

import static net.tngroup.common.responses.Responses.*;

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

        List<Client> clientList = clientService.getAll();
        return okResponse(clientList);

    }

    @RequestMapping("/save")
    public ResponseEntity save(HttpServletRequest request, @RequestBody Client client) {
        try {

            List<Client> clientList = clientService.getAllByName(client.getName());
            if (clientList.size() == 1 && !clientList.get(0).getId().equals(client.getId()) || clientList.size() > 1)
                return conflictResponse("name");

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
