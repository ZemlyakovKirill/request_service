package ru.themlyakov.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import ru.themlyakov.filter.AuthFilter;
import ru.themlyakov.filter.ExceptionHandlerFilter;
import ru.themlyakov.util.CustomAuthEntryPoint;

import java.util.List;


@Configuration
@EnableWebSecurity
public class ServerSecurityConfig extends WebMvcConfigurationSupport {


    @Autowired
    private AuthFilter authFilter;


    @Autowired
    private ExceptionHandlerFilter exceptionHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(Customizer.withDefaults())
                .exceptionHandling(configurer->
                        configurer.authenticationEntryPoint(authenticationEntryPoint()))
                .authorizeHttpRequests(reqMatch ->
                        reqMatch.requestMatchers("login","denied").permitAll()
                                .requestMatchers("request/**").authenticated()
                                .requestMatchers("operator/**").hasRole("OPERATOR")
                                .requestMatchers("admin/**").hasRole("ADMIN")
                                .anyRequest().permitAll())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandler, AuthFilter.class)
                .build();
    }


    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
        super.addDefaultHttpMessageConverters(converters);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new CustomAuthEntryPoint();
    }
}
