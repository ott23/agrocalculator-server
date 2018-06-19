package net.tngroup.acserver.repositories;

import net.tngroup.acserver.models.CalculatorStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalculatorStatusRepository extends JpaRepository<CalculatorStatus, Integer> {

    List<CalculatorStatus> findAllByCalculatorId(int id, Pageable pageable);

}
