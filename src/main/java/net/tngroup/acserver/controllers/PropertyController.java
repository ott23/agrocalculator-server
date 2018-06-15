package net.tngroup.acserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.models.Property;
import net.tngroup.acserver.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/setting")
public class PropertyController {

    private PropertyRepository propertyRepository;

    @Autowired
    public PropertyController(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @RequestMapping
    public String getList() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Property> propertyList = propertyRepository.findAll();
            return objectMapper.writeValueAsString(propertyList);
        } catch (JsonProcessingException e) {
            ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

}
