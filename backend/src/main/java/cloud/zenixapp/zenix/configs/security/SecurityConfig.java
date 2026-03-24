package cloud.zenixapp.zenix.configs.security;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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
                        .requestMatchers(HttpMethod.POST, "/api/${api.version}/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/${api.version}/cadastro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/${api.version}/fila").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/${api.version}/users/barbeiros/{unidadeId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/${api.version}/servicos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/${api.version}/pagamentos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/${api.version}/clientes/telefone/{numero}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/${api.version}/clientes/retorno/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/${api.version}/clientes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/${api.version}/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/${api.version}/atendimentos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/${api.version}/clientes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/${api.version}/clientes/planos/{idCliente}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/${api.version}/clientes/ativar/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/${api.version}/clientes/planos/{idCliente}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/${api.version}/planos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/${api.version}/users/register").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/${api.version}/clientes/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/${api.version}/clientes/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/${api.version}/planos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/${api.version}/planos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/${api.version}/planos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/${api.version}/atendimentos/usuario/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/${api.version}/atendimentos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/${api.version}/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/${api.version}/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/${api.version}/users/{id}").hasRole("ADMIN")
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
