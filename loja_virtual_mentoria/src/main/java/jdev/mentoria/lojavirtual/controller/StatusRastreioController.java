package jdev.mentoria.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.model.StatusRastreio;
import jdev.mentoria.lojavirtual.repository.StatusRastreioRepository;

//metodo q retorna uma lista com os STATUSRASTREIO de uma VENDACOMPRALOJAVIRTUAL
//pois cada VENDACOMPRALOJAVIRTUAL tera VARIOS STATUSRASTREIO
//1 STATUSRASREIO informando q saiu de SP... Outro STATUSRASTREIO informando
//q ta na transportadora... Outro STATUSRASTREIO informando q ta em TIJUCAS-SC
//
@RestController
public class StatusRastreioController {
	
	@Autowired
	private StatusRastreioRepository statusRastreioRepository;
	
	
	@ResponseBody
	@GetMapping(value = "**/listaRastreioVenda/{idVenda}")
	public ResponseEntity<List<StatusRastreio>> listaRastreioVenda (@PathVariable ("idVenda") Long idVenda){
		
		List<StatusRastreio> statusRastreios =	 statusRastreioRepository.listaRastreioVenda(idVenda);
		
		return new ResponseEntity<List<StatusRastreio>>(statusRastreios, HttpStatus.OK);
		
	}

}
