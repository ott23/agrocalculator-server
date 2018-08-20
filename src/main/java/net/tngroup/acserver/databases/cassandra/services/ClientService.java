package net.tngroup.acserver.databases.cassandra.services;

import net.tngroup.acserver.databases.cassandra.models.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService {

    List<Client> getAll();

    List<Client> getAllByName(String name);

    Client getById(UUID id);

    void save(Client client);

    void deleteById(UUID id);

}
