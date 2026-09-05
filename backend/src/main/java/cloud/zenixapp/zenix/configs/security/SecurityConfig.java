package cloud.zenixapp.zenix.configs.security;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${api-url}")
    private String api_url;

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        //HealthCheck
                        .requestMatchers(HttpMethod.GET, "/" + api_url + "/health").permitAll()
                        // Atendimentos
                        .requestMatchers(HttpMethod.PUT, "/" + api_url + "/atendimentos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/" + api_url + "/atendimentos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/" + api_url + "/atendimentos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/" + api_url + "/atendimentos/admin").hasRole("ADMIN")
                        // Cadastro
                        .requestMatchers(HttpMethod.POST, "/" + api_url + "/cadastro").permitAll()
                        // Clientes
                        .requestMatchers(HttpMethod.GET, "/" + api_url + "/clientes/telefone/{numero}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/" + api_url + "/clientes/retorno/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/" + api_url + "/clientes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/" + api_url + "/clientes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/" + api_url + "/clientes/planos/{idCliente}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/" + api_url + "/clientes/ativar/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/" + api_url + "/clientes/planos/{idCliente}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/" + api_url + "/clientes/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/" + api_url + "/clientes/{id}").hasRole("ADMIN")
                        // Fila de Atendimento
                        .requestMatchers(HttpMethod.POST, "/" + api_url + "/fila").permitAll()
                        // Forma de Pagamento
                        .requestMatchers(HttpMethod.GET, "/" + api_url + "/pagamentos").permitAll()
                        // Planos
                        .requestMatchers(HttpMethod.GET, "/" + api_url + "/planos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/" + api_url + "/planos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/" + api_url + "/planos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/" + api_url + "/planos/{id}").hasRole("ADMIN")
                        // Serviços
                        .requestMatchers(HttpMethod.GET, "/" + api_url + "/servicos").permitAll()
                        // Usuários
                        .requestMatchers(HttpMethod.POST, "/" + api_url + "/users/register").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/" + api_url + "/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/" + api_url + "/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/" + api_url + "/users/barbeiros/{unidadeId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/" + api_url + "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/" + api_url + "/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/" + api_url + "/users/{id}").hasRole("ADMIN")
                        // Basta estar autenticado que vai poder realizar as demais requisições!
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
        config.setAllowedOrigins(List.of("http://localhost:5173", "https://app.zenixapp.cloud", "https://barber.zenixapp.cloud"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
