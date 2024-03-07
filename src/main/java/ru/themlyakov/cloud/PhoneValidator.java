package ru.themlyakov.cloud;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("phones")
public interface PhoneValidator {

    @RequestMapping(value = "api/v1/clean/phone",method = RequestMethod.POST,consumes = "application/json")
    String validatePhone(@RequestBody List<String> phones);
}
