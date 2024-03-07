package ru.themlyakov.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import ru.themlyakov.entity.Request;
import ru.themlyakov.entity.User;
import ru.themlyakov.service.RequestService;
import ru.themlyakov.service.UserService;
import ru.themlyakov.util.RequestStatus;
import ru.themlyakov.util.exception.FailedToCreateException;
import ru.themlyakov.util.exception.IncorrectDataException;

import java.security.Principal;
import java.util.List;

@RestController
public class UserRequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userService;


    @RequestMapping(method = RequestMethod.POST, value = "/request/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Request createRequest(Principal principal, @RequestParam String name,
                                 @RequestParam String message,
                                 @RequestParam String phone) throws JsonProcessingException, FailedToCreateException {
        User user = userService.findByUsername(principal.getName());
        Request request = new Request(name, message, phone, user);
        boolean created = requestService.createRequest(request);
        if (created) {
            return request;
        }
        throw new FailedToCreateException("Request not created");
    }


    @RequestMapping(method = RequestMethod.GET, value = "/request/all")
    public List<Request> getUserRequests(Authentication authentication, @RequestParam Integer page,
                                                    @RequestParam Sort.Direction direction,
                                                    @RequestParam(required = false) RequestStatus status) {
        User user = userService.findByUsername(authentication.getName());
        return requestService.findUserRequests(user,page,direction,status);
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/request/send/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Request sendRequest(Authentication authentication, @PathVariable("id") Long id){
        return requestService.sendRequest(authentication,id);
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/request/edit")
    @ResponseStatus(HttpStatus.CREATED)
    public Request editRequest(Authentication authentication, @RequestBody Request request)
            throws JsonProcessingException, IncorrectDataException, MissingServletRequestParameterException {
        return requestService.editDraftRequest(authentication,request);
    }






}
