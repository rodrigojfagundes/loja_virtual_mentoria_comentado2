package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//a CLASS/ENTITY principal e DataNotificacaoApiJunotPagamento a partir
//dela as outras sao instanciadas... para receber os dados da JUNO
//pelo WEBHOOK
//
//class/entity de nome PAYERNOTIFICACAOAPIJUNOPAGA q sera isntanciada na
//class/entity CHARGENOTIFICACAOPAGAPIJUNO.java...
//
//e q basicamente vai ser usada para guardar os dados q vao vir
//atraves do WEBHOOK
//
//vamos receber esses dados no WEBHOOK (metodo q vai ta rodando na
//loja virtual e q a APIJUNO vai enviar dados para ele...
//dados tipo informando se o pagamento deu certo...)
public class PayerNotificaoApiJunoPaga implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String document;
	private String id;

	//instanciando um obj/var de nome ADDRESS do tipo 
	//ADDRESSPAYERPAGAMENTOJUNO... q ira armaenar alguns dados
	//q vao vim da JUNO...
	//
	//vamos receber esses dados no WEBHOOK (metodo q vai ta rodando na
	//loja virtual e q a APIJUNO vai enviar dados para ele...
	//dados tipo informando se o pagamento deu certo...)
	private AddressPayerPagamentoJuno address = new AddressPayerPagamentoJuno();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AddressPayerPagamentoJuno getAddress() {
		return address;
	}

	public void setAddress(AddressPayerPagamentoJuno address) {
		this.address = address;
	}

}
