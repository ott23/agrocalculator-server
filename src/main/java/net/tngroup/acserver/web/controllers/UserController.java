package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.acserver.databases.h2.models.User;
import net.tngroup.acserver.databases.h2.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static net.tngroup.acserver.web.controllers.Responses.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
