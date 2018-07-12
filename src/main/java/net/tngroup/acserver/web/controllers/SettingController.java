package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.acserver.databases.h2.models.Setting;
import net.tngroup.acserver.databases.h2.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static net.tngroup.acserver.web.controllers.Responses.*;

@CrossOrigin
@RestController
@RequestMapping("/setting")
public class SettingController {

    private SettingService settingService;

    @Autowired
    public SettingController(
            SettingService settingService) {
        this.settingService = settingService;
    }

    @RequestMapping
    public ResponseEntity getList() {
        try {
            List<Setting> settingList = settingService.getAll();
            return okFullResponse(settingList);
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/getByCalculator/{id}")
    public ResponseEntity getListByCalculatorId(@PathVariable int id) {
        try {
            List<Setting> settingList = settingService.getAllByCalculatorId(id);
            return okResponse(settingList);
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/setForCalculator")
    public ResponseEntity setForCalculator(@RequestBody String jsonRequest) {
        try {
            Setting inputSetting = new ObjectMapper().readValue(jsonRequest, Setting.class);
            Setting setting = settingService.getByNameAndCalculatorId(inputSetting.getName(), inputSetting.getCalculator().getId());
            if (setting != null) {
                setting.setValue(inputSetting.getValue());
            } else {
                setting = new Setting(inputSetting.getName(), inputSetting.getValue());
                setting.setCalculator(inputSetting.getCalculator());
            }
            settingService.save(setting);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/set")
    public ResponseEntity set(@RequestBody String jsonRequest) {
        try {
            Setting setting = new ObjectMapper().readValue(jsonRequest, Setting.class);
            settingService.save(setting);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable int id) {
        try {
            Setting setting = settingService.getById(id);
            if (setting != null && setting.getCalculator() != null) {
                settingService.deleteById(id);
            } else {
                throw new Exception("Setting not found");
            }
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

}