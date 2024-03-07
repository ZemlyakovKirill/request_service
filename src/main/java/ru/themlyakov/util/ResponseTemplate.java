package ru.themlyakov.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseTemplate<T>{
    private int status;
    private T response;
}
