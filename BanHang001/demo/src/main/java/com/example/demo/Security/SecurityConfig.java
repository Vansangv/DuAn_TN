package com.example.demo.Security;

import com.example.demo.Login.CustomAuthenticationSuccessHandler;
import com.example.demo.Service.LogHeThongService;
import com.example.demo.Service.LoginHistoryService;
import com.example.demo.Service.NguoiDungService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    private final NguoiDungService nguoiDungService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(NguoiDungService nguoiDungService, PasswordEncoder passwordEncoder) {
        this.nguoiDungService = nguoiDungService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(nguoiDungService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/forgot-password", "/reset-password/**", "/css/**", "/js/**").permitAll()
                        // Quản lý người dùng
                        .requestMatchers("/nguoi-dung/them", "/nguoi-dung/sua/**", "/nguoi-dung/xoa/**").hasRole("ADMIN")
                        .requestMatchers("/nguoi-dung/**").hasAnyRole("ADMIN", "NHANVIEN")
                        // Quản lý vai trò người dùng
                        .requestMatchers("/vai-tro-nguoi-dung/add", "/vai-tro-nguoi-dung/update/**").hasRole("ADMIN")
                        .requestMatchers("/vai-tro-nguoi-dung/**").hasAnyRole("ADMIN", "NHANVIEN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );
        return http.build();
    }


    @Bean
    public AuthenticationSuccessHandler successHandler(LoginHistoryService loginHistoryService, LogHeThongService logHeThongService) {
        return new CustomAuthenticationSuccessHandler(nguoiDungService, loginHistoryService, logHeThongService);
    }
}
