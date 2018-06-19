package net.tngroup.acserver.services;

import net.tngroup.acserver.models.Setting;
import net.tngroup.acserver.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingServiceImpl implements SettingService {

    private SettingRepository settingRepository;

    @Autowired
    public SettingServiceImpl(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public List<Setting> getAll() {
        return settingRepository.findAll();
    }

    @Override
    public Setting getByNameAndCalculatorId(String name, Integer id) {
        return settingRepository.findByNameAndCalculatorId(name, id).orElse(null);
    }

    @Override
    public void add(Setting setting) {
        settingRepository.save(setting);
    }
}
