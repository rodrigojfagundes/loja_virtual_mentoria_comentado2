package jdev.mentoria.lojavirtual.model.dto;


//essa classe RESPOSTAPAGAMENTOCREDITCARD sera instanciada no
//COBRANCAGERADACARTAOCREDITOASAAS
//
//e basicamente vai armazenar o retorno em JSON q a API
//da ASAAS vai nos passar apos um cliente
//fazer uma compra no cartao e essa compra ficando ok...
//vai retornar o numero do cartao, a bandeira do cartao e um token...
public class RespostaPagamentoCreditCard {
	
	private String creditCardNumber;
	private String creditCardBrand;
	private String creditCardToken;

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public String getCreditCardBrand() {
		return creditCardBrand;
	}

	public void setCreditCardBrand(String creditCardBrand) {
		this.creditCardBrand = creditCardBrand;
	}

	public String getCreditCardToken() {
		return creditCardToken;
	}

	public void setCreditCardToken(String creditCardToken) {
		this.creditCardToken = creditCardToken;
	}

}