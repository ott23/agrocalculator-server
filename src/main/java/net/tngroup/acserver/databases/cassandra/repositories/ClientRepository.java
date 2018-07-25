package net.tngroup.acserver.databases.cassandra.repositories;

import net.tngroup.acserver.databases.cassandra.models.Client;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Lazy
public interface ClientRepository extends CassandraRepository<Client, UUID> {

    @AllowFiltering
    List<Client> findAllByName(String name);
}
