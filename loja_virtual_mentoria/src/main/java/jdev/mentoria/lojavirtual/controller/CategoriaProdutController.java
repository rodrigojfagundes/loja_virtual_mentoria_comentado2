package jdev.mentoria.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.model.CategoriaProduto;
import jdev.mentoria.lojavirtual.model.dto.CatgoriaProdutoDto;
import jdev.mentoria.lojavirtual.repository.CategoriaProdutoRepository;

@RestController
public class CategoriaProdutController {
	
	@Autowired
	private CategoriaProdutoRepository categoriaProdutoRepository; 
	
	//buscar as categorias de produtos pelo NOME/DESCRICAO
	@ResponseBody
	@GetMapping(value = "**/buscarPorDescCategoria/{desc}")
	public ResponseEntity<List<CategoriaProduto>> buscarPorDesc(@PathVariable("desc") String desc) { 
		
		//o prof fez um OBJ com o nome de ACESSO, mas como e uma LISTA de
		//CATEGORIAPRODUTO... entao acho mais adequado o nome de 
		//categoriaProduto
		List<CategoriaProduto> categoriaProduto = categoriaProdutoRepository.buscarCategoriaDes(desc.toUpperCase());
		
		return new ResponseEntity<List<CategoriaProduto>>(categoriaProduto,HttpStatus.OK);
	}
	
	
	
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/deleteCategoria") /*Mapeando a url para receber JSON*/
	public ResponseEntity<?> deleteAcesso(@RequestBody CategoriaProduto categoriaProduto) { /*Recebe o JSON e converte pra Objeto*/
		
		if (categoriaProdutoRepository.findById(categoriaProduto.getId()).isPresent() == false) {
			return new ResponseEntity("Categoria já foi removida",HttpStatus.OK);
		}
		
		categoriaProdutoRepository.deleteById(categoriaProduto.getId());
		
		return new ResponseEntity("Categoria Removida",HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping(value = "**/salvarCategoria")
	public ResponseEntity<CatgoriaProdutoDto> salvarCategoria(@RequestBody CategoriaProduto categoriaProduto) throws ExceptionMentoriaJava {
		
		if (categoriaProduto.getEmpresa() == null || (categoriaProduto.getEmpresa().getId() == null)) {
			throw new ExceptionMentoriaJava("A empresa deve ser informada.");
		}
		
		if (categoriaProduto.getId() == null && categoriaProdutoRepository.existeCatehoria(categoriaProduto.getNomeDesc())) {
			throw new ExceptionMentoriaJava("Não pode cadastar categoria com mesmo nome.");
		}
		
		//convertendo o CATEGORIAPRODUTO para CATEGORIAPRODUTODTO
		CategoriaProduto categoriaSalva = categoriaProdutoRepository.save(categoriaProduto);
		
		CatgoriaProdutoDto catgoriaProdutoDto = new CatgoriaProdutoDto();
		catgoriaProdutoDto.setId(categoriaSalva.getId());
		catgoriaProdutoDto.setNomeDesc(categoriaSalva.getNomeDesc());
		catgoriaProdutoDto.setEmpresa(categoriaSalva.getEmpresa().getId().toString());
		
		return new ResponseEntity<CatgoriaProdutoDto>(catgoriaProdutoDto, HttpStatus.OK);
	}

}
