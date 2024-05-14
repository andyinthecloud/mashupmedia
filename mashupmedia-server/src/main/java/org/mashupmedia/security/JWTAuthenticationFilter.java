package org.mashupmedia.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import org.mashupmedia.dto.login.LoginPayload;
import org.mashupmedia.dto.login.SecurityPayload;
import org.mashupmedia.model.account.User;
import org.mashupmedia.util.DateHelper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl(SecurityConstants.SIGN_UP_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        try {
            LoginPayload loginPayload = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginPayload.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginPayload.getUsername(),
                            loginPayload.getPassword(),
                            new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        LocalDateTime localDateExpires = LocalDateTime.now().plusHours(SecurityConstants.EXPIRATION_HOURS);
        Date dateExpires = DateHelper.toDate(localDateExpires);

        Algorithm algorithm = Algorithm.HMAC512(SecurityConstants.SECRET);
        String token = JWT.create()
                .withIssuer(SecurityConstants.ISSUER)
                .withSubject(((User) authentication.getPrincipal()).getUsername())
                .withExpiresAt(dateExpires)
                .sign(algorithm);

        SecurityPayload userPayload = SecurityPayload
                .builder()
                .token(token)
                .username(authentication.getName())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(userPayload));
        response.getWriter().flush();
    }
}
