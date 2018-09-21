package net.tngroup.acserver.controllers;

import net.tngroup.acserver.databases.h2.models.User;
import net.tngroup.acserver.databases.h2.services.UserService;
import net.tngroup.acserver.web.controllers.UserController;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static net.tngroup.common.responses.Responses.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class UserControllerTests {

    private UserController userController;
    private UserService userService;
    private HttpServletRequest httpServletRequest;

    @Before
    public void before() {

        userService = mock(UserService.class);
        httpServletRequest = mock(HttpServletRequest.class);

        userController = new UserController(userService);
    }

    @Test
    public void givenValidRequest_whenCallGetList_thenShouldBeReturnList() {

        final List<User> mockList = List.of();

        when(userService.getAll()).thenReturn(mockList);

        assertEquals(
                userController.getList(httpServletRequest),
                okResponse(mockList)
        );
    }

    @Test
    public void givenIdFromNonExistUser_whenCallGetById_thenShouldBeReturnNonFound() throws Exception {

        when(userService.getById(anyInt())).thenReturn(null);
        assertEquals(
                userController.getById(httpServletRequest, 0),
                nonFoundResponse()
        );
    }

    @Test
    public void givenValidRequest_whenCallGetById_thenShouldBeReturnUser() throws Exception {

        final User user = new User();
        when(userService.getById(anyInt())).thenReturn(user);

        assertEquals(
                userController.getById(httpServletRequest, 0),
                okResponse(user)
        );
    }

    @Test
    public void givenExistUser_whenCallSave_thenShouldBeReturnConflictResponse(){

        final User user = new User();
        user.setId(0);

        final User existUser = new User();
        existUser.setId(1);
        when(userService.getAllByUsername(any())).thenReturn(List.of(existUser));

        assertEquals(
                userController.save(httpServletRequest, user),
                conflictResponse("user")
        );

        verify(userService, times(0)).save(any());
    }

    @Test
    public void givenValidUser_whenCallSave_thenShouldBeReturnConflictResponse(){

        final User user = new User();
        user.setId(0);
        when(userService.getAllByUsername(any())).thenReturn(List.of());

        assertEquals(
                userController.save(httpServletRequest, user),
                successResponse()
        );

        verify(userService, times(1)).save(user);
    }

    @Test
    public void givenRequestForDeleteUser_whenCallDelete_thenAlwaysReturnSuccces(){

        assertEquals(
                userController.deleteById(httpServletRequest, 0),
                successResponse()
        );

        verify(userService, times(1)).deleteById(0);
    }



}
