package net.tngroup.acserver.databases.h2.repositories;

import net.tngroup.acserver.databases.h2.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;

@Repository
public interface NodeRepository extends JpaRepository<Node, Integer> {

    List<Node> findAllByName(String name);
    List<Node> findAllByStatus(boolean status);
    List<Node> findAllByConnection(boolean connection);
    List<Node> findAllByAddress(SocketAddress address);
    Optional<Node> findByAddressAndConnection(SocketAddress address, boolean connection);
    Optional<Node> findByCode(String code);

}
