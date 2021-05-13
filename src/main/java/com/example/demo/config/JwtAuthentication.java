package com.example.demo.config;

import com.auth0.jwt.JWT;
import org.springframework.security.core.userdetails.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.auth0.jwt.algorithms.Algorithm;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JwtAuthentication extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JwtAuthentication(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.err.println("attemptAuthentication");

        //System.err.println(username);
        /*String requestData = request.getReader().lines().collect(Collectors.joining());
        System.err.println(requestData);*/

        BufferedReader reader = request.getReader();
        Gson gson = new Gson();
        AuthForm authForm = gson.fromJson(reader, AuthForm.class);
        System.out.println(authForm.getEmail());

        String email = authForm.getEmail();
        String password = authForm.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authenticationToken);
        //return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //super.successfulAuthentication(request, response, chain, authResult);
        List<String> roles = new ArrayList<>();

        User user = (User) authResult.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256("mySecret");
        String jwtAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (24 * 24 * 60 * 60 * 1000)))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles)
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access-token", jwtAccessToken);

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}

@Data
@ToString
class AuthForm {
    public String email;
    public String password;
}
