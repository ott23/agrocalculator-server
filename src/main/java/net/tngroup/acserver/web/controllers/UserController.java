package net.tngroup.acserver.web.controllers;

import net.tngroup.acserver.databases.h2.models.User;
import net.tngroup.acserver.databases.h2.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static net.tngroup.common.responses.Responses.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping
    public ResponseEntity getList(HttpServletRequest request) {

        final List<User> userList = userService.getAll();
        return okResponse(userList);
    }

    @RequestMapping("/getById/{id}")
    public ResponseEntity getById(HttpServletRequest request, @PathVariable int id) throws Exception {

        final User user = userService.getById(id);
        if (user == null) return nonFoundResponse();
        return okResponse(user);
    }

    @RequestMapping("/save")
    public ResponseEntity save(HttpServletRequest request, @RequestBody User user) {

        final List<User> userList = userService.getAllByUsername(user.getUsername());
        if (userList.size() == 1 && !userList.get(0).getId().equals(user.getId()) || userList.size() > 1)
            return conflictResponse("user");
        userService.save(user);
        return successResponse();

    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(HttpServletRequest request, @PathVariable int id) {

        userService.deleteById(id);
        return successResponse();
    }

}
