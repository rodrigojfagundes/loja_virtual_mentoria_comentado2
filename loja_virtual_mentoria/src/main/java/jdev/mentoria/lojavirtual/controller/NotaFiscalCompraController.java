package jdev.mentoria.lojavirtual.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.model.NotaFiscalCompra;
import jdev.mentoria.lojavirtual.model.NotaFiscalVenda;
import jdev.mentoria.lojavirtual.model.dto.ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO;
import jdev.mentoria.lojavirtual.model.dto.ObejtoRequisicaoRelatorioProdutoAlertaEstoque;
import jdev.mentoria.lojavirtual.model.dto.ObjetoRelatorioStatusCompra;
import jdev.mentoria.lojavirtual.repository.NotaFiscalCompraRepository;
import jdev.mentoria.lojavirtual.repository.NotaFiscalVendaRepository;
import jdev.mentoria.lojavirtual.service.NotaFiscalCompraService;

@RestController
public class NotaFiscalCompraController {
	
	@Autowired
	private NotaFiscalCompraRepository notaFiscalCompraRepository;
	
	@Autowired
	private NotaFiscalVendaRepository notaFiscalVendaRepository;
	
	@Autowired
	private NotaFiscalCompraService notaFiscalCompraService;
	
	
	@ResponseBody
	@PostMapping(value = "**/relatorioStatusCompra")
	public ResponseEntity<List<ObjetoRelatorioStatusCompra>> relatorioStatusCompra (@Valid 
			             @RequestBody  ObjetoRelatorioStatusCompra objetoRelatorioStatusCompra){
		
		List<ObjetoRelatorioStatusCompra> retorno = new ArrayList<ObjetoRelatorioStatusCompra>();
		
		retorno = notaFiscalCompraService.relatorioStatusVendaLojaVirtual(objetoRelatorioStatusCompra);
		
		return new ResponseEntity<List<ObjetoRelatorioStatusCompra>>(retorno, HttpStatus.OK);
		
	}
	
