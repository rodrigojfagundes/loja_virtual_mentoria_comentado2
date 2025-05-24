package jdev.mentoria.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.model.CupDesc;
import jdev.mentoria.lojavirtual.repository.CupDescontoRepository;

@RestController
public class CupDescontoController {
	
	@Autowired
	private CupDescontoRepository cupDescontoRepository;
	
	
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
