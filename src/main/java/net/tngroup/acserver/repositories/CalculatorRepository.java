package net.tngroup.acserver.repositories;

import net.tngroup.acserver.models.Calculator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.SocketAddress;
import java.util.List;

@Repository
public interface CalculatorRepository extends JpaRepository<Calculator, Integer> {


    Calculator findClientById(int id);

    Calculator findClientByAddress(SocketAddress address);

    List<Calculator> findAllByActive(boolean active);

    List<Calculator> findAllByNeedKey(boolean needKey);
}
