
package jdev.mentoria.lojavirtual.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.PessoaJuridica;
import jdev.mentoria.lojavirtual.model.Usuario;
import jdev.mentoria.lojavirtual.repository.PessoaFisicaRepository;
import jdev.mentoria.lojavirtual.repository.PessoaRepository;
import jdev.mentoria.lojavirtual.repository.UsuarioRepository;

@Service
public class PessoaUserService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ServiceSendEmail serviceSendEmail;
	
	@Autowired
	private PessoaFisicaRepository pessoaFisicaRepository;
	
	
	public PessoaJuridica salvarPessoaJuridica(PessoaJuridica juridica) {
		
	//	juridica = pessoaRepository.save(juridica);
		
		
		for (int i = 0; i< juridica.getEnderecos().size(); i++) {
			juridica.getEnderecos().get(i).setPessoa(juridica);
			juridica.getEnderecos().get(i).setEmpresa(juridica);
		}
		
		juridica = pessoaRepository.save(juridica);
		
		//verificando se ja existe um usuario para essa PESSOAJURIDICA,
		//usuario com USERNAME e PASSWORD... Iremos consultar por ID e 
		//e-mail/login
		Usuario usuarioPj = usuarioRepository
				.findUserByPessoa(juridica.getId(), juridica.getEmail());
		
		
		//se nao tiver usuario para essa PESSOAJURIDICA, vamos fazer o cadastro
		if (usuarioPj == null) {
			
			String constraint = usuarioRepository.
					consultaConstraintAcesso();
			
			if (constraint != null) {
				jdbcTemplate.execute("begin; alter table usuarios_acesso drop constraint " + constraint +"; commit;");
			}
			
			//criando um novo usuario para cadastrar a pessoa juridica
			//pois qd cad uma pessoa juridica é criado automaticamente um
			//usuario para acessar o sistema
			usuarioPj = new Usuario();
			usuarioPj.setDataAtualSenha(Calendar.getInstance().getTime());
			usuarioPj.setEmpresa(juridica);
			usuarioPj.setPessoa(juridica);
			usuarioPj.setLogin(juridica.getEmail());
			
			//a senha gerada aleartoriamente é a data e os min atual
			String senha = "" + Calendar.getInstance().getTimeInMillis();
			String senhaCript = new BCryptPasswordEncoder().encode(senha);
			
			usuarioPj.setSenha(senhaCript);
			
			usuarioPj = usuarioRepository.save(usuarioPj);
			
			usuarioRepository.insereAcessoUser(usuarioPj.getId());
			//inserindo PJ/EMPRESA com ROLE/ACESSO DINAMICO no caso "ROLE_ADMIN"
			//alem do ROLE_USER q e o padrao...
			usuarioRepository.insereAcessoUserPj(usuarioPj.getId(), "ROLE_ADMIN");
			
			StringBuilder menssagemHtml = new StringBuilder();
			
			menssagemHtml.append("<b>Segue a baixo seus dados de acesso para a loja virtual </b><br />");
			menssagemHtml.append("<b>Login: </b>"+juridica.getEmail() +"<br />");
			menssagemHtml.append("<b>Senha: </b>").append(senha).append("<br /><br />");
			menssagemHtml.append("<b>Obrigado! </b>");
			
			//enviando email com o titulo, o conteudo e o e-mail de destino
			//e-mail de destino e o q foi cad na hora de criar a empresa 
			//(PESSOAJURIDICA)
			try {
			serviceSendEmail.enviarEmailHtml(		
					"Acesso Gerado para Loja Virtual",
					menssagemHtml.toString(),
					juridica.getEmail());
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
			
		
		
		//se tiver usuario para essa PJ dai vamos retornar 
		//a pessoa juridica
		return juridica;
	}

	
	
	
	//metodo para salvar uma PESSOAFISICA
	public PessoaFisica salvarPessoaFisica(PessoaFisica pessoaFisica) {
		
		for (int i = 0; i< pessoaFisica.getEnderecos().size(); i++) {
			pessoaFisica.getEnderecos().get(i).setPessoa(pessoaFisica);
			//pessoaFisica.getEnderecos().get(i).setEmpresa(pessoaFisica);
		}
		
		pessoaFisica = pessoaFisicaRepository.save(pessoaFisica);
		
		//verificando se ja existe um usuario para essa PESSOAFISICA,
		//usuario com USERNAME e PASSWORD... Iremos consultar por ID e 
		//e-mail/login
		Usuario usuarioPf = usuarioRepository
				.findUserByPessoa(pessoaFisica.getId(), pessoaFisica.getEmail());
		
		
		//se nao tiver usuario para essa PESSOAFISICA vamos fazer o cadastro
		if (usuarioPf == null) {
			
			String constraint = usuarioRepository.
					consultaConstraintAcesso();
			
			if (constraint != null) {
				jdbcTemplate.execute("begin; alter table usuarios_acesso drop constraint " + constraint +"; commit;");
			}
			
			//criando um novo usuario para cadastrar a PESSOAFISICA
			usuarioPf = new Usuario();
			usuarioPf.setDataAtualSenha(Calendar.getInstance().getTime());
			usuarioPf.setEmpresa(pessoaFisica.getEmpresa());
			usuarioPf.setPessoa(pessoaFisica);
			usuarioPf.setLogin(pessoaFisica.getEmail());
			
			//a senha gerada aleartoriamente é a data e os min atual
			String senha = "" + Calendar.getInstance().getTimeInMillis();
			String senhaCript = new BCryptPasswordEncoder().encode(senha);
			
			usuarioPf.setSenha(senhaCript);			
			usuarioPf = usuarioRepository.save(usuarioPf);
			
			usuarioRepository.insereAcessoUser(usuarioPf.getId());
			
			StringBuilder menssagemHtml = new StringBuilder();
			
			menssagemHtml.append("<b>Segue a baixo seus dados de acesso para a loja virtual </b><br />");
			menssagemHtml.append("<b>Login: </b>"+pessoaFisica.getEmail() +"<br />");
			menssagemHtml.append("<b>Senha: </b>").append(senha).append("<br /><br />");
			menssagemHtml.append("<b>Obrigado! </b>");
			
			//enviando email com o titulo, o conteudo e o e-mail de destino
			//e-mail de destino e o q foi cad na hora de criar a empresa 
			//(PESSOAJURIDICA)
			try {
			serviceSendEmail.enviarEmailHtml(		
					"Acesso Gerado para Loja Virtual",
					menssagemHtml.toString(),
					pessoaFisica.getEmail());
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
			
		
		
		//se tiver usuario para essa PJ dai vamos retornar 
		//a pessoa juridica
		return pessoaFisica;
		
		
	}
	
}
