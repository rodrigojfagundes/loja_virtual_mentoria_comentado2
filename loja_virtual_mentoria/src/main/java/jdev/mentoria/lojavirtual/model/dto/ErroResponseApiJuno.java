package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//
//class com os atributos/var com a estrutura de erro da JUNO
//para quando enviamos um JSON para juno para criar um pagamento
//se der algum erro, a juno retorna um JSON com essa estrutura
//timestamp, status, erro, etc... dai para armazenar isso
//precisamos dessa class com esses atributos...
//
//sera utilizada no pagamentocontroller.java...
public class ErroResponseApiJuno implements Serializable {

	private static final long serialVersionUID = 1L;

	private String timestamp;

	private String status;

	private String error;

	private String path;

	private List<DetailsErroJunoApi> details = new ArrayList<DetailsErroJunoApi>();

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<DetailsErroJunoApi> getDetails() {
		return details;
	}

	public void setDetails(List<DetailsErroJunoApi> details) {
		this.details = details;
	}

	public String listaErro() {
		String retorno = "";
		
		for (DetailsErroJunoApi detailsErroJunoApi : details) {
			
			retorno += detailsErroJunoApi.getMessage() + "\n";
		}
		
		return retorno;
	}

}
