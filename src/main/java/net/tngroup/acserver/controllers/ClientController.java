package net.tngroup.acserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.acserver.models.Client;
import net.tngroup.acserver.models.Task;
import net.tngroup.acserver.repositories.ClientRepository;
import net.tngroup.acserver.repositories.TaskRepository;
import net.tngroup.acserver.services.CipherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    TaskRepository taskRepository;

    @RequestMapping
    public String getList() {
        String response;
        try {
            List<Client> clientList = clientRepository.findAllByIsAccepted(true);
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.writeValueAsString(clientList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            response = "Server error";
        }
        return response;
    }

    @RequestMapping("/acceptById/{id}")
    public String acceptById(@PathVariable int id) {
        String response;
        try {
            if (clientRepository.findById(id).isPresent()) {
                Client client = clientRepository.findById(id).get();
                if (client.isAccepted()) {
                    throw new Exception("Client is already accepted");
                }
                client.setKey(CipherService.generateAes256Key());
                client.setConfirmed(false);
                clientRepository.save(client);
                Task task = new Task(client, "accept");
                task.setValue(client.getKey().toString());
                taskRepository.save(task);
                response = "Success";
            } else {
                throw new Exception("Client not found");
            }
        } catch (Exception e) {
            response = "Server error";
        }
        return response;
    }

}
