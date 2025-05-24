package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//a CLASS/ENTITY principal e DataNotificacaoApiJunotPagamento a partir
//dela as outras sao instanciadas... para receber os dados da JUNO
//pelo WEBHOOK
//
//criado class/entity de nome CHARGENOTIFICACAOPAGAPIJUNO
//q sera instanciada no ATTRIBUTESNOTIFICACAOPAGAJUNO.java
//e q basicamente essa class/entity aqui tera atributos/var
//q vao ser passados em formato JSON pela JUNO...
//
//vamos receber esses dados no WEBHOOK (metodo q vai ta rodando na
//loja virtual e q a APIJUNO vai enviar dados para ele...
//dados tipo informando se o pagamento deu certo...)
public class ChargeNotificaoPagApiJuno implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String code;
	private String amount;
	private String status;
	private String dueDate;
	
	//criando um atributo/var de nome PAYER do tipo
	//PAYERNOTIFICACAOAPIJUNOPAGA q vai receber os dados
	//q veem do JSON q vem da JUNO
	//atraves do WEBHOOK
	//
	//vamos receber esses dados no WEBHOOK (metodo q vai ta rodando na
	//loja virtual e q a APIJUNO vai enviar dados para ele...
	//dados tipo informando se o pagamento deu certo...)
	private PayerNotificaoApiJunoPaga payer = new PayerNotificaoApiJunoPaga();
	
	public void setPayer(PayerNotificaoApiJunoPaga payer) {
		this.payer = payer;
	}
	
	public PayerNotificaoApiJunoPaga getPayer() {
		return payer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

}
