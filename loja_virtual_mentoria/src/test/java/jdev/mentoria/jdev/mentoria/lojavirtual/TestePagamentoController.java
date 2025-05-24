package jdev.mentoria.lojavirtual;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jdev.mentoria.lojavirtual.controller.PagamentoController;
import jdev.mentoria.lojavirtual.controller.RecebePagamentoWebHookApiJuno;
import junit.framework.TestCase;

//class para testarmos o PAGAMENTOCONTROLLER.JAVA 
//e RECEBEPAGAMENTOWEBHOOKAPIJUNO.JAVA para testar a integracao com
//a API do ASAAS
//
@Profile("test")
@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class TestePagamentoController extends TestCase {
	
	//instanciando um obj/var PAGAMENTOCONTROLLER e usando a
	//ANNOTATION @AUTOWIRED para fazermos INJECAO DE DEPENDENCIAS...
	//
	@Autowired
	private PagamentoController pagamentoController;
	
	//criando um obj/var do tipo RECEBEPAGAMENTOWEBHOOKAPIJUNO de nome
	//RECEBEPAGAMENTOWEBHOOKAPIJUNO... e fazendo injecao de depedencias
	@Autowired
	private RecebePagamentoWebHookApiJuno recebePagamentoWebHookApiJuno;
	
	//criando um obj/var do tipo WEBAPPLICATIONCONTEXT de nome WAC
	//para carregar o contexto da aplicacao para fazer o teste...
	@Autowired
	private WebApplicationContext wac;
	
	
	//criandoo metodo de teste TESTRECEBENOTIFICACAOPAGAMENTOAPIASAAS
	//para testar o
	// WEBHOOK q esta no RECEBEPAGAMENTOWEBHOOKAPIJUNO
	//no metodo RECEBENOTIFICACAOAPIASAAS
	//
	@Test
	public void testRecebeNotificacaoPagamentoApiAsaas() throws Exception {
		
		//criando um obj do tipo DEFAULTMOCKMVCBUILDER de nome BUILDER
		//para podermos MOCKAR ou seja SIMULAR
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(wac);
		MockMvc mockMvc = builder.build();
		
		//informando o arquivo q tem o JSON para enviarmos para
		//fazermos o teste... Vamos enviar como se esse SON
		//tivesse vindo pela asaas
		String json = new String(Files.readAllBytes(Paths.get("C:\\temp\\java\\loja_virtual_mentoria\\src\\test\\java\\jdev\\mentoria\\jdev\\mentoria\\lojavirtual\\jsonwebhookasaas.txt")));
		
		//usando o metodo PERFORM do MOCKMVC para simular uma requisicao ao
		//WEBHOOK/metodo/endpoint NOTIFICACAOAPIASAAS q esta no REQUISICAOJUNOBOLETO
		//e passando o JSON q pegamos do arquivo de texto e ta na var/obj
		//do TIPO STRING de nome JSON... e passando os cabecalhos
		//formato json e utf-8, etc...
		ResultActions retornoApi = mockMvc.perform(MockMvcRequestBuilders.post("/requisicaojunoboleto/notificacaoapiasaas")
				.content(json)
				.accept("application/json;charset=UTF-8")
				.contentType("application/json;charset=UTF-8"));
		
	 System.out.println(retornoApi.andReturn().getRequest().getContentAsString());
		
		
	}
	
	
	
	
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
