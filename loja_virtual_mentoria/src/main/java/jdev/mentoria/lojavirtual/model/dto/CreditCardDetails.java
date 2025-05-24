package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//class q sera instanciada no PAGAMENTOCARTAOCREDITO.JAVA
//e q basicamente tera armazenado os HASH dos cartoes
//e pq pelo o q eu entendi e criptografado toda as informacoes
//do cartao de credito dai elas viram um hash... +ou- isso
public class CreditCardDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private String creditCardId = "";
	private String creditCardHash = "";

	public String getCreditCardId() {
		return creditCardId;
	}

	public void setCreditCardId(String creditCardId) {
		this.creditCardId = creditCardId;
	}

	public String getCreditCardHash() {
		return creditCardHash;
	}

	public void setCreditCardHash(String creditCardHash) {
		this.creditCardHash = creditCardHash;
	}

}
