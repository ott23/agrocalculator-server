package net.tngroup.acserver.repositories;

import net.tngroup.acserver.models.Calculator;
import net.tngroup.acserver.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {

    Property findPropertyByNameAndCalculator(String name, Calculator calculator);

}
