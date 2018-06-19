package net.tngroup.acserver.services;

import net.tngroup.acserver.models.Setting;

import java.util.List;

public interface SettingService {

    List<Setting> getAll();

    Setting getByNameAndCalculatorId(String name, Integer id);

    void add(Setting setting);

}
