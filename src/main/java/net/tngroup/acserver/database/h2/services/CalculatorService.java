package net.tngroup.acserver.database.h2.services;

import net.tngroup.acserver.database.h2.models.Calculator;

import java.net.SocketAddress;
import java.util.List;

public interface CalculatorService {

    List<Calculator> getAll();
    List<Calculator> getAllByName(String name);
    List<Calculator> getAllByAddress(SocketAddress address);

    Calculator getById(int id);
    Calculator getByCode(String code);
    Calculator getByAddressAndConnection(SocketAddress address, boolean connection);

    void updateAllStatus(boolean status);
    void updateAllConnection(boolean connection);
    void updateAllArchiveByAddress(SocketAddress address, boolean archive);

    void updateKeyById(int id, String encodedKey);
    void updateStatusById(int id, boolean status);
    void updateArchiveById(int id, boolean archive);
    void updateConnectionById(int id, boolean connection);

    void save(Calculator calculator);

    void removeById(int id);

}
