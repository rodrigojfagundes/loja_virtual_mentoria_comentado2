package jdev.mentoria.lojavirtual.controller;


import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.enums.ApiTokenIntegracao;
import jdev.mentoria.lojavirtual.enums.StatusContaReceber;
import jdev.mentoria.lojavirtual.model.ContaReceber;
import jdev.mentoria.lojavirtual.model.Endereco;
import jdev.mentoria.lojavirtual.model.ItemVendaLoja;
import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.StatusRastreio;
import jdev.mentoria.lojavirtual.model.VendaCompraLojaVirtual;
import jdev.mentoria.lojavirtual.model.dto.ConsultaFreteDTO;
import jdev.mentoria.lojavirtual.model.dto.EmpresaTransporteDTO;
import jdev.mentoria.lojavirtual.model.dto.ItemVendaDTO;
import jdev.mentoria.lojavirtual.model.dto.VendaCompraLojaVirtualDTO;
import jdev.mentoria.lojavirtual.repository.ContaReceberRepository;
import jdev.mentoria.lojavirtual.repository.EnderecoRepository;
import jdev.mentoria.lojavirtual.repository.NotaFiscalVendaRepository;
import jdev.mentoria.lojavirtual.repository.StatusRastreioRepository;
import jdev.mentoria.lojavirtual.repository.Vd_Cp_Loja_virt_repository;
import jdev.mentoria.lojavirtual.service.ServiceSendEmail;
import jdev.mentoria.lojavirtual.service.VendaService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@RestController
public class Vd_Cp_loja_Virt_Controller {
	
	@Autowired
	private Vd_Cp_Loja_virt_repository vd_Cp_Loja_virt_repository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private PessoaController pessoaController;
	
	@Autowired
	private NotaFiscalVendaRepository notaFiscalVendaRepository;
	
	@Autowired
	private StatusRastreioRepository statusRastreioRepository;
	
	@Autowired
	private VendaService vendaService;
	
	@Autowired
	private ContaReceberRepository contaReceberRepository;
	
	@Autowired
	private ServiceSendEmail serviceSendEmail;
	
	
	@ResponseBody
	@PostMapping(value = "**/salvarVendaLoja")
	public ResponseEntity<VendaCompraLojaVirtualDTO> salvarVendaLoja(@RequestBody @Valid VendaCompraLojaVirtual vendaCompraLojaVirtual) throws ExceptionMentoriaJava, UnsupportedEncodingException, MessagingException {
		
		//como o valor nao vem montado pelo JSON... Nos precisamos
		//acessar os OBJ/atributos e instanciar eles
		//tipo no JSON vem a EMPRESA mas ela nao ta instanciado na 
		//VENDACOMPRALOJAVIRTUAL... Dai nos temos q instanciar objeto
		//por objeto
		vendaCompraLojaVirtual.getPessoa().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		PessoaFisica pessoaFisica = pessoaController.salvarPf(vendaCompraLojaVirtual.getPessoa()).getBody();
		vendaCompraLojaVirtual.setPessoa(pessoaFisica);
		
		vendaCompraLojaVirtual.getEnderecoCobranca().setPessoa(pessoaFisica);
		vendaCompraLojaVirtual.getEnderecoCobranca().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		Endereco enderecoCobranca = enderecoRepository.save(vendaCompraLojaVirtual.getEnderecoCobranca());
		vendaCompraLojaVirtual.setEnderecoCobranca(enderecoCobranca);
		
		vendaCompraLojaVirtual.getEnderecoEntrega().setPessoa(pessoaFisica);
		vendaCompraLojaVirtual.getEnderecoEntrega().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		Endereco enderecoEntrega = enderecoRepository.save(vendaCompraLojaVirtual.getEnderecoEntrega());
		vendaCompraLojaVirtual.setEnderecoEntrega(enderecoEntrega);
		
		vendaCompraLojaVirtual.getNotaFiscalVenda().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		
		
		//fazendo associacao de objeto
		//
		//meio q estamos pegando PRODUTO por PRODUTO da VENDACOMPRALOJAVIRTUAL
		//e esses PRODUTOS estao dentro do ITEMVENDALOJAS
		for(int i = 0; i < vendaCompraLojaVirtual.getItemVendaLojas().size(); i++) {
			vendaCompraLojaVirtual.getItemVendaLojas()
			.get(i).setEmpresa(vendaCompraLojaVirtual.getEmpresa());
			
			vendaCompraLojaVirtual.getItemVendaLojas()
			.get(i).setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		}
		
		
		/*Salva primeiro a venda e todo os dados*/
		vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.saveAndFlush(vendaCompraLojaVirtual);
		
		
		//fazendo a SIMULACAO de um STATUSRASTREIO... Para TESTES
		//no futuro ele vai servir como um START para a conexao com a API
		//das TRANSPORTADORA
		//
		StatusRastreio statusRastreio = new StatusRastreio();
		statusRastreio.setCentroDistribuicao("Loja Local");
		statusRastreio.setCidade("Local");
		statusRastreio.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		statusRastreio.setEstado("Local");
		statusRastreio.setStatus("Inicio compra");
		statusRastreio.setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
				
		statusRastreioRepository.save(statusRastreio);
		
		
		
		/*Associa a venda gravada no banco com a nota fiscal*/
		vendaCompraLojaVirtual.getNotaFiscalVenda().setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		
		/*Persiste novamente as nota fiscal novamente pra ficar amarrada na venda*/
		notaFiscalVendaRepository.saveAndFlush(vendaCompraLojaVirtual.getNotaFiscalVenda());
		
		//AQUI O NOME DO OBJ/VAR poderia ser vendaCompraLojaVirtualDTO
		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		compraLojaVirtualDTO.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
		
		compraLojaVirtualDTO.setPessoa(vendaCompraLojaVirtual.getPessoa());
		
		compraLojaVirtualDTO.setEntrega(
				vendaCompraLojaVirtual.getEnderecoEntrega());
		
		compraLojaVirtualDTO.setCobranca(
				vendaCompraLojaVirtual.getEnderecoCobranca());
		
		
		compraLojaVirtualDTO.setValorDesc(
				vendaCompraLojaVirtual.getValorDesconto());
		
		compraLojaVirtualDTO.setValorFrete(
				vendaCompraLojaVirtual.getValorFret());

		compraLojaVirtualDTO.setId(vendaCompraLojaVirtual.getId());
		
		for (ItemVendaLoja item: vendaCompraLojaVirtual
				.getItemVendaLojas()) {
						
			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());
			
			compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
			
		}
		
