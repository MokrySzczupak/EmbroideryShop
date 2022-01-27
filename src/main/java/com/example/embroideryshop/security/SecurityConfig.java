package com.example.embroideryshop.security;

import com.example.embroideryshop.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;
    private final ObjectMapper objectMapper;
    private final RestAuthenticationSuccessHandler successHandler;
    private final RestAuthenticationFailureHandler failureHandler;
    private final UserDetailsServiceImpl userDetailsService;
    private final String secret;

    public SecurityConfig(DataSource dataSource, ObjectMapper objectMapper, RestAuthenticationSuccessHandler successHandler,
                          RestAuthenticationFailureHandler failureHandler,
                          UserDetailsServiceImpl userDetailsService,
                          @Value("${jwt.secret}") String secret) {
        this.dataSource = dataSource;
        this.objectMapper = objectMapper;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.userDetailsService = userDetailsService;
        this.secret = secret;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select email,username,password from user where email = ?")
                .authoritiesByUsernameQuery("select email,authority from authorities where email = ?");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http.authorizeRequests()
                .mvcMatchers("/register").permitAll()
                .mvcMatchers(HttpMethod.GET, "/products/**").permitAll()
                .mvcMatchers(HttpMethod.POST, "/products/category").hasAuthority("ADMIN")
                .mvcMatchers(HttpMethod.PUT, "/products/category").hasAuthority("ADMIN")
                .mvcMatchers(HttpMethod.POST, "/products").hasAuthority("ADMIN")
                .mvcMatchers(HttpMethod.DELETE, "/products/**").hasAuthority("ADMIN")
                .mvcMatchers(HttpMethod.PUT, "/products").hasAuthority("ADMIN")
                .mvcMatchers("/cart/all").hasAnyAuthority("ADMIN")
                .mvcMatchers("/cart/complete/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(authenticationFilter())
                .addFilter(new JwtAuthorizationFilter(userDetailsService(), authenticationManager(), secret))
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .headers().frameOptions().disable();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Override
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
//        web.debug(true);
    }


    private JsonObjectAuthenticationFilter authenticationFilter() throws Exception {
        JsonObjectAuthenticationFilter authenticationFilter = new JsonObjectAuthenticationFilter(objectMapper);
        authenticationFilter.setAuthenticationSuccessHandler(successHandler);
        authenticationFilter.setAuthenticationFailureHandler(failureHandler);
        authenticationFilter.setAuthenticationManager(super.authenticationManager());
        return authenticationFilter;
    }

}