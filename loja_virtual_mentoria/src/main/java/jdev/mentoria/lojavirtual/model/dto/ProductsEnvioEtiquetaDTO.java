package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;


//class/entity de nome PRODUCTSENVIOETIQUETADTO q tera as informacoes
//basicas do PRODUTO, como NOME, QUANTIDADE, VALORUNITARIO,
//e essas informacoes nos vao ser chamadas na class/entity
//ENVIOETIQUETADTO e serao enviadas para a API do MELHORENVIO
//para pd calcular o frete
public class ProductsEnvioEtiquetaDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	private String name;
	private String quantity;
	private String unitary_value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getUnitary_value() {
		return unitary_value;
	}
	public void setUnitary_value(String unitary_value) {
		this.unitary_value = unitary_value;
	}
	
	
	
	
	
}
