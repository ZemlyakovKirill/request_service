package ru.themlyakov.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.PersistenceException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import ru.themlyakov.util.exception.*;

@RestControllerAdvice
public class ResponseAdviceImpl implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String dataException(PersistenceException exception) {
        return "Fetching or saving data exception";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String requestMissed(MissingServletRequestParameterException ex, WebRequest request) {
        return "Missed " +
                ex.getParameterType() +
                " request parameter " +
                ex.getParameterName();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String methodMismatched(MethodArgumentTypeMismatchException ex) {
        return "Argument " +
                ex.getName() +
                " request type " +
                ex.getParameter().getParameterType().getSimpleName();
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public String tokenException() {
        return "Incorrect Token";
    }

    @ExceptionHandler({UserRoleMissed.class, UserAlreadyOperatorException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String userException(RuntimeException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    public String methodNotAllowedException(HttpRequestMethodNotSupportedException ex) {
        return "Method '" +
                ex.getMethod() +
                "' not allowed to this path";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String pageNotFoundException(NoHandlerFoundException ex) {
        return "Path '" +
                ex.getRequestURL() +
                "' not found";
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String notFoundException(NotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler({IncorrectDataException.class, JsonProcessingException.class, FailedToCreateException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String incorrectDataException(RuntimeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public String authException() {
        return "Access denied";
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return null;
        }
        ServletServerHttpResponse servletResponse = (ServletServerHttpResponse) response;
        int status = servletResponse.getServletResponse().getStatus();

        final ResponseTemplate<Object> output = new ResponseTemplate<>();
        output.setResponse(body);
        output.setStatus(status);
        return output;
    }
}
