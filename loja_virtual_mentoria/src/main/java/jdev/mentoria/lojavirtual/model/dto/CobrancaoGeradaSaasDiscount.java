package jdev.mentoria.lojavirtual.model.dto;



//vai ficar instanciado dentro do COBRANCAGERADAASSASDATA
//e basicamente quando der um get para pegar as cobrancas
//q tem para cada cliente, essa classe vai mostrar os
//disconto q o cliente teve em cada compra...
public class CobrancaoGeradaSaasDiscount {

	private Double value;
	private String limitDate;
	private Integer dueDateLimitDays;
	private String type;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getLimitDate() {
		return limitDate;
	}

	public void setLimitDate(String limitDate) {
		this.limitDate = limitDate;
	}

	public Integer getDueDateLimitDays() {
		return dueDateLimitDays;
	}

	public void setDueDateLimitDays(Integer dueDateLimitDays) {
		this.dueDateLimitDays = dueDateLimitDays;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}