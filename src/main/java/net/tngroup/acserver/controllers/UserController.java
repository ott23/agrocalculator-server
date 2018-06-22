package net.tngroup.acserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.models.User;
import net.tngroup.acserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity getList() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<User> userList = userService.getAll();
            String response = objectMapper.writeValueAsString(userList);
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/getById/{id}")
    public ResponseEntity getById(@PathVariable int id) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Get user
            User user = userService.getById(id);
            if (user == null) throw new Exception("User not found");
            // Form json
            String response = objectMapper.writeValueAsString(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/getByUsername/{username}")
    public ResponseEntity getByUsername(@PathVariable String username) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Get user
            User user = userService.getByUsername(username);
            if (user == null) throw new Exception("User not found");
            // Form json
            String response = objectMapper.writeValueAsString(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/add")
    public ResponseEntity add(@RequestBody String jsonRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonResponse = objectMapper.createObjectNode();
        try {
            User user = objectMapper.readValue(jsonRequest, User.class);
            userService.add(user);
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
            userService.deleteById(id);
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
