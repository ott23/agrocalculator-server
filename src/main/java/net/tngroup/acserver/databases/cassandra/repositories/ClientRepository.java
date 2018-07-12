package net.tngroup.acserver.databases.cassandra.repositories;

import net.tngroup.acserver.databases.cassandra.models.Client;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends CassandraRepository<Client, UUID> {

    @AllowFiltering
    List<Client> findAllByName(String name);
}
