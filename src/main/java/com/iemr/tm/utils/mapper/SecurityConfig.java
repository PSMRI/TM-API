package com.iemr.tm.utils.mapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.iemr.tm.utils.exception.CustomAccessDeniedHandler;
import com.iemr.tm.utils.exception.CustomAuthenticationEntryPoint;


@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
	private final RoleAuthenticationFilter roleAuthenticationFilter;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	public SecurityConfig(RoleAuthenticationFilter roleAuthenticationFilter,
	                      CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
	                      CustomAccessDeniedHandler customAccessDeniedHandler) {
	    this.roleAuthenticationFilter = roleAuthenticationFilter;
	    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
	    this.customAccessDeniedHandler = customAccessDeniedHandler;
	}

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
    csrfTokenRepository.setCookieHttpOnly(true);
    csrfTokenRepository.setCookiePath("/");
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/user/*").permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler)
        )
        .addFilterBefore(roleAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
}
