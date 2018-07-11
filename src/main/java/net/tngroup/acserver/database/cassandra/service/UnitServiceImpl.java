package net.tngroup.acserver.database.cassandra.service;

import net.tngroup.acserver.database.cassandra.models.Unit;
import net.tngroup.acserver.database.cassandra.repositories.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnitServiceImpl implements UnitService {

    private UnitRepository unitRepository;

    @Autowired
    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public List<Unit> getAll() {
        return unitRepository.findAll();
    }

    @Override
    public List<Unit> getAllByNameOrImei(String name, String imei) {
        Set<Unit> unitSet = new HashSet<>();
        unitSet.addAll(unitRepository.findAllByName(name));
        unitSet.addAll(unitRepository.findAllByImei(imei));
        return new ArrayList<>(unitSet);
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
