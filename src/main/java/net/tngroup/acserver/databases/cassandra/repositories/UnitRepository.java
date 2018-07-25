package net.tngroup.acserver.databases.cassandra.repositories;

import net.tngroup.acserver.databases.cassandra.models.Unit;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Lazy
public interface UnitRepository extends CassandraRepository<Unit, UUID> {

    @AllowFiltering
    List<Unit> findAllByName(String name);

    @AllowFiltering
    List<Unit> findAllByImei(String imei);

}
