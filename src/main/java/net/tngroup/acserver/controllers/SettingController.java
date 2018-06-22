package net.tngroup.acserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.models.Setting;
import net.tngroup.acserver.services.SettingService;
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

    @RequestMapping
    public ResponseEntity getList() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Setting> settingList = settingService.getAll();
            String response = objectMapper.writeValueAsString(settingList);
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/getByCalculator/{id}")
    public ResponseEntity getListByCalculatorId(@PathVariable int id) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Setting> settingList = settingService.getAllByCalculatorId(id);
            String response = objectMapper.writeValueAsString(settingList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/setForCalculator")
    public ResponseEntity setForCalculator(@RequestBody String jsonRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonResponse = objectMapper.createObjectNode();
        try {
            Setting inputSetting = objectMapper.readValue(jsonRequest, Setting.class);
            Setting setting = settingService.getByNameAndCalculatorId(inputSetting.getName(), inputSetting.getCalculator().getId());

            if (setting != null) {
                setting.setValue(inputSetting.getValue());
            } else {
                setting = new Setting(inputSetting.getName(), inputSetting.getValue());
                setting.setCalculator(inputSetting.getCalculator());
            }
            settingService.addOrUpdate(setting);
            jsonResponse.put("response", "Success");
            String response = jsonResponse.toString();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/set")
    public ResponseEntity set(@RequestBody String jsonRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonResponse = objectMapper.createObjectNode();
        try {
            Setting setting = objectMapper.readValue(jsonRequest, Setting.class);
            settingService.addOrUpdate(setting);
            jsonResponse.put("response", "Success");
            String response = jsonResponse.toString();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable int id) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        try {
            Setting setting = settingService.getById(id);
            if(setting != null && setting.getCalculator() != null) {
                settingService.deleteById(id);
            } else {
                throw new Exception("Setting not found");
            }
            jsonResponse.put("response", "Success");
            String response = jsonResponse.toString();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
