package jdev.mentoria.lojavirtual.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.model.ContaPagar;
import jdev.mentoria.lojavirtual.repository.ContaPagarRepository;

@Controller
@RestController
public class ContaPagarController {


	@Autowired
	private ContaPagarRepository contaPagarRepository;

	// @ResponseBody para poder dar um retorno da API
	// @Postmapping para mapear a url para receber um JSON
	// @RequestBody recebe um JSON e converte em um OBJ do tipo CONTAPAGAR
	// @ResponseEntity encapsula os dados em HTTP
	@ResponseBody
	@PostMapping(value = "**/salvarContaPagar")
	//OBS O PROF DEIXOU O NOME COMO SALVARACESSO, mas o CORRETO E SALVARPRODUTO
	public ResponseEntity<ContaPagar> salvarContaPagar(@RequestBody @Valid ContaPagar contaPagar) throws ExceptionMentoriaJava {
		
		if(contaPagar.getEmpresa() == null || contaPagar.getEmpresa().getId() <=0) {
			throw new ExceptionMentoriaJava("Empresa responsavel deve ser informada");
		}
	

		if(contaPagar.getPessoa() == null || contaPagar.getPessoa().getId() <=0) {
			throw new ExceptionMentoriaJava("Pessoa responsavel deve ser informada");
		}
	
		if(contaPagar.getPessoa_fornecedor() == null || contaPagar.getPessoa_fornecedor().getId() <=0) {
			throw new ExceptionMentoriaJava("Fornecedor responsavel deve ser informada");
		}
		
		//se o ID tiver NULL e pq e uma conta nova
		if(contaPagar.getId() == null) {
			List<ContaPagar> contaPagars = contaPagarRepository.buscaContaDesc(
					contaPagar.getDescricao().toUpperCase().trim());
			if (!contaPagars.isEmpty()) {
				throw new ExceptionMentoriaJava("Ja existe conta a pagar com a mesma descricao");
			}
		}
		
		
		ContaPagar contaPagarSalva = contaPagarRepository.save(contaPagar);
		return new ResponseEntity<ContaPagar>(contaPagarSalva, HttpStatus.OK);

	}

	@ResponseBody
	@PostMapping(value = "**/deleteContaPagar")
	public ResponseEntity<String> deleteContaPagar(@RequestBody ContaPagar contaPagar) {

		contaPagarRepository.deleteById(contaPagar.getId());
		return new ResponseEntity<String>("ContaPagar Removido", HttpStatus.OK);

	}

	@ResponseBody
	@DeleteMapping(value = "**/deleteContaPagarPorId/{id}")
	public ResponseEntity<String> deleteContaPagarPorId(@PathVariable("id") Long id) {

		contaPagarRepository.deleteById(id);
		return new ResponseEntity<String>("ContaPagar Removido", HttpStatus.OK);

	}

	@ResponseBody
	@GetMapping(value = "**/obterContaPagar/{id}")
	public ResponseEntity<ContaPagar> obterContaPagar(@PathVariable("id") Long id) throws ExceptionMentoriaJava {

		ContaPagar contaPagar = contaPagarRepository.findById(id).orElse(null);

		if (contaPagar == null) {
			throw new ExceptionMentoriaJava("Não encontrou ContaPagar com codigo: " + id);
		}

		return new ResponseEntity<ContaPagar>(contaPagar, HttpStatus.OK);

	}

	@ResponseBody
	@GetMapping(value = "**/buscarContaPagarDesc/{desc}")
	public ResponseEntity<List<ContaPagar>> buscarContaPagarDesc(@PathVariable("desc") String desc) {

		List<ContaPagar> contaPagar = contaPagarRepository.buscaContaDesc(desc.toUpperCase());

		return new ResponseEntity<List<ContaPagar>>(contaPagar, HttpStatus.OK);

	}

}
