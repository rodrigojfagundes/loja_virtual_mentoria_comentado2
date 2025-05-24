package jdev.mentoria.lojavirtual.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import jdev.mentoria.lojavirtual.model.Usuario;

//
//Filtro para usar o servico, 
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
	

	//configurando o gerenciador de autenticacao
	public JWTLoginFilter(String url, AuthenticationManager authenticationManager) {
		
		//obriga a autenticar a url
		super(new AntPathRequestMatcher(url));
		
		//gerenciador de autenticacao
		setAuthenticationManager(authenticationManager);
		
	}
	
	


	//retorna o usuario ao processar na autenticacao
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
	
	//obter o usuario	
		Usuario user = new ObjectMapper()
		.readValue(request.getInputStream(), Usuario.class);
		
		//retorna o user com login e senha
		return getAuthenticationManager()
				.authenticate(
						new UsernamePasswordAuthenticationToken(
								user.getLogin(), user.getSenha()));
	}
	
	@Override
	protected void successfulAuthentication(
			HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		try {
			new JWTTokenAutenticacaoService()
			.addAuthentication(response, authResult.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
