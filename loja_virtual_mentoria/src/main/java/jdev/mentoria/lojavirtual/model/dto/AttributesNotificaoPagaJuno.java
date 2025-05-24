package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//a CLASS/ENTITY principal e DataNotificacaoApiJunotPagamento a partir
//dela as outras sao instanciadas... para receber os dados da JUNO
//pelo WEBHOOK
//
//class/entity ATTRIBUTESNOTIFICACAOPAGAJUNO q sera instanciada
//no ATTIBUTESNOTIFICACAOPAGAPIJUNO (nome parecido o prof escreveu
//com erro de portugues...)
//
//basicamente vamos criar um OBJ apartir dessa class/entity no
//ATTIBUTESNOTIFICACAOPAGAPIJUNO de nome ATTRIBUTES
//q vai armazenar as informacoes q vao vim da JUNO API
//quando a JUNO enviar para nos um JSON atraves do nosso WEBHOOK
//q e um METODO q tem na LOJAVIRTUAL para a JUNO enviar um JSON
//avisando se o pagamento feito pelo cliente deu certo... +ou- isso...
public class AttributesNotificaoPagaJuno implements Serializable {

	private static final long serialVersionUID = 1L;

	//esses atributos/var serao preenchido com os valores q a API JUNO
	//nos enviar...
	private String createdOn;
	private String date;
	private String releaseDate;
	private String amount;
	private String fee;
	private String status;
	private String type;

	//criando var/atributos/obj de nome CHARGE do tipo 
	//CHARGENOTIFICACAOPAGAPIJUNO... q tem atributos/var
	//q vao receber os dados q serao passados pela API
	//JUNO do melhor envio... 
	private ChargeNotificaoPagApiJuno charge = new ChargeNotificaoPagApiJuno();

	//instanciando um obj/var do tipo PIXCHARGEJUNO de nome
	//PIX, q tem atributos/var/obj q vamos receber atraves do
	//
	//vamos receber esses dados no WEBHOOK (metodo q vai ta rodando na
	//loja virtual e q a APIJUNO vai enviar dados para ele...
	//dados tipo informando se o pagamento deu certo...)
	private PixChargeJuno pix = new PixChargeJuno();

	private String digitalAccountId;

	public PixChargeJuno getPix() {
		return pix;
	}

	public void setPix(PixChargeJuno pix) {
		this.pix = pix;
	}

	public String getDigitalAccountId() {
		return digitalAccountId;
	}

	public void setDigitalAccountId(String digitalAccountId) {
		this.digitalAccountId = digitalAccountId;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ChargeNotificaoPagApiJuno getCharge() {
		return charge;
	}

	public void setCharge(ChargeNotificaoPagApiJuno charge) {
		this.charge = charge;
	}

}
