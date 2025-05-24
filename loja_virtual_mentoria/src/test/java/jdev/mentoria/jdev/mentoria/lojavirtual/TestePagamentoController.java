package jdev.mentoria.lojavirtual;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import jdev.mentoria.lojavirtual.controller.PagamentoController;
import junit.framework.TestCase;

//class para testarmos o PAGAMENTOCONTROLLER.JAVA
//
@Profile("test")
@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class TestePagamentoController extends TestCase {
	
	//instanciando um obj/var PAGAMENTOCONTROLLER e usando a
	//ANNOTATION @AUTOWIRED para fazermos INJECAO DE DEPENDENCIAS...
	//
	@Autowired
	private PagamentoController pagamentoController;
	
	
	//testando o metodo/endpoint FINALIZARCOMPRACARTAOASAAS
	//passando os dados para o metodo, o NUMERO DE CARTAO,
	//NOME DO DONO DO CARTAO, CVV DO CARTAO, MES DE EXPIRACAO DO CARTAO,
	//ANO DE EXPIRACAO DO CARTAO, ID DA VENDACOMPRALOJAVIRTUAL, 
	//CPF DE QM TA COMPRANDO, QUANTIDADE DE PARCELAS, CEP DE QM TA COMPRANDO,
	// RUA DE QM TA COMPRANDO, NUMERO DE QM TA COMPRANDO, ESTADO DE QM TA COMPRANDO
	//CIDADE DE QM TA COMPRANDO... OBS NO CASO DO NOME CLIENTE 9
	//ELE e o cliente ID 137... o endereco de cobranca a RUA e UMA
	//e o ENDERECO DE ENTREGA e outra rua... mas o restante e o mesmo...
	//no teste a baixo coloquei o endereco de cobranca... 
	//COBRANCA = Rua Barbosa OK
	//ENTREGA Rua Rui Barbosa OK
	//
	//AHH TALVEZ O CEP TENHA Q TER o - "TRACINHO" ou talvez nao...
	@Test
	public void testfinalizarCompraCartaoAsaas() throws Exception {
		pagamentoController.finalizarCompraCartaoAsaas("5126462892278565", "NOME CLIENTE 9",
													  "612", "06",
													  "2025", 27L, "68409959097",
													  2, "75830112", "Rua Barbosa OK",
													  "62", "PR", "Curitiba");
	}

}
	
//}
