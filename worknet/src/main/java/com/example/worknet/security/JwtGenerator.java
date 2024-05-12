package com.example.worknet.security;

import com.example.worknet.dto.LoginUserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.charset.StandardCharsets;
import java.security.Key;

public class JwtGenerator {

    private final AuthenticationManager authenticationManager;
    private final String jwtSecret = "worknet_key_is_really_strong_try_to_hack_us";

    public JwtGenerator(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public String generateToken(@RequestBody LoginUserDTO loginUserDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDTO.getEmail(), loginUserDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        return Jwts.builder()
                .setSubject(authentication.getName())
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
