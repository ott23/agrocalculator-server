package net.tngroup.acserver.databases.h2.repositories;

import net.tngroup.acserver.databases.h2.models.NodeStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeStatusRepository extends JpaRepository<NodeStatus, Integer> {

    List<NodeStatus> findAllByNodeId(int id, Pageable pageable);

}
