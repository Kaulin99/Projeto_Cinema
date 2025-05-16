package br.edu.cefsa.cinema.config;


import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Lista de rotas que devem redirecionar para login com parâmetro redirect
    private static final List<String> PROTECTED_REDIRECT_PATHS = List.of(
            "/perfil",
            "/minha-conta",
            "/carrinho"
    );

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/error",
                "/favicon.ico"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/plataforma/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/perfil", "/api/usuarios/perfil/**", "/minha-conta", "/api/carrinho")
                        .authenticated()
                        .requestMatchers(
                                "/",
                                "/login",
                                "/cadastro"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // .formLogin(form -> form
                //         .loginPage("/login")
                //         .loginProcessingUrl("/api/auth/login")
                //         .usernameParameter("email")
                //         .passwordParameter("senha")
                //         .successHandler((request, response, authentication) -> {
                //             // UsuarioDetails usuarioDetails = (UsuarioDetails) authentication.getPrincipal();
                //             // Usuario usuario = usuarioDetails.getUsuario();
                //             request.getSession().setAttribute("usuario", usuario);
                //             String redirectUrl = request.getParameter("redirect");

                //             if (authentication.getAuthorities().stream()
                //                     .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                //                 response.sendRedirect(redirectUrl != null ? redirectUrl : "/plataforma/admin/dashboard");
                //             } else {
                //                 response.sendRedirect(redirectUrl != null ? redirectUrl : "/");
                //             }
                //         })
                //         .failureUrl("/login?error=true")
                //         .permitAll()
                // )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            String path = request.getRequestURI();

                            if (PROTECTED_REDIRECT_PATHS.contains(path)) {
                                response.sendRedirect("/login?redirect=" + path);
                            } else if (path.startsWith("/api/")) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autorizado");
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?expired=true")
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}