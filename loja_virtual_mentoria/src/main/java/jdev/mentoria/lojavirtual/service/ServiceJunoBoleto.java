package jdev.mentoria.lojavirtual.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import jdev.mentoria.lojavirtual.enums.ApiTokenIntegracao;
import jdev.mentoria.lojavirtual.model.AccessTokenJunoAPI;
import jdev.mentoria.lojavirtual.model.BoletoJuno;
import jdev.mentoria.lojavirtual.model.VendaCompraLojaVirtual;
import jdev.mentoria.lojavirtual.model.dto.AsaasApiPagamentoStatus;
import jdev.mentoria.lojavirtual.model.dto.BoletoGeradoApiJuno;
import jdev.mentoria.lojavirtual.model.dto.ClienteAsaasApiPagamento;
import jdev.mentoria.lojavirtual.model.dto.CobrancaApiAsaas;
import jdev.mentoria.lojavirtual.model.dto.CobrancaGeradaAsassApi;
import jdev.mentoria.lojavirtual.model.dto.CobrancaGeradaAssasData;
import jdev.mentoria.lojavirtual.model.dto.CobrancaJunoAPI;
import jdev.mentoria.lojavirtual.model.dto.ConteudoBoletoJuno;
import jdev.mentoria.lojavirtual.model.dto.CriarWebHook;
import jdev.mentoria.lojavirtual.model.dto.ObjetoPostCarneJuno;
import jdev.mentoria.lojavirtual.model.dto.ObjetoQrCodePixAsaas;
import jdev.mentoria.lojavirtual.repository.AccesTokenJunoRepository;
import jdev.mentoria.lojavirtual.repository.BoletoJunoRepository;
import jdev.mentoria.lojavirtual.repository.Vd_Cp_Loja_virt_repository;
import jdev.mentoria.lojavirtual.util.ValidaCPF;

