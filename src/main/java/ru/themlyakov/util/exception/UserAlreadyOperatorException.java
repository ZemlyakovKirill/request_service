package ru.themlyakov.util.exception;

public class UserAlreadyOperatorException extends RuntimeException {
    public UserAlreadyOperatorException(String message) {
        super(message);
    }
}
