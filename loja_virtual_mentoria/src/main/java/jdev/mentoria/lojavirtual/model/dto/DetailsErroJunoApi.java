package jdev.mentoria.lojavirtual.model.dto;

//class DETAILSERROJUNOAPI q sera instanciada no na class 
//ERRORESPONSEAPIJUNO.JAVA e q basicamentevai servir para gravar
//informacoes de caso de erro ao enviar um pagamento para juno
//atraves de json
public class DetailsErroJunoApi {

	private String field;
	private String message;
	private String errorCode;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
