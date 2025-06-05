package br.edu.cefsa.cinema.config;

import java.util.Arrays;

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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository; // Para proteção CSRF com cookies
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Classe de configuração para o Spring Security.
 * Define as regras de autenticação, autorização, login, logout, CSRF, CORS, etc.
 */
@Configuration
@EnableWebSecurity // Habilita a segurança web do Spring
@EnableMethodSecurity // Habilita a segurança a nível de método (ex: @PreAuthorize)
public class SecurityConfig {

    /**
     * Configura o Spring Security para ignorar certas requisições,
     * como para a página de erro padrão, favicon e o console do H2.
     * @return Um customizador para a segurança web.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/error",
                "/favicon.ico",
                "/h2-console/**"   // Libera o acesso ao console do H2 Database (apenas para desenvolvimento)
        );
    }

    /**
     * Define a cadeia de filtros de segurança principal para as requisições HTTP.
     * Configura CORS, CSRF, autorização de rotas, formulário de login e logout.
     * @param http Objeto HttpSecurity para construir a cadeia de filtros.
     * @return O SecurityFilterChain construído.
     * @throws Exception Se ocorrer um erro durante a configuração.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configuração do CORS (Cross-Origin Resource Sharing)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Configuração do CSRF (Cross-Site Request Forgery)
                .csrf(csrf -> csrf
                        // Usa um repositório de tokens CSRF baseado em cookie, acessível por JavaScript
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                // Define as regras de autorização para as requisições HTTP
                .authorizeHttpRequests(auth -> auth
                        // Requisições para /administracao/** exigem que o usuário tenha a ROLE_ADMIN
                        .requestMatchers("/administracao/**").hasRole("ADMIN")
                        // Permite acesso público (sem autenticação) a estas rotas e recursos estáticos
                        .requestMatchers("/","/usuarios/cadastrar", "/usuarios/cadastro","/usuarios/login", "/lol/**", "/valorant/**","/css/**", "/js/**","/img/**", "/webjars/**", "/genericos/**").permitAll()
                        // Qualquer outra requisição exige que o usuário esteja autenticado
                        .anyRequest().authenticated()
                )
                // Configura o formulário de login
                .formLogin(form -> form
                        .loginPage("/usuarios/login")         // URL da página de login customizada
                        .loginProcessingUrl("/usuarios/login") // URL para onde o formulário de login envia os dados (POST)
                        .defaultSuccessUrl("/", true)       // URL para redirecionar após login bem-sucedido
                        .failureUrl("/usuarios/login?erro=true") // URL para redirecionar após falha no login
                        .permitAll()                          // Permite acesso à página de login para todos
                )
                // Configura o logout
                .logout(logout -> logout
                        .logoutUrl("/logout")                 // URL para acionar o logout
                        .logoutSuccessUrl("/usuarios/login?logout") // URL para redirecionar após logout bem-sucedido
                        .permitAll()                          // Permite acesso à funcionalidade de logout para todos
                );

        return http.build(); // Constrói e retorna a cadeia de filtros de segurança
    }

    /**
     * Define as configurações de CORS para a aplicação.
     * Permite todas as origens, métodos e cabeçalhos (configuração aberta para desenvolvimento).
     * @return A fonte de configuração CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Permite requisições de qualquer origem
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // Métodos HTTP permitidos
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite todos os cabeçalhos
        configuration.setAllowCredentials(false); // Define se credenciais (como cookies) são permitidas em requisições cross-origin

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a configuração a todas as rotas
        return source;
    }

    /**
     * Define o codificador de senhas usado na aplicação (BCrypt).
     * @return Uma instância do BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expõe o AuthenticationManager como um Bean do Spring.
     * Necessário para processos de autenticação, especialmente se houver autenticação customizada.
     * @param authConfig A configuração de autenticação do Spring.
     * @return O AuthenticationManager.
     * @throws Exception Se ocorrer um erro ao obter o AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}