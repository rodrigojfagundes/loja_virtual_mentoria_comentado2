package jdev.mentoria.lojavirtual.controller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.model.BoletoJuno;
import jdev.mentoria.lojavirtual.model.dto.AttibutesNotificaoPagaApiJuno;
import jdev.mentoria.lojavirtual.model.dto.DataNotificacaoApiJunotPagamento;
import jdev.mentoria.lojavirtual.repository.BoletoJunoRepository;


//criando o CONTROLLER RECEBEPAGAMENTOWEBHOOKAPIJUNO...
//q basicamente é um METODO/ENDPOINT q vai RECEBER informacoes
//da JUNO, informacoes tipo SE O PAGAMENTO DO CLIENTE DA LOJAVIRTUAL
//deu CERTO...
@Controller
@RequestMapping(value = "/requisicaojunoboleto")
public class RecebePagamentoWebHookApiJuno implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//instanciando um obj de nome BOLETOJUNOREPOSITORY do tipo 
	//BOLETOJUNOREPOSITORY
	@Autowired
	private BoletoJunoRepository boletoJunoRepository; 
	
	
	//metodo de nome RECEBENOTIFICACAOPAGAMENTOJUNOAPIV2
	//q recebera da JUNO um JSON q tera os seus campos salvo nos
	//atributos/var do tipo DATANOTIFICACAOAPIJUNOTPAGAMENTO
	//de nome dataNotificacaoApiJunotPagamento
	@ResponseBody
	@RequestMapping(value = "/notificacaoapiv2", consumes = {"application/json;charset=UTF-8"},
	headers = "Content-Type=application/json;charset=UTF-8", method = RequestMethod.POST)
	private HttpStatus recebeNotificaopagamentojunoapiv2(@RequestBody DataNotificacaoApiJunotPagamento dataNotificacaoApiJunotPagamento) {
		
		//pecorrendo a lista de pagamento recebidos
		for (AttibutesNotificaoPagaApiJuno data : dataNotificacaoApiJunotPagamento.getData()) {
			
			//codigo do pix(chave) e o codigo de barra do boleto
			//vao ficar salvos na string CODIGOBOLETOPIX
			 String codigoBoletoPix = data.getAttributes().getCharge().getCode();
			 
			 //status se foi pago ou nao
			 String status = data.getAttributes().getStatus();
			 
			 //boolean para ver se foi pagou (CONFIRMED) ou nao...
			 boolean boletoPago = status.equalsIgnoreCase("CONFIRMED") ? true : false;
			 
			 //buscando o codigo do boleto
			 BoletoJuno boletoJuno = boletoJunoRepository.findByCode(codigoBoletoPix);
			 
			 //verificando se o boleto foi pago... (esta com o STATUS de pago)
			 //atraves da confirmacao do webhook...
			 //tipo a juno colocou no atributo QUITADO com true...
			 //entao quando for ver aqui o valor do var/atributo QUITADO for true
			 //e pq o boleto/pix foi pago...
			 if (boletoJuno != null && !boletoJuno.isQuitado() && boletoPago) {
				 
				 //se na nosso BANCODEDADOS ta o quitado como false... 
				 //e a JUNO disse para nos q foi pago o PIX/BOLETO
				 //vamos chamar o metodo QUITARBOLETOBYID e passar
				 //o ID do boleto q a JUNO disse q foi pago...
				 boletoJunoRepository.quitarBoletoById(boletoJuno.getId());
				 System.out.println("Boleto: " + boletoJuno.getCode() + " foi quitado ");
				 
			 }
		}
		
		return HttpStatus.OK;
	}
	
	

}
