package ru.themlyakov.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.themlyakov.entity.Request;
import ru.themlyakov.entity.User;

import java.util.List;

@Data
@AllArgsConstructor
public class UserRequestView {
    private User user;
    private List<Request> requests;
}
