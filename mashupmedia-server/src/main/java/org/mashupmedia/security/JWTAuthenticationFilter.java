package org.mashupmedia.security;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.mashupmedia.dto.login.LoginPayload;
import org.mashupmedia.dto.login.SecurityPayload;
import org.mashupmedia.model.User;
import org.mashupmedia.util.DateHelper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
        // Date dateExpires = DateHelper.toDate(LocalDateTime.now().minusMinutes(1));

        String token = JWT.create()
                .withSubject(((User) authentication.getPrincipal()).getUsername())
                .withExpiresAt(dateExpires)
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

        SecurityPayload userPayload = SecurityPayload
                .builder()
                .token(token)
                .username(authentication.getName())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(userPayload));
        response.getWriter().flush();
    }
}