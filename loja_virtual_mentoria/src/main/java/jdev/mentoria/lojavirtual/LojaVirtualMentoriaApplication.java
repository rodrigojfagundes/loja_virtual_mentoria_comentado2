package jdev.mentoria.lojavirtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@EntityScan mapeia as class model e gera as tabela no banco
//@ComponentScan busca em todo o projeto anotacoes do SpringBoot
//@EnableJPARepositories para indicar qual a pasta de repository
//@EnableTransactionManagement para gerenciar as transacoes com o banco
@SpringBootApplication
@EntityScan(basePackages = "jdev.mentoria.lojavirtual.model")
@ComponentScan(basePackages = {"jdev.*"})
@EnableJpaRepositories(basePackages = {"jdev.mentoria.lojavirtual.repository"})
@EnableTransactionManagement
public class LojaVirtualMentoriaApplication {

	public static void main(String[] args) {
		
		System.out.println(new BCryptPasswordEncoder().encode("123"));//
		
		
		SpringApplication.run(LojaVirtualMentoriaApplication.class, args);
	}

}
