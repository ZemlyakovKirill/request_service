package ru.themlyakov.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.themlyakov.entity.Request;
import ru.themlyakov.entity.User;
import ru.themlyakov.service.RequestService;
import ru.themlyakov.service.UserService;
import ru.themlyakov.util.RequestStatus;

import java.util.List;

@RestController
public class AdminRequestController {

    @Autowired
    UserService userService;

    @Autowired
    RequestService requestService;

    @RequestMapping(method = RequestMethod.GET, value = "admin/user/all")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @RequestMapping(method = RequestMethod.GET, value = "admin/request/all")
    public List<Request> getAllRequests(@RequestParam int page,
                                        @RequestParam RequestStatus status,
                                        @RequestParam Sort.Direction direction,
                                        @RequestParam(required = false) String name) {
        return requestService.getSendAcceptedDeclinedRequests(page, status, direction, name);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "admin/user/grant/{id}")
    public User grantUser(@PathVariable("id") Long id) {
        return userService.grantUser(id);
    }


}
