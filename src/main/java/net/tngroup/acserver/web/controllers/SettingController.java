package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.database.h2.models.Setting;
import net.tngroup.acserver.database.h2.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    private ResponseEntity okResponse(Object o) throws JsonProcessingException {
        String response = new ObjectMapper().writeValueAsString(o);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity okFullResponse(Object o) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        String response = objectMapper.writeValueAsString(o);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity successResponse() {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        jsonResponse.put("response", "Success");
        String response = jsonResponse.toString();
        return ResponseEntity.ok(response);
    }

    private ResponseEntity badResponse(Exception e) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        jsonResponse.put("response", "Server error: " + e.getMessage());
        String response = jsonResponse.toString();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
