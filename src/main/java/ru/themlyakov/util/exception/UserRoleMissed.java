package ru.themlyakov.util.exception;

public class UserRoleMissed extends RuntimeException {
    public UserRoleMissed(String message) {
        super(message);
    }
}
