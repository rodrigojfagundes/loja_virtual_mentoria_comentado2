package jdev.mentoria.lojavirtual.controller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.model.BoletoJuno;
import jdev.mentoria.lojavirtual.model.dto.AttibutesNotificaoPagaApiJuno;
import jdev.mentoria.lojavirtual.model.dto.DataNotificacaoApiJunotPagamento;
import jdev.mentoria.lojavirtual.model.dto.NotificacaoPagamentoApiAsaas;
import jdev.mentoria.lojavirtual.repository.BoletoJunoRepository;


//criando o CONTROLLER RECEBEPAGAMENTOWEBHOOKAPIJUNO...
//q basicamente é um METODO/ENDPOINT q vai RECEBER informacoes
//da JUNO(ASAAS), informacoes tipo SE O PAGAMENTO DO CLIENTE DA LOJAVIRTUAL
//deu CERTO... 
@Controller
@RequestMapping(value = "/requisicaojunoboleto")
public class RecebePagamentoWebHookApiJuno implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//instanciando um obj de nome BOLETOJUNOREPOSITORY do tipo 
	//BOLETOJUNOREPOSITORY
	@Autowired
	private BoletoJunoRepository boletoJunoRepository; 
	

	
	//
	//criando o metodo RECEBENOTIFICACAOPAGAMENTOAPIASAAS q ficara
	//disponivel pela url /NOTIFICACAOAPIASAAS em q a asaas vai
	//enviar um OBJ do tipo NOTIFICACAOPAGAMENTOAPIASAAS
	//para nos informando q uma determinada
	//VENDACOMPRALOJAVIRTUAL teve o pagamento feito...
	//
	@ResponseBody
	@RequestMapping(value = "/notificacaoapiasaas", consumes = {"application/json;charset=UTF-8"},
	headers = "Content-Type=application/json;charset=UTF-8", method = RequestMethod.POST)
	public ResponseEntity<String> recebeNotificacaoPagamentoApiAsaas(@RequestBody NotificacaoPagamentoApiAsaas notificacaoPagamentoApiAsaas) {
		
		//criando um BOLETOJUNO do tipo BOLETOJUNO (q sera usado pela ASAAS)
		//q ira receber o ID da fatura q foi paga
		BoletoJuno boletoJuno = boletoJunoRepository.findByCode(notificacaoPagamentoApiAsaas.idFatura());
		
		//verificando se existe o boleto
		if (boletoJuno == null) {
			return new ResponseEntity<String>("Boleto/Fatura não encontrada no banco de dados", HttpStatus.OK);
		}
		
		//se o boleto existir... vamos alterar o status dele para quitado...
		//ou seja boleto pago... e salvando no banco de dados da
		//lojavirtualmentoria
		if (boletoJuno != null 
				&& notificacaoPagamentoApiAsaas.boletoPixFaturaPaga()
				&& !boletoJuno.isQuitado()) {
			 
			 boletoJunoRepository.quitarBoletoById(boletoJuno.getId());
			 System.out.println("Boleto: " + boletoJuno.getCode() + " foi quitado ");
			 /**Fazendo qualquer regra de negocio que vc queira*/
			 
			 return new ResponseEntity<String>("Recebido do Asaas, boleto id: " + boletoJuno.getId(),HttpStatus.OK);
		}else {
			System.out.println("Fatura :"  
		          + notificacaoPagamentoApiAsaas.idFatura() 
		          + " não foi processada, quitada: " 
		          + notificacaoPagamentoApiAsaas.boletoPixFaturaPaga() 
		          + " valor quitado : "+ boletoJuno.isQuitado());
		}
		
		return new ResponseEntity<String>("Não foi processado a fatura : " + notificacaoPagamentoApiAsaas.idFatura(), HttpStatus.OK);
	}
	
	
	
	
	
	
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
