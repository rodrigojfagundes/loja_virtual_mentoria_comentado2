package jdev.mentoria.lojavirtual.model.dto;


//class/entity CLIENTASAASAPIPAGAMENTO para
//caso nao exista um CLIENTE na ASAAS com as INFORMACOES da
//pessoa q esta comprando o PRODUTO na LOJAVIRTUAL
//esse CLIENTE SERA CRIADO na ASAAS... E assim a ASAAS vai conseguir
//associar uma cobranca a um cliente
public class ClienteAsaasApiPagamento {
	
	private String name;
	private String email;
	private String cpfCnpj;
	private String phone;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCpfCnpj() {
		return cpfCnpj;
	}
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	
	
}
