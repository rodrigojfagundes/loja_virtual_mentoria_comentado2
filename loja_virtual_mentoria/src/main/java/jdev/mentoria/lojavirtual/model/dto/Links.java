package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//
//basicamente a class/entity link tera uma class/entity instanciada como um
//obj/var/atributo de nome self... e q nele ficara salvo os link dos boletos
//gerado pela API JUNO apos nos passarmos as informacoes para API da JUNO
//gerar o boleto... dai o retorno e um OBJ em JSON q entre as informacoes
//esta o link dos boleto...
public class Links implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Self self = new Self();
	
	
	public void setSelf(Self self) {
		this.self = self;
	}
	
	public Self getSelf() {
		return self;
	}

}
