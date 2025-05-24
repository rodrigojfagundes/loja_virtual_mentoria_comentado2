package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//a CLASS/ENTITY principal e DataNotificacaoApiJunotPagamento a partir
//dela as outras sao instanciadas... para receber os dados da JUNO
//pelo WEBHOOK
//
//class/entity de nome ADDRESSPAYERPAGAMENTOJUNO q tem atributos/var
//relacionadas ao endereco do pagante... e q tera seus
//dados preenchidos atraves
//do JSON q a JUNO nos enviar para o nosso WEBHOOK
//
//vamos receber esses dados no WEBHOOK (metodo q vai ta rodando na
//loja virtual e q a APIJUNO vai enviar dados para ele...
//dados tipo informando se o pagamento deu certo...)
public class AddressPayerPagamentoJuno implements Serializable {

	private static final long serialVersionUID = 1L;

	private String street;
	private String number;
	private String complement;
	private String city;
	private String state;
	private String postCode;
	private String neighborhood;

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

}
