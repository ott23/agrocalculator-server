package net.tngroup.acserver.services;

import net.tngroup.acserver.models.Calculator;
import net.tngroup.acserver.repositories.CalculatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.SocketAddress;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalculatorServiceImpl implements CalculatorService {

    private CalculatorRepository calculatorRepository;

    @Autowired
    public CalculatorServiceImpl(CalculatorRepository calculatorRepository) {
        this.calculatorRepository = calculatorRepository;
    }

    @Override
    public List<Calculator> getAll() {
        return calculatorRepository.findAll().stream()
                .peek(c -> c.setTasks(c.getTasks().stream().filter(t -> !t.isConfirmed()).collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Calculator> getAllByKey(boolean key) {
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
    public Calculator getByAddressAndConnection(SocketAddress address, boolean connection) {
        return calculatorRepository.findByAddressAndConnection(address, connection).orElse(null);
    }

    @Override
    public void updateAllStatus(boolean status) {
        calculatorRepository.findAllByStatus(!status).forEach(c -> {
            c.setStatus(status);
            calculatorRepository.save(c);
        });
    }

    @Override
    public void updateAllConnection(boolean connection) {
        calculatorRepository.findAllByConnection(!connection).forEach(c -> {
            c.setConnection(connection);
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
    public void updateKeyById(int id, boolean key) {
        calculatorRepository.findById(id).ifPresent(c -> {
            c.setKey(key);
            calculatorRepository.save(c);
        });
    }

    @Override
    public void updateEncodedKeyById(int id, String encodedKey) {
        calculatorRepository.findById(id).ifPresent(c -> {
            c.setEncodedKey(encodedKey);
            calculatorRepository.save(c);
        });
    }

    @Override
    public void updateStatusById(int id, boolean status) {
        calculatorRepository.findById(id).ifPresent(c -> {
            c.setStatus(status);
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
    public void updateConnectionById(int id, boolean connection) {
        calculatorRepository.findById(id).ifPresent(c -> {
            c.setConnection(connection);
            calculatorRepository.save(c);
        });
    }

    @Override
    public void addOrUpdate(Calculator calculator) {
        calculatorRepository.save(calculator);
    }

    @Override
    @Transactional
    public void removeById(int id) {
        calculatorRepository.deleteById(id);
    }
}
