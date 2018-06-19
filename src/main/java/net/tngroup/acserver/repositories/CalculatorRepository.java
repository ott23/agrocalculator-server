package net.tngroup.acserver.repositories;

import net.tngroup.acserver.models.Calculator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalculatorRepository extends JpaRepository<Calculator, Integer> {

    List<Calculator> findAllByKey(String key);
    List<Calculator> findAllByAddress(SocketAddress address);
    Optional<Calculator> findByAddressAndActive(SocketAddress address, boolean active);
    Optional<Calculator> findByName(String name);

}
