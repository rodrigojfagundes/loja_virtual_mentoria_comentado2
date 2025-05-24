package jdev.mentoria.lojavirtual;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import jdev.mentoria.lojavirtual.controller.PessoaController;
import jdev.mentoria.lojavirtual.enums.TipoEndereco;
import jdev.mentoria.lojavirtual.model.Endereco;
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
		pessoaJuridica.setEmail("huhuhoo@gmail.com");
		pessoaJuridica.setTelefone("459977975800");
		pessoaJuridica.setInscEstadual("6565");
		pessoaJuridica.setInscMunicipal("5445");
		pessoaJuridica.setNomeFantasia("73979832");
		pessoaJuridica.setRazaoSocial("64826847");
		
		Endereco endereco1 = new Endereco();
		endereco1.setBairro("Bairro Aqui");
		endereco1.setCep("7777");
		endereco1.setComplemento("Casa");
		//EMPRESA e a PESSOAJURIDICA, q foi cad acima
		endereco1.setEmpresa(pessoaJuridica);
		endereco1.setNumero("28");
		//PESSOA vai ser UMA PESSOAJURIDICA/EMPRESA pq e qm vai receber a COBRANCA
		endereco1.setPessoa(pessoaJuridica);
		endereco1.setRuaLogra("Avenida Brasil");
		endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
		endereco1.setUf("SC");
		endereco1.setCidade("Balneario Camboriu");
		
		
		Endereco endereco2 = new Endereco();
		endereco2.setBairro("Bairro Centro");
		endereco2.setCep("777742");
		endereco2.setComplemento("Apartamento");
		//EMPRESA e a PESSOAJURIDICA, q foi cad acima
		endereco2.setEmpresa(pessoaJuridica);
		endereco2.setNumero("35");
		//PESSOA vai ser UMA PESSOAJURIDICA/EMPRESA pq e qm vai receber
		//uma encomenda por exemplo... Dai se for
		//uma encomenda para uma EMPRESA receber
		endereco2.setPessoa(pessoaJuridica);
		endereco2.setRuaLogra("Avenida Brasil");
		endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
		endereco2.setUf("SC");
		endereco2.setCidade("Tijucas");
		
		
		pessoaJuridica.getEnderecos().add(endereco2);
		pessoaJuridica.getEnderecos().add(endereco1);
		
		pessoaJuridica = pessoaController.salvarPj(pessoaJuridica).getBody();
		
		assertEquals(true, pessoaJuridica.getId() > 0);
		
		for(Endereco endereco : pessoaJuridica.getEnderecos()) {
			assertEquals(true, endereco.getId() > 0);
		}
		
		assertEquals(2, pessoaJuridica.getEnderecos().size());
		
		
		//pessoaJuridica = pessoaUserService
			//	 .salvarPessoaJuridica(pessoaJuridica);
		
	}
	
	
	
}
