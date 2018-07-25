package net.tngroup.acserver.databases.h2.repositories;

import net.tngroup.acserver.databases.h2.models.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Integer> {

    Optional<Setting> findByNameAndNodeId(String name, Integer id);

    List<Setting> findAllByNodeId(Integer id);

}
