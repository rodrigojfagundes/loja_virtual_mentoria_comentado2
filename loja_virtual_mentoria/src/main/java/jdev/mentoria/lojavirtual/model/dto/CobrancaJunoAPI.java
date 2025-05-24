package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//na hora de passar/enviar o JSON para API da JUNO precisamos passar/enviar
//algumas informacoes, como CHARGE e BILLING
//q dentro do CHARGE tem varios atributos/variaveis
//e dentro do BILLING tem varios ATRIBUTOS/VARIAVEIS
//dai vamos juntar todos em um unico JSON e enviar para a API da JUNO..
//
//q a API da JUNO ira utilizar essas informacoes para gerar os boletos/pix...
public class CobrancaJunoAPI implements Serializable {

	private static final long serialVersionUID = 1L;

	//instanciando um obj do tipo charge, vamos preencher os atributos dele
	//e enviar para API da JUNO... Junto com o Billing e outras coisas...
	private Charge charge = new Charge();

	//instanciando o obj do tipo billing, vamos preencher os atributos dele
	//e enviar para API da JUNO... Juntocom o Charge e outras coisas...
	//o billing tem os dados do comprador...
	private Billing billing = new Billing();

	public Charge getCharge() {
		return charge;
	}

	public void setCharge(Charge charge) {
		this.charge = charge;
	}

	public Billing getBilling() {
		return billing;
	}

	public void setBilling(Billing billing) {
		this.billing = billing;
	}

}
