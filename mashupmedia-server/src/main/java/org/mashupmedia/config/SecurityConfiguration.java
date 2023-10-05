package org.mashupmedia.config;

import org.mashupmedia.security.CustomUserDetailsService;
import org.mashupmedia.security.JWTAuthenticationFilter;
import org.mashupmedia.security.JWTAuthorizationFilter;
import org.mashupmedia.service.AdminManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@ComponentScan("org.mashupmedia.security")
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final ObjectMapper objectMapper;

    private final AdminManager adminManager;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder,
            CustomUserDetailsService userDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.applyPermitDefaultValues();

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity httpSecurity, 
        AuthenticationManager authenticationManager,
        HandlerMappingIntrospector introspector)
            throws Exception {



        httpSecurity
                .csrf()
                .disable()
                // .authorizeHttpRequests(
                //         authorise -> authorise
                //                 .anyRequest().authenticated()

                // )
                .cors()
                .and()
                .authorizeHttpRequests(
                    authorise -> authorise
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/**").permitAll()
                )

                // .and()
                // .regexMatchers(".*/api(?!.*(security/login)).*").authenticated()
                // .ant
                // .antMatchers(".*/api(?!.*(security/login)).*").authenticated()
                // .antMatchers("/**").permitAll()
                // .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager, this.objectMapper))
                .addFilter(new JWTAuthorizationFilter(authenticationManager, adminManager))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return httpSecurity.build();
    }

}
