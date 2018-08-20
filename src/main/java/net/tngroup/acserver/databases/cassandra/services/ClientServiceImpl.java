package net.tngroup.acserver.databases.cassandra.services;

import net.tngroup.acserver.databases.cassandra.models.Client;
import net.tngroup.acserver.databases.cassandra.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {

    private ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> getAllByName(String name) {
        return clientRepository.findAllByName(name);
    }

    @Override
    public Client getById(UUID id) {
        return clientRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Client client) {
        clientRepository.save(client);
    }

    @Override
    public void deleteById(UUID id) {
        clientRepository.deleteById(id);
    }
}
