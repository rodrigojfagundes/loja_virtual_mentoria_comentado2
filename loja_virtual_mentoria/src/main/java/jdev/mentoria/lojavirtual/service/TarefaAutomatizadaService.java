package jdev.mentoria.lojavirtual.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import jdev.mentoria.lojavirtual.model.Usuario;
import jdev.mentoria.lojavirtual.repository.UsuarioRepository;

@Component
@Service
public class TarefaAutomatizadaService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceSendEmail serviceSendEmail;
	
	
	//@Scheduled(initialDelay = 2000, fixedDelay = 86400000) /*Roda a cada 24 horas*/
	@Scheduled(cron = "0 0 11 * * *", zone = "America/Sao_Paulo") //Vai rodar todo dia as 11 horas da manhã horario de Sao paulo
	//vai rodar a cada 24 horas
	//@Scheduled(cron = "0 0 11 * * *", zone = "America/Sao_Paulo")
	//
	//metodo para pegar todos os usuarios com a senha de mais de 90 dias
	//e enviar uma notificacao para eles
	public void notificarUserTrocaSenha() throws UnsupportedEncodingException, MessagingException, InterruptedException {
		
		List<Usuario> usuarios = usuarioRepository.usuarioSenhaVencida();
		
		for (Usuario usuario : usuarios) {
			
			
			StringBuilder msg = new StringBuilder();
			msg.append("Olá, ").append(usuario.getPessoa().getNome()).append("<br/>");
			msg.append("Está na hora de trocar sua senha, já passou 90 dias de validade.").append("<br/>");
			msg.append("Troque sua senha a loja virtual do Alex - JDEV treinamento");
			
			//enviando a mensagem acima para o e-mail do usuario q esta com
			//a mesma senha a mais de 90 dias
			serviceSendEmail.enviarEmailHtml("Troca de senha", msg.toString(), usuario.getLogin());
			
			//para enviar um e-mail a cada 3 segundos
			Thread.sleep(3000);
			
		}
		
		
	}

}
