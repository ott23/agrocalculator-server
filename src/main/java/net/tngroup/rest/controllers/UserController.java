package net.tngroup.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.rest.models.User;
import net.tngroup.rest.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping
    public String getList() {
        String response;
        try {
            List<User> userList = userRepository.findAll();
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.writeValueAsString(userList);
        } catch (JsonProcessingException e) {
            response = "Server error";
        }
        return response;
    }

    @RequestMapping("/getById/{id}")
    public String getById(@PathVariable int id) {
        String response;
        try {
            if (userRepository.findById(id).isPresent()) {
                ObjectMapper mapper = new ObjectMapper();
                User user = userRepository.findById(id).get();
                response = mapper.writeValueAsString(user);
            } else {
                throw new Exception("User not found");
            }
        } catch (Exception e) {
            response = "Server error";
        }
        return response;
    }

    @RequestMapping("/getByUsername/{username}")
    public String getByUsername(@PathVariable String username) {
        String response;
        try {
            User user = userRepository.findUserByUsername(username);
            if (user != null) {
                ObjectMapper mapper = new ObjectMapper();
                response = mapper.writeValueAsString(user);
            } else {
                throw new Exception("User not found");
            }
        } catch (Exception e) {
            response = "Server error";
        }
        return response;
    }

    @RequestMapping("/add")
    public String add(@RequestBody String jsonRequest) {
        String response;
        try {
            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(jsonRequest, User.class);
            user = userRepository.save(user);
            response = mapper.writeValueAsString(user);
        } catch (Exception e) {
            response = "Server error";
        }
        return response;
    }

    @RequestMapping("/delete/{id}")
    public void deleteById(@PathVariable int id) {
        userRepository.deleteById(id);
    }

}