//criando um service para criacao de boleto, e pix e cartao
//
//o nome e SERVICEJUNOBOLETO, mas seera utilizado tbm pela ASAAS
@Service
public class ServiceJunoBoleto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private AccessTokenJunoService accessTokenJunoService;
	
	@Autowired
	private AccesTokenJunoRepository accesTokenJunoRepository;
	
	@Autowired
	private Vd_Cp_Loja_virt_repository vd_Cp_Loja_virt_repository;
	
	@Autowired
	private BoletoJunoRepository boletoJunoRepository;
	
	
	
	//
	//metodo para verificar se tem um cliente para associar
	//a cobranca a um cliente/customer
	//
	//esse metodo BUSCACLIENTEPESSOAAPIASAAS recebe um OBJ/VAR
	//do tipo OBJETOPOSTCARNEJUNO de nome DADOS q 
	//tem algumas informacoes descricao da compra, e informacoes
	//do cliente (qm vai pagar o boleto/pix/cartao, etc...)
	/**
	 * retorna o id do customer/cliente/pessoa
	 * @throws Exception 
	 * */
	public String buscaClientePessoaApiAsaas(ObjetoPostCarneJuno dados) throws Exception {
		
		/*id do cliente para ligar com a cobranca*/
		String customer_id = "";
		
		/*criando/iniciando consultando o cliente*/
		
		
		// instanciando um CLIENT do tipo CLIENT
		//
		// informando q a url da API do ASAAS nao precisa de certificado ssl
		Client client = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
		
		// criando um var/obj dotipo WEBRESOURCE de nome WEBRESORUCE
		// q recebe a URL/LINK de onde deve ser feita a solicitacao
		//
		//e vamos buscar pelo o e-mail, e-mail esse q vai vim no
		//nosso OBJ/VAR de nome DADOS do tipo OBJETOPOSTCARNEJUNO
		WebResource webResource = client.resource(
				AsaasApiPagamentoStatus.URL_API_ASAAS + "customers?email="+dados.getEmail());
		
		
		// criando um CLIENTRESPONSE do tipo CLIENTRESPONSE
		// q vai receber o retorno do metodo ACCEPT do WEBRESOURCE
		// metodo no qual passamos
		// informando o tipo de formatacao, e montando o cabecalho
		// para fazer as requisicoes a API da Asaas
		// tbm informando o nosso token gerado pela asaas e tals
		// NAOSEI SE e um BEARER_TOKEN, mas e um token q a ASAAS GEROU
		// e tbm informamos para qual URL da asaas vai ser feita a
		// requisicao do tipo GET
		//
		// o retorno sera uma chave pix aleartoria gerada pela asaas
		ClientResponse clientResponse = webResource.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("access_token", AsaasApiPagamentoStatus.API_KEY)
				.get(ClientResponse.class);
		
		//criando um LINKEDHAHMAP q vai retornar uma CHAVE e um OBJECT
		//para gente... Uma lista com chaves e valores
		//
		LinkedHashMap<String, Object> parser = new JSONParser(
				clientResponse.getEntity(String.class)).parseObject();
		
		//pegamos o valor da resposta
		clientResponse.close();
		
		//integer de nome total para dizer se retornou o cliente ou nao
		//ou seja se o cliente existe ou nao
		//
		Integer total = Integer.parseInt(
				parser.get("totalCount").toString());
		
		//se o total de cliente com esse e-mail for MENOR OU IGUAL A 0
		//entao tem q criar esse CLIENTE
		
		/*criar cliente*/
		if(total <= 0) {
			//se o cliente nao existir, vamos criar ele, 
			//passando os dados para o OBJ/VAR de nome 
			//CLIENTEASAASAPIPAGAMENTO
			ClienteAsaasApiPagamento clienteAsaasApiPagamento = new ClienteAsaasApiPagamento();
			
			//verificando se o CPF e valido
			//se os dados q estamos recebendo GETPAYERCPFCNPJ
			//(PAYERCPFCNPJ = pessoa q vai pagar/cliente) do obj/var
			//DADOS tem um CPF valido
			if (!ValidaCPF.isCPF(dados.getPayerCpfCnpj())) {
				//se caso venha um cpf invalido, nos vamos usar
				//o cpf aleartorio a baixo 600...
				clienteAsaasApiPagamento.setCpfCnpj("60051803046");
			}else {
				//se o CPF for valido o CLIENTEASAASAPIPAGAMENTO
				//vai receber os dados do GETPAYERCPFCNPJ do DADOS
				clienteAsaasApiPagamento.setCpfCnpj(dados.getPayerCpfCnpj());
			}
			
			//como o CPF e valido vamos passar os outros dados
			//
			clienteAsaasApiPagamento.setEmail(dados.getEmail());
			clienteAsaasApiPagamento.setName(dados.getPayerName());
			clienteAsaasApiPagamento.setPhone(dados.getPayerPhone());
			
			
			
			// instanciando um CLIENT2 do tipo CLIENT
			//
			// informando q a url da API do ASAAS nao precisa de 
			//certificado ssl
			Client client2 = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
			
			// criando um var/obj dotipo WEBRESOURCE de nome WEBRESORUCE
			// q recebe a URL/LINK de onde deve ser feita a solicitacao
			//
			WebResource webResource2 = client2.
					resource(
							AsaasApiPagamentoStatus.URL_API_ASAAS + "customers");
			
									
			// criando um CLIENTRESPONSE2 do tipo CLIENTRESPONSE
			// q vai receber o retorno do metodo ACCEPT do WEBRESOURCE2
			// metodo no qual passamos
			// informando o tipo de formatacao, e montando o cabecalho
			// para fazer as requisicoes a API da Asaas
			// tbm informando o nosso token gerado pela asaas e tals
			// NAOSEI SE e um BEARER_TOKEN, mas e um token q a ASAAS GEROU
			// e tbm informamos para qual URL da asaas vai ser feita a
			// requisicao
			//
			//dai nos fazemos um POST informando o CLIENTRESPONSE q
			//e onde ta armazenado os cabecalhos, tokens, etc...
			//e informamos tbm o CLIENTEASAASAPIPAGAMENTO
			//q e onde tem as informacoes email, cpf, telefone do
			//cliente para a ASAAS pd associar uma COBRANCA a um CLIENTE
			//ou seja e enviado um JSON com as info do CLIENTE para a ASAAS
			ClientResponse clientResponse2 = webResource2.accept("application/json;charset=UTF-8")
					.header("Content-Type", "application/json")
					.header("access_token", AsaasApiPagamentoStatus.API_KEY)
					.post(ClientResponse.class, new ObjectMapper().writeValueAsBytes(clienteAsaasApiPagamento));
			
			
			//criando um LINKEDHAHMAP q vai retornar uma CHAVE e um OBJECT
			//para gente... Uma lista com chaves e valores
			//
			//no CLIENTEPARSE2 vai ter o retorno do cliente criado
			//na asaas
			LinkedHashMap<String, Object> parser2 = new JSONParser(clientResponse2.getEntity(String.class)).parseObject();
			clientResponse2.close();
			
			//pegando o ID do cliente q foi CRIADO na ASAAS
			//dai vamos associar o id a uma cobranca...
			customer_id = parser2.get("id").toString();
			
		}
		else {
			/*Já tem cliente cadastrado*/
			//
			//pegando a resposta do parser q é onde ta salvo
			//se existe ou nao cliente q vai ta dentro do DATA
			//dentro do parser... o DATA e um ARRAY...
			//vamos usar o LIST<OBJECT> para converter ele de ARRAY
			//para LIST de OBJECT
			//
			List<Object> data = (List<Object>) parser.get("data");
			//pegando o primeiro valor q no caso e o ID
			//do cliente na ASAAS... removendo as "" ASPAS
			customer_id = new Gson().toJsonTree(data.get(0))
					.getAsJsonObject().get("id").toString()
					.replaceAll("\"", "");
		}
		
		return customer_id;
		
	}
	
	//metodo de nomeCRIARCHAVEPIXASAAS... para criar CHAVEPIX
	//na API de PAGAMENTO ASAAS...
	/**
	 * Cria a chave da API Asass para o PIX;
	 * @return Chave
	 */
	public String criarChavePixAsaas() throws Exception {

		// instanciando um CLIENT do tipo CLIENT
		//
		// informando q a url da API do ASAAS nao precisa de certificado ssl
		Client client = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
		
		// criando um var/obj dotipo WEBRESOURCE de nome WEBRESORUCE
		// q recebe a URL/LINK de onde deve ser feita a solicitacao
		WebResource webResource = client.resource(AsaasApiPagamentoStatus.URL_API_ASAAS + "pix/addressKeys");
				
		// criando um CLIENTRESPONSE do tipo CLIENTRESPONSE
		// q vai receber o retorno do metodo ACCEPT do WEBRESOURCE
		// metodo no qual passamos
		// informando o tipo de formatacao, e montando o cabecalho
		// para fazer as requisicoes a API da Asaas
		// tbm informando o nosso token gerado pela asaas e tals
		// NAOSEI SE e um BEARER_TOKEN, mas e um token q a ASAAS GEROU
		// e tbm informamos para qual URL da asaas vai ser feita a
		// requisicao do tipo post
		//
		// o retorno sera uma chave pix aleartoria gerada pela asaas
		ClientResponse clientResponse = webResource.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("access_token", AsaasApiPagamentoStatus.API_KEY)
				.post(ClientResponse.class, "{\"type\":\"EVP\"}");

		String strinRetorno = clientResponse.getEntity(String.class);
		clientResponse.close();
		return strinRetorno;

		
		
		
	}
	
	

	
	
	//metodo para cancelar boleto e qrcode pix gerado... 
	//q recebe um ID/CODE em STRING
	//o CODE parece q é o ID_CHR_BOLETO... um tipo de ID dos boleto
	//gerado pela API da JUNO na hora q eles criam um boleto...
	//
	public String cancelarBoleto(String code) throws Exception {
		
		//instanciando o token
		AccessTokenJunoAPI accessTokenJunoAPI = this.obterTokenApiJuno();
		
		//informando q a url api.juno.com.br nao precisa de certificado ssl
		Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
		//criando um var/obj dotipo WEBRESOURCE de nome WEBRESORUCE
		//q recebe a URL/LINK de onde deve ser feita a solicitacao
		WebResource webResource = client.resource("https://api.juno.com.br/charges/"+code+"/cancelation");
		
		//passando os tokens e o CODE/ID recebido no inicio do metodo
		//para a URL q esta instanciada em WEBRESOURCE (url de onde ta
		//o metodo api da juno)... e passando as outras informacoes tbm...
		ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_JSON)
				.header("X-Api-Version", 2)
				.header("X-Resource-Token", ApiTokenIntegracao.TOKEN_PRIVATE_JUNO)
				.header("Authorization", "Bearer " + accessTokenJunoAPI.getAccess_token())
				.put(ClientResponse.class);
		
		//se o boleto for cancelado com sucesso vamos deletar o codigo
		//de barra dele do banco de dados
		if (clientResponse.getStatus() == 204) {
			
			boletoJunoRepository.deleteByCode(code);
			
			return "Cancelado com sucesso";
		}
		
		return clientResponse.getEntity(String.class);
		
	}
	
	
	
	//gerar carne/boleto/cobranca no ASAAS...
	//q recebe um OBJETOPOSTCARNEJUNO do tipo OBJETOPOSTCARNEJUNO
	//q tem atributos onde tem as informacoes relacionadas
	//a qm ta cobrando o cpf, telefone, parcelas, etc...
	//
	//embora esteja JUNO (OBJETOPOSTCARNEJUNO) sera usado pelo ASAAS
	//
	public String gerarCarneApiAsaas(ObjetoPostCarneJuno objetoPostCarneJuno) throws Exception {
		
		//procurando o ID da venda do objetoPostCarneJuno no nosso BANCO	
		VendaCompraLojaVirtual vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.findById(objetoPostCarneJuno.getIdVenda()).get();

		
		//
		//criando um OBJ/VAR de nome COBRANCAAPIASAAS do tipo
		//COBRANCAAPIASAAS q basicamente vai receber
		//as informacoes do CONSUMER... ou seja do CLIENTE/PESSOA
		//q ta comprando, a pessoa q vai pagar a cobranca
		//
		//SE o CUSTOMER/CLIENTE nao existir na ASAAS... Dai
		//a ASAAS vai receber o NOME, CPF, EMAIL, etc...
		//e a ASAAS vai criar esse CLIENTE, para a ASAAS pd
		//associar uma COBRANCA a PESSOA... tipo Boleto ABC foi
		//gerado para o CLIENTE FULANO...
		//
		//Tipo se a pessoa tiver conta na LOJAVIRTUALMENTORIA
		//mas nao tiver na ASAAS dai a LOJAVIRUTUALMENTORIA
		//vai pegar as INFO e-mail/cpf/nome, etc... e passar
		//para a ASAAS pd criar uma conta na asaas tbm...
		CobrancaApiAsaas cobrancaApiAsaas = new CobrancaApiAsaas();
		cobrancaApiAsaas.setCustomer(this.buscaClientePessoaApiAsaas(objetoPostCarneJuno));
		
		
		
		
		/*PIX, BOLETO OU UNDEFINED*/
		//tipo do pagamento, boleto, pix...
		cobrancaApiAsaas.setBillingType("UNDEFINED"); /*Gerando tanto PIX quanto Boleto*/
		cobrancaApiAsaas.setDescription("Pix ou Boleto gerado para ao cobrança, cód: " + vendaCompraLojaVirtual.getId());
		//passando para o obj, cobrancaapiasaas o valor da compra
		//q esta sendo feita na lojavirtualmentoria
		cobrancaApiAsaas.setInstallmentValue(vendaCompraLojaVirtual.getValorTotal().floatValue());
		cobrancaApiAsaas.setInstallmentCount(1);
		
		//instanciando um OBJ/VAR de nome DAVENCIMENTO
		//do tipo CALENDAR
		//q basicamente vai receber a DATA de HJ + 7 DIAS
		//ou seja esse e o TEMPO q o CLIENTE tem para pagar
		//a cobranca
		Calendar daVencimento = Calendar.getInstance();
		daVencimento.add(Calendar.DAY_OF_MONTH, 7);
		cobrancaApiAsaas.setDueDate(
				new SimpleDateFormat("yyyy-MM-dd")
				.format(daVencimento.getTime()));
		
		
		//passando para o nosso VAR/OBJ COBRANCAAPIASAAS
		//se tem DESCONTO ou JUROS...
		cobrancaApiAsaas.getInterest().setValue(1F);
		cobrancaApiAsaas.getFine().setValue(1F);
		
		
		//convertendo o OBJ/VAR COBRANCAAPIASSAS para um JSON
		//para podermos enviar para API da ASAAS
		//
		//para isso vamos chamar METODO WRITEVALUEASSTRING do 
		//OBJECTMAPPER e passar para ele o
		//obj/var COBRANCAAPIASAAS q e um OBJ/VAR do TIPO
		//POSTCARNEBOLETOJUNO o retorno vai ficar salva no
		//STRING de nome JSON
		//
		String json  = new ObjectMapper().writeValueAsString(cobrancaApiAsaas);
		//pelo o q eu entendi e para ignorar o SSL
		Client client = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
		//URL/LINK onde da API da ASAAS para GERAR CARNE/COBRANCA/PAGAMENTOS
		WebResource webResource = client.resource(AsaasApiPagamentoStatus.URL_API_ASAAS + "payments");
		
		
		//criando um CLIENTRESPONSE do tipo CLIENTRESPONSE q recebe
		//o var/obj WEBRESOURCE criado acima... e a ele atribuindo
		//alguns cabecalhos tipo FORMATACAO UTF8, FORMATO JSON
		//passando o TOKEN gerado pela ASAAS, e tbm
		//vamos passar o COBRANCAAPIASAAS CONVERTIDO para JSON
		//ficando como STRING JSON...
		ClientResponse clientResponse = webResource
				.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("access_token", AsaasApiPagamentoStatus.API_KEY)
				.post(ClientResponse.class, json);
		
		//aqui vamos armazenar o RETORNO da API ASAAS apos o POST da linha
		//acima
		String stringRetorno = clientResponse.getEntity(String.class);
		clientResponse.close();
		
		
		/*Buscando parcelas geradas - para salvar elas no BANCO
		 * da LOJAVIRTUALMENTORIA*/	
		
		//
		//aparentemente aqui estamos montando uma lista de parcelas
		//para quando buscar na api os pagamento se for
		//parcelado vai trazer uma lista de parcelas
		LinkedHashMap<String, Object> parser = new JSONParser(stringRetorno).parseObject();
		String installment = parser.get("installment").toString();
		
		//ignorando ssl, e informando o link para fazer a requisicao
		Client client2 = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
		WebResource webResource2 = client2.resource(AsaasApiPagamentoStatus.URL_API_ASAAS + "payments?installment=" + installment);
		//montando o cabecalho
		ClientResponse clientResponse2 = webResource2
				.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("access_token", AsaasApiPagamentoStatus.API_KEY)
				.get(ClientResponse.class);
		
		
		String retornoCobrancas = clientResponse2.getEntity(String.class);
		
		//instanciando um OBJECTMAPPER
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		
		//convertendo a STRING RETORNOCOBRANCAS...(q a asaas retornou)
		//para um OBJ/VAR de nome LISTACOBRANCA do tipo 
		//COBRANCAGERADAASASSAPI... EU ACHO
		CobrancaGeradaAsassApi listaCobranca = objectMapper
				.readValue(retornoCobrancas, new TypeReference<CobrancaGeradaAsassApi>() {});
		
		
		//criando uma LISTA do TIPO BOLETOJUNO de nome BOLETOJUNOS
		//isso acontece pq pd ser q o retorno acima seja
		//MAIS de uma cobranca... Pois foi parcelado...
		List<BoletoJuno> boletoJunos = new ArrayList<BoletoJuno>();
		//quantidade de parcelas
		int recorrencia = 1;
		
		//
		//instanciando um obj/var COBRANCAGERADAASSASDATA de nome
		//DATA q serve para gravar as info de cobranca como descricao
		//parcelas, status, etc..
		for (CobrancaGeradaAssasData data : listaCobranca.getData()) {
			
			//criando um BOLETOJUNO q na VDD e um BOLETOASAAS
			//do tipo BOLETOJUNO
			BoletoJuno boletoJuno = new BoletoJuno();
			
			//preenchendo os dados do BOLETOJUNO/BOLETOASSAS
			boletoJuno.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
			boletoJuno.setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
			boletoJuno.setCode(data.getId());
			boletoJuno.setLink(data.getInvoiceUrl());
			boletoJuno.setDataVencimento(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(data.getDueDate())));
			boletoJuno.setCheckoutUrl(data.getInvoiceUrl());
			boletoJuno.setValor(new BigDecimal(data.getValue()));
			boletoJuno.setIdChrBoleto(data.getId());
			boletoJuno.setInstallmentLink(data.getInvoiceUrl());
			boletoJuno.setRecorrencia(recorrencia);

			//boletoJuno.setIdPix(c.getPix().getId());
			
			//criando um OBJ/VAR de nome CODEPIXASAAS do tipo
			//OBJETOQRCODEPIXASAAS
			//q vai ficar salvo o QRCODE PIX gerado pela ASAAS
			ObjetoQrCodePixAsaas codePixAsaas = this.buscarQrCodeCodigoPix(data.getId());
			
			//associando ao OBJ/VAR BOLETOJUNO o 
			//payload q e o codigo copia e cola do pix gerado pela asaas
			boletoJuno.setPayloadInBase64(codePixAsaas.getPayload());
			//associando a var/obj BOLETOJUNO uma imagem de QRCODE
			//do pix q a asaas gerou
			boletoJuno.setImageInBase64(codePixAsaas.getEncodedImage());
			
			boletoJunos.add(boletoJuno);
			recorrencia ++;
		}
		
		//salvando os boletos no BANCO da LOJAVIRTUALMENTORIA
		boletoJunoRepository.saveAllAndFlush(boletoJunos);
		
		return boletoJunos.get(0).getCheckoutUrl();
		
		
	}
	
	//metodo de nome BUSCARQRCODECODIGOPIX... Q recebe
	//um ID de cobranca
	//e a aprtir disso vai retornar o qrcode gerado pela asaas
	public ObjetoQrCodePixAsaas buscarQrCodeCodigoPix(String idCobranca) throws Exception {
		
		//informando q pd ignorar ssl
		Client client = new HostIgnoringCliente(AsaasApiPagamentoStatus.URL_API_ASAAS).hostIgnoringCliente();
		//informando o link da api da asaas
		WebResource webResource = client.resource(AsaasApiPagamentoStatus.URL_API_ASAAS + "payments/"+idCobranca +"/pixQrCode");
		
		//preenchendo cabecalho, e aparentemente vai fazer uma requisicao
		//para a asaas informando um CODIGO de venda dai a asaas
		//retorna o qrcode PIX  de acordo com o codigo de venda passado...
		ClientResponse clientResponse = webResource
				.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("access_token", AsaasApiPagamentoStatus.API_KEY)
				.get(ClientResponse.class);
		
		String stringRetorno = clientResponse.getEntity(String.class);
		clientResponse.close();
		
		ObjetoQrCodePixAsaas codePixAsaas = new ObjetoQrCodePixAsaas();
		
		LinkedHashMap<String, Object> parser = new JSONParser(stringRetorno).parseObject();
		codePixAsaas.setEncodedImage(parser.get("encodedImage").toString());
		codePixAsaas.setPayload(parser.get("payload").toString());
		
		return codePixAsaas;
	}
	
	
	
	//metodo para gerar os boleto e pix... q recebe um OBJ do tipo OBJETOPOSTCARNERJUNO
	//
	public String gerarCarneApi(ObjetoPostCarneJuno objetoPostCarneJuno) throws Exception {
		
		VendaCompraLojaVirtual vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.findById(objetoPostCarneJuno.getIdVenda()).get();
		
		//instanciando um obj do tipo cobrancajunoapi
		CobrancaJunoAPI cobrancaJunoAPI = new CobrancaJunoAPI();
		
		//passando os valores para os atributos
		//chave pix, descricao, valor, etc...
		cobrancaJunoAPI.getCharge().setPixKey(ApiTokenIntegracao.CHAVE_BOLETO_PIX);
		cobrancaJunoAPI.getCharge().setDescription(objetoPostCarneJuno.getDescription());
		cobrancaJunoAPI.getCharge().setAmount(Float.valueOf(objetoPostCarneJuno.getTotalAmount()));
		cobrancaJunoAPI.getCharge().setInstallments(Integer.parseInt(objetoPostCarneJuno.getInstallments()));
		
		//data do boleto/pix....
		Calendar dataVencimento = Calendar.getInstance();
		dataVencimento.add(Calendar.DAY_OF_MONTH, 7);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
		cobrancaJunoAPI.getCharge().setDueDate(dateFormat.format(dataVencimento.getTime()));
		
		//juros, tempo q pd ser pago apos o vencimento, etc...
		cobrancaJunoAPI.getCharge().setFine(BigDecimal.valueOf(1.00));
		cobrancaJunoAPI.getCharge().setInterest(BigDecimal.valueOf(1.00));
		cobrancaJunoAPI.getCharge().setMaxOverdueDays(10);
		cobrancaJunoAPI.getCharge().getPaymentTypes().add("BOLETO_PIX");
		
		//nome, documento, e-mail, etc...
		cobrancaJunoAPI.getBilling().setName(objetoPostCarneJuno.getPayerName());
		cobrancaJunoAPI.getBilling().setDocument(objetoPostCarneJuno.getPayerCpfCnpj());
		cobrancaJunoAPI.getBilling().setEmail(objetoPostCarneJuno.getEmail());
		cobrancaJunoAPI.getBilling().setPhone(objetoPostCarneJuno.getPayerPhone());
		
		//verificando se tem token, para pd enviar os daos para API da JUNO
		//para pd criar um boleto/pix...
		AccessTokenJunoAPI accessTokenJunoAPI = this.obterTokenApiJuno();
		if (accessTokenJunoAPI != null) {
			
			//ignorar ssl/certificado para o link api.juno.com.br...
			Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
			//as requisicoes vao ser feitas para a api q ta no link
			//api.juno.com.br/charges
			WebResource webResource = client.resource("https://api.juno.com.br/charges");
			
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(cobrancaJunoAPI);
			
			//criando o cabecalho passando a solicitacao + o token privado + o bearer
			//token gerado pela juno... e mais o nosso OBJ JSON do tipo objectmapper 
			//q tem o as informacoes q foram adicionadas no OBJ 
			//COBRANCAJUNOAPI convertidas no formato JSON......
			ClientResponse clientResponse = webResource
					.accept("application/json;charset=UTF-8")
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("X-API-Version", 2)
					.header("X-Resource-Token", ApiTokenIntegracao.TOKEN_PRIVATE_JUNO)
					.header("Authorization", "Bearer " + accessTokenJunoAPI.getAccess_token())
					.post(ClientResponse.class, json);
			
			String stringRetorno = clientResponse.getEntity(String.class);
			
			if (clientResponse.getStatus() == 200) { /*Retornou com sucesso*/
				
				clientResponse.close();
				objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY); /*Converte relacionamento um para muitos dentro de json*/
				
				//o retorno do metodo acima, apos passarmos as informacoes
				//para a API da juno criar o boleto, nos vamos salvar o retorno
				//da api da juno q sera um boleto... vamos salvar
				//em um obj/var de nome JSONRETORNOOBJ do tipo
				//BOLETOGERADOAPIJUNO :)
				BoletoGeradoApiJuno jsonRetornoObj = objectMapper.readValue(stringRetorno,
						   new TypeReference<BoletoGeradoApiJuno>() {});
				
				int recorrencia = 1;
				
				//lista de boletojuno
				List<BoletoJuno> boletoJunos = new ArrayList<BoletoJuno>();
				
				//for para percorrer BOLETO (C) por BOLETO... Pois mtas vezes
				//e retornado pela api uma LISTA de boleto...
				for (ConteudoBoletoJuno c : jsonRetornoObj.get_embedded().getCharges()) {
					
					//instanciando um BOLETOJUNO
					BoletoJuno boletoJuno = new BoletoJuno();
					
					//preenchendo as informacoes desse boleto
					boletoJuno.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
					boletoJuno.setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
					boletoJuno.setCode(c.getCode());
					boletoJuno.setLink(c.getLink());
					boletoJuno.setDataVencimento(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(c.getDueDate())));
					boletoJuno.setCheckoutUrl(c.getCheckoutUrl());
					boletoJuno.setValor(new BigDecimal(c.getAmount()));
					boletoJuno.setIdChrBoleto(c.getId());
					boletoJuno.setInstallmentLink(c.getInstallmentLink());
					boletoJuno.setIdPix(c.getPix().getId());
					boletoJuno.setPayloadInBase64(c.getPix().getPayloadInBase64());
					boletoJuno.setImageInBase64(c.getPix().getImageInBase64());
					boletoJuno.setRecorrencia(recorrencia);
					
					boletoJunos.add(boletoJuno);
					recorrencia ++;
					
				}
				
				//salvando o boleteo no banco...
				boletoJunoRepository.saveAllAndFlush(boletoJunos);
				
				return boletoJunos.get(0).getLink();
				
			}else {
				return stringRetorno;
			}
			
		}else {
			return "Não exite chave de acesso para a API";
		}
		
	}
	
	
	
	
	
	//metodo para permitir gerar chave de autorizacao (um token) 
	//para permitir gerar chave pix com a juno api
	public String geraChaveBoletoPix() throws Exception {
		
		//obtendo um token... q e o bearer token...
		AccessTokenJunoAPI accessTokenJunoAPI = this.obterTokenApiJuno();
		
		//ignorarndo certificado ssl para esse link
		Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
		
		//o webresource a baixo aparentemente vai receber o link de onde
		//e gerado a chave pix... eu acho...
		WebResource webResource = client.resource("https://api.juno.com.br/pix/keys");
		//WebResource webResource = client.resource("https://api.juno.com.br/api-integration/pix/keys");
		
		//em resumo montando o cabecalho http para fazer a requisicao...
		//instanciando o obj webresource q ja tem o link
		//e passando para ele outros paramenetros para fazer a requisicao
		//
		//o token privado e um token q a juno gera e q
		//nos pegamos no site da juno... tbm temos q passar o bearer token
		//RANDOM_KEY e pq estamos pedindo para gerar um chave de pix
		//randomica...
		ClientResponse clientResponse = webResource
				.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("X-API-Version", 2)
				.header("X-Resource-Token", ApiTokenIntegracao.TOKEN_PRIVATE_JUNO)
				.header("Authorization", "Bearer " + accessTokenJunoAPI.getAccess_token())
				.post(ClientResponse.class, "{ \"type\": \"RANDOM_KEY\" }");
		
		         //.header("X-Idempotency-Key", "chave-boleto-pix")
		return clientResponse.getEntity(String.class);
		
		
	}
	
	
	
	
	
	//metodo para passar ou gerar token bearer da api juno...
	//
	public AccessTokenJunoAPI obterTokenApiJuno() throws Exception {
		
		//verificando se tem token ativo na API da JUNO
		AccessTokenJunoAPI accessTokenJunoAPI = accessTokenJunoService.buscaTokenAtivo();
		
		//se nao tiver (ou expirado) vamos passar o CLIENTID e SECRETID 
		//gerado pela API JUNO nas configuracoes para conseguir um novo token
		if (accessTokenJunoAPI == null || (accessTokenJunoAPI != null && accessTokenJunoAPI.expirado()) ) {
			
			String clienteID = "vi7QZerW09C8JG1o";
			String secretID = "$A_+&ksH}&+2<3VM]1MZqc,F_xif_-Dc";
			
			//acho q estamos removendo necessidade de certificado ssl
			Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
			//informando qual sera o link q nao precisaremos de certificado ssl
			//q sera o link onde iremos passar o CLIENTID e SECRETID
			WebResource webResource = client.resource("https://api.juno.com.br/authorization-server/oauth/token?grant_type=client_credentials");
			
			//var/obj do tipo STRING de nome BASICCHAVE q reecbe o
			//CLIENT_ID + SECRETID
			String basicChave = clienteID + ":" + secretID;
			//
			//convertendo o clientid e secretid para base64 e salvando
			//na var string TOKEN_AUTENTICACAO
			String token_autenticao = DatatypeConverter.printBase64Binary(basicChave.getBytes());
			
			//montando um cabecalho de req http
			//e passando o CLIENTID e SECRETID q ta na var
			//TOKEN_AUTENTICACAO... Para a API da JUNO gerar um novo
			//BEARER TOKEN
			ClientResponse clientResponse = webResource.
					accept(MediaType.APPLICATION_FORM_URLENCODED)
					.type(MediaType.APPLICATION_FORM_URLENCODED)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.header("Authorization", "Basic " + token_autenticao)
					.post(ClientResponse.class);
			
			//se conseguiu um novo token com a juno apos
			//passar o client id e secret id
			//vamos deletar o token antigo do banco...
			//pq o antigo ja expirou...
			if (clientResponse.getStatus() == 200) { /*Sucesso*/
				accesTokenJunoRepository.deleteAll();
				accesTokenJunoRepository.flush();
				
				AccessTokenJunoAPI accessTokenJunoAPI2 = clientResponse.getEntity(AccessTokenJunoAPI.class);
				accessTokenJunoAPI2.setToken_acesso(token_autenticao);
				
				accessTokenJunoAPI2 = accesTokenJunoRepository.saveAndFlush(accessTokenJunoAPI2);
				
				return accessTokenJunoAPI2;
			}else {
				return null;
			}
			
			//se caso ja tinha um token e nao precisou passar o CLIENTID
			//e CLIENTSECRET dai so vai retornar o ACCESTOKENJUNOAPI a baixo
		}else {
			return accessTokenJunoAPI;
		}
	}
	
	/*
	 * {"id":"wbh_AE815607C1F5A94934934A2EA3CA0180","url":"https://lojavirtualmentoria-env.eba-bijtuvkg.sa-east-1.elasticbeanstalk.com/loja_virtual_mentoria/requisicaojunoboleto/notificacaoapiv2","secret":"23b85f4998289533ed3ee310ae9d5bd3f803fadac7fb1ecff0296fbf1bb060f8","status":"ACTIVE","eventTypes":[{"id":"evt_DC2E7E8848B08C62","name":"DOCUMENT_STATUS_CHANGED","label":"O status de um documento foi alterado","status":"ENABLED"}],"_links":{"self":{"href":"https://api.juno.com.br/api-integration/notifications/webhooks/wbh_AE815607C1F5A94934934A2EA3CA0180"}}}
	 * 
	 * */
	//
	//metodo CRIARWEBHOOK... basicamente um WEBHOOK é um METODO q
	//sera ACESSADO pela API da JUNO... Ou seja a JUNO vai fazer requisicoes
	//para esse metodo... Por isso  colocamoso sistema em uma hospedagem
	//
	//ou seja em vez de nos se conectar na JUNO a JUNO vai se conectar
	//no nosso sistema...
	//
	//tipo uma compra teveo cartao nao aprovado...
	//dai a JUNO acessa a nossa API/WEBHOOK e envia uma informacao
	//dizendo q deu problema no pagamento por cartao... +ou- isso...
	//
	//aqui estamos recebendo um OBJ/VAR do tipo CRIARWEBHOOK de nome
	//CRIARWEBHOOK e nele tem o nosso link da LOJAVIRTUAL na AWS
	//para a JUNO se conectar...
	//
	//e vamos enviar essa informacao para a JUNO saber onde se conectar
	//para ela nos enviar informacoes se tipo o pagamento de uma compra ta ok
	public String criarWebHook(CriarWebHook criarWebHook) throws Exception {
		
	    AccessTokenJunoAPI accessTokenJunoAPI = this.obterTokenApiJuno();
		
		Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
		WebResource webResource = client.resource("https://api.juno.com.br/notifications/webhooks");
		
		String json = new ObjectMapper().writeValueAsString(criarWebHook);
		
		
		//enviando para a JUNO o nosso URL da AWS e o link certinho
		//para ela acessar o nosso WEBHOOK
		ClientResponse clientResponse = webResource
				.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("X-API-Version", 2)
				.header("X-Resource-Token", ApiTokenIntegracao.TOKEN_PRIVATE_JUNO)
				.header("Authorization", "Bearer " + accessTokenJunoAPI.getAccess_token())
				.post(ClientResponse.class, json);
		
		 String resposta = clientResponse.getEntity(String.class);
		 clientResponse.close();
		
		return resposta;
		
	}
	
	
	//metodo para listar todos os webhooks da aplicacao loja_virtual_mentoria
	//
	public String listaWebHook() throws Exception {
		
		//pegando o nosso token da juno
	    AccessTokenJunoAPI accessTokenJunoAPI = this.obterTokenApiJuno();
		
	    //instanciando os obj client e webresource e passando para eles
	    //os links da juno
		Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
		WebResource webResource = client.resource("https://api.juno.com.br/notifications/webhooks");
		
		//monstando a requisicao em json q vamos fazer para
		//a juno
		ClientResponse clientResponse = webResource
				.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("X-API-Version", 2)
				.header("X-Resource-Token", ApiTokenIntegracao.TOKEN_PRIVATE_JUNO)
				.header("Authorization", "Bearer " + accessTokenJunoAPI.getAccess_token())
				.get(ClientResponse.class);
		
		//armazenando o retorno da api da juno 
		//com os nossos webhooks q a api da juno tem acesso
		//na var/obj do tipo string de nome resposta
		 String resposta = clientResponse.getEntity(String.class);
		 
		return resposta;
		
	}
	
	
	//metodo para deletar os webhooks da nossa aplicacao
	//loja_virtual_mentoria... basicamente webhook sao metodos/endpoint
	//q a nossa aplicacao fornece para a juno... e a juno insere
	//informacoes na nossa loja_virtual_mentoria atraves desses
	//metodos/endpoint... tipo a juno avisa q o pagamento por
	//cartao de credito esta ok... entao a loja_virtual_mentoria
	//pode permitir a compra do produto... ja q o pagamento por
	//cartao de credito ficou ok
	public void deleteWebHook(String idWebHook) throws Exception {
		
	    AccessTokenJunoAPI accessTokenJunoAPI = this.obterTokenApiJuno();
		
		Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
		WebResource webResource = client.resource("https://api.juno.com.br/notifications/webhooks/" + idWebHook);
		
		webResource
				.accept("application/json;charset=UTF-8")
				.header("Content-Type", "application/json")
				.header("X-API-Version", 2)
				.header("X-Resource-Token", ApiTokenIntegracao.TOKEN_PRIVATE_JUNO)
				.header("Authorization", "Bearer " + accessTokenJunoAPI.getAccess_token())
				.delete();
		
		
	}

	

}
