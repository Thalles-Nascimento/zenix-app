package cloud.zenixapp.zenix.services.security;

import cloud.zenixapp.zenix.entities.Usuarios;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

//  Determinando a chave privada para criação do hash/criptografia
    @Value("${api.security.secret}")
    private String secretKey;

//  Metodo para gerar o Token
    public String generatedToken(Usuarios user){
        try {
//          Utilizando o Hash256 para gerar a criptografia
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

//          Criando e retornando o Token
            return JWT.create()
                    .withIssuer("zenix") // Dizendo qual aplicação gerou o token
                    .withSubject(user.getEmail()) // Sujeito que está recebendo o token/sendo usado para gerar
                    .withExpiresAt(this.expirationDate()) // Quando o token expirará
                    .sign(algorithm);

        } catch (JWTCreationException jwte) {
            throw new RuntimeException("Erro na hora de criar a autenticação");
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .withIssuer("zenix")
                    .build()
                    .verify(token)
                    .getSubject();

        }catch (JWTVerificationException jwte){
            return null;
        }
    }


//  Metodo para gerar o tempo de duração do Token
    private Instant expirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}
