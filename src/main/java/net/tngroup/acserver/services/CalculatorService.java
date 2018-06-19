package net.tngroup.acserver.services;

import net.tngroup.acserver.models.Calculator;

import java.net.SocketAddress;
import java.util.List;

public interface CalculatorService {

    List<Calculator> getAll();
    List<Calculator> getAllByKey(String key);

    Calculator getById(int id);
    Calculator getByName(String name);
    Calculator getByAddressAndActive(SocketAddress address, boolean active);

    void updateAllActive(boolean active);
    void updateAllArchiveByAddress(SocketAddress address, boolean active);

    void updateKeyById(int id, String key);
    void updateArchiveById(int id, boolean archive);
    void updateActiveById(int id, boolean active);

    void addOrUpdate(Calculator calculator);

}
