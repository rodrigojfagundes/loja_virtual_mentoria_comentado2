package jdev.mentoria.lojavirtual.controller;


import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.enums.StatusContaReceber;
import jdev.mentoria.lojavirtual.model.ContaReceber;
import jdev.mentoria.lojavirtual.model.Endereco;
import jdev.mentoria.lojavirtual.model.ItemVendaLoja;
import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.StatusRastreio;
import jdev.mentoria.lojavirtual.model.VendaCompraLojaVirtual;
import jdev.mentoria.lojavirtual.model.dto.ItemVendaDTO;
import jdev.mentoria.lojavirtual.model.dto.VendaCompraLojaVirtualDTO;
import jdev.mentoria.lojavirtual.repository.ContaReceberRepository;
import jdev.mentoria.lojavirtual.repository.EnderecoRepository;
import jdev.mentoria.lojavirtual.repository.NotaFiscalVendaRepository;
import jdev.mentoria.lojavirtual.repository.StatusRastreioRepository;
import jdev.mentoria.lojavirtual.repository.Vd_Cp_Loja_virt_repository;
import jdev.mentoria.lojavirtual.service.ServiceSendEmail;
import jdev.mentoria.lojavirtual.service.VendaService;


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
	
	
	
}
