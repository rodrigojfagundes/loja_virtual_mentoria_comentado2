package jdev.mentoria.lojavirtual;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import jdev.mentoria.lojavirtual.controller.PessoaController;
import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.PessoaJuridica;
import jdev.mentoria.lojavirtual.repository.PessoaRepository;
import jdev.mentoria.lojavirtual.service.PessoaUserService;
import junit.framework.TestCase;

@Profile("test")
@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class TestePessoaUsuario extends TestCase {
	
	@Autowired
	private PessoaController pessoaController;
	
//	@Autowired
//	private PessoaUserService pessoaUserService;
	
	
	@Test
	public void testCadPessoaFisica() throws ExceptionMentoriaJava {
		
		//antes de cad uma pessoa fisica temos q cad uma pessoa juridica
		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		pessoaJuridica.setCnpj("" + Calendar.getInstance().getTimeInMillis());
		pessoaJuridica.setNome("Alex Fando");
		pessoaJuridica.setEmail("rodrigo@gmail.com");
		pessoaJuridica.setTelefone("459977975800");
		pessoaJuridica.setInscEstadual("6565");
		pessoaJuridica.setInscMunicipal("5445");
		pessoaJuridica.setNomeFantasia("73979832");
		pessoaJuridica.setRazaoSocial("64826847");
		
		pessoaController.salvarPj(pessoaJuridica);
		//pessoaJuridica = pessoaUserService
			//	 .salvarPessoaJuridica(pessoaJuridica);
		
	}
	
	
	
}
