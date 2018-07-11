package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.database.cassandra.models.Client;
import net.tngroup.acserver.database.h2.models.User;
import net.tngroup.acserver.database.h2.services.UserService;
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

    private ResponseEntity okResponse(Object o) throws JsonProcessingException {
        String response = new ObjectMapper().writeValueAsString(o);
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

    private ResponseEntity conflictResponse() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @RequestMapping
    public ResponseEntity getList() {
        try {
            List<User> userList = userService.getAll();
            return okResponse(userList);
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/getById/{id}")
    public ResponseEntity getById(@PathVariable int id) {
        try {
            User user = userService.getById(id);
            if (user == null) throw new Exception("User not found");
            return okResponse(user);
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/save")
    public ResponseEntity save(@RequestBody String jsonRequest) {
        try {
            User user = new ObjectMapper().readValue(jsonRequest, User.class);

            List<User> userList = userService.getAllByUsername(user.getUsername());
            if (userList.size() == 1 && !userList.get(0).getId().equals(user.getId()) || userList.size() > 1) return conflictResponse();

            userService.save(user);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable int id) {
        try {
            userService.deleteById(id);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

}
