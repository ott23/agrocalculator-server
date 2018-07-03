package net.tngroup.acserver.repositories;

import net.tngroup.acserver.models.Calculator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalculatorRepository extends JpaRepository<Calculator, Integer> {

    List<Calculator> findAllByName(String name);
    List<Calculator> findAllByStatus(boolean status);
    List<Calculator> findAllByConnection(boolean connection);
    List<Calculator> findAllByAddress(SocketAddress address);
    Optional<Calculator> findByAddressAndConnection(SocketAddress address, boolean connection);
    Optional<Calculator> findByCode(String code);

}
