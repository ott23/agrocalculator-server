package net.tngroup.acserver.services;

import net.tngroup.acserver.models.Setting;

import java.util.List;

public interface SettingService {

    List<Setting> getAll();

    List<Setting> getAllByCalculatorId(int id);

    Setting getById(int id);

    Setting getByNameAndCalculatorId(String name, Integer id);

    void addOrUpdate(Setting setting);

    void deleteById(int id);

}
