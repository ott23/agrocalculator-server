package net.tngroup.acserver.databases.h2.services;

import net.tngroup.acserver.databases.h2.models.Node;
import net.tngroup.acserver.databases.h2.repositories.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.SocketAddress;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeServiceImpl implements NodeService {

    private NodeRepository nodeRepository;

    @Autowired
    public NodeServiceImpl(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    public List<Node> getAll() {
        return nodeRepository.findAll().stream()
                .peek(c -> c.setTasks(c.getTasks().stream().filter(t -> !t.isConfirmed()).collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Node> getAllByName(String name) {
        return nodeRepository.findAllByName(name);
    }

    @Override
    public List<Node> getAllByAddress(SocketAddress address) {
        return nodeRepository.findAllByAddress(address);
    }

    @Override
    public Node getById(int id) {
        return nodeRepository.findById(id).orElse(null);
    }

    @Override
    public Node getByCode(String code) {
        return nodeRepository.findByCode(code).orElse(null);
    }

    @Override
    public Node getByAddressAndConnection(SocketAddress address, boolean connection) {
        return nodeRepository.findByAddressAndConnection(address, connection).orElse(null);
    }

    @Override
    public void updateAllStatus(boolean status) {
        nodeRepository.findAllByStatus(!status).forEach(c -> {
            c.setStatus(status);
            nodeRepository.save(c);
        });
    }

    @Override
    public void updateAllConnection(boolean connection) {
        nodeRepository.findAllByConnection(!connection).forEach(c -> {
            c.setConnection(connection);
            nodeRepository.save(c);
        });
    }

    @Override
    public void updateAllArchiveByAddress(SocketAddress address, boolean archive) {
        nodeRepository.findAllByAddress(address).forEach(c -> {
            c.setArchive(archive);
            nodeRepository.save(c);
        });
    }

    @Override
    public void updateKeyById(int id, String encodedKey) {
        nodeRepository.findById(id).ifPresent(c -> {
            c.setKey(encodedKey);
            nodeRepository.save(c);
        });
    }

    @Override
    public void updateStatusById(int id, boolean status) {
        nodeRepository.findById(id).ifPresent(c -> {
            c.setStatus(status);
            nodeRepository.save(c);
        });
    }

    @Override
    public void updateArchiveById(int id, boolean archive) {
        nodeRepository.findById(id).ifPresent(c -> {
            c.setArchive(archive);
            nodeRepository.save(c);
        });
    }

    @Override
    public void updateConnectionById(int id, boolean connection) {
        nodeRepository.findById(id).ifPresent(c -> {
            c.setConnection(connection);
            nodeRepository.save(c);
        });
    }

    @Override
    public void save(Node node) {
        try {
            nodeRepository.save(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void removeById(int id) {
        nodeRepository.deleteById(id);
    }
}
