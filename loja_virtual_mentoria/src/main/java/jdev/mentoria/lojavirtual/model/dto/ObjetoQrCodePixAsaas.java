package jdev.mentoria.lojavirtual.model.dto;

//basicamente essa classe vai servir para ser instanciada
//no SERVICEJUNOBOLETO.JAVA para quando a API ASAAS gerar o QRCODE
//de um PIX para ser pago...
//e tera algumas informacoes como EPIRACAO, VALOR, IMAGEM, etc...
public class ObjetoQrCodePixAsaas {
	
	private String encodedImage;
	private String payload;
	private String expirationDate;
	private String success;

	public String getEncodedImage() {
		return encodedImage;
	}

	public void setEncodedImage(String encodedImage) {
		this.encodedImage = encodedImage;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	

}
