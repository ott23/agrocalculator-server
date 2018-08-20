package net.tngroup.acserver.databases.cassandra.services;

import net.tngroup.acserver.databases.cassandra.models.Unit;
import net.tngroup.acserver.databases.cassandra.repositories.UnitRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnitServiceImpl implements UnitService {

    private UnitRepository unitRepository;

    public UnitServiceImpl(@Lazy UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public List<Unit> getAll() {
        return unitRepository.findAll();
    }

    @Override
    public List<Unit> getAllByImei(String imei) {
        return unitRepository.findAllByImei(imei);
    }

    @Override
    public void save(Unit unit) {
        unitRepository.save(unit);
    }

    @Override
    public void deleteById(UUID id) {
        unitRepository.deleteById(id);
    }
}
