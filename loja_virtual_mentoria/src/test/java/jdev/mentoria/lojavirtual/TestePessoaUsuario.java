package jdev.mentoria.lojavirtual;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.PessoaJuridica;
import jdev.mentoria.lojavirtual.repository.PessoaRepository;
import jdev.mentoria.lojavirtual.service.PessoaUserService;
import junit.framework.TestCase;

@Profile("test")
@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class TestePessoaUsuario extends TestCase {
	
	@Autowired
	private PessoaUserService pessoaUserService;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	
	@Test
	public void testCadPessoaFisica() {
		
		//antes de cad uma pessoa fisica temos q cad uma pessoa juridica
		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		pessoaJuridica.setCnpj("865545598956556");
		pessoaJuridica.setNome("Alex Fernando");
		pessoaJuridica.setEmail("alex.fernando.egidio@gmail.com");
		pessoaJuridica.setTelefone("45999795800");
		pessoaJuridica.setInscEstadual("65565");
		pessoaJuridica.setInscMunicipal("54445");
		pessoaJuridica.setNomeFantasia("739279832");
		pessoaJuridica.setRazaoSocial("648236847");
		
		pessoaRepository.save(pessoaJuridica);
		
		
		/*
		PessoaFisica pessoaFisica = new PessoaFisica();
		pessoaFisica.setCpf("0597975788");
		pessoaFisica.setNome("Alex Fernando");
		pessoaFisica.setEmail("alex.fernando.egidio@gmail.com");
		pessoaFisica.setTelefone("45999795800");
		pessoaFisica.setEmpresa(pessoaFisica);
		*/
	}
	
	
	
}
