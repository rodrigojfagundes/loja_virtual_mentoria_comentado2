package jdev.mentoria.lojavirtual.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;

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
import jdev.mentoria.lojavirtual.service.ServiceSendEmail;

@Controller
@RestController
public class ProdutoController {

	@Autowired
	private AcessoService acessoService;
	
	@Autowired
	private ServiceSendEmail serviceSendEmail;

	@Autowired
	private ProdutoRepository produtoRepository;

	// @ResponseBody para poder dar um retorno da API
	// @Postmapping para mapear a url para receber um JSON
	// @RequestBody recebe um JSON e converte em um OBJ do tipo PRODUTO
	// @ResponseEntity encapsula os dados em HTTP
	@ResponseBody
	@PostMapping(value = "**/salvarProduto")
	//OBS O PROF DEIXOU O NOME COMO SALVARACESSO, mas o CORRETO E SALVARPRODUTO
	public ResponseEntity<Produto> salvarProduto(@RequestBody @Valid Produto produto) throws ExceptionMentoriaJava, MessagingException, IOException {
		
		if(produto.getTipoUnidade() == null || produto.getTipoUnidade().trim().isEmpty()) {
			throw new ExceptionMentoriaJava("Tipo da unidadedeve ser informada");
		}
		
		if(produto.getNome().length() < 10) {
			throw new ExceptionMentoriaJava("O nome do produto deve ter mais de 10 letras");
		}
		
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
		
		if(produto.getQtdEstoque() < 1) {
			throw new ExceptionMentoriaJava("O produto deve ter no minimo 1 no estoque");
		}
		
		if(produto.getImagens() == null || produto.getImagens().isEmpty() || produto.getImagens().size() == 0) {
			throw new ExceptionMentoriaJava("Deve ser informado imagens para o produto");
		}
		if (produto.getImagens().size() < 3) {
			throw new ExceptionMentoriaJava("Deve ser informado pelo menos 3 imagens para o produto");
		}
		if(produto.getImagens().size() > 6) {
			throw new ExceptionMentoriaJava("Deve ser informado no maximo 6 imagens");
		}
		
		//para podermos associar uma imagem a um produto
		//pois quando a imagem chega por JSON nao se sabe de
		//qual produto q e a imagem
		//
		//estamos associando a IMAGEM X ao PRODUTO... Isso para poder
		//fazer associacao de chave estrangeira no BANCO
		//
		//a IMAGEM vai vim criptografada em BASE64 atraves do JSON
		if (produto.getId() == null) {
			for(int x = 0; x < produto.getImagens().size(); x++) {
				produto.getImagens().get(x).setProduto(produto);
				produto.getImagens().get(x).setEmpresa(produto.getEmpresa());
				
				String base64Image = "";
				
				if(produto.getImagens().get(x).getImagemOriginal().contains("data:image")) {
					base64Image = produto.getImagens()
							.get(x).getImagemOriginal().split(",")[1];
				} else {
					base64Image = produto
							.getImagens().get(x).getImagemOriginal();
				}
				
				//converter a imagem de Base64 para um array/list de byte
				byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
				
				
				BufferedImage bufferedImage =  ImageIO
						.read(new ByteArrayInputStream(imageBytes));
				
				//se conseguiu converter de Base64 para lista/array de bytes
				if(bufferedImage != null) {
					//verificando o tipo da imagem se ela é png, jpg, etc...
					
					int type = bufferedImage.getType() == 0 ?
							BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
					
					//convertendo a imagem para 800x600
					int largura = Integer.parseInt("800");
					int altura = Integer.parseInt("600");
					
					//agora vamos escrever/gravar a nova IMAGEM com o
					//tamanho novo ou SEJA 800x600
					BufferedImage resizedImage = new BufferedImage(
							largura, altura, type);
					
					Graphics2D g = resizedImage.createGraphics();
					
					g.drawImage(bufferedImage, 0, 0, largura, altura, null);
					//mostrando na tela
					g.dispose();
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					
					//agora vamos escrever ele em um formato tipo o PNG
					ImageIO.write(resizedImage, "png", baos);
					
					//agora temos a miniatura da imagem
					String miniImgBase64 = "data:image/png;base64" + DatatypeConverter
							.printBase64Binary(baos.toByteArray());
					
					produto.getImagens().get(x).setImagemMiniatura(miniImgBase64);
					
					//agora vamos limpar os valores q estao nas variaveis/atributos
					//para nao ocupar mta memoria RAM
					bufferedImage.flush();
					resizedImage.flush();
					baos.flush();
					baos.close();
					
					
				}
			}
		}
		
		
		Produto produtoSalvo = produtoRepository.save(produto);
		
		if(produto.getAlertaQtdeEstoque() && produto.getQtdEstoque() <= 1) {
			StringBuilder html = new StringBuilder();
			html.append("<h2>")
			.append("Produto: " + produto.getNome())
			.append(" com estoque baixo: " + produto.getQtdEstoque());
			html.append("<p> Id Prod.:").append(produto.getId()).append("</p>");
			
			if(produto.getEmpresa().getEmail() != null) {
				serviceSendEmail.enviarEmailHtml(
						"Produto sem estoque", html.toString(), 
						produto.getEmpresa().getEmail());
			}
			
		}
		
		
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
