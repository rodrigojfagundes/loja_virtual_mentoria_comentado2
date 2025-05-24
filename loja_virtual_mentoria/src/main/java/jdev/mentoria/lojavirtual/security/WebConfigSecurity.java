package jdev.mentoria.lojavirtual.security;

import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jdev.mentoria.lojavirtual.service.ImplementacaoUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebConfigSecurity extends WebSecurityConfigurerAdapter implements HttpSessionListener {
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		//permitindo acesso ao metodo/endpoint REQUISICAOJUNOBOLETO(WEBHOOK)
		//sem precisar ter login no sistema...
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index","/pagamento/**","/resources/**","/static/**","/templates/**","classpath:/static/**","classpath:/resources/**","classpath:/templates/**").permitAll()
		.antMatchers(HttpMethod.POST, "/requisicaojunoboleto/**", "/notificacaoapiv2","/pagamento/**","/resources/**","/static/**","/templates/**","classpath:/static/**","classpath:/resources/**","classpath:/templates/**").permitAll()
		.antMatchers(HttpMethod.GET, "/requisicaojunoboleto/**", "/notificacaoapiv2","/pagamento/**","/resources/**","/static/**","/templates/**","classpath:/static/**","classpath:/resources/**","classpath:/templates/**").permitAll()
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		
		//redireciona ou da um retorno para index quando desloga
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		
		//mapear a parte de logout
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		//filtra as requisicoes para login de JWT
		.and().addFilterAfter(new JWTLoginFilter("/login", authenticationManager()),
				UsernamePasswordAuthenticationFilter.class)
		
		.addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
		
	}
	
	//metodo para implementar o servico de autenticacao
	//ira consultaro user no banco com o SpringSecurity
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(implementacaoUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
		
	}
	
	

	//ignorando URLS no momento para nao autenticar
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().
		    antMatchers(HttpMethod.GET, "/requisicaojunoboleto/**", "/notificacaoapiv2","/pagamento/**","/resources/**","/static/**","/templates/**","classpath:/static/**","classpath:/resources/**","classpath:/templates/**","/webjars/**","/WEB-INF/classes/static/**")
		   .antMatchers(HttpMethod.POST,"/requisicaojunoboleto/**", "/notificacaoapiv2","/pagamento/**","/resources/**","/static/**","/templates/**","classpath:/static/**","classpath:/resources/**","classpath:/templates/**","/webjars/**","/WEB-INF/classes/static/**");
		/* Ingnorando URL no momento para nao autenticar */
	}

}
