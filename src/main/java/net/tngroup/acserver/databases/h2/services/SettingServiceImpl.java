package net.tngroup.acserver.databases.h2.services;

import net.tngroup.acserver.databases.h2.models.Setting;
import net.tngroup.acserver.databases.h2.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<Setting> getAllByCalculatorId(int id) {
        List<Setting> settingList = settingRepository.findAllByNodeId(null);
        List<Setting> calculatorSettingList = settingRepository.findAllByNodeId(id);

        return settingList.stream().map(setting -> calculatorSettingList
                .stream()
                .filter(s -> setting.getName().equals(s.getName()))
                .findFirst()
                .orElse(setting))
                .peek(s -> s.setNode(null))
                .collect(Collectors.toList());
    }

    @Override
    public Setting getById(int id) {
        return settingRepository.findById(id).orElse(null);
    }

    @Override
    public Setting getByNameAndCalculatorId(String name, Integer id) {
        return settingRepository.findByNameAndNodeId(name, id).orElse(null);
    }

    @Override
    public void save(Setting setting) {
        settingRepository.save(setting);
    }

    @Override
    public void deleteById(int id) {
        settingRepository.deleteById(id);
    }
}
