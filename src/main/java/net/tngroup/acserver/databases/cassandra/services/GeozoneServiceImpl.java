package net.tngroup.acserver.databases.cassandra.services;


import net.tngroup.acserver.databases.cassandra.models.Geozone;
import net.tngroup.acserver.databases.cassandra.repositories.GeozoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GeozoneServiceImpl implements GeozoneService {

    private GeozoneRepository geozoneRepository;

    @Autowired
    public GeozoneServiceImpl(@Lazy GeozoneRepository geozoneRepository) {
        this.geozoneRepository = geozoneRepository;
    }

    @Override
    public List<Geozone> getAll() {
        return geozoneRepository.findAll();
    }

    @Override
    public Geozone getById(UUID id) {
        return geozoneRepository.findById(id).orElse(null);
    }

    @Override
    public Geozone save(Geozone Geozone) {
        return geozoneRepository.save(Geozone);
    }

    @Override
    public void deleteById(UUID id) {
        geozoneRepository.deleteById(id);
    }
}
