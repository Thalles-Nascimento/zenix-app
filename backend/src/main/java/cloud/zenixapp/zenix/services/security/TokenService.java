package cloud.zenixapp.zenix.services.security;

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
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("zenixapp")
                    .withSubject(user.getEmail())
                    .withExpiresAt(this.expiratedToken())
                    .sign(algorithm);

            return token;
        }
        catch(JWTCreationException exception){
            throw new RuntimeException("Erro na geração do Token");
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
            return "";
        }
    }

    private Instant expiratedToken(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}
