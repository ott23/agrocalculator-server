package net.tngroup.acserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.models.User;
import net.tngroup.acserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(
            UserService userService) {
        this.userService = userService;
    }


    @RequestMapping
    public String getList() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<User> userList = userService.getAll();
            return objectMapper.writeValueAsString(userList);
        } catch (JsonProcessingException e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping("/getById/{id}")
    public String getById(@PathVariable int id) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Get user
            User user = userService.getById(id);
            if (user == null) throw new Exception("User not found");
            // Form json
            return objectMapper.writeValueAsString(user);
        } catch (Exception e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping("/getByUsername/{username}")
    public String getByUsername(@PathVariable String username) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Get user
            User user = userService.getByUsername(username);
            if (user == null) throw new Exception("User not found");
            // Form json
            return objectMapper.writeValueAsString(user);
        } catch (Exception e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping("/add")
    public String add(@RequestBody String jsonRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonResponse = objectMapper.createObjectNode();
        try {
            User user = objectMapper.readValue(jsonRequest, User.class);
            userService.add(user);
            jsonResponse.put("response", "Success");
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
        }
        return jsonResponse.toString();
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable int id) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        try {
            userService.deleteById(id);
            jsonResponse.put("response", "Success");
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
        }
        return jsonResponse.toString();
    }

}
