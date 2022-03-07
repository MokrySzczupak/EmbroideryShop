package com.example.embroideryshop.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final long expirationTime;
    private final long expirationTimeRefresh;
    private final String secret;

    public RestAuthenticationSuccessHandler(
            @Value("${jwt.expirationTime}") long expirationTime,
            @Value("${jwt.expirationTimeRefresh}") long expirationTimeRefresh,
            @Value("${jwt.secret}") String secret) {
        this.expirationTime = expirationTime;
        this.expirationTimeRefresh = expirationTimeRefresh;
        this.secret = secret;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String accessToken = createAccessToken(principal.getUsername());
        String refreshToken = createRefreshToken(principal.getUsername());
        response.setHeader("Authorization", "Bearer " + accessToken + " refresh_token Bearer " + refreshToken);
    }

    private String createAccessToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withClaim("type", TokenType.ACCESS_TOKEN.name())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secret));
    }

    private String createRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withClaim("type", TokenType.REFRESH_TOKEN.name())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeRefresh))
                .sign(Algorithm.HMAC256(secret));
    }

}
