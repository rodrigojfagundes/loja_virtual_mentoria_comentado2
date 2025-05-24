package jdev.mentoria.lojavirtual;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import jdev.mentoria.lojavirtual.model.dto.CriarWebHook;
import jdev.mentoria.lojavirtual.model.dto.ObjetoPostCarneJuno;
import jdev.mentoria.lojavirtual.service.ServiceJunoBoleto;
import junit.framework.TestCase;

@Profile("dev")
@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class TesteJunoBoleto extends TestCase {
	
	@Autowired
	private ServiceJunoBoleto serviceJunoBoleto;
	
	
	////
	//do CLIENTE q comprou a VENDACOMPRALOJAVIRTUAL de ID 27...
	//q no caso foi o NOMECLIENTE9@GMAIL.COM, NOME CLIENTE 9, etc...
	@Test
	public void testgerarCarneApiAsaas() throws Exception {
		
		ObjetoPostCarneJuno dados = new ObjetoPostCarneJuno();
		dados.setEmail("nomecliente9@gmail.com");
		dados.setPayerName("NOME CLIENTE 9");
		dados.setPayerCpfCnpj("68409959097");
		dados.setPayerPhone("45995801289");
		dados.setIdVenda(27L);
		
		String retorno = serviceJunoBoleto.gerarCarneApiAsaas(dados);
		
		System.out.println(retorno);
	}
	
	////
	@Test
	public void testbuscaClientePessoaApiAsaas()  throws Exception{
		
		ObjetoPostCarneJuno dados = new ObjetoPostCarneJuno();
		dados.setEmail("alex.fernando.egidio@gmail.com");
		dados.setPayerName("alex fernando egidio");
		dados.setPayerCpfCnpj("05916564937");
		dados.setPayerPhone("45999795800");
		
		String  customer_id =serviceJunoBoleto.buscaClientePessoaApiAsaas(dados);
	
		System.out.println(customer_id);
		
		assertEquals("cus_000006634810", customer_id);
	}
	
	
	//este funciona mas nao pelo teste, mas sim pelo arquivo
	//ASAASCONTROLLER.JAVA
	@Test
	public void testcriarChavePixAsaas() throws Exception {
		
		String chaveAPi = serviceJunoBoleto.criarChavePixAsaas();
		
		System.out.println("Chave Asass API" + chaveAPi);
	}
	
	
	
	
	//DAQUI PARA BAIXO E DA JUNO... PD IGNORAR...
	@Test
	public void deleteWebHook() throws Exception {
		
		serviceJunoBoleto.deleteWebHook("wbh_E71095B5BF65E8D2DB018EE8A89BACB8");
		
	}
	

	
	@Test
	public void listaWebHook() throws Exception {
		
		String retorno = serviceJunoBoleto.listaWebHook();
		
		System.out.println(retorno);
	}
	
	@Test
	public void testeCriarWebHook() throws Exception {
		
		CriarWebHook criarWebHook = new CriarWebHook();
		criarWebHook.setUrl("https://api2.lojavirtualjdev.com.br/loja_virtual_mentoria/requisicaojunoboleto/notificacaoapiv2");
		criarWebHook.getEventTypes().add("BILL_PAYMENT_STATUS_CHANGED");
		criarWebHook.getEventTypes().add("PAYMENT_NOTIFICATION");
		
		String retorno = serviceJunoBoleto.criarWebHook(criarWebHook);
		
		System.out.println(retorno);
	}
	

}