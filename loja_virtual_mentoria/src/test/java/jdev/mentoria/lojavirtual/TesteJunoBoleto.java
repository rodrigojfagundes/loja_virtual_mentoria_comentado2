package lojavirtual;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import jdev.mentoria.lojavirtual.LojaVirtualMentoriaApplication;
import jdev.mentoria.lojavirtual.model.dto.CriarWebHook;
import jdev.mentoria.lojavirtual.model.dto.ObjetoPostCarneJuno;
import junit.framework.TestCase;

@Profile("dev")
@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class TesteJunoBoleto extends TestCase {
	
	@Autowired
	private TesteJunoBoleto serviceJunoBoleto;
	
	//testar passando um cliente para ver se busca
	@Test
	public void testbuscaClientePessoaApiAsaas()  throws Exception{
		
		ObjetoPostCarneJuno dados = new ObjetoPostCarneJuno();
		dados.setEmail("alex.fernando.egidio@gmail.com");
		dados.setPayerName("alex fernando egidio");
		dados.setPayerCpfCnpj("05916564937");
		dados.setPayerPhone("45999795800");
		
		String  customer_id =serviceJunoBoleto.buscaClientePessoaApiAsaas(dados);
		
		assertEquals("cus_000055741916", customer_id);
	}
	
	
	@Test
	public void testcriarChavePixAsaas() throws Exception {
		String chaveApi = serviceJunoBoleto.testcriarChavePixAsaas();;
		
		System.out.println("Chave Asaas API" + chaveApi);
		
	}
	
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
