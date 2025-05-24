package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*Objeto principal recebimento api juno boleto pix - webhook*/
//
//com essa class/entity ela sera usada como um OBJ/var
//e sera instanciada em um METODO/ENDPOINT q sera chamado pela
//JUNO... E com o metodo/endpoint q vai receber um var/obj do tipo
//DATANOTIFICACAOAPIJUNOTPAGAMENTO nos vamos receber a informacao
//da JUNO falando se o PAGAMENTO por PIX, Boleto, CARTAO, etc... foi ok
//
public class DataNotificacaoApiJunotPagamento implements Serializable {

	private static final long serialVersionUID = 1L;

	private String eventId;
	private String eventType;
	private String timestamp;

	//instanciando uma LISTA de OBJ/VAR do tipo 
	//ATTIBUTESNOTIFICACAOPAGAAPIJUNO q tem outros atributos
	//q serao recebidos em um JSON q vai vim do JUNO
	//a JUNO vai acessar um metodo/endpoint do NOSSO SISTEMA/LOJAVIRTUAL
	//e enviar um JSON e algumas informacoes vao ser salvas
	//na LISTA do tipo ATTIBUTESNOTIFICACAOPAGAAPIJUNO de nome DATA
	private List<AttibutesNotificaoPagaApiJuno> data = new ArrayList<AttibutesNotificaoPagaApiJuno>();

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public List<AttibutesNotificaoPagaApiJuno> getData() {
		return data;
	}

	public void setData(List<AttibutesNotificaoPagaApiJuno> data) {
		this.data = data;
	}

}
