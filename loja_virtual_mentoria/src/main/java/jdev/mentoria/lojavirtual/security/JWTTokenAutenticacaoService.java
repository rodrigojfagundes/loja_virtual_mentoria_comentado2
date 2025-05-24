package jdev.mentoria.lojavirtual.security;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

//Essa class JWTTOkenAutenticacaoService, tem basicamente 2 funcoes
//Criar a autenticacao e retornar tambem a autenticacao JWT
@Service
@Component
public class JWTTokenAutenticacaoService {
	
	//token de validade de 11 dias
	private static final long EXPIRATION_TIME = 959990000;
	private static final String SECRET = "ss/-*-*sds565dsd-s/d-s*dsds";
	private static final String TOKEN_PREFIX = "Bearer";
	private static final String HEADER_STRING = "Authorization";
	
	//gera o token e da a resposta para o cliente com o JWT
	public void addAuthentication(
			HttpServletResponse response, String username) throws Exception {
		
		//Montagem do Token
	
		//chama o gerador de Token e adiciona o user
		//e informamos o tempo de expiracao do token q ta na var EXPIRATION_TIME
		//e com signwith informamos o algoritmo de criptografia e a senha
		//para fazer a assinatura do token
		String JWT = Jwts.builder()
				.setSubject(username)
				.setExpiration(new Date(
						System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		
		//Exe: Bearer *-/hash do token JWT
		/*Exe: Bearer *-/a*dad9s5d6as5d4s5d4s45dsd5
		 * 4s.sd4s4d45s45d4sd54d45s4d5s.ds5d5s5d5s65d6s6d*/
		String token = TOKEN_PREFIX + " " + JWT;
		
		
		//Da a resposta para a tela e para o cliente, tipo outra API, 
		//Navegador, aplicativo, javascript, etc...
		response.addHeader(HEADER_STRING, token);
		
		/*Usado para ver no Postman para teste*/
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
		
	}
	
}
