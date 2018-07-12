package net.tngroup.acserver.databases.h2.services;

import net.tngroup.acserver.databases.h2.models.Setting;

import java.util.List;

public interface SettingService {

    List<Setting> getAll();

    List<Setting> getAllByCalculatorId(int id);

    Setting getById(int id);

    Setting getByNameAndCalculatorId(String name, Integer id);

    void save(Setting setting);

    void deleteById(int id);

}
