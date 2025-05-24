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
		
		
		//6 - traz uma lista de agency (agencias) de transportadoras
		//tipo tem uma AGENCIA da TRANSPORTADORA JDLOG em TIJUCAS
		//uma em SAO JOSE, etc... Agencia e meio como loja...
		//
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
		  .url("https://sandbox.melhorenvio.com.br/api/v2/me/shipment/agencies?company=2&country=BR&state=SC&city=Joinville")
		  .get()
		  .addHeader("accept", "application/json")
		  .addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
		  .build();

		Response response = client.newCall(request).execute();
		
		System.out.println(response.body().string());
		
		
		
		
		
		
		
		
		
		//5 - Faz impressao das etiquetas (pede para a API do
		//MELHORENVIO gerar um link com a etiqueta
		//
		//OkHttpClient client = new OkHttpClient();
		//
		//MediaType mediaType = MediaType.parse("application/json");
		//RequestBody body = RequestBody.create(mediaType, "{\"mode\":\"private\",\"orders\":[\"9e55f440-38c6-4491-8884-18dae893ae58\"]}");
		//Request request = new Request.Builder()
		  //.url("https://sandbox.melhorenvio.com.br/api/v2/me/shipment/print")
		  //.post(body)
		  //.addHeader("Accept", "application/json")
		  //.addHeader("Content-Type", "application/json")
		  //.addHeader("Authorization", "Bearer Token_Melhor_Envio")
		  //.addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
		  //.build();

		//Response response = client.newCall(request).execute();
		
		
		//System.out.println(response.body().string());
		
		
		
		
		
		
		//4 - GERACAO DA ETIQUETA
		//
		//OkHttpClient client = new OkHttpClient();
		//
		//MediaType mediaType = MediaType.parse("application/json");
		//RequestBody body = RequestBody.create(mediaType, "{\"orders\":[\"9e55f440-38c6-4491-8884-18dae893ae58\"]}");
		//Request request = new Request.Builder()
		//.url("https://sandbox.melhorenvio.com.br/api/v2/me/shipment/generate")
		//.post(body)
		//.addHeader("Accept", "application/json")
		//.addHeader("Content-Type", "application/json")
		//.addHeader("Authorization", "Bearer Token_Melhor_Envio")
		//.addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
		//.build();

		//Response response = client.newCall(request).execute();
		
		//System.out.println(response.body().string());
		
		
		
		
		
		//1 - INSERE FRETE DE FRETE
		//
		//OkHttpClient client = new OkHttpClient();
		//
		//MediaType mediaType = MediaType.parse("application/json");
		//RequestBody body = RequestBody.create(mediaType, "{\"from\":{\"name\":\"teste\",\"phone\":\"77\",\"address\":\"iji\",\"city\":\"tijucas\",\"postal_code\":\"88200000\",\"document\":\"278.045.610-83\"},\"to\":{\"name\":\"oooo\",\"phone\":\"567576\",\"address\":\"joijoij\",\"city\":\"curitiba\",\"postal_code\":\"75830-112\"},\"options\":{\"receipt\":true,\"own_hand\":true,\"reverse\":true,\"non_commercial\":true,\"insurance_value\":\"63287678326786823767863286\"},\"service\":3,\"volumes\":[{\"height\":20,\"width\":20,\"length\":20,\"weight\":20}],\"agency\":49}");
		//Request request = new Request.Builder()
		//.url("https://sandbox.melhorenvio.com.br/api/v2/me/cart")
		//.post(body)
		//.addHeader("Accept", "application/json")
		//.addHeader("Content-Type", "application/json")
		//.addHeader("Authorization", "Bearer Token_Melhor_Envio")
		//.addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
		//.build();
		//
		//Response response = client.newCall(request).execute();
		//
		//System.out.println(response.body().string());

		
		//######################
		
		//2 - FAZ A COMPRA DO FRETE PARA A ETIQUETA
		//
		//OkHttpClient client = new OkHttpClient();
		//
		//MediaType mediaType = MediaType.parse("application/json");
		//RequestBody body = RequestBody.create(mediaType, "{\"orders\":[\"9e55f440-38c6-4491-8884-18dae893ae58\"]}");
		//Request request = new Request.Builder()
		//.url("https://sandbox.melhorenvio.com.br/api/v2/me/shipment/checkout")
		//.post(body)
		//.addHeader("Accept", "application/json")
		//.addHeader("Content-Type", "application/json")
		//.addHeader("Authorization", "Bearer Token_Melhor_Envio")
		//.addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
		//.build();
		//
		//Response response = client.newCall(request).execute();
		//   
		//System.out.println(response.body().string());
		
		
		
		
	}
}
