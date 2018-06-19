package net.tngroup.acserver.repositories;

import net.tngroup.acserver.models.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Integer> {

    Optional<Setting> findByNameAndCalculatorId(String name, Integer id);

}
