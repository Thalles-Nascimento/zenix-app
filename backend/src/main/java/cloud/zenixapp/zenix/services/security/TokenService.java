package cloud.zenixapp.zenix.services.security;

import cloud.zenixapp.zenix.configs.exceptions.TokenCreateException;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.secret}")
    private String secret;

    public String generateToken(Usuarios user){
        try {
            if (user.getTenant() == null){
                throw new TokenCreateException("Usuário sem tenant associado");
            }
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("zenixapp")
                    .withSubject(user.getEmail())
                    .withClaim("tenantId", user.getTenant())
                    .withExpiresAt(this.expiratedToken())
                    .sign(algorithm);
        }
        catch(JWTCreationException exception){
            throw new TokenCreateException(exception.getMessage());
        }
    }

    public String extractTenantId(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("zenixapp")
                    .build()
                    .verify(token)
                    .getClaim("tenantId")
                    .asString();
        } catch (JWTCreationException exception) {
            throw new TokenCreateException(exception.getMessage());
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("zenixapp")
                    .build()
                    .verify(token)
                    .getSubject();

        }catch(JWTCreationException exception){
            throw new TokenCreateException(exception.getMessage());
        }
    }

    private Instant expiratedToken(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}
