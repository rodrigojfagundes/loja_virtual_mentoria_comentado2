package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


//apos nos enviarmos os dados atraves do OBJ instanciados no 
//COBRANCAJUNOAPI.java... A API da Juno vai dar um retorno...
//um retorno em JSON... esse retorno vamos salvar atras dos obj instanciados
//aqui...
//
//a maior parte das informacoes vao ficar dentro do OBJ de nome CHARGES
//qe uma lista do tipo CONTEUDOBOLETOJUNO
public class Embedded implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<ConteudoBoletoJuno> charges = new ArrayList<ConteudoBoletoJuno>();
	
	public void setCharges(List<ConteudoBoletoJuno> charges) {
		this.charges = charges;
	}
	
	public List<ConteudoBoletoJuno> getCharges() {
		return charges;
	}

}
