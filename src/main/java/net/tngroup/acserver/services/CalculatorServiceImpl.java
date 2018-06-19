package net.tngroup.acserver.services;

import net.tngroup.acserver.models.Calculator;
import net.tngroup.acserver.repositories.CalculatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.SocketAddress;
import java.util.List;

@Service
public class CalculatorServiceImpl implements CalculatorService {

    private CalculatorRepository calculatorRepository;

    @Autowired
    public CalculatorServiceImpl(CalculatorRepository calculatorRepository) {
        this.calculatorRepository = calculatorRepository;
    }

    @Override
    public List<Calculator> getAll() {
        return calculatorRepository.findAll();
    }

    @Override
    public List<Calculator> getAllByKey(String key) {
        return calculatorRepository.findAllByKey(key);
    }


    @Override
    public Calculator getById(int id) {
        return calculatorRepository.findById(id).orElse(null);
    }

    @Override
    public Calculator getByName(String name) {
        return calculatorRepository.findByName(name).orElse(null);
    }

    @Override
    public Calculator getByAddressAndActive(SocketAddress address, boolean active) {
        return calculatorRepository.findByAddressAndActive(address, active).orElse(null);
    }

    @Override
    public void updateAllActive(boolean active) {
        calculatorRepository.findAll().forEach(c -> {
            c.setActive(active);
            calculatorRepository.save(c);
        });
    }

    @Override
    public void updateAllArchiveByAddress(SocketAddress address, boolean archive) {
        calculatorRepository.findAllByAddress(address).forEach(c -> {
            c.setArchive(archive);
            calculatorRepository.save(c);
        });
    }

    @Override
    public void updateKeyById(int id, String key) {
        calculatorRepository.findById(id).ifPresent(c -> {
            c.setKey(key);
            calculatorRepository.save(c);
        });
    }

    @Override
    public void updateArchiveById(int id, boolean archive) {
        calculatorRepository.findById(id).ifPresent(c -> {
            c.setArchive(archive);
            calculatorRepository.save(c);
        });
    }

    @Override
    public void updateActiveById(int id, boolean active) {
        calculatorRepository.findById(id).ifPresent(c -> {
            c.setActive(active);
            calculatorRepository.save(c);
        });
    }

    @Override
    public void addOrUpdate(Calculator calculator) {
        calculatorRepository.save(calculator);
    }
}
