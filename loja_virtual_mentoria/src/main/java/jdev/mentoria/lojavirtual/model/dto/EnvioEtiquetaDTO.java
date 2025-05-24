package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//class/entidade que ira armazenar os atributos/var/OBJETOS
// tipo FROMENVIOETIQUETADTO(info de ORIGEM do PRODUTO)
//tipo TOENVIOETIQUETADTO (info de DESTINO do PRODUTO)
//PRODUCTSENVIOETIQUETADTO (info dos PRODUTOS a ser ENVIADO)
//etc... para serem enviados
//e recebidos para a API MELHORENVIO para poder calcular o frete... 
//O envio e recebimento das informacoes e atraves de JSON
//
public class EnvioEtiquetaDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String service;
	private String agency;
	
	//criando um obj/var do tipo FROMENVIOETIQUETADTO q tera
	//as informacoes de ORIGEM do ENVIO... tipo cidade, estado, nome, etc...
	//
	private FromEnvioEtiquetaDTO from = new FromEnvioEtiquetaDTO();
	
	//criando um obj/var do tipo TOENVIOETIQUETADTO q tera
	//as informacoes de DESTINO do ENVIO... tipo cidade, estado, nome, etc...
	//
	private ToEnvioEtiquetaDTO to = new ToEnvioEtiquetaDTO();
	
	//criando uma LISTA de OBJ do tipo PRODUCTSENVIOETIQUETADTO
	//e no PRODUCTSENVIOETIQUETADTO tem as var/atributos name, quantity
	//univary_value para a API do MELHORENVIO conseguir calcular o preco
	//do FRETE...
	private List<ProductsEnvioEtiquetaDTO> products = new ArrayList<ProductsEnvioEtiquetaDTO>();
	
	//criando uma LISTA de VOLUMES/CAIXAS (VOLUMESENVIOETIQUETADTO)
	//q tem as informacoes em atributos/var como peso, altura, largura
	//para a API do MELHORENVIO pd calcular o preco do frete
	//
	private List<VolumesEnvioEtiquetaDTO> volumes = new ArrayList<VolumesEnvioEtiquetaDTO>();

	
	//instanciando o nosso obj/var OPTIONS, q basicamente
	//tem alguns informacoes adicionais na hora de fazer o transporte
	//
	private OptionsEnvioEtiquetaDTO options = new OptionsEnvioEtiquetaDTO();
	
	public void setOptions(OptionsEnvioEtiquetaDTO options) {
		this.options = options;
	}
	
	public OptionsEnvioEtiquetaDTO getOptions() {
		return options;
	}
	
	
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public FromEnvioEtiquetaDTO getFrom() {
		return from;
	}

	public void setFrom(FromEnvioEtiquetaDTO from) {
		this.from = from;
	}

	public ToEnvioEtiquetaDTO getTo() {
		return to;
	}

	public void setTo(ToEnvioEtiquetaDTO to) {
		this.to = to;
	}

	public List<ProductsEnvioEtiquetaDTO> getProducts() {
		return products;
	}

	public void setProducts(List<ProductsEnvioEtiquetaDTO> products) {
		this.products = products;
	}

	public List<VolumesEnvioEtiquetaDTO> getVolumes() {
		return volumes;
	}

	public void setVolumes(List<VolumesEnvioEtiquetaDTO> volumes) {
		this.volumes = volumes;
	}
	
	
	
	
	
}
