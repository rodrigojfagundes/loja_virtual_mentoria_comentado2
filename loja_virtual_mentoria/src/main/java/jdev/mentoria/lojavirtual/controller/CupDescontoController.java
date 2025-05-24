package jdev.mentoria.lojavirtual.controller;

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
import jdev.mentoria.lojavirtual.model.CupDesc;
import jdev.mentoria.lojavirtual.model.MarcaProduto;
import jdev.mentoria.lojavirtual.repository.CupDescontoRepository;

@RestController
public class CupDescontoController {
	
	@Autowired
	private CupDescontoRepository cupDescontoRepository;
	
	
	
	@ResponseBody
	@GetMapping(value = "**/obterCupom/{id}")
	public ResponseEntity<CupDesc> obterCupom(@PathVariable("id") Long id) throws ExceptionMentoriaJava {

		CupDesc cupDesc = cupDescontoRepository.findById(id).orElse(null);

		if (cupDesc == null) {
			throw new ExceptionMentoriaJava("Não encontrou CupomDesconto com codigo: " + id);
		}

		return new ResponseEntity<CupDesc>(cupDesc, HttpStatus.OK);

	}
	
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteCupomPorId/{id}")
	public ResponseEntity<?> deleteCupomPorId(@PathVariable("id") Long id) {

		cupDescontoRepository.deleteById(id);
		return new ResponseEntity("CupomDesconto Removido", HttpStatus.OK);

	}

	
	@ResponseBody
	@PostMapping(value = "**/salvarCupDesconto")
	public ResponseEntity<CupDesc> salvarCupDesc(
			@RequestBody @Valid CupDesc cupDesc) throws ExceptionMentoriaJava {

		
		CupDesc  cupDesc2 = cupDescontoRepository.save(cupDesc);
		
		return new ResponseEntity<CupDesc>(cupDesc2, HttpStatus.OK);

			
	}

	
	
	//buscando os CUPONSDESCONTOS de uma EMPRESA por ID da EMPRESA
	@ResponseBody
	@GetMapping(value = "**/listaCupomDesc/{idEmpresa}")
	public ResponseEntity<List<CupDesc>> listaCupomDesc(
			@PathVariable("idEmpresa") Long idEmpresa){		
		
		return new ResponseEntity<List<CupDesc>>(cupDescontoRepository
				.cupDescontoPorEmpresa(idEmpresa), HttpStatus.OK);		
		
	}
	
	
	
	//metodo para trazer todos os cupoms descontos da plataforma...
	//de todas as empresas/lojas
	@ResponseBody
	@GetMapping(value = "**/listaCupomDesc")
	public ResponseEntity<List<CupDesc>> listaCupomDesc(){		
		
		return new ResponseEntity<List<CupDesc>>(cupDescontoRepository
				.findAll(), HttpStatus.OK);		
		
	}
	
}
