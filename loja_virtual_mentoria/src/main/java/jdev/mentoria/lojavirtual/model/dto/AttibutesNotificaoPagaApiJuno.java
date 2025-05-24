package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//a CLASS/ENTITY principal e DataNotificacaoApiJunotPagamento a partir
//dela as outras sao instanciadas... para receber os dados da JUNO
//pelo WEBHOOK
//
//class/entity de nome ATTIBUTESNOTIFICACAOPAGAAPIJUNO q sera
//instanciada no DATANOTIFICACAOAPIJUNOTPAGAMENTO.JAVA
//e basicamente ira armazenar algumas informacoes q vamos recebr
//por JSON da JUNO... quando a JUNO enviar informacoes para o nosso
//WEBHOOK
//
//vamos receber esses dados no WEBHOOK (metodo q vai ta rodando na
//loja virtual e q a APIJUNO vai enviar dados para ele...
//dados tipo informando se o pagamento deu certo...)
public class AttibutesNotificaoPagaApiJuno implements Serializable {

	private static final long serialVersionUID = 1L;

	private String entityId;
	private String entityType;

	//instanciando um OBJ/VAR do tipo ATTRIBUTESNOTIFICACAOPAGAJUNO
	//de nome ATTRIBUTES q vai armazenar informacoes do JSON
	//q sera enviado pela JUNO com a NOTIFICACAO relacionada a PAGAMAMENTO
	private AttributesNotificaoPagaJuno attributes = new AttributesNotificaoPagaJuno();

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public AttributesNotificaoPagaJuno getAttributes() {
		return attributes;
	}

	public void setAttributes(AttributesNotificaoPagaJuno attributes) {
		this.attributes = attributes;
	}

}
