package net.tngroup.acserver.databases.h2.services;

import net.tngroup.acserver.databases.h2.models.Node;

import java.net.SocketAddress;
import java.util.List;

public interface NodeService {

    List<Node> getAll();
    List<Node> getAllByName(String name);
    List<Node> getAllByAddress(SocketAddress address);

    Node getById(int id);
    Node getByCode(String code);
    Node getByAddressAndConnection(SocketAddress address, boolean connection);

    void updateAllStatus(boolean status);
    void updateAllConnection(boolean connection);
    void updateAllArchiveByAddress(SocketAddress address, boolean archive);

    void updateKeyById(int id, String encodedKey);
    void updateStatusById(int id, boolean status);
    void updateArchiveById(int id, boolean archive);
    void updateConnectionById(int id, boolean connection);

    void save(Node node);

    void removeById(int id);

}
