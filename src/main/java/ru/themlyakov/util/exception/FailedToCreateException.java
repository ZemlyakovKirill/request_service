package ru.themlyakov.util.exception;

public class FailedToCreateException extends RuntimeException {
    public FailedToCreateException(String message) {
        super(message);
    }
}
