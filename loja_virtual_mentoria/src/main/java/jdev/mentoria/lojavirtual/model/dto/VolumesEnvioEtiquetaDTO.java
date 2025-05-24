package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//Essa classe/entity tera as informacoes sobre os VOLUMES/CAIXAS
//q vao ser colocadas no caminhao da transportadora... Altura, largura,
//peso, etc...
//essa classe sera chamada no ENVIOETIQUETADTO e sera enviada
//em formato de JSON para a API do MELHORENVIO poder calcular o frete
public class VolumesEnvioEtiquetaDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String height;
	private String width;
	private String length;
	private String weight;

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

}
