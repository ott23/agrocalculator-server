package net.tngroup.acserver.databases.h2.services;

import net.tngroup.acserver.databases.h2.models.NodeStatus;
import net.tngroup.acserver.databases.h2.repositories.NodeStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeStatusServiceImpl implements NodeStatusService {

    private NodeStatusRepository nodeStatusRepository;

    @Autowired
    public NodeStatusServiceImpl(NodeStatusRepository nodeStatusRepository) {
        this.nodeStatusRepository = nodeStatusRepository;
    }

    @Override
    public List<NodeStatus> getByCalculatorId(int id) {
        Pageable pageable = PageRequest.of(0, 20, Sort.Direction.DESC, "dateTime");
        return nodeStatusRepository.findAllByNodeId(id, pageable);
    }

    @Override
    public void save(NodeStatus nodeStatus) {
        this.nodeStatusRepository.save(nodeStatus);
    }
}
