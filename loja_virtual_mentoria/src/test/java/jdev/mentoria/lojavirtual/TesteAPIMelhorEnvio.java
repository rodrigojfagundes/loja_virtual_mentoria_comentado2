package jdev.mentoria.lojavirtual;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jdev.mentoria.lojavirtual.enums.ApiTokenIntegracao;
import jdev.mentoria.lojavirtual.model.dto.EmpresaTransporteDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TesteAPIMelhorEnvio {

	public static void main(String[] args) throws Exception {
		
		//instanciando um obj/var do tipo OKHTTPCLIENT de nome CLIENT
		OkHttpClient client = new OkHttpClient().newBuilder().build();

		//Estamos enviando um JSON q esta na VAR/OBJ BODY com 1 CEP de
		//ORIGEM e um CEP de DESTINO e 3 PRODUTOS para
		//o LINK da API do MELHORENVIO,junto com isso estamos enviando
		//o nosso TOKEN de autenticacao na API do MELHORENVIO
		//dai a API do melhor ENVIO ve as INFORMACOES q estao no
		//BODY(JSON) como produtos, destino, quantidade, etc...
		//e retorna um var/obj REPONSE em JSON com as INFORMACOES como PRECO
		//para fazer a entrega em cada transportadora etc...
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, "{\"from\":{\"postal_code\":\"96020360\"},\"to\":{\"postal_code\":\"01018020\"},\"products\":[{\"id\":\"x\",\"width\":11,\"height\":17,\"length\":11,\"weight\":0.3,\"insurance_value\":10.1,\"quantity\":1},{\"id\":\"y\",\"width\":16,\"height\":25,\"length\":11,\"weight\":0.3,\"insurance_value\":55.05,\"quantity\":2},{\"id\":\"z\",\"width\":22,\"height\":30,\"length\":11,\"weight\":1,\"insurance_value\":30,\"quantity\":1}]}");
		Request request = new Request.Builder()
		  .url("https://sandbox.melhorenvio.com.br/api/v2/me/shipment/calculate")
		  .method("POST", body)
		  //.post(body)
		  .addHeader("Accept", "application/json")
		  .addHeader("Content-Type", "application/json")
		  .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
		  .addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
		  .build();

		Response response = client.newCall(request).execute();
		
		//System.out.println(response.body().string());
		
		
		
		//criando uma var/obj do tipo JSONNODE de nome jsonnode
		//q e onde vai ficar salvo o retorno em JSON da API
		//do MELHORENVIO apos nos passarmos acima os PRODUTOS e a
		//ORIGEM e DESTINO... Dai dessa forma conseguimos ter os
		//dados em um FORMATO com ID e VALOR...
		//tipo ID 1 = nome, tempo, preco para o SEDEX transportar
		//ID 2 = nome, tempo, preco para a JadLog transportar... etc...
		JsonNode jsonNode = new ObjectMapper()
				.readTree(response.body().string());
		
		//varrendo as informacoes em JSON q foram retornadas
		//pela API do MELHORENVIO e ITERANDO/VARRENDO ela...
		//
		Iterator<JsonNode> iterator = jsonNode.iterator();
		
		//criando uma LISTA do TIPO EMPRESATRANSPORTEDTO de nome
		//EMPRESATRANSPORTEDTOS...
		//pois o MELHORENVIO retorna um JSON com UMA LISTA de EMPRESAS
		//q fazem determinado TRANSPORTE... Com o ID, NOME, PRECO, etc...
		//
		List<EmpresaTransporteDTO> empresaTransporteDTOs = new ArrayList<EmpresaTransporteDTO>();
		
		//usando o while para pecorrer o iterator q é onde ta
		//q tem os dados do JSONNODE (q possui o retorno em JSON da
		//API do MELHORENVIO)
		//
		while(iterator.hasNext()) {
			JsonNode node = iterator.next();
			
		
			//pegando os campos q sao retornados no JSON e estao no
			//NODE q e um OBJ do tipo JSONNODE... e passando essas
			//informacoes para o nosso DTO q e um OBJ do tipo
			//EMPRESATRANSPORTEDTO
			//
			EmpresaTransporteDTO empresaTransporteDTO = new EmpresaTransporteDTO();
			if(node.get("id") != null) {
				empresaTransporteDTO.setId(node.get("id").asText());
				
			}

			//se no JSON retornado pela API do MELHORENVIO o ATRIBUTO/CAMPO
			//NAME NAO for NULL... Dai vamos pegar o valor q ta em NAME
			//e passar para o atributo NOME do nosso OBJ EMPRESATRANSPORTEDTO
			if(node.get("name") != null) {
				empresaTransporteDTO.setNome(node.get("name").asText());
			}					
			
			if(node.get("price") != null) {
			empresaTransporteDTO.setValor(node.get("price").asText());
		}
			
			if(node.get("company") != null) {				
			empresaTransporteDTO.setEmpresa(node.get("company").asText());
			empresaTransporteDTO.setPicture(node.get("company").get("picture").asText());
		}
			
			//SE todos os DADOS acima estiverem OK, ou seja NADA estiver
			//NULL... Dai vamos ADD essa EMPRESATRANSPORTEDTO a LISTA
			//EMPRESATRANSPORTESDTOS
			if(empresaTransporteDTO.dadosOK()) {
				empresaTransporteDTOs.add(empresaTransporteDTO);
			}			
		//}			
	}
		System.out.println(empresaTransporteDTOs);
		
	}
}

