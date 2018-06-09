package net.tngroup.acserver.repositories;

import net.tngroup.acserver.models.CalculatorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculatorStatusRepository extends JpaRepository<CalculatorStatus, Integer> {
}
