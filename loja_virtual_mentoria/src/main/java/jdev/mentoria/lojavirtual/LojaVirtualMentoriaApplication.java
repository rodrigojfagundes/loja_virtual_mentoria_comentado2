package jdev.mentoria.lojavirtual;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;


//@EntityScan mapeia as class model e gera as tabela no banco
//@ComponentScan busca em todo o projeto anotacoes do SpringBoot
//@EnableJPARepositories para indicar qual a pasta de repository
//@EnableTransactionManagement para gerenciar as transacoes com o banco
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EntityScan(basePackages = "jdev.mentoria.lojavirtual.model")
@ComponentScan(basePackages = {"jdev.*"})
@EnableJpaRepositories(basePackages = {"jdev.mentoria.lojavirtual.repository"})
@EnableTransactionManagement
public class LojaVirtualMentoriaApplication implements AsyncConfigurer{

	public static void main(String[] args) {
		
		//System.out.println(new BCryptPasswordEncoder().encode("123"));
		
		SpringApplication.run(LojaVirtualMentoriaApplication.class, args);
	}
	
	
	
	//para executar tarefas de forma assincrona, no caso o envio de e-mail
	@Override
	@Bean
	public Executor getAsyncExecutor() {
		
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("Assyncrono Thread");
		executor.initialize();
		
		return executor;
	}

	

}
