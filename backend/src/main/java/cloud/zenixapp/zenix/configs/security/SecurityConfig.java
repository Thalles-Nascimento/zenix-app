package cloud.zenixapp.zenix.configs.security;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/fila").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/barbeiros/{unidadeId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/servicos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/pagamentos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/clientes/telefone/{numero}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/clientes/retorno/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/clientes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/atendimentos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/atendimentos/usuario/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/atendimentos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{id}").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "https://app.zenixapp.cloud"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
