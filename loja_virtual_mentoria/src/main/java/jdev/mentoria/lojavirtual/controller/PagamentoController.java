package jdev.mentoria.lojavirtual.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import jdev.mentoria.lojavirtual.enums.ApiTokenIntegracao;
import jdev.mentoria.lojavirtual.model.AccessTokenJunoAPI;
import jdev.mentoria.lojavirtual.model.BoletoJuno;
import jdev.mentoria.lojavirtual.model.VendaCompraLojaVirtual;
import jdev.mentoria.lojavirtual.model.dto.BoletoGeradoApiJuno;
import jdev.mentoria.lojavirtual.model.dto.CobrancaJunoAPI;
import jdev.mentoria.lojavirtual.model.dto.ConteudoBoletoJuno;
import jdev.mentoria.lojavirtual.model.dto.ErroResponseApiJuno;
import jdev.mentoria.lojavirtual.model.dto.PagamentoCartaoCredito;
import jdev.mentoria.lojavirtual.model.dto.PaymentsCartaoCredito;
import jdev.mentoria.lojavirtual.model.dto.RetornoPagamentoCartaoJuno;
import jdev.mentoria.lojavirtual.model.dto.VendaCompraLojaVirtualDTO;
import jdev.mentoria.lojavirtual.repository.BoletoJunoRepository;
import jdev.mentoria.lojavirtual.repository.Vd_Cp_Loja_virt_repository;
import jdev.mentoria.lojavirtual.service.HostIgnoringCliente;
import jdev.mentoria.lojavirtual.service.ServiceJunoBoleto;
import jdev.mentoria.lojavirtual.service.VendaService;
import jdev.mentoria.lojavirtual.util.ValidaCPF;


