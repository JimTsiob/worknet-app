package com.example.worknet.config;

import com.example.worknet.security.JwtGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/**").permitAll();
            auth.anyRequest().authenticated();
        }).csrf(AbstractHttpConfigurer::disable)
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/users/login")
//                        .defaultSuccessUrl("/users/") // Redirect to homepage on successful login
//                )
//                .logout(logout ->
//                        logout.deleteCookies("remove")
//                                .invalidateHttpSession(false)
//                                .logoutUrl("/logout")
//                                .logoutSuccessUrl("/login") // Redirect to login page with logout message
                /*)*/.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtGenerator generator(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return new JwtGenerator(authenticationManager(authenticationConfiguration));
    }

}
