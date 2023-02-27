package org.mashupmedia.security;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.User;
import org.mashupmedia.service.AdminManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final AdminManager adminManager;

    public JWTAuthorizationFilter(AuthenticationManager authManager, AdminManager adminManager) {
        super(authManager);
        this.adminManager = adminManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    // Reads the JWT from the Authorization header, and then uses JWT to validate
    // the token
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING)
                .replaceFirst(SecurityConstants.TOKEN_PREFIX, "")
                .trim();

        if (StringUtils.isBlank(token)) {
            return null;
        }

        String username = null;

        try {
            // parse the token.
            Algorithm algorithm = Algorithm.HMAC512(SecurityConstants.SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(SecurityConstants.ISSUER)
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            username = decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            log.info("Unable to verify token", e);
            return null;
        }

        if (StringUtils.isBlank(username)) {
            return null;
        }

        User user = adminManager.getUser(username);
        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());

    }
}
