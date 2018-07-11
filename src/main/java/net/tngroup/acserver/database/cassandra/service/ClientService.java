package net.tngroup.acserver.database.cassandra.service;

import net.tngroup.acserver.database.cassandra.models.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService {

    List<Client> getAll();

    List<Client> getAllByName(String name);

    Client getById(UUID id);

    void save(Client client);

    void deleteById(UUID id);

}
