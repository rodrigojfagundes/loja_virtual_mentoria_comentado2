package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;


//essa class/entity de nome BILLING vai ter os atributos/var
//preenchidos e sera instanciada no COBRANCAJUNOAPI.JAVA
//e vamos enviar em JSON para a API da JUNO...
//para pd gerar o boleto/pix...
//
//pelo o q eu entendi o BILLING e os dados do COMPRADOR
public class Billing implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String document;
	private String email;

	private String phone;

	//meio q se tiver true a JUNO envia o boleto para o e-mail da pessoa
	//q comprou
	private boolean notify = true;

	public String getName() {
		return name;
	}

	public void setName(String nome) {
		this.name = nome;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isNotify() {
		return notify;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}

}
