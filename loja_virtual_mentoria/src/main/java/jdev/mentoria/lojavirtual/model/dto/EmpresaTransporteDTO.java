package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;


//Essta CLASS/ENTITY do tipo DTO vai ter os atributos/var 
//correspondente as informacoes retornadas pela a API do MELHORENVIO
//no que se refere a empresas de transporte
public class EmpresaTransporteDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	
	private String id;
	private String nome;
	private String valor;
	private String empresa;
	private String picture;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getValor() {
		return valor;
	}
	
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public String getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	
	public String getPicture() {
		return picture;
	}
	
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	
	//metodo para verificar se TODOS os CAMPOS foram passados CORRETAMENTE
	//para q se caso TRUE sera add na LISTA(lista de EMPRESATRANSPORTEDTOS)
	//q ta no TESTEAPIMELHORENVIO.JAVA... A empresas tem q ser ADD
	//na LISTA EMPRESATRANSPORTESDTOS pq a API MELHORENVIO geralmente
	//retorna uma LISTA com o PRECO, TEMPO, NOME de todas as empresas
	//q fazem determinado transporte
	//
	public boolean dadosOK() {		
		if(id != null && empresa != null && valor != null && nome != null) {
			return true;
		}		
		return false;
	}
	
	
	
	
	
}
