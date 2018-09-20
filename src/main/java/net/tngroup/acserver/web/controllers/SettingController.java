package net.tngroup.acserver.web.controllers;

import net.tngroup.acserver.databases.h2.models.Setting;
import net.tngroup.acserver.databases.h2.services.SettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static net.tngroup.common.responses.Responses.*;


@CrossOrigin
@RestController
@RequestMapping("/setting")
public class SettingController {

    private SettingService settingService;

    public SettingController(
            SettingService settingService) {
        this.settingService = settingService;
    }

    @RequestMapping
    public ResponseEntity getList(HttpServletRequest request) {

        final List<Setting> settingList = settingService.getAll();
        return okFullResponse(settingList);
    }

    @RequestMapping("/getByNode/{id}")
    public ResponseEntity getListByCalculatorId(HttpServletRequest request, @PathVariable Integer id) {

        final List<Setting> settingList = settingService.getAllByCalculatorId(id);
        return okResponse(settingList);
    }

    @RequestMapping("/setForNode")
    public ResponseEntity setForCalculator(HttpServletRequest request, @RequestBody Setting inputSetting) {

        //TODO Возможно NPE если в node будет null
        Setting setting = settingService.getByNameAndCalculatorId(
                inputSetting.getName(), inputSetting.getNode().getId()
        );
        if (setting != null) {
            setting.setValue(inputSetting.getValue());
        } else {
            //TODO Возможно NPE тк конструктор требует не Null в аргументах
            setting = new Setting(inputSetting.getName(), inputSetting.getValue());
            setting.setNode(inputSetting.getNode());
        }
        settingService.save(setting);
        return successResponse();

    }

    @RequestMapping("/set")
    public ResponseEntity set(HttpServletRequest request, @RequestBody Setting setting) {

        settingService.save(setting);
        return successResponse();
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(HttpServletRequest request, @PathVariable int id) throws Exception {

        final Setting setting = settingService.getById(id);
        if (setting != null && setting.getNode() != null) {
            settingService.deleteById(id);
        } else {
            throw new Exception("Setting not found");
        }
        return successResponse();
    }

}
