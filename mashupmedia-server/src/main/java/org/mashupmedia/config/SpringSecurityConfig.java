package org.mashupmedia.config;

import org.mashupmedia.security.CustomUserDetailsService;
import org.mashupmedia.security.JWTAuthenticationFilter;
import org.mashupmedia.security.JWTAuthorizationFilter;
import org.mashupmedia.security.SecurityConstants;
import org.mashupmedia.service.AdminManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@ComponentScan("org.mashupmedia.security")
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Lazy
    private AdminManager adminManager;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SpringSecurityConfig() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return this.bCryptPasswordEncoder;
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
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


    @Override
    protected void configure(HttpSecurity httpSecurity)
            throws Exception {

        httpSecurity
                .csrf().disable().authorizeRequests().and()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,  SecurityConstants.SIGN_UP_URL).permitAll()
                // .antMatchers("/mashupmedia/**/*").permitAll()
                
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), this.objectMapper))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), adminManager))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//        httpSecurity.cors().and()
//                .authorizeRequests()
////                .antMatchers("/h2-console/**").permitAll()
////                .antMatchers("/**/*.{js,html,css}").permitAll()
////                .antMatchers("/", "/api/user").permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .formLogin()
//                .loginProcessingUrl("/perform_login")
//                .failureUrl("/login?error=true")
//                .and().csrf().disable();


//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
////                .ignoringAntMatchers("/h2-console/**");
//        httpSecurity.headers()
//                .frameOptions()
//                .sameOrigin();
    }

}
