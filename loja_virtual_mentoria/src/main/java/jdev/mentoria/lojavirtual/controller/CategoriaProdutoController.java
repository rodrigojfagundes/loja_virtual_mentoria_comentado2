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
import jdev.mentoria.lojavirtual.model.Acesso;
import jdev.mentoria.lojavirtual.model.CategoriaProduto;
import jdev.mentoria.lojavirtual.model.dto.CategoriaProdutoDto;
import jdev.mentoria.lojavirtual.repository.CategoriaProdutoRepository;

@RestController
public class CategoriaProdutoController {
	
	@Autowired
	private CategoriaProdutoRepository categoriaProdutoRepository;
	
	
	//buscar as categorias de produtos pelo NOME/DESCRICAO
	@ResponseBody
	@GetMapping(value = "**/buscarPorDescCategoria/{desc}")
	public ResponseEntity<List<CategoriaProduto>> buscarPorDesc(@PathVariable("desc") String desc) {

		List<CategoriaProduto> categoriaProduto = categoriaProdutoRepository.buscarCategoriaDes(desc.toUpperCase());

		return new ResponseEntity<List<CategoriaProduto>>(categoriaProduto, HttpStatus.OK);

	}

	
	@ResponseBody
	@PostMapping(value = "**/deleteCategoria")
	public ResponseEntity<?> deleteCategoriaProduto(@RequestBody CategoriaProduto categoriaProduto) {

		
		if(categoriaProdutoRepository
				.findById(categoriaProduto.getId()).isPresent()) {
		
			return new ResponseEntity("Categoria ja foi Removida", HttpStatus.OK);
		}
		
		categoriaProdutoRepository.deleteById(categoriaProduto.getId());
		return new ResponseEntity("Categoria Produto Removida", HttpStatus.OK);

	}
	
	
	@ResponseBody
	@PostMapping(value = "**/salvarCategoria")
	public ResponseEntity<CategoriaProdutoDto> salvarCategoria(
			@RequestBody CategoriaProduto categoriaProduto) throws ExceptionMentoriaJava {
		
		
		if(categoriaProduto.getEmpresa() == null || (categoriaProduto.getEmpresa().getId() == null)) {
			throw new ExceptionMentoriaJava("A empresa deve ser informada");
		}
		
		if(categoriaProduto.getId() == null && categoriaProdutoRepository
				.existeCategoria(categoriaProduto.getNomeDesc())) {
			throw new ExceptionMentoriaJava("Nao pode cadastrar categoria com o mesmo nome");
		}
		
		
		CategoriaProduto categoriaSalva = categoriaProdutoRepository
				.save(categoriaProduto);
		
		//convertendo o CATEGORIAPRODUTO para CATEGORIAPRODUTODTO
		CategoriaProdutoDto categoriaProdutoDto = new CategoriaProdutoDto();
		categoriaProdutoDto.setId(categoriaSalva.getId());
		categoriaProdutoDto.setNomeDesc(categoriaSalva.getNomeDesc());
		categoriaProdutoDto.setEmpresa(categoriaSalva.getEmpresa().getId().toString());
		
		
		return new ResponseEntity<CategoriaProdutoDto>(categoriaProdutoDto, HttpStatus.OK);
	}
	
}
