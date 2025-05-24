package jdev.mentoria.lojavirtual.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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
import jdev.mentoria.lojavirtual.model.dto.EnvioEtiquetaDTO;
import jdev.mentoria.lojavirtual.model.dto.ItemVendaDTO;
import jdev.mentoria.lojavirtual.model.dto.ObjetoPostCarneJuno;
import jdev.mentoria.lojavirtual.model.dto.ProductsEnvioEtiquetaDTO;
import jdev.mentoria.lojavirtual.model.dto.TagsEnvioDto;
import jdev.mentoria.lojavirtual.model.dto.VendaCompraLojaVirtualDTO;
import jdev.mentoria.lojavirtual.model.dto.VolumesEnvioEtiquetaDTO;
import jdev.mentoria.lojavirtual.repository.ContaReceberRepository;
import jdev.mentoria.lojavirtual.repository.EnderecoRepository;
import jdev.mentoria.lojavirtual.repository.NotaFiscalVendaRepository;
import jdev.mentoria.lojavirtual.repository.StatusRastreioRepository;
import jdev.mentoria.lojavirtual.repository.Vd_Cp_Loja_virt_repository;
import jdev.mentoria.lojavirtual.service.ServiceJunoBoleto;
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
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ServiceJunoBoleto serviceJunoBoleto;


	@ResponseBody
	@PostMapping(value = "**/salvarVendaLoja")
	public ResponseEntity<VendaCompraLojaVirtualDTO> salvarVendaLoja(
			@RequestBody @Valid VendaCompraLojaVirtual vendaCompraLojaVirtual) throws ExceptionMentoriaJava, UnsupportedEncodingException, MessagingException {

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
		for (int i = 0; i < vendaCompraLojaVirtual.getItemVendaLojas().size(); i++) {
			vendaCompraLojaVirtual.getItemVendaLojas().get(i).setEmpresa(vendaCompraLojaVirtual.getEmpresa());
			vendaCompraLojaVirtual.getItemVendaLojas().get(i).setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		}

		/* Salva primeiro a venda e todo os dados */
		vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.saveAndFlush(vendaCompraLojaVirtual);
		
		/* Associa a venda gravada no banco com a nota fiscal */
		vendaCompraLojaVirtual.getNotaFiscalVenda().setVendaCompraLojaVirtual(vendaCompraLojaVirtual);

		/* Persiste novamente as nota fiscal novamente pra ficar amarrada na venda */
		notaFiscalVendaRepository.saveAndFlush(vendaCompraLojaVirtual.getNotaFiscalVenda());

		//AQUI O NOME DO OBJ/VAR q o prof deixou era COMPRALOJAVIRTUALDTO
		//mas acho mais acho mais adaquedado VENDACOMPRALOJAVIRTUALDTO
		VendaCompraLojaVirtualDTO vendaCompraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		vendaCompraLojaVirtualDTO.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
		vendaCompraLojaVirtualDTO.setPessoa(vendaCompraLojaVirtual.getPessoa());

		vendaCompraLojaVirtualDTO.setEntrega(vendaCompraLojaVirtual.getEnderecoEntrega());
		vendaCompraLojaVirtualDTO.setCobranca(vendaCompraLojaVirtual.getEnderecoCobranca());

		vendaCompraLojaVirtualDTO.setValorDesc(vendaCompraLojaVirtual.getValorDesconto());
		vendaCompraLojaVirtualDTO.setValorFrete(vendaCompraLojaVirtual.getValorFret());
		vendaCompraLojaVirtualDTO.setId(vendaCompraLojaVirtual.getId());

		for (ItemVendaLoja item : vendaCompraLojaVirtual.getItemVendaLojas()) {

			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());

			vendaCompraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
		}
		
		
		ContaReceber contaReceber = new ContaReceber();
		contaReceber.setDescricao("Venda da loja virtual nº: " + vendaCompraLojaVirtual.getId());
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
		msgemail.append("Olá, ").append(pessoaFisica.getNome()).append("</br>");
		msgemail.append("Você realizou a compra de nº: ").append(vendaCompraLojaVirtual.getId()).append("</br>");
		msgemail.append("Na loja ").append(vendaCompraLojaVirtual.getEmpresa().getNomeFantasia());
		/*assunto, msg, destino*/
		serviceSendEmail.enviarEmailHtml("Compra Realizada", msgemail.toString(), pessoaFisica.getEmail());
		
		/*Email para o vendedor*/
		msgemail = new StringBuilder();
		msgemail.append("Você realizou uma venda, nº " ).append(vendaCompraLojaVirtual.getId());
		serviceSendEmail.enviarEmailHtml("Venda Realizada", msgemail.toString(), vendaCompraLojaVirtual.getEmpresa().getEmail());

		return new ResponseEntity<VendaCompraLojaVirtualDTO>(vendaCompraLojaVirtualDTO, HttpStatus.OK);
	}

	@ResponseBody
	@GetMapping(value = "**/consultaVendaId/{id}")
	public ResponseEntity<VendaCompraLojaVirtualDTO> consultaVendaId(@PathVariable("id") Long idVenda) {

		//OBS: O NOME DO OBJ/VAR O PROF DEIXOU COMO COMPRALOJAVIRTUAL...
		//MAS COMO E UMA VENDACOMPRALOJAVIRTUAL eu resolvi DEIXAR o OBJ/VAR
		//com o nome de VENDACOMPRALOJAVIRTUAL
		VendaCompraLojaVirtual vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.findByIdExclusao(idVenda);
		
		if (vendaCompraLojaVirtual == null) {
			vendaCompraLojaVirtual = new VendaCompraLojaVirtual();
		}

		//convertendo para DTO
		
		//AQUI O NOME DO OBJ/VAR o prof deixou como COMPRALOJAVIRTUALDTO
		//mas eu acho mas adequado VENDACOMPRALOJAVIRTUALDTO
		VendaCompraLojaVirtualDTO vendaCompraLojaVirtualDTO = vendaService.consultaVenda(vendaCompraLojaVirtual);

		return new ResponseEntity<VendaCompraLojaVirtualDTO>(vendaCompraLojaVirtualDTO, HttpStatus.OK);
	}
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteVendaTotalBanco/{idVenda}")
	public ResponseEntity<String> deleteVendaTotalBanco(@PathVariable(value = "idVenda") Long idVenda) {
		
		vendaService.exclusaoTotalVendaBanco(idVenda);
		
		return new ResponseEntity<String>("Venda excluida com sucesso.",HttpStatus.OK);
		
	}
		
	//para exclusao logica... Ou seja nao e deletado e sim
	//sera oculto na hora de fazer o get
	@ResponseBody
	@DeleteMapping(value = "**/deleteVendaTotalBanco2/{idVenda}")
	public ResponseEntity<String> deleteVendaTotalBanco2(@PathVariable(value = "idVenda") Long idVenda) {
		
		vendaService.exclusaoTotalVendaBanco2(idVenda);
		
		return new ResponseEntity<String>("Venda excluida logicamente com sucesso!.",HttpStatus.OK);
		
	}
	
	
	//para ativar um uma compravenda apos ela ter sido deletada logicamente
	@ResponseBody
	@PutMapping(value = "**/ativaRegistroVendaBanco/{idVenda}")
	public ResponseEntity<String> ativaRegistroVendaBanco(@PathVariable(value = "idVenda") Long idVenda) {
		
		vendaService.ativaRegistroVendaBanco(idVenda);
		
		return new ResponseEntity<String>("Venda ativada com sucesso!.",HttpStatus.OK);
		
	}
	
	
	//metodo para pesquisar VENDACOMPRALOJAVIRTUAL entre 2 datas...
	@ResponseBody
	@GetMapping(value = "**/consultaVendaDinamicaFaixaData/{data1}/{data2}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> 
	                            consultaVendaDinamicaFaixaData(
	                            		@PathVariable("data1") String data1,
	                            		@PathVariable("data2") String data2) throws ParseException{
		
		//OBS: O NOME DO OBJ/VAR O PROF DEIXOU COMO COMPRALOJAVIRTUAL...
		//MAS COMO E UMA VENDACOMPRALOJAVIRTUAL eu resolvi DEIXAR o OBJ/VAR
		//com o nome de VENDACOMPRALOJAVIRTUAL
		List<VendaCompraLojaVirtual> compraLojaVirtual = null;
		
		//passando as datas q recebemos la em cima para o VENDASERVICE
		compraLojaVirtual = vendaService.consultaVendaFaixaData(data1, data2);
		
		//se o retorno for uma lista null, dai vamos apenas instanciar
		//ela para nao dar nullpointerexception
		//
		if (compraLojaVirtual == null) {
			compraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		
		//criando uma LISTA de VENDACOMPRALOJAVIRTUALDTO
		//o nome do obj/atributo o prof deixou o nome 
		//COMPRALOJAVIRTUALDTOLIST achei mais adequado
		//VENDACOMPRALOJAVIRTUALDTOLIST
		//
         List<VendaCompraLojaVirtualDTO> vendaCompraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		//VCL significa VENDACOMPRALOJA
		for (VendaCompraLojaVirtual vcl : compraLojaVirtual) {
			
			//convertendo para DTO		
			//AQUI O NOME DO OBJ/VAR o prof deixou como COMPRALOJAVIRTUALDTO
			//mas eu acho mais adequado VENDACOMPRALOJAVIRTUALDTO
			VendaCompraLojaVirtualDTO vendaCompraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
	
			vendaCompraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			vendaCompraLojaVirtualDTO.setPessoa(vcl.getPessoa());
	
			vendaCompraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			vendaCompraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
	
			vendaCompraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			vendaCompraLojaVirtualDTO.setValorFrete(vcl.getValorFret());
			vendaCompraLojaVirtualDTO.setId(vcl.getId());

			for (ItemVendaLoja item : vcl.getItemVendaLojas()) {
	
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
	
				vendaCompraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
			}
			
			vendaCompraLojaVirtualDTOList.add(vendaCompraLojaVirtualDTO);
		
		}

		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(vendaCompraLojaVirtualDTOList, HttpStatus.OK);
		
	}
	
	
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
								              @PathVariable("tipoconsulta") String tipoconsulta) {

		//OBS: O NOME DO OBJ/VAR O PROF DEIXOU COMO COMPRALOJAVIRTUAL...
		//MAS COMO E UMA VENDACOMPRALOJAVIRTUAL eu resolvi DEIXAR o OBJ/VAR
		//com o nome de VENDACOMPRALOJAVIRTUAL
		//
		//OBS o PROF DEIXOU O NOME DE COMPRALOJAVIRTUAL (nome do obj/var)
		List<VendaCompraLojaVirtual> vendaCompraLojaVirtual = null;
		
		//verificando SE o tipo da consulta é por IDPRODUTO
		if (tipoconsulta.equalsIgnoreCase("POR_ID_PROD")) {
			
			vendaCompraLojaVirtual =   vd_Cp_Loja_virt_repository.vendaPorProduto(Long.parseLong(valor));
			
		//verificando se a consulta e pelo NOMEPRODUTO
		}else if (tipoconsulta.equalsIgnoreCase("POR_NOME_PROD")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorNomeProduto(valor.toUpperCase().trim());
		}
		else if (tipoconsulta.equalsIgnoreCase("POR_NOME_CLIENTE")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorNomeCliente(valor.toUpperCase().trim());
		}
		else if (tipoconsulta.equalsIgnoreCase("POR_ENDERECO_COBRANCA")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorEndereCobranca(valor.toUpperCase().trim());
		}
		else if (tipoconsulta.equalsIgnoreCase("POR_ENDERECO_ENTREGA")) {
			vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorEnderecoEntrega(valor.toUpperCase().trim());
		}
		
		if (vendaCompraLojaVirtual == null) {
			vendaCompraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		//criando uma LISTA de VENDACOMPRALOJAVIRTUALDTO
		//o nome do obj/atributo o prof deixou o nome 
		//COMPRALOJAVIRTUALDTOLIST mas eu achei mais adequado
		//VENDACOMPRALOJAVIRTUALDTOLIST
		List<VendaCompraLojaVirtualDTO> vendaCompraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		//VCL significa VENDACOMPRALOJA
		for (VendaCompraLojaVirtual vcl : vendaCompraLojaVirtual) {
			
		//convertendo para DTO		
		//AQUI O NOME DO OBJ/VAR o prof deixou como COMPRALOJAVIRTUALDTO
			//mas eu achei maisadequado VENDACOMPRALOJAVIRTUALDTO
			VendaCompraLojaVirtualDTO vendaCompraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
	
			vendaCompraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			vendaCompraLojaVirtualDTO.setPessoa(vcl.getPessoa());
	
			vendaCompraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			vendaCompraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
	
			vendaCompraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			vendaCompraLojaVirtualDTO.setValorFrete(vcl.getValorFret());
			vendaCompraLojaVirtualDTO.setId(vcl.getId());

			for (ItemVendaLoja item : vcl.getItemVendaLojas()) {
	
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
	
				vendaCompraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
			}
			
			vendaCompraLojaVirtualDTOList.add(vendaCompraLojaVirtualDTO);
		
		}

		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(vendaCompraLojaVirtualDTOList, HttpStatus.OK);
	}
	
	//meio q nos passamos o ID de um CLIENTE e vamos ver
	//quais foram as VENDASCOMPRALOJAVIRTUAL (vendacompra) q
	//foram para esse CLIENTE
	@ResponseBody
	@GetMapping(value = "**/vendaPorCliente/{idCliente}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> vendaPorCliente(@PathVariable("idCliente") Long idCliente) {

		//OBS: O NOME DO OBJ/VAR O PROF DEIXOU COMO COMPRALOJAVIRTUAL...
		//MAS COMO E UMA VENDACOMPRALOJAVIRTUAL eu resolvi DEIXAR o OBJ/VAR
		//com o nome de VENDACOMPRALOJAVIRTUAL
		List<VendaCompraLojaVirtual> vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorCliente(idCliente);
		
		if (vendaCompraLojaVirtual == null) {
			vendaCompraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
	
		//criando uma LISTA de VENDACOMPRALOJAVIRTUALDTO
		//o nome do obj/atributo o prof deixou como COMPRALOJAVIRTUALDTOLIST
		//mas eu achei mais adequado VENDACOMPRALOJAVIRTUALDTOLIST
		List<VendaCompraLojaVirtualDTO> vendaCompraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		//VCL significa VENDACOMPRALOJA
		for (VendaCompraLojaVirtual vcl : vendaCompraLojaVirtual) {
			
			//convertendo para DTO		
			//AQUI O NOME DO OBJ/VAR o prof deixou com o nome de 
			//COMPRALOJAVIRTUALDTO, mas eu achei mais adequado
			//VENDACOMPRALOJAVIRTUALDTO
			VendaCompraLojaVirtualDTO vendaCompraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
	
			vendaCompraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			vendaCompraLojaVirtualDTO.setPessoa(vcl.getPessoa());
	
			vendaCompraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			vendaCompraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
	
			vendaCompraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			vendaCompraLojaVirtualDTO.setValorFrete(vcl.getValorFret());
			vendaCompraLojaVirtualDTO.setId(vcl.getId());

			for (ItemVendaLoja item : vcl.getItemVendaLojas()) {
	
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
	
				vendaCompraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
			}
			
			vendaCompraLojaVirtualDTOList.add(vendaCompraLojaVirtualDTO);
		
		}

		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(vendaCompraLojaVirtualDTOList, HttpStatus.OK);
	}
	
	
	
	//meio q nos passamos o ID de um produto e vamos ver
	//quais foram as VENDASCOMPRALOJAVIRTUAL (vendacompra) q esses
	//produto foram vendidos...
	@ResponseBody
	@GetMapping(value = "**/consultaVendaPorProdutoId/{id}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> consultaVendaPorProduto(@PathVariable("id") Long idProd) {

		//OBS: O NOME DO OBJ/VAR O PROF DEIXOU COMO COMPRALOJAVIRTUAL...
		//MAS COMO E UMA VENDACOMPRALOJAVIRTUAL eu resolvi DEIXAR o OBJ/VAR
		//com o nome de VENDACOMPRALOJAVIRTUAL
		List<VendaCompraLojaVirtual> vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorProduto(idProd);
		
		if (vendaCompraLojaVirtual == null) {
			vendaCompraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		//criando uma LISTA de VENDACOMPRALOJAVIRTUALDTO
		//o nome do obj/atributo o prof deixou como COMPRALOJAVIRTUALDTOLIST
		//mas eu achei VENDACOMPRALOJAVIRTUALDTOLIST mais adequado
		List<VendaCompraLojaVirtualDTO> vendaCompraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		//VCL significa VENDACOMPRALOJA
		for (VendaCompraLojaVirtual vcl : vendaCompraLojaVirtual) {
			//convertendo para DTO
			//AQUI O NOME DO OBJ/VAR o prof deixou como COMPRALOJAVIRTUALDTO
			//mas eu achei mais adequado VENDACOMPRALOJAVIRTUALDTO
			VendaCompraLojaVirtualDTO vendaCompraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
	
			vendaCompraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			vendaCompraLojaVirtualDTO.setPessoa(vcl.getPessoa());
	
			vendaCompraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			vendaCompraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
	
			vendaCompraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			vendaCompraLojaVirtualDTO.setValorFrete(vcl.getValorFret());
			vendaCompraLojaVirtualDTO.setId(vcl.getId());

			for (ItemVendaLoja item : vcl.getItemVendaLojas()) {
	
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
	
				vendaCompraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
			}
			
			vendaCompraLojaVirtualDTOList.add(vendaCompraLojaVirtualDTO);
		
		}

		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(vendaCompraLojaVirtualDTOList, HttpStatus.OK);
	}
	
	
	//metodo para cancelar uma etiqueta gerada pela API DO MELHORENVIO
	//etiqueta e um papel q nos vamos colar nas caixas
	//q vao ser buscadas pela transportadora...
	@ResponseBody
	@GetMapping(value = "**/cancelaEtiqueta/{idEtiqueta}/{descricao}")
	public ResponseEntity<String> cancelaEtiqueta(@PathVariable String idEtiqueta, @PathVariable String reason_id, @PathVariable String descricao) throws IOException{
	
		OkHttpClient client = new OkHttpClient().newBuilder() .build();
		okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
		okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "{\n    \"order\": {\n        \"id\": \""+idEtiqueta+"\",\n        \"reason_id\": \""+reason_id+"\",\n        \"description\": \""+descricao+"\"\n    }\n}");
		okhttp3.Request request = new Request.Builder()
				  .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX+"api/v2/me/shipment/cancel")
				  .method("POST", body)
				  .addHeader("Accept", "application/json")
				  .addHeader("Content-Type", "application/json")
				  .addHeader("Authorization", "Bearer "+ ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
				  .addHeader("User-Agent", "suporte@jdevtreinamento.com.br")
				  .build();
		
		okhttp3.Response response = client.newCall(request).execute();
		
		//a resposta da API DO MELHORENVIO
		return new ResponseEntity<String>(response.body().string(), HttpStatus.OK);
	}
	
	//metodo/endpoint q recebe um codigo de venda
	@ResponseBody
	@GetMapping(value = "**/imprimeCompraEtiquetaFrete/{idVenda}")
	public ResponseEntity<String> imprimeCompraEtiquetaFrete(
			@PathVariable Long idVenda) throws ExceptionMentoriaJava, IOException {
		
		//criando um obj do tipo VENDACOMPRALOJAVIRTUAL de nome 
		//COMPRALOJAVIRTUAL q vai receber o retorno do METODO FINDBYID
		//do VD_CP_LOJA_VIRT_REPOSITORY q basicamente procura no BANCO
		//se EXISTE uma COMPRAVENDALOJAVIRTUAL(VENDA) com o ID passado
		VendaCompraLojaVirtual compraLojaVirtual = vd_Cp_Loja_virt_repository
				.findById(idVenda).orElseGet(null);
		
		//se nao tiver diz q a VENDA NAO FOI ENCONTRADA
		if (compraLojaVirtual == null) {
			return new ResponseEntity<String>(
					"Venda não encontrada", HttpStatus.OK);
		}
		
		List<Endereco> enderecos = enderecoRepository.enderecoPj(
				compraLojaVirtual.getEmpresa().getId());
		compraLojaVirtual.getEmpresa().setEnderecos(enderecos);
		
		//intanciando um var/obj/atributo do tipo ENVIOETIQUETADTO
		//de nome ENVIOETIQUETADTO
		EnvioEtiquetaDTO envioEtiquetaDTO = new EnvioEtiquetaDTO();
		
		//chamando a ENTITY/CLASS FROM(ORIGEM) q esta instanciada
		//no ENVIOETIQUETADTO, e passando as INFORMACOES da nossa
		//COMPRALOJAVIRTUAL (ID recebido acima) trazendo coisas como
		//NOME, TELEFONE, EMAIL, etc...
		//pois o FROM é a ORIGEM, e a ORIGEM do PRODUTO e a LOJAVIRTUAL
		//
		envioEtiquetaDTO.setService(compraLojaVirtual.getServicoTransportadora());
		envioEtiquetaDTO.setAgency("49");
		envioEtiquetaDTO.getFrom().setName(compraLojaVirtual.getEmpresa().getNome());
		envioEtiquetaDTO.getFrom().setPhone(compraLojaVirtual.getEmpresa().getTelefone());
		envioEtiquetaDTO.getFrom().setEmail(compraLojaVirtual.getEmpresa().getEmail());
		envioEtiquetaDTO.getFrom().setCompany_document(compraLojaVirtual.getEmpresa().getCnpj());
		envioEtiquetaDTO.getFrom().setState_register(compraLojaVirtual.getEmpresa().getInscEstadual());
		envioEtiquetaDTO.getFrom().setAddress(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getRuaLogra());
		envioEtiquetaDTO.getFrom().setComplement(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getComplemento());
		envioEtiquetaDTO.getFrom().setComplement("Empresa");
		envioEtiquetaDTO.getFrom().setNumber(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getNumero());
		envioEtiquetaDTO.getFrom().setDistrict(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getEstado());
		envioEtiquetaDTO.getFrom().setCity(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getCidade());
		envioEtiquetaDTO.getFrom().setCountry_id("BR");
		envioEtiquetaDTO.getFrom().setPostal_code(compraLojaVirtual.getEmpresa().getEnderecos().get(0).getCep());
		envioEtiquetaDTO.getFrom().setNote("Não há");

		//MESMO BLOCO Q O DE CIMA... SO Q DE TESTE ESSE
		//COM O BLOCO DE CODIGO A BAIXO FUNCIONOU
		//
		//envioEtiquetaDTO.setService("3");
		//envioEtiquetaDTO.setAgency("49");
		//envioEtiquetaDTO.getFrom().setName("Loja Virtual");
		//envioEtiquetaDTO.getFrom().setPhone("4899912345");
		//envioEtiquetaDTO.getFrom().setEmail("emaildaloja@lojajava.com.br");
		//envioEtiquetaDTO.getFrom().setCompany_document("83.475.913/0001-91");
		//envioEtiquetaDTO.getFrom().setState_register("1234567");
		//envioEtiquetaDTO.getFrom().setAddress("Magina BR-101");
		//envioEtiquetaDTO.getFrom().setComplement("Empresa");
		//envioEtiquetaDTO.getFrom().setNumber("10");
		//envioEtiquetaDTO.getFrom().setDistrict("SC");
		//envioEtiquetaDTO.getFrom().setCity("Tijucas");
		//envioEtiquetaDTO.getFrom().setCountry_id("BR");
		//envioEtiquetaDTO.getFrom().setPostal_code("88209-999");
		//envioEtiquetaDTO.getFrom().setNote("Não há");
		
		System.out.println(envioEtiquetaDTO.toString());

		//chamando a ENTITY/CLASS TO(DESTINO) q esta instanciada
		//no ENVIOETIQUETADTO, e passando as INFORMACOES da nossa
		//COMPRALOJAVIRTUAL.PESSOA (ID recebido acima) trazendo coisas como
		//NOME, TELEFONE, EMAIL, etc... Pois qm vai comprar ou seja
		//RECEBER o PRODUTO é uma PESSOAFISICA, por isso estamos
		//pegando as INFO (nome, email, cpf, etcc...) do GETPESSOA....
		//pois o TO é o DESTINO, e o DESTINO do PRODUTO e a LOJAVIRTUAL
		//q no caso é o ENDERECO DE UMA PESSOA FISICA...
		//
		envioEtiquetaDTO.getTo().setName(compraLojaVirtual.getPessoa().getNome());
		envioEtiquetaDTO.getTo().setPhone(compraLojaVirtual.getPessoa().getTelefone());
		envioEtiquetaDTO.getTo().setEmail(compraLojaVirtual.getPessoa().getEmail());
		envioEtiquetaDTO.getTo().setDocument(compraLojaVirtual.getPessoa().getCpf());
		envioEtiquetaDTO.getTo().setAddress(compraLojaVirtual.getPessoa().enderecoEntrega().getRuaLogra());
		envioEtiquetaDTO.getTo().setComplement(compraLojaVirtual.getPessoa().enderecoEntrega().getComplemento());
		envioEtiquetaDTO.getTo().setNumber(compraLojaVirtual.getPessoa().enderecoEntrega().getNumero());
		envioEtiquetaDTO.getTo().setDistrict(compraLojaVirtual.getPessoa().enderecoEntrega().getEstado());
		//envioEtiquetaDTO.getTo().setDistrict("PR");
		envioEtiquetaDTO.getTo().setCity(compraLojaVirtual.getPessoa().enderecoEntrega().getCidade());
		envioEtiquetaDTO.getTo().setState_abbr(compraLojaVirtual.getPessoa().enderecoEntrega().getUf());
		envioEtiquetaDTO.getTo().setCountry_id(compraLojaVirtual.getPessoa().enderecoEntrega().getUf());
		envioEtiquetaDTO.getTo().setCountry_id("BR");
		envioEtiquetaDTO.getTo().setPostal_code(compraLojaVirtual.getPessoa().enderecoEntrega().getCep());
		envioEtiquetaDTO.getTo().setNote("Não há");

		//IGUAL O CODIGO ACIMA SO Q NA MAO..
		//COM O BAIXO FUNCINOU...
		//
		//envioEtiquetaDTO.getTo().setName("Pedro");
		//envioEtiquetaDTO.getTo().setPhone("4899997777");
		//envioEtiquetaDTO.getTo().setEmail("pedrogenteboa@gmail.com");
		//envioEtiquetaDTO.getTo().setDocument("55680795019");
		//envioEtiquetaDTO.getTo().setAddress("Rua Rui Barbosa");
		//envioEtiquetaDTO.getTo().setComplement("Apartamento");
		//envioEtiquetaDTO.getTo().setNumber("31");
		//envioEtiquetaDTO.getTo().setDistrict("PR");
		//envioEtiquetaDTO.getTo().setCity("Curitiba");
		//envioEtiquetaDTO.getTo().setState_abbr("PR");
		//envioEtiquetaDTO.getTo().setCountry_id("BR");
		//envioEtiquetaDTO.getTo().setPostal_code("75830-112");
		//envioEtiquetaDTO.getTo().setNote("Não há");
		
		
		System.out.println(envioEtiquetaDTO.toString());
		
		//instanciando uma LISTA do tipo PRODUCTSENVIOETIQUETADTO
		//de nome PRODUCTS
		//
		List<ProductsEnvioEtiquetaDTO> products = new ArrayList<ProductsEnvioEtiquetaDTO>();
		
		//instanciando uma VAR/OBJ do tipo ITEMVENDALOJA de nome ITEMVENDALOJA
		//q vai receber o q tiver no ITEMVENDALOJA da COMPRALOJAVIRTUAL
		//ou seja... Vai receber os PRODUTO.JAVA q estao sendo COMPRADOS
		//pelo CLIENTE
		//
		for (ItemVendaLoja itemVendaLoja : compraLojaVirtual.getItemVendaLojas()) {
			
			//instanciando um obj/var de nome DTO do tipo PRODUCTSENVIOETIQUETADTO
			//
			ProductsEnvioEtiquetaDTO dto = new ProductsEnvioEtiquetaDTO();
			
			//passando as iformações q estao no nosso ITEMVENDALOJA
			//q basicamente é uma class/entity com uma LISTA de PRODUTO.JAVA
			//q foram comprados pelo cliente da loja
			//e usando essas informacoes para preencher o DTO do tipo
			//PRODUCTSNEVIOETIQUETADTO
			//
			dto.setName(itemVendaLoja.getProduto().getNome());
			dto.setQuantity(itemVendaLoja.getQuantidade().toString());
			dto.setUnitary_value("" + itemVendaLoja.getProduto().getValorVenda().doubleValue());
			
			//adicionando o nosso DTO do tipo PRODUCTSENVIOETIQUETADTO
			//na nossa LISTA de PRODUCTSENVIOETIQUETADTO de nome PRODUCTS
			//
			products.add(dto);
			
		}
		
		//passando a lista de products acima para o nosso obj/var
		//ENVIOETIQUETADTO
		//
		envioEtiquetaDTO.setProducts(products);
		
		
		//instanciando uma LISTA do tipo VOLUMESENVIOETIQUETADTO de nome VOLUMES
		//
		List<VolumesEnvioEtiquetaDTO> volumes = new ArrayList<VolumesEnvioEtiquetaDTO>();
		
		//instanciando uma VAR/OBJ do tipo ITEMVENDALOJA de nome ITEMVENDALOJA
		//q vai receber o q tiver no ITEMVENDALOJA da COMPRALOJAVIRTUAL
		//ou seja... Vai receber os PRODUTO.JAVA q estao sendo COMPRADOS
		//pelo CLIENTE
		//
		for (ItemVendaLoja itemVendaLoja : compraLojaVirtual.getItemVendaLojas()) {
			
			//instanciando um OBJ/VAR de nome DTO do tipo VOLUMESENVIOETIQUETADTO
			//
			VolumesEnvioEtiquetaDTO dto = new VolumesEnvioEtiquetaDTO();
			
			//passando as iformações q estao no nosso ITEMVENDALOJA
			//q basicamente é uma class/entity com uma LISTA de PRODUTO.JAVA
			//q foram comprados pelo cliente da loja
			//e usando essas informacoes para preencher o DTO do tipo
			//PRODUCTSNEVIOETIQUETADTO, informacoes relacionadas ao
			//VOLUME, ou seja, altura, profundidade, peso, largura...
			//
			dto.setHeight(itemVendaLoja.getProduto().getAltura().toString());
			dto.setLength(itemVendaLoja.getProduto().getProfundidade().toString());
			dto.setWeight(itemVendaLoja.getProduto().getPeso().toString());
			dto.setWidth(itemVendaLoja.getProduto().getLargura().toString());
			
			//adicionando o nosso DTO do tipo VOLUMESENVIOETIQUETADTO na nossa
			//LISTA de EVOLUMESENVIOETIQUETADTO de nome VOLUMES
			//
			volumes.add(dto);
			
			//} TALVEZ PRECISE
		}
		
		//passando a nossa LISTA de VOLUME de nome VOLUMES para o
		//nosso OBJ/VAR ENVIOETIQUETADTO
		//
		envioEtiquetaDTO.setVolumes(volumes);
		
		//passando alguns valores estaticos/padroes para o
		//o OBJ/VAR OPTIONS q esta instanciado no ENVIOETIQUETADTO
		//
		//e outras informacoes nos pegamos da propia COMPRAVENDALOJAVIRTUAL
		//como NOME, Numero da NOTAFISCAL... etc...
		envioEtiquetaDTO.getOptions().setInsurance_value("" + compraLojaVirtual.getValorTotal().doubleValue());
		envioEtiquetaDTO.getOptions().setReceipt(false);
		envioEtiquetaDTO.getOptions().setOwn_hand(false);
		envioEtiquetaDTO.getOptions().setReverse(false);
		envioEtiquetaDTO.getOptions().setNon_commercial(false);
		envioEtiquetaDTO.getOptions().getInvoice().setKey(compraLojaVirtual.getNotaFiscalVenda().getNumero());
		//envioEtiquetaDTO.getOptions().getInvoice().setKey("31190307586261000184550010000092481404848162");
		envioEtiquetaDTO.getOptions().setPlatform(compraLojaVirtual.getEmpresa().getNomeFantasia());

		
		//instanciando um OBJ/VAR do tipo TAGSENVIODTO de nome DTOTAGENVIO
		//q vamos basicamente passar o ID da COMPRA na LOJAVIRTUAL
		//e vamos deixar em null o link direto para acessar a compra
		//na lojavirtual... tipo clique aqui e veja a compra...
		//
		TagsEnvioDto dtoTagEnvio = new TagsEnvioDto();
		dtoTagEnvio.setTag("Identificação do pedido na plataforma, exemplo:" + compraLojaVirtual.getId());
		dtoTagEnvio.setUrl(null);
		
		//passando o nosso obj/var do tipo TAGSENVIODTO de nome DTOTAGENVIO
		//para o OBJ/VAR OPTIONS q ta no nosso OBJ/VAR ENVIOETIQUETADTO
		//
		envioEtiquetaDTO.getOptions().getTags().add(dtoTagEnvio);

		//transformando o obj/var ENVIOETIQUEDTO em um STRING
		//de nome JSONENVIO
		//
		String jsonEnvio = new ObjectMapper().writeValueAsString(envioEtiquetaDTO);
		
		//basicamente usando o TOKEN, o EMAIL, e preenchendo os cabecalhos
		//e TBM nos transformamos o OBJ/VAR JSONENVIO q e do TIPO STRING
		//e um OBJ/VAR de nome BODY do tipo JSON
		//dai nos enviamos ele para a URL da API DO MELHORENVIO
		//
		//dai dessa forma nos vamos conseguir passar todas as informacoes
		//q a API do MELHORENVIO pede, dai vamos conseguir CADASTRAR
		//um ENVIO... E eles vao falar o valor para transportar e tals...
		//DAIII dps em outro metodo se a gente concordar com o valor
		//nos fazemos a COMPRA desse ENVIO... Dai a transportadora
		//vai gerar uma etiqueta, o caminhao vai passar na LOJAVIRTUAL
		//pegar o pacote e levar para o cliente... +ou- isso...
		//
	    OkHttpClient client = new OkHttpClient().newBuilder().build();
	    okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
	     okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, jsonEnvio);
			okhttp3.Request request = new okhttp3.Request.Builder()
			  .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/cart")
			  .method("POST", body)
			  .addHeader("Accept", "application/json")
			  .addHeader("Content-Type", "application/json")
			  .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
			  .addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
			  .build();
			
			//apos EXEC o bloco de codigo acima, ou seja enviar as info
			//para o MELHORNEVIO... Ela (MELHORENVIO) vai nos retornar
			//um JSON com um ID e algumas informacoes como valores,
			//tempo, e a transportadoras e fazem esse trageto...
			//esssas informacoes vao ser salvar no arquivo RESPONSE
			//do tipo OKHTTP3.RESPONSE
			//
			okhttp3.Response response = client.newCall(request).execute();
			
			
			String respostaJson = response.body().string();
			
			//criando um OBJ/VAR do tipo JSONNODE de nome JSONNODE q vai
			//RECEBER o valor q ta no OBJ/VAR RESPOSTA.... Q e o retorno
			//em JSON da API do MELHORENVIO...
			//
			JsonNode jsonNode = new ObjectMapper().readTree(respostaJson);
			
			//verificando se no JSON de retorno da API do MELHORENVIO
			//tem a PALAVRA ERRO escrito
			if (respostaJson.contains("error")) {
				throw new ExceptionMentoriaJava(respostaJson);
			}
			
			
			//O retorno da API do MELHORENVIO agora ta no OBJ/VAR JSONNODE
			//e vamos ITERAR sobre esse obj/var... Ou seja procurar
			//nela o CAMPO ID e salvar o ID em uma var de nome IDETIQUETA
			//do tipo STRING...
			//
			Iterator<JsonNode> iterator = jsonNode.iterator();			
			String idEtiqueta = "";
			
			while(iterator.hasNext()) {
				JsonNode node = iterator.next();
				if (node.get("id") != null) {
					idEtiqueta = node.get("id").asText();	
				} else {
					idEtiqueta = node.asText();
				}
				break;
			}

			//OBS: esse ID ali de cima, tambem chamado de 
			//CODIGOETIQUETA/IDETIQUETA... Nos vamos usar ele no proximo
			//passo, para se caso a gente CONCORDE com o VALOR, E o TEMPO
			//para transporte, nos vamos passar esse ID para um outro
			//metodo da APIMELHORENVIO e dizer q queremos COMPRAR esse
			//TRANSPORTE, ou seja q SIM o caminhao pd vir buscar o produto
			//na LOJAVIRTUAL e levar para o CLIENTE(pessoa q comprou o 
			//produto)... Dai a API do MELHORENVIO gera uma ETIQUETA
			//um papel q e colado na caixa do produto e tals...
			
			
		    /*Salvando o código da etiqueta*/
			//gravando na TABELA VENDACOMPRALOJAVIRTUAL o CODIGODAETIQUETA
			//gerado pela API DO MELHORENVIO
			//Dai vamos associar ao ID de UMA
			//VENDACOMPRALOJAVIRTUAL o ID de uma ETIQUETA
			//
			//como ta dando bug com o ORM JPA/Hibernate...SpringData...
			//o prof disse para usarmos o JDBC
			//
			jdbcTemplate.execute("begin; update vd_cp_loja_virt set codigo_etiqueta = '"+idEtiqueta+"' where id = "+compraLojaVirtual.getId()+"  ;commit;");
		    //vd_Cp_Loja_virt_repository.updateEtiqueta(idEtiqueta, compraLojaVirtual.getId());


		    //agora com o CODIGOETIQUETA em maos nos vamos COMPRAR
		    //a ETIQUETA... para isso vamos ter q passar o 
		    //IDETIQUETA para a API do MELHORENVIO...
		    //
		    //
		    //
		    //basicamente CRIAMOS obj/var CLIENTECOMPRA do tipo OKHTTPCLIENT
		    //um obj/var de nome MEDIATYPEC do tipo OKHTTP3.MEDIATYPE
		    //um obj/var de nome BODYC do tipo OKHTTP3.REQUESTBODY
		    //um obj/var de nome REQUESTC do tipo OKHTTP3.REQUEST
		    //dai preenchemos os cabecalhos informamos o nosso TOKEN
		    //e email e enviamos o ID q ta na VAR/OBJ IDETIQUETA
		    //para a URL do MELHORENVIO... ou seja informamos
		    //q vamos COMPRAR o TRANSPORTE... ou seja a pd vim buscar
		    //o produto e levar para o cliente, nos aceitamos o valor e
		    //o tempo...
			//
			OkHttpClient clientCompra = new OkHttpClient().newBuilder().build();
			 okhttp3.MediaType mediaTypeC =  okhttp3.MediaType.parse("application/json");
			 okhttp3.RequestBody bodyC =  okhttp3.RequestBody.create(mediaTypeC, "{\n    \"orders\": [\n        \""+idEtiqueta+"\"\n    ]\n}");
			 okhttp3.Request requestC = new  okhttp3.Request.Builder()
			  .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX  + "api/v2/me/shipment/checkout")
			  .method("POST", bodyC)
			  .addHeader("Accept", "application/json")
			  .addHeader("Content-Type", "application/json")
			  .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
			  .addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
			  .build();
			 
			 //exec a acao acima... de enviar o IDETIQUETA para a API do
			 //MELHORENVIO e salvando o JSON q eles deram de resultado
			 //na VAR/OBJ RESPONSEC
			 okhttp3.Response responseC = clientCompra.newCall(requestC).execute();
			 	 
			 //verificando se foi possivel realizar a compra da etiqueta
			 //
			 if (!responseC.isSuccessful()) {
				 return new ResponseEntity<String>("Não foi possível realizar a compra da etiqueta", HttpStatus.OK); 
			 }
			 

			 //pedindo para a API do MELHORENVIO GERAR a ETIQUETA
			 //para nos IMPRIMIR no PRODUTO... ETIQUETA essa q foi
			 //COMPRADA no METODO ACIMA... A ETIQUETA e COMPRADA
			 //no momento em OCLIENTE da loja aceita
			 //os valores do frete, tempo
			 //etc...
			 //
			 //basicamente instanciamos os OBJ/VAR passamos os cabecalhos
			 //informamos o TOKEN nosso... E enviamos o ID da ETIQUET
			 //para a URL da API do MELHORENVIO
			 //
				OkHttpClient clientGe = new OkHttpClient().newBuilder().build();
				 okhttp3.MediaType mediaTypeGe =  okhttp3.MediaType.parse("application/json");
				 okhttp3.RequestBody bodyGe =  okhttp3.RequestBody.create(mediaTypeGe, "{\n    \"orders\":[\n        \""+idEtiqueta+"\"\n    ]\n}");
				 okhttp3.Request requestGe = new  okhttp3.Request.Builder()
				  .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX  + "api/v2/me/shipment/generate")
				  .method("POST", bodyGe)
				  .addHeader("Accept", "application/json")
				  .addHeader("Content-Type", "application/json")
				  .addHeader("Authorization", "Bearer " +  ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
				  .addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
				  .build();
				
				 //realizando a requisicao acima... ou seja passando os
				 //parametros acima para a API do MELHORENVIO
				 //e salvando o retorno deles em JSON na var/obj
				 //RESPONSEGE do tipo OKHTTP3.RESPONSE
				 //
				 okhttp3.Response responseGe = clientGe.newCall(requestGe).execute();

			 
				 //verificando se nao deu erro
				 //
				 if (!responseGe.isSuccessful()) {
					 return new ResponseEntity<String>("Não foi possível gerar a etiqueta", HttpStatus.OK); 
				 }
				 

				 //instanciando os var/obj 
				 //o obj/var CLIENTIM do tipo OKHTTPCLIENT
				 //o obj/var MEDIATYPEIM do typo OKHTTP3.MEDIATYPE
				 //o obj/var BODYIM do tipo OKHTTP3.REQUESTBODY
				 //o obj/var REQUESTIM do tipo OKHTTP3.REQUEST
				 //dai passamos os cabecalhos
				 //e o ID da ETIQUETA q queremos q a API do MELHORENVIO
				 //gerem ou seja deixar no formato pronto para imprimir
				 //tbm passamos o nosso email e o token de autenticacao
				 //
					/*Faz impresão das etiquetas*/
					
					OkHttpClient clientIm = new OkHttpClient().newBuilder().build();
					okhttp3.MediaType mediaTypeIm = MediaType.parse("application/json");
					okhttp3.RequestBody bodyIm = okhttp3.RequestBody.create(mediaTypeIm, "{\n    \"mode\": \"private\",\n    \"orders\": [\n        \""+idEtiqueta+"\"\n    ]\n}");
							okhttp3.Request requestIm = new Request.Builder()
							  .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX  + "api/v2/me/shipment/print")
							  .method("POST", bodyIm)
							  .addHeader("Accept", "application/json")
							  .addHeader("Content-Type", "application/json")
							  .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
							  .addHeader("User-Agent", "rodrigojosefagundes@gmail.com")
							  .build();
							
							
							//executando a funcao acima, e salvando
							//o resultado q a API do MELHORENVIO retorna
							//na var/obj RESPONSEIM do tipo
							//OKHTTP3.RESPONSE
							okhttp3.Response responseIm = clientIm.newCall(requestIm).execute();

							//verificando se deu algum erro no retorno
							if (!responseIm.isSuccessful()) {
								 return new ResponseEntity<String>("Não foi imprimir a etiqueta.", HttpStatus.OK); 
							}	
							
							//salvando o retorno da API do MELHORENVIO em uma STRING
							//de nome URLETIQUETA... Lembrando o retorno e um link/url
							//para a PESSOA Q VENDEU o PRODUTO acessar e IMPRIMIR
							//a ETIQUETA e COLAR na CAIXA do PRODUTO...
							 String urlEtiqueta = responseIm.body().string();
							
							 
							 //colocando a URL da ETIQUETA gerada pela API do MELHORENVIO
							 //no campo URLETIQUETA da VENDACOMPRALOJAVIRTUAL correspondente
							 //ou seja associando tipo a VENDACOMPRALOJAVIRTUAL ID 28
							 //a URLETIQUETA http://...
							jdbcTemplate.execute("begin; update vd_cp_loja_virt set url_imprime_etiqueta =  '"+urlEtiqueta+"'  where id = " + compraLojaVirtual.getId() + ";commit;");
							//vd_Cp_Loja_virt_repository.updateURLEtiqueta(urlEtiqueta, compraLojaVirtual.getId());
							 
					        OkHttpClient clientRastreio = new OkHttpClient().newBuilder().build();
					        okhttp3.MediaType mediaTypeR = okhttp3.MediaType.parse("application/json");
					        okhttp3.RequestBody bodyR = okhttp3.RequestBody.create(mediaTypeR, "{\n    \"orders\": [\n        \"9e55f440-38c6-4491-8884-18dae893ae58\"\n    ]\n}");
					        okhttp3.Request requestR = new Request.Builder()
							  .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX+ "api/v2/me/shipment/tracking")
							  .method("POST", bodyR)
							  .addHeader("Accept", "application/json")
							  .addHeader("Content-Type", "application/json")
							  .addHeader("Authorization", "Bearer " +  ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
							  .addHeader("User-Agent", "rodrigojosefagundes@gmail.com").build();
							
					        //fazendo a requisicao acima e armazenando o RETORNO em JSON
					        //do melhor envio na VAR/OBJ RESPONSER do tipo RESPONSE
							Response responseR = clientRastreio.newCall(requestR).execute();
							
							//convertendo o RESPONSER em uma STRING e salvando com o nome de
							//JSONNODER
							JsonNode jsonNodeR = new ObjectMapper().readTree(responseR.body().string());
							
							//percorrendo o JSONNODER 
							Iterator<JsonNode> iteratorR = jsonNodeR.iterator();
							
							//crianod uma VAR de nome IDTRACKING (o prof deixou IDETIQUETAR)
							//mas  correto  IDTRACKING... pq e o ID de RASTREIO da encomenda
							String idTrackingR = "";
							
							//percorrendo a VAR/OBJ INTERETOR
							//q e onde ta o retorno da API do MELHORENVIO
							//e procurando pela palavra TRACKING
							//no JSON retornado... e salvando o valor
							//do TRACKING na var/obj IDTRACKINGR
							while(iteratorR.hasNext()) {
								JsonNode node = iteratorR.next();
								 if (node.get("tracking") != null) {
									 idTrackingR = node.get("tracking").asText();
								 }else {
									 idTrackingR = node.asText(); 
								 }
								break;
							}
							
							 List<StatusRastreio> rastreios =	statusRastreioRepository.listaRastreioVenda(idVenda);
							 
							 if (rastreios.isEmpty()) {
								 
								 StatusRastreio rastreio = new StatusRastreio();
								 rastreio.setEmpresa(compraLojaVirtual.getEmpresa());
								 rastreio.setVendaCompraLojaVirtual(compraLojaVirtual);
								 rastreio.setUrlRastreio("https://www.melhorrastreio.com.br/rastreio/" + idTrackingR);
								 
								 statusRastreioRepository.saveAndFlush(rastreio);
							 }else {
								 statusRastreioRepository.salvaUrlRastreio("https://www.melhorrastreio.com.br/rastreio/" + idTrackingR, idVenda);
							 }
							
		return new ResponseEntity<String>("Sucesso", HttpStatus.OK);
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
	
	//metodo/endpoint para gerar boleto ou gerar qrcode pix
	@ResponseBody
	@PostMapping(value = "**/gerarBoletoPix")
	public ResponseEntity<String> gerarBoletoPix(@RequestBody @Valid ObjetoPostCarneJuno objetoPostCarneJuno) throws Exception{
		return  new ResponseEntity<String>(serviceJunoBoleto.gerarCarneApi(objetoPostCarneJuno), HttpStatus.OK);
	}
	
	//metodo/endpoint para cancelar boleto ou qrcode pix
	@ResponseBody
	@PostMapping(value = "**/cancelarBoletoPix")
	public ResponseEntity<String> cancelarBoletoPix(@RequestBody @Valid String code) throws Exception{
		//chamando o metoto cancelar boleto q ta no nosso obj/var
		//servicejunoboleto
		return new ResponseEntity<String>(serviceJunoBoleto.cancelarBoleto(code), HttpStatus.OK);
	}

}
