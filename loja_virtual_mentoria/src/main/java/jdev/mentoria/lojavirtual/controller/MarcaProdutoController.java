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
import jdev.mentoria.lojavirtual.model.MarcaProduto;
import jdev.mentoria.lojavirtual.repository.MarcaRepository;


@Controller
@RestController
public class MarcaProdutoController {
	
	
	@Autowired
	private MarcaRepository marcaRepository;

	// @ResponseBody para poder dar um retorno da API
	// @Postmapping para mapear a url para receber um JSON
	// @RequestBody recebe um JSON e converte em um OBJ do tipo MARCAPRODUTO
	// @ResponseEntity encapsula os dados em HTTP	
	@ResponseBody 
	@PostMapping(value = "**/salvarMarca")
	public ResponseEntity<MarcaProduto> salvarMarca(@RequestBody @Valid MarcaProduto marcaProduto ) throws ExceptionMentoriaJava { /*Recebe o JSON e converte pra Objeto*/
		
		// antes de cadastrar um MARCAPRODUTO sera verificado no banco se ja tem
		// algum acesso com a mesma descricao/nome
		if (marcaProduto.getId() == null) {
		  List<MarcaProduto> marcaProdutos  = marcaRepository.buscarMarcaDesc(marcaProduto.getNomeDesc().toUpperCase());
		  
		  if (!marcaProdutos.isEmpty()) {
			  throw new ExceptionMentoriaJava("Já existe Marca com a descrição/nome: " + marcaProduto.getNomeDesc());
		  }
		}
		
		
		MarcaProduto marcaProdutoSalvo = marcaRepository.save(marcaProduto);
		
		return new ResponseEntity<MarcaProduto>(marcaProdutoSalvo, HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	//Mapeando a url para receber JSON
	@PostMapping(value = "**/deleteMarca")
	public ResponseEntity<?> deleteMarca(@RequestBody MarcaProduto marcaProduto) { /*Recebe o JSON e converte pra Objeto*/
		
		marcaRepository.deleteById(marcaProduto.getId());
		
		return new ResponseEntity("Marca produto Removido",HttpStatus.OK);
	}
	

	//@Secured({ "ROLE_GERENTE", "ROLE_ADMIN" })
	@ResponseBody
	@DeleteMapping(value = "**/deleteMarcaPorId/{id}")
	public ResponseEntity<?> deleteMarcaPorId(@PathVariable("id") Long id) { 
		
		marcaRepository.deleteById(id);
		
		return new ResponseEntity("Marca Produto Removido",HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/obterMarcaProduto/{id}")
	public ResponseEntity<MarcaProduto> obterMarcaProduto(@PathVariable("id") Long id) throws ExceptionMentoriaJava { 
		
		MarcaProduto marcaProduto = marcaRepository.findById(id).orElse(null);
		
		if (marcaProduto == null) {
			throw new ExceptionMentoriaJava("Não encontrou Marca Produto com código: " + id);
		}
		
		return new ResponseEntity<MarcaProduto>(marcaProduto,HttpStatus.OK);
	}
	
	
	//buscando marcaproduto por descricao/nome
	@ResponseBody
	@GetMapping(value = "**/buscarMarcaProdutoPorDesc/{desc}")
	public ResponseEntity<List<MarcaProduto>> buscarMarcaProdutoPorDesc(@PathVariable("desc") String desc) { 
		
		List<MarcaProduto>  marcaProdutos = marcaRepository.buscarMarcaDesc(desc.toUpperCase().trim());
		
		return new ResponseEntity<List<MarcaProduto>>(marcaProdutos,HttpStatus.OK);
	}
	
	
	

}
