package net.tngroup.acserver.databases.h2.services;

import net.tngroup.acserver.databases.h2.models.NodeStatus;

import java.util.List;

public interface NodeStatusService {

    List<NodeStatus> getByCalculatorId(int id);

    void save(NodeStatus nodeStatus);

}
