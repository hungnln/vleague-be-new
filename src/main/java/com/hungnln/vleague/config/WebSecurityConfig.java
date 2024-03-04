package com.hungnln.vleague.config;

import com.hungnln.vleague.constant.response.ResponseStatusDTO;
import com.hungnln.vleague.exceptions.CustomAccessDeniedHandler;
import com.hungnln.vleague.exceptions.CustomAuthenticationFailureHandler;
import com.hungnln.vleague.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(
        prePostEnabled = false,
        securedEnabled = false,
        jsr250Enabled =true)
@RequiredArgsConstructor
public class WebSecurityConfig {

//    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfig corsConfig;
    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
            // other public endpoints of your API may be appended to this array
    };
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() {
//        return new JwtAuthenticationFilter();
//    }
//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
//        authenticationManagerBuilder.inMemoryAuthentication().withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
//        return authenticationManagerBuilder.build();
//    }

//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userService);
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        return authenticationProvider;
//    }
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.builder().username("admin").password(passwordEncoder().encode("admin")).roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(admin);
//    }



    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,AuthenticationManager authenticationManagerBean) throws Exception {
         http
                 .httpBasic(AbstractHttpConfigurer::disable)
                 .formLogin(AbstractHttpConfigurer::disable)
//                 .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> authException))
                 .cors(cor->cor.configurationSource(corsConfig.corsConfigurationSource()))
                 .csrf(AbstractHttpConfigurer::disable)
                 .logout(AbstractHttpConfigurer::disable);
        http
                .authorizeHttpRequests(r->{
                    r.requestMatchers(antMatcher("/api/**"));
                    r.requestMatchers("/api/v1/login/**").permitAll();
                    r.requestMatchers(AUTH_WHITELIST).permitAll();
                    r.anyRequest().authenticated();
                })

                 .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e->
                    e.accessDeniedHandler((request, response, accessDeniedException) ->{
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.setContentType("application/json");
                        PrintWriter writer = response.getWriter();
                        writer.println("{ " +
                                "\"status\": \"" + ResponseStatusDTO.FAILURE + "\"," +
                                "\"message\": \"Access Denied: " + accessDeniedException.getMessage() + "\" }");
                    })
                     )
                .exceptionHandling(e->
                        e.authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            PrintWriter writer = response.getWriter();
                            writer.println("{ " +
                                    "\"status\": \"" + ResponseStatusDTO.FAILURE + "\"," +
                                    "\"message\": \"Authorize: " + authException.getMessage() + "\" }");

                        })
                )
                .authenticationManager(authenticationManagerBean);
         return http.build();
    }

}
