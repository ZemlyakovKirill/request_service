package ru.themlyakov.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.themlyakov.entity.User;
import ru.themlyakov.entity.UserRole;
import ru.themlyakov.repository.UserRepository;
import ru.themlyakov.repository.UserRoleRepository;
import ru.themlyakov.util.JwtUtils;
import ru.themlyakov.util.Role;
import ru.themlyakov.util.exception.NotFoundException;
import ru.themlyakov.util.exception.UserAlreadyOperatorException;
import ru.themlyakov.util.exception.UserRoleMissed;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRoleRepository roleRepository;

    public List<User> allUsers() {
        List<User> response = new ArrayList<>();
        Iterable<User> users = userRepository.findAll();
        users.forEach(response::add);
        return response;
    }

    public User findUserById(Long id) throws  NotFoundException{
        assert id!=null;
        return userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws NotFoundException {
        return userRepository.findByName(username).orElseThrow(() -> new NotFoundException("User Not Found"));
    }

    public User findByUsername(String username) throws NotFoundException{

        return userRepository.findByName(username).orElseThrow(() -> new NotFoundException("User not found"));
    }


    public String generateToken(User user){
        return jwtUtils.generateToken(user.getName());
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByUsernameContains(String name) throws NotFoundException{
        return userRepository.findByNameContains(name).orElseThrow(()-> new NotFoundException("User not found"));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User grantUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        int temp = 0;
        List<Role> roles = user.getRoles().stream().map(UserRole::getRole).toList();
        if(roles.contains(Role.ROLE_USER)&&!roles.contains(Role.ROLE_OPERATOR)){
            UserRole role = roleRepository.findByRoleIs(Role.ROLE_OPERATOR).orElseThrow(()-> new NotFoundException("Role not found"));
            user.getRoles().add(role);
            return userRepository.save(user);

        }
        else if(roles.contains(Role.ROLE_USER)) throw new UserAlreadyOperatorException("User already operator");
        else throw new UserRoleMissed("Role user is not set");
    }
}
