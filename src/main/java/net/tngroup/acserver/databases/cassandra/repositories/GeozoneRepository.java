package net.tngroup.acserver.databases.cassandra.repositories;

import net.tngroup.acserver.databases.cassandra.models.Geozone;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Lazy
public interface GeozoneRepository extends CassandraRepository<Geozone, UUID> {

    @AllowFiltering
    List<Geozone> findAllByName(String name);

    @AllowFiltering
    List<Geozone> findAllByClient(UUID id);

}
