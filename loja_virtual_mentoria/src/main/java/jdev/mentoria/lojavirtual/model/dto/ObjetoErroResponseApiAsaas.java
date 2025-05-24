package jdev.mentoria.lojavirtual.model.dto;

//class/entity q tera os erros quando for enviado um json 
//de pagamento por cartao para a api da asaas no PAGAMENTOCONTROLLER.JAVA
//
public class ObjetoErroResponseApiAsaas {

	private String code;
	private String description;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}