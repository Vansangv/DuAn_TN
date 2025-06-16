package com.example.demo.Security;

import com.example.demo.Login.CustomAuthenticationSuccessHandler;
import com.example.demo.Service.LogHeThongService;
import com.example.demo.Service.LoginHistoryService;
import com.example.demo.Service.NguoiDungService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.core.annotation.Order;

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

    //offline
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login", "/register", "/forgot-password", "/reset-password/**", "/css/**", "/js/**").permitAll()
//                        // Quản lý người dùng
//                        .requestMatchers("/nguoi-dung/them", "/nguoi-dung/sua/**", "/nguoi-dung/xoa/**").hasRole("ADMIN")
//                        .requestMatchers("/nguoi-dung/**").hasAnyRole("ADMIN", "NHANVIEN")
//                        // Quản lý vai trò người dùng
//                        .requestMatchers("/vai-tro-nguoi-dung/add", "/vai-tro-nguoi-dung/update/**").hasRole("ADMIN")
//                        .requestMatchers("/vai-tro-nguoi-dung/**").hasAnyRole("ADMIN", "NHANVIEN")
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .successHandler(successHandler)
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login")
//                        .permitAll()
//                );
//        return http.build();
//    }


    // online
    @Bean
    @Order(1)
    public SecurityFilterChain onlineSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/login-online", "/online-home", "/css/**", "/js/**") // áp dụng cho các URL này
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login-online", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login-online")
                        .loginProcessingUrl("/login-online") // submit form tại đây
                        .defaultSuccessUrl("/online-home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout-online")
                        .logoutSuccessUrl("/login-online")
                        .permitAll()
                );

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/forgot-password", "/reset-password/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/nguoi-dung/them", "/nguoi-dung/sua/**", "/nguoi-dung/xoa/**").hasRole("ADMIN")
                        .requestMatchers("/nguoi-dung/**").hasAnyRole("ADMIN", "NHANVIEN")
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
