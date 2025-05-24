package jdev.mentoria.lojavirtual.model.dto;


//class sera instanciada no COBRANCAGERADAASSASDATA.JAVA
//quando nos pegarmos as cobranca dos cliente
//aqui vai ostrar o interest ou seja o juros das cobrancas
//parceladas...
public class CobrancaoGeradaSaasInterest {

	private Double value;
	private String type;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}