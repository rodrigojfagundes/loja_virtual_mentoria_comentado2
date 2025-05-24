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
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
//	@Autowired
//	private PessoaUserService pessoaUserService;
	
	
	
	@Test
	public void testCadPessoaJuridica() throws ExceptionMentoriaJava {
		
		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		//pessoaJuridica.setCnpj("" + Calendar.getInstance().getTimeInMillis());
		pessoaJuridica.setCnpj("93.918.688/0001-74");
		pessoaJuridica.setNome("Alex Fando");
		pessoaJuridica.setEmail("lexo@gmail.com");
		pessoaJuridica.setTelefone("479775800");
		pessoaJuridica.setInscEstadual("64324732565");
		pessoaJuridica.setInscMunicipal("54447324325");
		pessoaJuridica.setNomeFantasia("739797834234322");
		pessoaJuridica.setRazaoSocial("648268744324327");
		
		Endereco endereco1 = new Endereco();
		endereco1.setBairro("Bairro Aqui");
		endereco1.setCep("7777");
		endereco1.setComplemento("Casa");
		//como o BANCO foi DIVIDIDO por EMPRESAS, cada EMPRESA(LOJA) tem seu ID
		//com suas COMPRAS, VENDAS, PESSOAS, PRODUTOS, etc... 
		//dai estamos dizendo q esse ENDERECO 1 esta salvo 
		//na empresa do ID q ta no PESSOAJURIDICA
		endereco1.setEmpresa(pessoaJuridica);
		endereco1.setNumero("28");
		//aqui estamos associando esse ENDERECO1 ao ID de um PESSOA
		//q no caso e o ID q ta em PESSOAJURIDICA
		endereco1.setPessoa(pessoaJuridica);
		endereco1.setRuaLogra("Avenida Brasil");
		endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
		endereco1.setUf("SC");
		endereco1.setCidade("Balneario Camboriu");
		
		
		Endereco endereco2 = new Endereco();
		endereco2.setBairro("Bairro Centro");
		endereco2.setCep("777742");
		endereco2.setComplemento("Apartamento");
		//como o BANCO foi DIVIDIDO por EMPRESAS, cada EMPRESA(LOJA) tem seu ID
		//com suas COMPRAS, VENDAS, PESSOAS, PRODUTOS, etc... 
		//dai estamos dizendo q esse ENDERECO 2 esta salvo 
		//na empresa do ID q ta no PESSOAJURIDICA
		endereco2.setEmpresa(pessoaJuridica);
		endereco2.setNumero("35");
		//aqui estamos associando esse ENDERECO2 ao ID de um PESSOA
		//q no caso e o ID q ta em PESSOAJURIDICA
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
	
	
	
	//Teste de CADASTRO de PESSOAFISICA
	@Test
	public void testCadPessoaFisica() throws ExceptionMentoriaJava {
		
		//para CAD uma PESSOAFISICA precisamos dizer a
		//qual PESSOAJURIDICA/EMPRESA ela pertence isso pq o banco foi 
		//dividido por EMPRESAS/PJ... Tipo cada EMPRESA/PJ tem os seus
		//PRODUTOS, CATEGORIAS, VENDAS, etc... Dai dessa for quando CAD
		//uma PESSOAFISICA tem q informar a qual PJ/EMPRESA ela pertence
		//
		PessoaJuridica pessoaJuridica = pessoaRepository
				.existeCnpjCadastrado("1732651750286");
		
		PessoaFisica pessoaFisica = new PessoaFisica();
		pessoaFisica.setCpf("873.894.380-88");
		pessoaFisica.setNome("Alex Fando");
		pessoaFisica.setEmail("rodrigo27@gmail.com");
		pessoaFisica.setTelefone("459977975800");
		pessoaFisica.setEmpresa(pessoaJuridica);
		
		Endereco endereco1 = new Endereco();
		endereco1.setBairro("Bairro Aqui");
		endereco1.setCep("7777");
		endereco1.setComplemento("Casa");
		endereco1.setNumero("28");
		//informando a QUALPESSOA esse ENDERECO PARTENCE
		endereco1.setPessoa(pessoaFisica);
		endereco1.setRuaLogra("Avenida Brasil");
		endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
		endereco1.setUf("SC");
		endereco1.setCidade("Balneario Camboriu");
		//como o BANCO foi DIVIDIDO por EMPRESAS, cada empresa(LOJA) tem seu ID
		//com suas COMPRAS, VENDAS, PESSOAS, etc... dai estamos dizendo q
		//esse ENDERECO 1 esta salvo na empresa do ID q ta no PESSOAJURIDICA
		endereco1.setEmpresa(pessoaJuridica);
		
		
		Endereco endereco2 = new Endereco();
		endereco2.setBairro("Bairro Centro");
		endereco2.setCep("777742");
		endereco2.setComplemento("Apartamento");
		endereco2.setNumero("35");
		//INFORMANDO A QUEM (QUAL ID) da PESSOA Q A DONA DESSE ENDERECO
		endereco2.setPessoa(pessoaFisica);
		endereco2.setRuaLogra("Avenida Brasil");
		endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
		endereco2.setUf("SC");
		endereco2.setCidade("Tijucas");
		//como o BANCO foi DIVIDIDO por EMPRESAS, cada empresa(LOJA) tem seu ID
		//com suas COMPRAS, VENDAS, PESSOAS, etc... dai estamos dizendo q
		//esse ENDERECO 2 esta salvo na empresa do ID q ta no PESSOAJURIDICA
		endereco2.setEmpresa(pessoaJuridica);
		
		
		pessoaFisica.getEnderecos().add(endereco2);
		pessoaFisica.getEnderecos().add(endereco1);
		
		pessoaFisica = pessoaController.salvarPf(pessoaFisica).getBody();
		
		assertEquals(true, pessoaFisica.getId() > 0);
		
		for(Endereco endereco : pessoaFisica.getEnderecos()) {
			assertEquals(true, endereco.getId() > 0);
		}
		
		assertEquals(2, pessoaFisica.getEnderecos().size());
		
		
		//pessoaJuridica = pessoaUserService
			//	 .salvarPessoaJuridica(pessoaJuridica);
		
	}
	
	
	
}