//		OkHttpClient client = new OkHttpClient().newBuilder().build();
//		MediaType mediaType = MediaType.parse("application/json");
//		RequestBody body = RequestBody.create(mediaType,
//				"{ \"from\": { \"postal_code\": \"96020360\" }, \"to\": { \"postal_code\": \"01018020\" }, \"products\": [ { \"id\": \"x\", \"width\": 11, \"height\": 17, \"length\": 11, \"weight\": 0.3, \"insurance_value\": 10.1, \"quantity\": 1 } ] }");
//		Request request = new Request.Builder().
//				url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/cart")
//				.method("POST", body).addHeader("Accept", "application/json")
//				.addHeader("Content-Type", "application/json")
//				.addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
//				.addHeader("User-Agent", "rodrigojosefagundes@gmail.com").build();
//		Response response = client.newCall(request).execute();

//		System.out.println(response.body().string());
		
		
		//criando uma var/obj do tipo JSONNODE de nome jsonnode
		//q e onde vai ficar salvo o retorno em JSON da API
		//do MELHORENVIO apos nos passarmos acima os PRODUTOS e a
		//ORIGEM e DESTINO
//		JsonNode jsonNode = new ObjectMapper()
//				.readTree(response.body().string());
		
		
		//varrendo as informacoes em JSON q foram retornadas
		//pela API do MELHORENVIO e ITERANDO/VARRENDO ela...
		//
//		Iterator<JsonNode> iterator = jsonNode.iterator();
		
		//criand uma LISTA do tipo EMPRESATRANSPORTEDTO
		//
//		List<EmpresaTransporteDTO> empresaTransporteDTOs = new ArrayList<EmpresaTransporteDTO>();
		
		
		//usando o while para pecorrer o iterator q é onde ta
		//q tem os dados do JSONODE (q possui o retorno em JSON da
		//API do MELHORENVIO)
		//
//		while(iterator.hasNext()) {
//			JsonNode node = iterator.next();
			
			//instanciando um obj do tipo EMPRESATRANSPORTEDTO
//			EmpresaTransporteDTO empresaTransporteDTO = new EmpresaTransporteDTO();
			
			//pegando os campos q sao retornados no JSON e estao no
			//NODE q e um OBJ do tipo JSONNODE... e passando essas
			//informacoes para o nosso DTO q e um OBJ do tipo
			//EMPRESATRANSPORTEDTO
			//e p
//			if(node.get("id") != null) {
//				empresaTransporteDTO.setId(node.get("id").asText());
				
//			}
			
			//se no JSON retornado pela API do MELHORENVIO o ATRIBUTO/CAMPO
			//NAME NAO for NULL... Dai vamos pegar o valor q ta em NAME
			//e passar para o atributo NOME do nosso OBJ EMPRESATRANSPORTEDTO
//			if(node.get("name") != null) {
//				empresaTransporteDTO.setNome(node.get("name").asText());
//			}
//			
//			if(node.get("price") != null) {
//				empresaTransporteDTO.setValor(node.get("price").asText());
//			}
			
//			if(node.get("company") != null) {
//				empresaTransporteDTO.setEmpresa(node.get("company").asText());
//				empresaTransporteDTO.setPicture(node.get("company").get("picture").asText());
//			}
			
			//
			//SE todos os DADOS acima estiverem OK, ou seja NADA estiver
			//NULL... Dai vamos ADD essa EMPRESATRANSPORTEDTO a LISTA
			//EMPRESATRANSPORTESDTOS
//			if(empresaTransporteDTO.dadosOK()) {
//				empresaTransporteDTOs.add(empresaTransporteDTO);
//			}			
//		}
		
//		System.out.println(empresaTransporteDTOs);
		
//	}
//}
