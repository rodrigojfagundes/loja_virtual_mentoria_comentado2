package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//
//criando class/entity CRIARWEBHOOK q basicamente vai armazenar
//a url de onde ta rodando o nosso projeto de lojavirtual na aws
public class CriarWebHook implements Serializable {

	private static final long serialVersionUID = 1L;

	private String url;

	private List<String> eventTypes = new ArrayList<String>();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getEventTypes() {
		return eventTypes;
	}

	public void setEventTypes(List<String> eventTypes) {
		this.eventTypes = eventTypes;
	}

}
