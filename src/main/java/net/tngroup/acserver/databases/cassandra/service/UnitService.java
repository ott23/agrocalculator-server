package net.tngroup.acserver.databases.cassandra.service;

import net.tngroup.acserver.databases.cassandra.models.Unit;

import java.util.List;
import java.util.UUID;

public interface UnitService {

    List<Unit> getAll();

    List<Unit> getAllByNameOrImei(String name, String imei);

    void save(Unit unit);

    void deleteById(UUID id);

}
