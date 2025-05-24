package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

public class TagsEnvioDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//var/atributo tag tem uma identificacao do pedido dentro da plataforma
	//e url e o link direto para acessar o pedido dentro da loja/plataforma
	private String tag;
	private String url;
	
	
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
	
}
