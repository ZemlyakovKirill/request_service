package ru.themlyakov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.themlyakov.entity.Request;
import ru.themlyakov.service.RequestService;
import ru.themlyakov.util.RequestStatus;
import ru.themlyakov.util.UserRequestView;

import java.util.List;

@RestController
public class OperatorRequestController {


    @Autowired
    private RequestService requestService;

    @RequestMapping(method = RequestMethod.GET, value = "operator/request/all")
    public List<Request> getAllSendRequests(@RequestParam int page,
                                            @RequestParam Sort.Direction direction,
                                            @RequestParam(required = false) String name) {
        return requestService.getSendRequests(page, direction, name);
    }

    @RequestMapping(method = RequestMethod.GET, value = "operator/request/user")
    public UserRequestView getUserRequests(@RequestParam String username,
                                           @RequestParam int page,
                                           @RequestParam Sort.Direction direction) {
        return requestService.findUsernameRequests(username, page, direction);
    }

    @RequestMapping(method = RequestMethod.GET, value = "operator/request/{id}")
    public Request getRequest(@PathVariable("id") Long id) {
        return requestService.findRequestById(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "operator/request/accept/{id}")
    public Request acceptRequest(@PathVariable("id") Long id) {
        return requestService.acceptOrDeclineRequest(id, RequestStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "operator/request/decline/{id}")
    public Request declineRequest(@PathVariable("id") Long id) {
        return requestService.acceptOrDeclineRequest(id, RequestStatus.DECLINED);
    }

}
