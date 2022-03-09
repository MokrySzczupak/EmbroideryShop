package com.example.embroideryshop.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.embroideryshop.exception.InvalidRefreshTokenException;
import com.example.embroideryshop.security.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final String TOKEN_PREFIX = "Bearer ";
    private final String TOKEN_HEADER = "Authorization";
    private final UserDetailsServiceImpl userDetailsService;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expirationTime}")
    private long expirationTime;

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(TOKEN_HEADER);
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""));
            String userName = decodedJWT.getSubject();
            Claim claim = decodedJWT.getClaim("type");
            UserDetails user = userDetailsService.loadUserByUsername(userName);
            if (user != null && claim.asString().equals(TokenType.REFRESH_TOKEN.name())) {
                String accessToken = createToken(userName);
                response.setHeader("Authorization", TOKEN_PREFIX + accessToken);
                return;
            }
        }
        throw new InvalidRefreshTokenException();
    }

    private String createToken(String userName) {
        return JWT.create()
                .withSubject(userName)
                .withClaim("type", TokenType.ACCESS_TOKEN.name())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secret));
    }
}
