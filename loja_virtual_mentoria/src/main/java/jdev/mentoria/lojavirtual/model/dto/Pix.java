package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//criando a class/entity pix q basicamente tera as informacoes de PIX q
//vao vim da API da JUNO... e sera instanciado no PAYMENTS.java
public class Pix implements Serializable {

	private static final long serialVersionUID = 1L;

	//id
	private String id;
	//url(o copia e cola do pix)
	private String payloadInBase64;
	//acho q e a imagem em qr-code
	private String imageInBase64;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPayloadInBase64() {
		return payloadInBase64;
	}

	public void setPayloadInBase64(String payloadInBase64) {
		this.payloadInBase64 = payloadInBase64;
	}

	public String getImageInBase64() {
		return imageInBase64;
	}

	public void setImageInBase64(String imageInBase64) {
		this.imageInBase64 = imageInBase64;
	}

}