	//relatorio de produto comprado pela NOTAFISCAL
	@ResponseBody
	@PostMapping(value = "**/relatorioProdCompradoNotaFiscal")
	public ResponseEntity<List<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO>> relatorioProdCompradoNotaFiscal
	    (@Valid @RequestBody ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO obejtoRequisicaoRelatorioProdCompraNotaFiscalDto){
		
		List<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO> retorno = 
				new ArrayList<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO>();
		
		retorno = notaFiscalCompraService.gerarRelatorioProdCompraNota(obejtoRequisicaoRelatorioProdCompraNotaFiscalDto);
		
		
		return new ResponseEntity<List<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDTO>>(retorno, HttpStatus.OK);
		
	}
	
	
	//RELATORIO PRODUTO ALERTA ESTOQUE BAIXO
	@ResponseBody
	@PostMapping(value = "**/relatorioProdAlertaEstoque")
	public ResponseEntity<List<ObejtoRequisicaoRelatorioProdutoAlertaEstoque>> relatorioProdAlertaEstoque
	    (@Valid @RequestBody ObejtoRequisicaoRelatorioProdutoAlertaEstoque obejtoRequisicaoRelatorioProdCompraNotaFiscalDto ){
		
		List<ObejtoRequisicaoRelatorioProdutoAlertaEstoque> retorno = 
				new ArrayList<ObejtoRequisicaoRelatorioProdutoAlertaEstoque>();
		
		retorno = notaFiscalCompraService.gerarRelatorioAlertaEstoque(obejtoRequisicaoRelatorioProdCompraNotaFiscalDto);
		
		
		return new ResponseEntity<List<ObejtoRequisicaoRelatorioProdutoAlertaEstoque>>(retorno, HttpStatus.OK);
		
	}
	
	
	
	
	@ResponseBody 
	@PostMapping(value = "**/salvarNotaFiscalCompra")
	public ResponseEntity<NotaFiscalCompra> salvarNotaFiscalCompra(@RequestBody @Valid NotaFiscalCompra notaFiscalCompra) throws ExceptionMentoriaJava { /*Recebe o JSON e converte pra Objeto*/
		
		if (notaFiscalCompra.getId() == null) {
		  
		  	//ESSE DE BAIXO FUNCIONA ele chama o metodo BUSCARNOTADESC
			//q ta no NOTAFISCALCOMPRAREPOSITORY... Mas como deu BUG no do
			//prof vou fazer outro metodo q chama o EXISTENOTACOMDESCRICAO
			//no NOTAFISCALREPOSITORY
//		if(notaFiscalCompra.getDescricaoObs() != null) {
//			List<NotaFiscalCompra> fiscalCompras = notaFiscalCompraRepository
//					.buscarNotaDesc(notaFiscalCompra.getDescricaoObs().toUpperCase().trim());
	
		  
			if (notaFiscalCompra.getDescricaoObs() != null) {
				boolean existe = notaFiscalCompraRepository.existeNotaComDescricao(notaFiscalCompra.getDescricaoObs().toUpperCase().trim());
			   
			   
			   	//ERA ASSIM e FUNCIONAVA 58 min AULA 13 sessao 5
				//if(!fiscalCompras.isEmpty()) {
				//	throw new ExceptionMentoriaJava("ja existe Nota de Compra com essa mesma descricao" + notaFiscalCompra.getDescricaoObs());
				//}
			   
				if(existe) {
				   throw new ExceptionMentoriaJava("Já existe Nota de compra com essa mesma descrição : " + notaFiscalCompra.getDescricaoObs());
			   }
			}
			
			
		}
		
		if (notaFiscalCompra.getPessoa() == null || notaFiscalCompra.getPessoa().getId() <= 0) {
			throw new ExceptionMentoriaJava("A Pessoa Juridica da nota fiscal deve ser informada.");
		}
		
		
		if (notaFiscalCompra.getEmpresa() == null || notaFiscalCompra.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("A empresa responsável deve ser infromada.");
		}
		
		
		if (notaFiscalCompra.getContaPagar() == null || notaFiscalCompra.getContaPagar().getId() <= 0) {
			throw new ExceptionMentoriaJava("A cponta a pagar da nota deve ser informada.");
		}
		
		
		NotaFiscalCompra notaFiscalCompraSalva = notaFiscalCompraRepository.save(notaFiscalCompra);
		
		return new ResponseEntity<NotaFiscalCompra>(notaFiscalCompraSalva, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteNotaFiscalCompraPorId/{id}")
	public ResponseEntity<?> deleteNotaFiscalCompraPorId(@PathVariable("id") Long id) { 
		
		//ANTES de DELETAR a NOTAFISCALCOMPRA temos q deletar a
		//LISTA de ITEMSPRODUTOS q estao nessa NOTAFISCALCOMPRA...
		//ou seja TEMOS q ANTES DELETAR OS PRODUTOS Q ESTAO NA NOTAFISCAL
		//
		notaFiscalCompraRepository.deleteItemNotaFiscalCompra(id);
		//aqui vamos realmente deletar a NOTAFISCALCOMPRA
		notaFiscalCompraRepository.deleteById(id); /*Deleta o pai*/
		
		return new ResponseEntity("Nota Fiscal Compra Removida",HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/obterNotaFiscalCompra/{id}")
	public ResponseEntity<NotaFiscalCompra> obterNotaFiscalCompra(@PathVariable("id") Long id) throws ExceptionMentoriaJava { 
		
		NotaFiscalCompra notaFiscalCompra = notaFiscalCompraRepository.findById(id).orElse(null);
		
		if (notaFiscalCompra == null) {
			throw new ExceptionMentoriaJava("Não encontrou Nota Fiscal com código: " + id);
		}
		
		return new ResponseEntity<NotaFiscalCompra>(notaFiscalCompra, HttpStatus.OK);
	}
	
		
	//metodo para trazer uma LISTA de NOTAFISCALVENDA q faz parte de uma
	//VENDACOMPRALOJAVIRTUAL
	@ResponseBody
	@GetMapping(value = "**/obterNotaFiscalCompraDaVenda/{idvenda}")
	public ResponseEntity<List<NotaFiscalVenda>> obterNotaFiscalCompraDaVenda(@PathVariable("idvenda") Long idvenda) throws ExceptionMentoriaJava { 
		
		
		//OBS: o PROF DEIXOU O NOME DO OBJ/VAR como NOTAFISCALCOMPRA...
		//mas o CORRETO para o nome do OBJ/VAR e NOTAFISCALVENDA
		List<NotaFiscalVenda> notaFiscalVenda = notaFiscalVendaRepository.buscaNotaPorVenda(idvenda);
		
		if (notaFiscalVenda == null) {
			throw new ExceptionMentoriaJava("Não encontrou Nota Fiscal de venda com código da venda: " + idvenda);
		}
		
		return new ResponseEntity<List<NotaFiscalVenda>>(notaFiscalVenda, HttpStatus.OK);
	}
	
	//metodo para trazer uma de NOTAFISCALVENDA q faz parte de uma
	//VENDACOMPRALOJAVIRTUAL
	@ResponseBody
	@GetMapping(value = "**/obterNotaFiscalCompraDaVendaUnico/{idvenda}")
	public ResponseEntity<NotaFiscalVenda> obterNotaFiscalCompraDaVendaUnico(@PathVariable("idvenda") Long idvenda) throws ExceptionMentoriaJava { 
		
		//OBS: o PROF DEIXOU O NOME DO OBJ/VAR como NOTAFISCALCOMPRA...
		//mas o CORRETO para o nome do OBJ/VAR e NOTAFISCALVENDA
		NotaFiscalVenda notaFiscalVenda = notaFiscalVendaRepository.buscaNotaPorVendaUnica(idvenda);
		
		if (notaFiscalVenda == null) {
			throw new ExceptionMentoriaJava("Não encontrou Nota Fiscal de venda com código da venda: " + idvenda);
		}
		
		return new ResponseEntity<NotaFiscalVenda>(notaFiscalVenda, HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/buscarNotaFiscalPorDesc/{desc}")
	public ResponseEntity<List<NotaFiscalCompra>> buscarNotaFiscalPorDesc(@PathVariable("desc") String desc) { 
		
		List<NotaFiscalCompra>  notaFiscalCompras = notaFiscalCompraRepository.buscarNotaDesc(desc.toUpperCase().trim());
		
		return new ResponseEntity<List<NotaFiscalCompra>>(notaFiscalCompras,HttpStatus.OK);
	}
	

}
