package jdev.mentoria.lojavirtual.model.dto;


//OBS, o prof nao ensinou a fazer esse arquivo...
//parece q ta faltando uma aula...
public class AsaasApiPagamentoStatus {
	
	public static String BOLETO = "BOLETO";
	public static String CREDIT_CARD = "CREDIT_CARD";
	public static String PIX = "PIX";
	public static String BOLETO_PIX = "UNDEFINED"; /*conbrança que pode ser paga por pir, boleto e cartão*/
	
	//URL do PROF
	//public static String URL_API_ASAAS = "https://www.asaas.com/api/v3/";
	
	//ESAS E DO SANDBOX
	public static String URL_API_ASAAS = "https://api-sandbox.asaas.com/v3/";
	
	//Link do endpoint ASAAS PRODUCAO (
	//public static String URL_API_ASAAS = "https://api.asaas.com/v3/";
	
	////
	public static String API_KEY = "Token_Asaas";
	
	//esse e o TOKEN de PRODUCAO
	//public static String API_KEY = "Token_prod_Asaas";
	
	

}
