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
import jdev.mentoria.lojavirtual.model.Acesso;
import jdev.mentoria.lojavirtual.model.Produto;
import jdev.mentoria.lojavirtual.repository.AcessoRepository;
import jdev.mentoria.lojavirtual.repository.ProdutoRepository;
import jdev.mentoria.lojavirtual.service.AcessoService;

@Controller
@RestController
public class ProdutoController {

	@Autowired
	private AcessoService acessoService;

	@Autowired
	private ProdutoRepository produtoRepository;

	// @ResponseBody para poder dar um retorno da API
	// @Postmapping para mapear a url para receber um JSON
	// @RequestBody recebe um JSON e converte em um OBJ do tipo PRODUTO
	// @ResponseEntity encapsula os dados em HTTP
	@ResponseBody
	@PostMapping(value = "**/salvarProduto")
	//OBS O PROF DEIXOU O NOME COMO SALVARACESSO, mas o CORRETO E SALVARPRODUTO
	public ResponseEntity<Produto> salvarProduto(@RequestBody @Valid Produto produto) throws ExceptionMentoriaJava {
		
		if(produto.getEmpresa() == null || produto.getEmpresa().getId() <=0) {
			throw new ExceptionMentoriaJava("Empresa responsavel deve ser informada");
		}
		
		if (produto.getId() == null) {
			List<Produto> produtos = produtoRepository.buscarProdutoNome(produto.getNome().toUpperCase(), produto.getEmpresa().getId());

			if (!produtos.isEmpty()) {
				throw new ExceptionMentoriaJava("Ja existe produto com esse nome: " + produto.getNome());
			}
		}
		
		if(produto.getCategoriaProduto() == null || produto.getCategoriaProduto().getId() <=0) {
			throw new ExceptionMentoriaJava("Categoria deve ser informada");
		}
		
		if(produto.getMarcaProduto() == null || produto.getMarcaProduto().getId() <=0) {
			throw new ExceptionMentoriaJava("Marca deve ser informada");
		}
		
		Produto produtoSalvo = produtoRepository.save(produto);
		return new ResponseEntity<Produto>(produtoSalvo, HttpStatus.OK);

	}

	@ResponseBody
	@PostMapping(value = "**/deleteProduto")
	public ResponseEntity<String> deleteProduto(@RequestBody Produto produto) {

		produtoRepository.deleteById(produto.getId());
		return new ResponseEntity<String>("Produto Removido", HttpStatus.OK);

	}

	@ResponseBody
	@DeleteMapping(value = "**/deleteProdutoPorId/{id}")
	public ResponseEntity<String> deleteProdutoPorId(@PathVariable("id") Long id) {

		produtoRepository.deleteById(id);
		return new ResponseEntity<String>("Produto Removido", HttpStatus.OK);

	}

	@ResponseBody
	@GetMapping(value = "**/obterProduto/{id}")
	public ResponseEntity<Produto> obterProduto(@PathVariable("id") Long id) throws ExceptionMentoriaJava {

		Produto produto = produtoRepository.findById(id).orElse(null);

		if (produto == null) {
			throw new ExceptionMentoriaJava("Não encontrou Produto com codigo: " + id);
		}

		return new ResponseEntity<Produto>(produto, HttpStatus.OK);

	}

	@ResponseBody
	@GetMapping(value = "**/buscarProdNome/{nome}")
	public ResponseEntity<List<Produto>> buscarProdNome(@PathVariable("nome") String nome) {

		List<Produto> produto = produtoRepository.buscarProdutoNome(nome.toUpperCase());

		return new ResponseEntity<List<Produto>>(produto, HttpStatus.OK);

	}

}