//controller/resource pagamento controller de nome PAGAMENTOCONTROLLER
//q tem varias var/atributos q terao os seus valores informados
//a partir das informacaoes q vao vim do PAGAMENTO.HTML
@Controller
public class PagamentoController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private Vd_Cp_Loja_virt_repository vd_Cp_Loja_virt_repository;
	
	@Autowired
	private VendaService vendaService; 
	
	@Autowired
	private ServiceJunoBoleto serviceJunoBoleto; 
	
	@Autowired
	private BoletoJunoRepository boletoJunoRepository; 
	
	//criando um metodo de nome PAGAMENTO do tipo 
	//GET na url pagamento/idvendacompra
	//o retorno dele e do tipo MODELANDVIEW, pois sera exibido em uma
	//pagina web... com o THIMELIFT (MVC) quando o backend
	//e java e spring e o frontend e html...
	//
	//com o ID da VENDACOMPRALOJAVIRTUAL q recebemos
	//vamos saber a QUAL VENDACOMPRALOJAVIRTUAL as informacoes
	//de pagamento(cartao de credito, pix) estao associadas...
	//
	@RequestMapping(method = RequestMethod.GET, value = "**/pagamento/{idVendaCompra}")
	public ModelAndView pagamento(@PathVariable(value = "idVendaCompra",
								 required = false) String idVendaCompra) {
		
		//instanciando um obj de nome MODELANDVIEW do tipo
		//MODELANDVIEW, e nele nos passamos o PAGAMENTO
		//q e a pagina PAGAMENTO.HTML q ta no /RESOURCES/TEMPLATES
		ModelAndView modelAndView = new ModelAndView("pagamento");
		
		//criando um obj do tipo COMPRALOJAVIRTUAL
		//(pd seria ser VENDACOMPRALOJAVIRTUAL)
		//q vai receber o retorno do REPOSITORY VC_CP_LOJA_VIRT_REPOSITORY
		//no qual passamos o ID de compra q veio como parametro no metodo/endpoint
		//acima... o endpoit/metodo pagamento
		VendaCompraLojaVirtual compraLojaVirtual = vd_Cp_Loja_virt_repository.findByIdExclusao(Long.parseLong(idVendaCompra));
		
		//verificando se apos a busca no banco de dados o 
		//compralojavirtual nao e null, ou seja se encontrou algo
		if (compraLojaVirtual == null) {
			modelAndView.addObject("venda", new VendaCompraLojaVirtualDTO());
		}else {
			modelAndView.addObject("venda", vendaService.consultaVenda(compraLojaVirtual));
		}
        
		//retornandoo modelandview q tras a nossa pagina
		//PAGAMENTO.HTML q ta em resources/templates...
		return modelAndView;
	}
	
	//metodo q recebe as informacoes q estao nos atributos/var
	//da PAGINA.HTML... foi enviado pelo AJAX
	//dai vamos validar esses dados para ver se ta tudo certinho
	//e vamos enviar eles para a Juno
	//
	//pelo o q eu prof falou as empresas de pagamento (juno, asaas, etc...)
	//fazem assim elas criam um PAGAMENTO do tipo BOLETO
	//dai se nos escolhemos pagar com CARTAO dai ela usa o CARTAO para pagar
	//o BOLETO... se escolhemos PIX a empresa usa o PIX para pagar o BOLETO
	//entao pelo o q eu entendi do q o prof falou sempre sera gerado um BOLETO
	//o q muda e a forma como o boleto vai ser pago... 
	//
	//PROF FALOU ISSO na AULA 11.24... API CHECKOUT ENDPOINT MONTANDO
	//COBRANCA PARTE 17
	//
	@RequestMapping(method = RequestMethod.POST, value = "**/finalizarCompraCartao")
	public ResponseEntity<String> finalizarCompraCartao(
			@RequestParam("cardHash") String cardHash,
			@RequestParam("cardNumber") String cardNumber,
			@RequestParam("holderName") String holderName,
			@RequestParam("securityCode") String securityCode,
			@RequestParam("expirationMonth") String expirationMonth,
			@RequestParam("expirationYear") String expirationYear,
			@RequestParam("idVendaCampo") Long idVendaCampo,
			@RequestParam("cpf") String cpf,
			@RequestParam("qtdparcela") Integer qtdparcela,
			@RequestParam("cep") String cep,
			@RequestParam("rua") String rua,
			@RequestParam("numero") String numero,
			@RequestParam("estado") String estado,
			@RequestParam("cidade") String cidade) throws Exception{
		
		
		//com a var/atributo IDVENDACAMPO q veio do frontend(PAGAMENTO.HTML)
		//vamos buscar de QUAL VENDACOMPRALOJAVIRTUAL q sera esse pagamento
		//pois com o ID do VENDACOMPRALOJAVIRTUAL, podemos saber 
		//qm e o COMPRADOR, ENDERECO, TRANSPORTE, PRODUTO, PRECO, etc...
		//
		VendaCompraLojaVirtual vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.
				                                    findById(idVendaCampo).orElse(null);
		
		if (vendaCompraLojaVirtual == null) {
			return new ResponseEntity<String>("Código da venda não existe!", HttpStatus.OK);
		}
		
		//verificando se os atributos estao com os dados ok
		//alguns vieram do frontend (PAGAMENTO.HTML) e outros do
		//VENDACOMPRALOJAVIRTUAL
		String cpfLimpo =  cpf.replaceAll("\\.", "").replaceAll("\\-", "");
		
		if (!ValidaCPF.isCPF(cpfLimpo)) {
			return new ResponseEntity<String>("CPF informado é inválido.", HttpStatus.OK);
		}
		
		
		if (qtdparcela > 12 || qtdparcela <= 0) {
			return new ResponseEntity<String>("Quantidade de parcelar deve ser de  1 até 12.", HttpStatus.OK);
		}
		
		if (vendaCompraLojaVirtual.getValorTotal().doubleValue() <= 0) {
			return new ResponseEntity<String>("Valor da venda não pode ser Zero(0).", HttpStatus.OK);
		}
		
		//pegandoo token JWT da juno (EU ACHO)
		 AccessTokenJunoAPI accessTokenJunoAPI = serviceJunoBoleto.obterTokenApiJuno();
		 
		 if (accessTokenJunoAPI == null) {
			 return new ResponseEntity<String>("Autorização bancária não foi encontrada.", HttpStatus.OK);
		 }
		 
		 //instanciando um obj do tipo COBRANCAJUNOAPI
		 CobrancaJunoAPI cobrancaJunoAPI = new CobrancaJunoAPI();
		 //gerando uma cobranca do tipo BOLETO q podera ser paga
		 //com o CARTAO DE CREDITO ou PIX... indepedente de como for o pagamento
		 //pelo o q o prof falou sempre sera gerado um boleto... o q muda
		 //e como sera pago o boleto, se sera com cartao, pix ou eu ler
		 //o cogido de barra e pagar o boleto...
		 cobrancaJunoAPI.getCharge().setPixKey(ApiTokenIntegracao.CHAVE_BOLETO_PIX);
		 cobrancaJunoAPI.getCharge().setDescription("Pagamento da venda: " + vendaCompraLojaVirtual.getId() + " para o cliente: " + vendaCompraLojaVirtual.getPessoa().getNome());
		 
		 //verificando se e avista ou parcelado
		 if (qtdparcela == 1) {
			 cobrancaJunoAPI.getCharge().setAmount(vendaCompraLojaVirtual.getValorTotal().floatValue());
		 }else {
			BigDecimal valorParcela = vendaCompraLojaVirtual.getValorTotal().divide(BigDecimal.valueOf(qtdparcela), RoundingMode.DOWN).setScale(2, RoundingMode.DOWN); 
			cobrancaJunoAPI.getCharge().setAmount(valorParcela.floatValue());
		 }
		 //passando a quantidade de parcela para o nosso obj/var/atributo
		 //COBRANCAJUNOAPI
		 cobrancaJunoAPI.getCharge().setInstallments(qtdparcela);
		 
		 //definindo a data de vencimento das parcelas...
		 Calendar dataVencimento = Calendar.getInstance();
		 dataVencimento.add(Calendar.DAY_OF_MONTH, 7);
		 SimpleDateFormat dateFormater = new SimpleDateFormat("yyy-MM-dd");
		 cobrancaJunoAPI.getCharge().setDueDate(dateFormater.format(dataVencimento.getTime()));
		 //juros, tempo para pagar, etc...
		 cobrancaJunoAPI.getCharge().setFine(BigDecimal.valueOf(1.00));
		 cobrancaJunoAPI.getCharge().setInterest(BigDecimal.valueOf(1.00));
		 cobrancaJunoAPI.getCharge().setMaxOverdueDays(7);
		 cobrancaJunoAPI.getCharge().getPaymentTypes().add("CREDIT_CARD");
		 
		 //pegando as INFO dos atributos/var/obj do VENDACOMPRALOJAVIRTUAL
		 //e passando o nome, telefone, etc da pessoa q comprou o produto
		 //para COBRANCAJUNOAPI
		 cobrancaJunoAPI.getBilling().setName(holderName);
		 cobrancaJunoAPI.getBilling().setDocument(cpfLimpo);
		 cobrancaJunoAPI.getBilling().setEmail(vendaCompraLojaVirtual.getPessoa().getEmail());
		 cobrancaJunoAPI.getBilling().setPhone(vendaCompraLojaVirtual.getPessoa().getTelefone());
		 
		 //pegando as informacoes q foram preenchidas
		 //nos obj/var/atributo COBRANCAJUNOAPI
		 //e enviando(post) em formato de JSON para o link do
		 //endpoint/metodo da API JUNO...
		 //
		 //PARA PODER GERAR UMA COBRANCA... OU SEJA GERAR UM BOLETO...
		 //Q sera pago com PIX ou CARTAO (independente da forma o boleto
		 //e gerado)
			Client client = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
			WebResource webResource = client.resource("https://api.juno.com.br/charges");
			
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(cobrancaJunoAPI);
			
			ClientResponse clientResponse = webResource
					.accept("application/json;charset=UTF-8")
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("X-API-Version", 2)
					.header("X-Resource-Token", ApiTokenIntegracao.TOKEN_PRIVATE_JUNO)
					.header("Authorization", "Bearer " + accessTokenJunoAPI.getAccess_token())
					.post(ClientResponse.class, json);
			
			//armazenando o retorno da JUNO apos enviarmos o JSON
			//com o obj/var COBRANCAJUNOAPI...
		  String stringRetorno = clientResponse.getEntity(String.class);
		  
		  //se o retorno da juno for diferente de 200 significa q deu
		  //algum problema...
		  if (clientResponse.getStatus() != 200) {
			  
			  ErroResponseApiJuno jsonRetornoErro = objectMapper.
					  readValue(stringRetorno, new TypeReference<ErroResponseApiJuno>() {});
			  
			  return new ResponseEntity<String>(jsonRetornoErro.listaErro(), HttpStatus.OK);
			  
		  }
		  
		  //caso o codigo de retorno da juno tenha sido 200 entao deu ok
		  //dai vamos ter o retorno da juno certinho dentro do
		  //var/obj clientResponse
		  clientResponse.close();
		 
		  //serve para converter o obj/var STRINGRETORNO em um
		  //obj da class
		  objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		  
		  //EU ACHO EU ACHO q estamos pegando o retorno da API JUNO
		  //q esta no STRINGRETORNO e passando para o OBJ/VAR
		  //de nome JSONRETORNO do tipo BOLETOGERADOAPIJUNO...
		  BoletoGeradoApiJuno jsonRetorno = objectMapper.
				  readValue(stringRetorno, new TypeReference<BoletoGeradoApiJuno>() { });
		  
		  //recorrencia para caso seja MAIS de um BOLETOSJUNO...
		  //mesmo q seja cartao de credito e gerado boleto...
		  int recorrencia = 1;
	
		  //se for parcelado vai gerar uma lista de BOLETOSJUNO
		  List<BoletoJuno> boletosJuno = new ArrayList<BoletoJuno>();
		  
		  //pegando o q ta no JSONRETORNO q e um obj/var do tipo
		  //BOLETOGERADOAPIJUNO e passando para o OBJ/VAR C do tipo
		  //CONTEUDOBOLETOJUNO
		  for (ConteudoBoletoJuno c : jsonRetorno.get_embedded().getCharges()) {
			  
			  //instaniando um obj/var de nome BOLETOJUNO do tipo
			  //BOLETOJUNO
			  BoletoJuno boletoJuno = new BoletoJuno();
			  
			  //passando as INFO q estao nas var/obj do C do tipo
			  //CONTEUDOBOLETOJUNO para o OBJ/VAR BOLETOJUNO do tipo
			  //BOLETOJUNO
			  boletoJuno.setChargeICartao(c.getId());
			  boletoJuno.setCheckoutUrl(c.getCheckoutUrl());
			  boletoJuno.setCode(c.getCode());
			  boletoJuno.setDataVencimento(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyy-MM-dd").parse(c.getDueDate())));
			  boletoJuno.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
			  boletoJuno.setIdChrBoleto(c.getId());
			  boletoJuno.setIdPix(c.getPix().getId());
			  boletoJuno.setImageInBase64(c.getPix().getImageInBase64());
			  boletoJuno.setInstallmentLink(c.getInstallmentLink());
			  boletoJuno.setLink(c.getLink());
			  boletoJuno.setPayloadInBase64(c.getPix().getPayloadInBase64());
			  boletoJuno.setQuitado(false);
			  boletoJuno.setRecorrencia(recorrencia);
			  boletoJuno.setValor(new BigDecimal(c.getAmount()).setScale(2, RoundingMode.HALF_UP));
			  boletoJuno.setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
			  
			  //salvando o boleto q foi gerado pela juno no nosso
			  //banco de dados da lojavirtualmentoria
			  boletoJuno = boletoJunoRepository.saveAndFlush(boletoJuno);
			  
			  //se for mais de uma parcela, vai se repetido o processo
			  //tipo se for em 7 parcelas vamos ter q repetir o processo
			  //acima 7 vezes...
			  boletosJuno.add(boletoJuno);
			  recorrencia ++;
			  
		  }
		  
		  
		  if (boletosJuno == null || (boletosJuno != null && boletosJuno.isEmpty())) {
			  return new ResponseEntity<String>("O registro financeiro não pode ser criado para pagamento", HttpStatus.OK);
		  }
		  
		  
		  /**------------------------REALIZANDO PAGAMENTO POR CARTÃO-------------------------**/
		  
		  //o cartao de credito sera utilizado para pagar o boleto
		  //entao na lista de boleto JUNO vamos pegar apenas o primeiro
		  //boleto e o sistema vai pagar automaticamente todos
		  //com o cartao de credito +OU- ISSO...
		  BoletoJuno boletoJunoQuitacao = boletosJuno.get(0);
		  
		  //basicamente vamos setar os dados q vieram da
		  //tela PAGAMENTO.HTML e enviar para a API da JUNO... prof falou
		  //nas aula 11.27
		  PagamentoCartaoCredito pagamentoCartaoCredito = new PagamentoCartaoCredito();
		  pagamentoCartaoCredito.setChargeId(boletoJunoQuitacao.getChargeICartao());
		  pagamentoCartaoCredito.getCreditCardDetails().setCreditCardHash(cardHash);
		  pagamentoCartaoCredito.getBilling().setEmail(vendaCompraLojaVirtual.getPessoa().getEmail());
		  pagamentoCartaoCredito.getBilling().getAddress().setState(estado);
		  pagamentoCartaoCredito.getBilling().getAddress().setNumber(numero);
		  pagamentoCartaoCredito.getBilling().getAddress().setCity(cidade);
		  pagamentoCartaoCredito.getBilling().getAddress().setStreet(rua);
		  pagamentoCartaoCredito.getBilling().getAddress().setPostCode(cep.replaceAll("\\-", "").replaceAll("\\.", ""));
		  
		  //informando qual e a url onde ta a API da juno
		  //para passar os dados por partao... e acho q estamos
		  //removendo ou ignorarando o ssl algo assim...
			Client clientCartao = new HostIgnoringCliente("https://api.juno.com.br/").hostIgnoringCliente();
			WebResource webResourceCartao = clientCartao.resource("https://api.juno.com.br/payments");
			
			ObjectMapper objectMapperCartao = new ObjectMapper();
			//passando o nosso obj/var PAGAMENTOCARTAOCREDITO para
			//converter em uma STRING e armazenar na STRING var/obj JSONCARTAO
			String jsonCartao = objectMapperCartao.writeValueAsString(pagamentoCartaoCredito);
			
			System.out.println("--------Envio dados pagamento cartão-----------: "+ jsonCartao);
			
			//enviando as informacoes do cartao para a API da juno
			//pagar o boleto q ela gerou automaticamente por
			//CARTAO
			ClientResponse clientResponseCartao = webResourceCartao
					.accept("application/json;charset=UTF-8")
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("X-API-Version", 2)
					.header("X-Resource-Token", ApiTokenIntegracao.TOKEN_PRIVATE_JUNO)
					.header("Authorization", "Bearer " + accessTokenJunoAPI.getAccess_token())
					.post(ClientResponse.class, jsonCartao);
			
		  String stringRetornoCartao = clientResponseCartao.getEntity(String.class);
		  
		  System.out.println("--------Retorno dados pagamento cartão-----------: "+ stringRetornoCartao);
		  
		  //verificando se o o retorno da juno for diferente de 200
		  //ou seja erro...
		  if (clientResponseCartao.getStatus() != 200) {
			  
			  ErroResponseApiJuno erroResponseApiJuno = objectMapper.
					  readValue(stringRetornoCartao, new TypeReference<ErroResponseApiJuno>() {} );
			  
			    for (BoletoJuno boletoJuno : boletosJuno) {
					serviceJunoBoleto.cancelarBoleto(boletoJuno.getCode());
				}
			  
			  return new ResponseEntity<String>(erroResponseApiJuno.listaErro(), HttpStatus.OK);
		  }
		  
		//criando um obj/var RETORNOPAGAMENTOCARTAOJUNO do tipo RETORNOPAGAMENTOCARTAOJUNO
		//q basicamente vai servir para armazenar o retorno em JSON da api da juno 
		  RetornoPagamentoCartaoJuno retornoPagamentoCartaoJuno = objectMapperCartao.
				  readValue(stringRetornoCartao, new TypeReference<RetornoPagamentoCartaoJuno>() { });
		  
		  if (retornoPagamentoCartaoJuno.getPayments().size() <= 0) {
			  
			  for (BoletoJuno boletoJuno : boletosJuno) {
					serviceJunoBoleto.cancelarBoleto(boletoJuno.getCode());
				}
			  
			  return new ResponseEntity<String>("Nenhum pagamento foi retornado para processar.", HttpStatus.OK);
		  }
		  
		  //criando um obj/var de nome CARTAOCREDITO do tipo PAYMENTSCARTAOCREDITO
		  //q vai receber o o GETPAYMENTS do RETORNOPAGAMENTOCARTAOJUNO
		  //q tem o JSON armazenado com o retorno da JUNO...
		  PaymentsCartaoCredito cartaoCredito = retornoPagamentoCartaoJuno.getPayments().get(0);
		  
		  //vamos verificar o q veio no GETSTATUS... do CARTAOCREDITO.GETSTATUS
		  //(pagamento por cartao)
		  //se o retorno for diferente de CONFIRMADO vamos deletar
		  //o codigo de barra do boleto do banco dedados
		  //mesmo sendo pago por cartao de credito ele gera um boleto
		  //e a juno paga o boleto com o cartao de credito +ou- isso...
		  if (!cartaoCredito.getStatus().equalsIgnoreCase("CONFIRMED")) {
			  for (BoletoJuno boletoJuno : boletosJuno) {
				serviceJunoBoleto.cancelarBoleto(boletoJuno.getCode());
			}
		  }
		  
		  if (cartaoCredito.getStatus().equalsIgnoreCase("DECLINED")) {
			  return new ResponseEntity<String>("Pagamento rejeito pela análise de risco", HttpStatus.OK);
		  }else  if (cartaoCredito.getStatus().equalsIgnoreCase("FAILED")) {
			  return new ResponseEntity<String>("Pagamento não realizado por falha", HttpStatus.OK);
		  }
		  else  if (cartaoCredito.getStatus().equalsIgnoreCase("NOT_AUTHORIZED")) {
			  return new ResponseEntity<String>("Pagamento não autorizado pela instituição responsável pleo cartão de crédito, no caso, a emissora do seu cartão.", HttpStatus.OK);
		  }
		  else  if (cartaoCredito.getStatus().equalsIgnoreCase("CUSTOMER_PAID_BACK")) {
			  return new ResponseEntity<String>("Pagamento estornado a pedido do cliente.", HttpStatus.OK);
		  }
		  else  if (cartaoCredito.getStatus().equalsIgnoreCase("BANK_PAID_BACK")) {
			  return new ResponseEntity<String>("Pagamento estornado a pedido do banco.", HttpStatus.OK);
		  }
		  else  if (cartaoCredito.getStatus().equalsIgnoreCase("PARTIALLY_REFUNDED")) {
			  return new ResponseEntity<String>("Pagamento parcialmente estornado.", HttpStatus.OK);
		  }
		  //se o retorno no GETSTATUS do CARTAOCREDITO for confirmado
		  //significa q o pagamento deu certo...
		  else  if (cartaoCredito.getStatus().equalsIgnoreCase("CONFIRMED")) {
			  
			  //como o pagamento deu certo... MESMO Q O PAGAMENTO
			  //SEJA POR CARTAO ele GERA UM BOLETO... entao vamos
			  //QUITAR (marcar como pago) o boleto
			   for (BoletoJuno boletoJuno : boletosJuno) {
				  boletoJunoRepository.quitarBoletoById(boletoJuno.getId());
			   }
			   
			   //atualizando a venda...
			  vd_Cp_Loja_virt_repository.updateFinalizaVenda(vendaCompraLojaVirtual.getId());
			  
			  return new ResponseEntity<String>("sucesso", HttpStatus.OK);
		  }
		 
		
		return new ResponseEntity<String>("Nenhuma operação realizada!",HttpStatus.OK);
		
	}

}
