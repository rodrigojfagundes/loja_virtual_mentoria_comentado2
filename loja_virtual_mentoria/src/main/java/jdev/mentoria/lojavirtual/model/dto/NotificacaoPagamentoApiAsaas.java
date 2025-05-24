package jdev.mentoria.lojavirtual.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//
//class NOTIFICACAOPAGAMENTOAPIASAAS... nos vamos usar ela para instanciar
//uma var/obj em RECEBEPAGAMENTOWEBHOOKAPIJUNO.JAVA... Para quando
//a API ASAAS se conectar ao WEBHOOK/METODO/ENDPOINT 
//RECEBENOTIFICACAOPAGAMENTOAPIASAAS q esta no RECEBEPAGAMENTOWEBHOOKAPIJUNO.JAVA
//a ASAAS vai enviar um OBJ em JSON q ficara salvo na class
//NOTIFICACAOPAGAMENTOAPIASAAS a baixo...
//para notificarmos q alguem efetuou uma compra na lojavirtualmentoria
//
//annotation @JSONIGNOREPROPERTIES e pq se caso a ASAAS envie um obj
//em json faltando preencher alguns atributos nao da erro pq ta faltando
//atributos... entao para nao da erro usamos a annotation @JSONIGNOREPROPERTIES
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificacaoPagamentoApiAsaas {

	private String event;

	//instanciando um obj/var payment do tipo payment
	private Payment payment = new Payment();

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	public String idFatura() {
		return payment.getId();
	}
	
	//verificando o status do payment/pagamento
	public String statusPagamento() {
		return getPayment().getStatus();
	}
	
	//se caso no STATUSPAGAMENTO tiver CONFIRMED ou RECEIVED entao
	//e pq foi pago
	public Boolean boletoPixFaturaPaga() {
		return statusPagamento().equalsIgnoreCase("CONFIRMED") || statusPagamento().equalsIgnoreCase("RECEIVED");
	}

}