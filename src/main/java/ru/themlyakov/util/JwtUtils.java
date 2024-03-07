package ru.themlyakov.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@PropertySource("classpath:/ru/themlyakov/")
public class JwtUtils {


    @Value("$(app.jwt.secret)")
    private String jwtSecret;
    @Value("#{new Integer('${app.jwt.expiration}')}")
    private Integer expiration;


    public boolean validateJwtToken(String authToken) throws io.jsonwebtoken.ExpiredJwtException, io.jsonwebtoken.MalformedJwtException, IllegalArgumentException {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
        return Objects.equals(getRemoteAddress(), claimsJws.getBody().getIssuer());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(getRemoteAddress())
                .setExpiration(new Date(new Date().getTime() + TimeUnit.MINUTES.toMillis(expiration)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private String getRemoteAddress() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        if (attribs instanceof NativeWebRequest) {
            HttpServletRequest request = (HttpServletRequest) ((NativeWebRequest) attribs).getNativeRequest();
            return request.getRemoteAddr();
        }
        return null;
    }

    public String getUsernameFromJwt(String jwt) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();
        return claims.getSubject();
    }
}
