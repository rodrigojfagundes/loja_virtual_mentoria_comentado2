package jdev.mentoria.lojavirtual.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jdev.mentoria.lojavirtual.model.Usuario;
import jdev.mentoria.lojavirtual.repository.UsuarioRepository;

@Service
public class TarefaAutomatizadaService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceSendEmail serviceSendEmail;
	
	
	@Scheduled(initialDelay = 2000, fixedDelay = 86400000 )//vai rodar a cada 24 horas
	//vai rodar todo dia as 11 da manha horario de Sao Paulo SP - Brasil
	//@Scheduled(cron = "0 0 11 * * *", zone = "America/Sao_Paulo")
	//
	//metodo para pegar todos os usuarios com a senha de mais de 90 dias
	//e enviar uma notificacao para eles
	public void notificarUserTrocaSenha() throws UnsupportedEncodingException, MessagingException, InterruptedException {
		
		List<Usuario> usuarios = usuarioRepository.usuarioSenhaVencida();
		
		for(Usuario usuario : usuarios) {
			StringBuilder msg = new StringBuilder();
			
			msg.append("Ola, ").append(usuario.getPessoa().getNome()).append("<br />");
			msg.append("Esta na hora de trocar a sua senha, ja passou 90 dias de validade").append("<br/>");
			msg.append("Troque sua senha a loja virtual do Rodrigo - JDev treinamento");
			
			//enviando a mensagem acima para o e-mail do usuario q esta com
			//a mesma senha a mais de 90 dias
			serviceSendEmail.enviarEmailHtml(
					"Troca de senha", msg.toString(), usuario.getLogin());
			
			//para enviar um e-mail a cada 3 segundos
			Thread.sleep(3000);
			
		}
		
		
	}
	
	
	
}
