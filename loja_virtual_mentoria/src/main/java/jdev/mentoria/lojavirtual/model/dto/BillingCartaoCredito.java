package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//classe q ira armazenar as informacoes referente ao email
//e endereco da pessoa q ta pagando por cartao de credito na
//JUNO e sera chaada na classe PAGAMENTOCARTAOCREDITO.JAVA
public class BillingCartaoCredito implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email = "";

	private boolean delayed = false;

	//instanciando uma var/obj de nome ADDRESS do tipo
	//ADDRESSCARTAOCREDITO q tera os dados de endereco de qm ta pagando
	//com o cartao de credito
	private AddressCartaoCredito address = new AddressCartaoCredito();

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isDelayed() {
		return delayed;
	}

	public void setDelayed(boolean delayed) {
		this.delayed = delayed;
	}

	public AddressCartaoCredito getAddress() {
		return address;
	}

	public void setAddress(AddressCartaoCredito address) {
		this.address = address;
	}

}
