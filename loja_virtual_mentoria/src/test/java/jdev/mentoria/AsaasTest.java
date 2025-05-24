import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jdev.mentoria.lojavirtual.service.ServiceJunoBoleto;

class AsaasTest {

	@Autowired
	private ServiceJunoBoleto serviceJunoBoleto;
	
	@Test
	public void testcriarChavePixAsaas() throws Exception {
		String chaveApi = serviceJunoBoleto.criarChavePixAsaas();
		
		System.out.println("Chave Asaas API" + chaveApi);
		
	
	

}
