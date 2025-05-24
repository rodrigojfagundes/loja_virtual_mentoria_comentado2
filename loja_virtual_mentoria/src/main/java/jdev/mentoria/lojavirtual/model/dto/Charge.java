package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


//criando a class/entity do tipo DTO de nome CHARGE q tera varios
//atributos/variaveis q sera utilizado para
//gerar o boleto/pix... Pois o CHARGE vai ser uma class/entity do tipo DTO
//q sera convertido para JSON e enviado junto com outros OBJ/class/entity
//para a API da JUNO para pd gerar um boleto/pix...
//
//o prof nao explicou exatamente para q serve cada atributo/var...
public class Charge implements Serializable {

	private static final long serialVersionUID = 1L;

	private String pixKey;
	private boolean pixIncludeImage = true;
	private String description;
	private List<String> references = new ArrayList<String>();
	//valor total
	private Float amount;
	private String dueDate;
	private Integer installments;
	//max dias para pagar
	private Integer maxOverdueDays;
	//multa por atraso
	private BigDecimal fine;
	//juros
	private BigDecimal interest;
	//lista do tipos de pagamento
	private List<String> paymentTypes = new ArrayList<String>();

	public String getPixKey() {
		return pixKey;
	}

	public void setPixKey(String pixKey) {
		this.pixKey = pixKey;
	}

	public boolean isPixIncludeImage() {
		return pixIncludeImage;
	}

	public void setPixIncludeImage(boolean pixIncludeImage) {
		this.pixIncludeImage = pixIncludeImage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getReferences() {
		return references;
	}

	public void setReferences(List<String> references) {
		this.references = references;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getInstallments() {
		return installments;
	}

	public void setInstallments(Integer installments) {
		this.installments = installments;
	}

	public Integer getMaxOverdueDays() {
		return maxOverdueDays;
	}

	public void setMaxOverdueDays(Integer maxOverdueDays) {
		this.maxOverdueDays = maxOverdueDays;
	}

	public BigDecimal getFine() {
		return fine;
	}

	public void setFine(BigDecimal fine) {
		this.fine = fine;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public List<String> getPaymentTypes() {
		return paymentTypes;
	}

	public void setPaymentTypes(List<String> paymentTypes) {
		this.paymentTypes = paymentTypes;
	}

}
