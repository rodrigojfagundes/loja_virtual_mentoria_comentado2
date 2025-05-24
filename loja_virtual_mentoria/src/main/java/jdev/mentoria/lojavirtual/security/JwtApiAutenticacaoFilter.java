package jdev.mentoria.lojavirtual.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;


//essa classe intercepta as requisicoes q vem para aplicacao Java+Spring para
//autenticar
//
/*Filtro onde todas as requisicoes serão capturadas para autenticar*/
public class JwtApiAutenticacaoFilter extends GenericFilterBean {

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		try {
		
			/*Estabele a autenticao do user*/
			
			//OS CODIGO A BAIXO TAVA COMENTADO NA VERSAO DO PROF
			
			Authentication authentication = new JWTTokenAutenticacaoService().
					getAuthetication((HttpServletRequest) request, (HttpServletResponse) response);
			
			/*Coloca o processo de autenticacao para o spring secutiry*/
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			chain.doFilter(request, response);
		
		}catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("Ocorreu um erro no sistema, avise o administrador: \n" + e.getMessage());
		}
		
	}
	
	

}
