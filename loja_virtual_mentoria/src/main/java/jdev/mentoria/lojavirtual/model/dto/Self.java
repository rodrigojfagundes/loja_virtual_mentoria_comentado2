package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;

//class/entety SELF... basicamente tem uma STRING de nome HREF q tera
//o link dos BOLETOS gerados pela juno...
//
//vai servir para apos nos passarmos os dados BILLING e CHARGE, etc...
//para a API da JUNO gerar o BOLETO/PIX... Ela vai nos retornar um JSON
//com mtas coisas... E um desses OBJ q vai no JSON e o SELF
//onde tera o link do boleto/pix... +ou- isso...
public class Self implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private String href;
	
	public void setHref(String href) {
		this.href = href;
	}
	
	public String getHref() {
		return href;
	}

}
