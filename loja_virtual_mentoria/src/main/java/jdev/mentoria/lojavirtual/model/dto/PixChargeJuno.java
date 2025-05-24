package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//a CLASS/ENTITY principal e DataNotificacaoApiJunotPagamento a partir
//dela as outras sao instanciadas... para receber os dados da JUNO
//pelo WEBHOOK
//
//
//class/entity q sera instanciada dentro do ATTRIBUTESNOTIFICACAOPAGAJUNO.JAVA
//e q tem atributos/var q vao vim apartir do JSON q a JUNO vai nos enviar
//para o nosso WEBHOOK
//
//vamos receber esses dados no WEBHOOK (metodo q vai ta rodando na
//loja virtual e q a APIJUNO vai enviar dados para ele...
//dados tipo informando se o pagamento deu certo...)
public class PixChargeJuno implements Serializable {

	private static final long serialVersionUID = 1L;

	private String txid;
	private String endToEndId;

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public String getEndToEndId() {
		return endToEndId;
	}

	public void setEndToEndId(String endToEndId) {
		this.endToEndId = endToEndId;
	}

}
