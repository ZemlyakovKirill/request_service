package ru.themlyakov.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.themlyakov.entity.User;
import ru.themlyakov.service.UserService;

@RestController
public class AuthController {

    @Autowired
    public UserService service;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String username, @RequestParam String password) {
        User user = service.findByUsername(username);
        return checkPasswordAndGenerateToken(user, password);
    }

    protected String checkPasswordAndGenerateToken(User user, String password) {
        if (user.getPassword().equals(password)) {
            String token = service.generateToken(user);
            return token;
        }
        return "Incorrect data";
    }

}
