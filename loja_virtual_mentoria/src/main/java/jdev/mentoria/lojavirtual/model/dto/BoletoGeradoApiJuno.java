package jdev.mentoria.lojavirtual.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


//
//
//nessa classe/entity estamos instanciando os OBJ EMBEDDED e LIST
//q neles iremos passar para as suas var/atributos o retorno em JSON
//da API da JUNO apos nos passarmos o JSON com os dados para gerar
//um boleto/pix... Retorno apos nos enviarmos o CHARGE e BILLING q estao
//instanciados no COBRANCAJUNOAPI.JAVA...
//+ou- isso...
public class BoletoGeradoApiJuno implements Serializable {

	private static final long serialVersionUID = 1L;

	//instanciando um OBJ/VAR de nome _EMBEDDED do tipo EMBEDDED
	//q basicamente tem atributos/var para armazenar o retorno
	//da API da JUNO... Com o boleto gerado...
	private Embedded _embedded = new Embedded();

	//lista com os links dos boletos gerados...
	//ACHO Q E A URL PARA VER O BOLETO
	private List<Links> _links = new ArrayList<Links>();

	public void set_embedded(Embedded _embedded) {
		this._embedded = _embedded;
	}

	public Embedded get_embedded() {
		return _embedded;
	}

	public void set_links(List<Links> _links) {
		this._links = _links;
	}

	public List<Links> get_links() {
		return _links;
	}

}
