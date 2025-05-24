package jdev.mentoria.lojavirtual;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	//class de configurações do SPRINGMVC... do modo MVC...
	//MVC e quando o projeto retorna uma pagina e nao um JSON...
	//nesse caso para parte de cartao de credito por enquanto vamos
	//precisar usar o modo MVC...
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

		return builder.sources(LojaVirtualMentoriaApplication.class);
	}

}
