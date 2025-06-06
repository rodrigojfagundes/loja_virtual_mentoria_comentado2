package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//class/entidade que ira armazenar os atributos/var 
//(NAME, PHONE, CITY, ETC...) a classe FROMENVIOETIQUETADTO vai ser
//chamada no ENVIOETIQUETADTO para os dados(var/atributos) serem enviados
//e recebidos para na API MELHORENVIO para poder calcular o frete...
//O envio e recebimento das informacoes e atraves de JSON
public class FromEnvioEtiquetaDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	//atributos q a API MELHOENVIO pede para parte de ORIGEM do
	//pacote/produto para poder calcular o frete
	private String name;
	private String phone;
	private String email;
	private String document;
	private String company_document;
	private String state_register;
	private String address;
	private String complement;
	private String number;
	private String district;
	private String city;
	private String country_id;
	private String postal_code;
	private String note;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getCompany_document() {
		return company_document;
	}

	public void setCompany_document(String company_document) {
		this.company_document = company_document;
	}

	public String getState_register() {
		return state_register;
	}

	public void setState_register(String state_register) {
		this.state_register = state_register;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry_id() {
		return country_id;
	}

	public void setCountry_id(String country_id) {
		this.country_id = country_id;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
