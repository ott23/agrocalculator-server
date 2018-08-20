package net.tngroup.acserver.databases.cassandra.services;

import net.tngroup.acserver.databases.cassandra.models.Geozone;

import java.util.List;
import java.util.UUID;

public interface GeozoneService {

    List<Geozone> getAll();

    Geozone getById(UUID id);

    Geozone save(Geozone Geozone);

    void deleteById(UUID id);

}
