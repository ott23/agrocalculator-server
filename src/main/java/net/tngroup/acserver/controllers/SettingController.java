package net.tngroup.acserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.models.Setting;
import net.tngroup.acserver.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String getList() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Setting> settingList = settingService.getAll();
            return objectMapper.writeValueAsString(settingList);
        } catch (JsonProcessingException e) {
            ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

}
