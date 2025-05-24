package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


//Classe/entidade do tipo DTO q tera 2 var com 
//o CEP de ORIGEM e CEP de DESTINO para ser passado para API do
//MELHORENVIO
//
public class ConsultaFreteDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	//instanciando uma var/obj do tipo FROMDTO de nome FROM
	//q tera o CEP de origem o PRODUTO
	private FromDTO from;
	
	//instanciando uma var/obj do tipo TODTO de nome TO
	//q tera o CEP de destino do PRODUTO
	private ToDTO to;
	
	//lista de PRODUTOS do FRETE
	//
	private List<ProductsDTO> products = new ArrayList<ProductsDTO>();

	public FromDTO getFrom() {
		return from;
	}

	public void setFrom(FromDTO from) {
		this.from = from;
	}

	public ToDTO getTo() {
		return to;
	}

	public void setTo(ToDTO to) {
		this.to = to;
	}

	public List<ProductsDTO> getProducts() {
		return products;
	}

	public void setProducts(List<ProductsDTO> products) {
		this.products = products;
	}

	
	
	
}
