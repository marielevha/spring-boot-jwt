package com.example.demo.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorization extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        String authorization = req.getHeader("authorization");
        String token, email;

        if (authorization != null && authorization.startsWith("Bearer")) {
            try {
                token = authorization.substring(7);
                Algorithm algorithm = Algorithm.HMAC256("mySecret");

                JWTVerifier jwtVerifier  = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(token);

                email = decodedJWT.getSubject();
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(email, null, null);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                filterChain.doFilter(req, resp);
            }
            catch (Exception e) {
                resp.setHeader("Error", e.getMessage());
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        else {
            filterChain.doFilter(req, resp);
        }
    }
}
