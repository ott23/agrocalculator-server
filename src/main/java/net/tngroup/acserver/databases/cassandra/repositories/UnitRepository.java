package net.tngroup.acserver.databases.cassandra.repositories;

import net.tngroup.acserver.databases.cassandra.models.Unit;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface UnitRepository extends CassandraRepository<Unit, UUID> {

    @AllowFiltering
    List<Unit> findAllByName(String name);

    @AllowFiltering
    List<Unit> findAllByImei(String imei);

}
