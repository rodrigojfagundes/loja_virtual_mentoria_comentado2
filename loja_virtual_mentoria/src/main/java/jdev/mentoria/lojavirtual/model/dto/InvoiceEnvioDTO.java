package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//class/entity INVOICEENVIODTO q sera chamada na class OPTIONSENVIODTO
//e basicamente tera um key/serial
//
public class InvoiceEnvioDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String key;

	
	public String getKey() {
		return key;
	}

	
	public void setKey(String key) {
		this.key = key;
	}
	
	
}