		ContaReceber contaReceber = new ContaReceber();
		contaReceber.setDescricao("Venda da loja virtual n: " + vendaCompraLojaVirtual.getId());
		contaReceber.setDtPagamento(Calendar.getInstance().getTime());
		contaReceber.setDtVencimento(Calendar.getInstance().getTime());
		contaReceber.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		contaReceber.setPessoa(vendaCompraLojaVirtual.getPessoa());
		contaReceber.setStatus(StatusContaReceber.QUITADA);
		contaReceber.setValorDesconto(vendaCompraLojaVirtual.getValorDesconto());
		contaReceber.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
		
		contaReceberRepository.saveAndFlush(contaReceber);
		
		//e-mail para comprador
		StringBuilder msgemail = new StringBuilder();
		msgemail.append("Ola, ").append(pessoaFisica.getNome()).append("</br>");
		msgemail.append("Voce realizou de n: ").append(vendaCompraLojaVirtual.getId()).append("</br>");
		msgemail.append("Na loja").append(vendaCompraLojaVirtual.getEmpresa().getNomeFantasia());
		
		//assunto, msg, destino
		serviceSendEmail.enviarEmailHtml("Compra Realizada", msgemail.toString(), pessoaFisica.getEmail());
		
		//email para vendedor
		msgemail = new StringBuilder();
		msgemail.append("Voce realizou uma venda,  nº ").append(vendaCompraLojaVirtual.getId());
		serviceSendEmail.enviarEmailHtml("Venda Realizada", msgemail.toString(), vendaCompraLojaVirtual.getEmpresa().getEmail());

		
		return new ResponseEntity<VendaCompraLojaVirtualDTO>(compraLojaVirtualDTO, HttpStatus.OK);
	
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/consultaVendaId/{id}")
	public ResponseEntity<VendaCompraLojaVirtualDTO> consultaVendaId(@PathVariable("id") Long idVenda){
		
		
		//OBS: O NOME DO OBJ/VAR O PROF DEIXOU COMO COMPRALOJAVIRTUAL...
		//MAS COMO E UMA VENDACOMPRALOJAVIRTUAL eu resolvi DEIXAR o OBJ/VAR
		//com o nome de VENDACOMPRALOJAVIRTUAL
		VendaCompraLojaVirtual vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository
				.findByIdExclusao(idVenda);
		
		if (vendaCompraLojaVirtual == null) {
			vendaCompraLojaVirtual = new VendaCompraLojaVirtual();
		}
		

		//convertendo para DTO
		
		//AQUI O NOME DO OBJ/VAR poderia ser vendaCompraLojaVirtualDTO
		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		compraLojaVirtualDTO.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
		
		compraLojaVirtualDTO.setPessoa(vendaCompraLojaVirtual.getPessoa());
		
		compraLojaVirtualDTO.setEntrega(
				vendaCompraLojaVirtual.getEnderecoEntrega());
		
		compraLojaVirtualDTO.setCobranca(
				vendaCompraLojaVirtual.getEnderecoCobranca());
		
		
		compraLojaVirtualDTO.setValorDesc(
				vendaCompraLojaVirtual.getValorDesconto());
		
		compraLojaVirtualDTO.setValorFrete(
				vendaCompraLojaVirtual.getValorFret());
		
		compraLojaVirtualDTO.setId(vendaCompraLojaVirtual.getId());
		
		
		for (ItemVendaLoja item: vendaCompraLojaVirtual
				.getItemVendaLojas()) {
						
			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());
			
			compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
			
		}

		
		return new ResponseEntity<VendaCompraLojaVirtualDTO>
		(compraLojaVirtualDTO, HttpStatus.OK);
		
	}
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteVendaTotalBanco/{idVenda}")
	public ResponseEntity<String> deleteVendaTotalBanco(@PathVariable(value = "idVenda") Long idVenda){
		
		vendaService.exclusaoTotalVendaBanco(idVenda);
		
		return new ResponseEntity<String>("Venda excluida com sucesso", HttpStatus.OK);
		
	}
	
	//para exclusao logica... Ou seja nao e deletado e sim
	//sera oculto na hora de fazer o get
	@ResponseBody
	@DeleteMapping(value = "**/deleteVendaTotalBanco2/{idVenda}")
	public ResponseEntity<String> deleteVendaTotalBanco2(@PathVariable(value = "idVenda") Long idVenda){
		
		vendaService.exclusaoTotalVendaBanco2(idVenda);
		
		return new ResponseEntity<String>("Venda excluida logicamente com sucesso", HttpStatus.OK);
		
	}
	
	
	//para ativar um uma compravenda apos ela ter sido deletada logicamente
	@ResponseBody
	@PutMapping(value = "**/ativaRegistroVendaBanco/{idVenda}")
	public ResponseEntity<String> ativaRegistroVendaBanco(@PathVariable(value = "idVenda") Long idVenda){
		
		vendaService.ativaRegistroVendaBanco(idVenda);
		
		return new ResponseEntity<String>("VendaCompraLojaVirtual ativada logicamente com sucesso", HttpStatus.OK);
		
	}
	
	
	//meio q nos passamos o ID de um CLIENTE e vamos ver
	//quais foram as VENDASCOMPRALOJAVIRTUAL (vendacompra) q
	//foram para esse CLIENTE
	@ResponseBody
	@GetMapping(value = "**/vendaPorCliente/{idCliente}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> vendaPorCliente(@PathVariable("idCliente") Long idCliente){
				
		//OBS: O NOME DO OBJ/VAR O PROF DEIXOU COMO COMPRALOJAVIRTUAL...
		//MAS COMO E UMA VENDACOMPRALOJAVIRTUAL eu resolvi DEIXAR o OBJ/VAR
		//com o nome de VENDACOMPRALOJAVIRTUAL
		List<VendaCompraLojaVirtual> vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository
				.vendaPorCliente(idCliente);
		
		if (vendaCompraLojaVirtual == null) {
			vendaCompraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}		
		//criando uma LISTA de VENDACOMPRALOJAVIRTUALDTO
		//o nome do obj/atributo poderia ser VENDACOMPRALOJAVIRTUALDTO
		//e NAO COMPRALOJAVIRTUALDTOLIST
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		//VCL significa VENDACOMPRALOJA
		for(VendaCompraLojaVirtual vcl : vendaCompraLojaVirtual) {
		//convertendo para DTO		
		//AQUI O NOME DO OBJ/VAR poderia ser vendaCompraLojaVirtualDTO
		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		
		compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());		
		compraLojaVirtualDTO.setPessoa(vcl.getPessoa());	
		compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());		
		compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());				
		compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());		
		compraLojaVirtualDTO.setValorFrete(vcl.getValorFret());		
		compraLojaVirtualDTO.setId(vcl.getId());
				
		for (ItemVendaLoja item: vcl.getItemVendaLojas()) {						
			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());
			
			compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
		}		
		compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
		}		
		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK);
	}
	
	
	
	
	
	//meio q nos passamos o ID de um produto e vamos ver
	//quais foram as VENDASCOMPRALOJAVIRTUAL (vendacompra) q esses
	//produto foram vendidos...
	@ResponseBody
	@GetMapping(value = "**/consultaVendaPorProdutoId/{id}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> consultaVendaPorProdutoId(@PathVariable("id") Long idProd){
				
		//OBS: O NOME DO OBJ/VAR O PROF DEIXOU COMO COMPRALOJAVIRTUAL...
		//MAS COMO E UMA VENDACOMPRALOJAVIRTUAL eu resolvi DEIXAR o OBJ/VAR
		//com o nome de VENDACOMPRALOJAVIRTUAL
		List<VendaCompraLojaVirtual> vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository
				.vendaPorProduto(idProd);
		
		if (vendaCompraLojaVirtual == null) {
			vendaCompraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}		
		//criando uma LISTA de VENDACOMPRALOJAVIRTUALDTO
		//o nome do obj/atributo poderia ser VENDACOMPRALOJAVIRTUALDTO
		//e NAO COMPRALOJAVIRTUALDTOLIST
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		//VCL significa VENDACOMPRALOJA
		for(VendaCompraLojaVirtual vcl : vendaCompraLojaVirtual) {
		//convertendo para DTO		
		//AQUI O NOME DO OBJ/VAR poderia ser vendaCompraLojaVirtualDTO
		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		
		compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());		
		compraLojaVirtualDTO.setPessoa(vcl.getPessoa());	
		compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());		
		compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());				
		compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());		
		compraLojaVirtualDTO.setValorFrete(vcl.getValorFret());		
		compraLojaVirtualDTO.setId(vcl.getId());
				
		for (ItemVendaLoja item: vcl.getItemVendaLojas()) {						
			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());
			
			compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
		}		
		compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
		}		
		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK);
	}
	
	//metodo para pesquisar VENDACOMPRALOJAVIRTUAL entre 2 datas...
	@ResponseBody
	@GetMapping(value = "**/consultaVendaDinamicaFaixaData/{data1}/{data2}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> consultaVendaDinamicaFaixaData(
			@PathVariable("data1")String data1, @PathVariable("data2") String data2) throws ParseException{
		
		
		//o NOME CORRETO DO OBJ/ATRIBUTO SERIA vendaCompraLojaVirtual...
		//mas o PROF colocou compralojavirtual...
		List<VendaCompraLojaVirtual> compraLojaVirtual = null;
				
		//passando as datas q recebemos la em cima para o VENDASERVICE
		compraLojaVirtual = vendaService
				.consultaVendaFaixaData(data1, data2);

		
		//se o retorno for uma lista null, dai vamos apenas instanciar
		//ela para nao dar nullpointerexception
		//
		if(compraLojaVirtual == null) {
			compraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		//criando uma LISTA de VENDACOMPRALOJAVIRTUALDTO
		//o nome do obj/atributo poderia ser VENDACOMPRALOJAVIRTUALDTO
		//e NAO COMPRALOJAVIRTUALDTOLIST
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		//VCL significa VENDACOMPRALOJA
		for(VendaCompraLojaVirtual vcl : compraLojaVirtual) {
		//convertendo para DTO		
		//AQUI O NOME DO OBJ/VAR poderia ser vendaCompraLojaVirtualDTO
		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		
		compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());		
		compraLojaVirtualDTO.setPessoa(vcl.getPessoa());	
		compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());		
		compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());				
		compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());		
		compraLojaVirtualDTO.setValorFrete(vcl.getValorFret());		
		compraLojaVirtualDTO.setId(vcl.getId());
				
		for (ItemVendaLoja item: vcl.getItemVendaLojas()) {						
			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());
			
			compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
		}		
		compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
		}
		
		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK);
	}
		
	//}
	
	
	//metodo de BUSCA POR CONSULTADINAMICA... Ou seja passando 2 parametros
	//q podem ser mudados...
	//
	//com esse metodo alem de nos passarmos o valor(ID) passamos o tipo
	//a baixo nos temos o TIPOCONSULTA... a baixo temos um IF e SE
	//for do tipo PROD_POR_ID dai e pq o ID q estamos passando e para
	//buscar um PRODUTO...
	//
	//ou tabem podemos passar um valor tipo GALAXY e o segundo parametro
	//ser POR_NOME_PROD... Dai vai procurar pelo nome do produto
	//
	//
	@ResponseBody
	@GetMapping(value = "**/consultaVendaDinamica/{valor}/{tipoconsulta}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> 
		consultaVendaDinamica(@PathVariable("valor") String valor,
				@PathVariable("tipoconsulta") String tipoconsulta){
				
		//OBS: O NOME DO OBJ/VAR O PROF DEIXOU COMO COMPRALOJAVIRTUAL...
		//MAS COMO E UMA VENDACOMPRALOJAVIRTUAL eu resolvi DEIXAR o OBJ/VAR
		//com o nome de VENDACOMPRALOJAVIRTUAL
		//
		//OBS o PROF DEIXOU O NOME DE COMPRALOJAVIRTUAL (nome do obj/var)
		List<VendaCompraLojaVirtual> vendaCompraLojaVirtual = null;
		
		
		//verificando SE o tipo da consulta é por IDPRODUTO
		if(tipoconsulta.equalsIgnoreCase("POR_ID_PROD")) {
		vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository
		.vendaPorProduto(Long.parseLong(valor));
		} 
		
		//verificando se a consulta e pelo NOMEPRODUTO
		else if(tipoconsulta.equalsIgnoreCase("POR_NOME_PROD")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository
					.vendaPorNomeProduto(valor.toUpperCase().trim());
		}
		
		else if(tipoconsulta.equalsIgnoreCase("POR_NOME_CLIENTE")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository
					.vendaPorNomeCliente(valor.toUpperCase().trim());
		}
		
		else if(tipoconsulta.equalsIgnoreCase("POR_ENDERECO_COBRANCA")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository
					.vendaPorEnderecoCobranca(valor.toUpperCase().trim());
		}
		
		else if(tipoconsulta.equalsIgnoreCase("POR_ENDERECO_ENTREGA")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository
					.vendaPorEnderecoEntrega(valor.toUpperCase().trim());
		}
				
		
		if (vendaCompraLojaVirtual == null) {
			vendaCompraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}		
		
		//criando uma LISTA de VENDACOMPRALOJAVIRTUALDTO
		//o nome do obj/atributo poderia ser VENDACOMPRALOJAVIRTUALDTO
		//e NAO COMPRALOJAVIRTUALDTOLIST
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		//VCL significa VENDACOMPRALOJA
		for(VendaCompraLojaVirtual vcl : vendaCompraLojaVirtual) {
		//convertendo para DTO		
		//AQUI O NOME DO OBJ/VAR poderia ser vendaCompraLojaVirtualDTO
		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		
		compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());		
		compraLojaVirtualDTO.setPessoa(vcl.getPessoa());	
		compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());		
		compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());				
		compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());		
		compraLojaVirtualDTO.setValorFrete(vcl.getValorFret());		
		compraLojaVirtualDTO.setId(vcl.getId());
				
		for (ItemVendaLoja item: vcl.getItemVendaLojas()) {						
			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());
			
			compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
		}		
		compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
		}		
		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK);
	}
	
	//metodo q vai receber as INFORMACOES como PRODUTOS, Tamanhos
	//Pesos, etc... E vai passar para a API do MELHORENVIO
	//q ira nos retornar as transportadoras e o preco, tempo, etc...
	@ResponseBody
	@PostMapping(value = "**/consultarFreteLojaVirtual")
	public ResponseEntity<List<EmpresaTransporteDTO>> consultaFrete(
			@RequestBody @Valid ConsultaFreteDTO consultaFreteDTO) throws Exception {
		
		
		//instanciando um obj/var do TIPO OBJECTMAPPER e de nome
		//OBJECTMAPPER...
		//
		//dps criamos uma VAR do TIPO STRING de nome JSON q ira
		//receber o retorno da funcao WEITEVALUEASSTRING do OBJECTMAPPER
		//apos ela receber o OBJ/VAR CONSULTAFRETEDTO q é um OBJ
		//q e passado pelo FRONTEND/POSTMAN e tem o CEP de ORIGEM
		//e DESTINO... Alem de uma LISTA DE PRODUTOS... com as
		//caracteristicas deles...
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(consultaFreteDTO);
		
		

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
		okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
		okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, json);
		okhttp3.Request request = new okhttp3.Request.Builder()
		  .url("https://sandbox.melhorenvio.com.br/api/v2/me/shipment/calculate")
		  .method("POST", body)
		  //.post(body)
		  .addHeader("Accept", "application/json")
		  .addHeader("Content-Type", "application/json")
		  .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
		  .addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
		  .build();

		okhttp3.Response response = client.newCall(request).execute();
		
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
	}

		return new ResponseEntity<List<EmpresaTransporteDTO>>(
				empresaTransporteDTOs, HttpStatus.OK);
	}
		
	}
	

