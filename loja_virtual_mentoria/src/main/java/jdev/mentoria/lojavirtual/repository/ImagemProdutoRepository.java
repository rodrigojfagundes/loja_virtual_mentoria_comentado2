package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jdev.mentoria.lojavirtual.model.ImagemProduto;

@Repository
@Transactional
public interface ImagemProdutoRepository extends JpaRepository<ImagemProduto, Long> {
	
	//PROF usou A eu usei IP pq e IMAGEMPRODUTO
	
	//metodo para buscar a imagem conforme o produto
	
	@Query("select ip from ImagemProduto ip where ip.produto.id =?1")
	List<ImagemProduto> buscaImagemProduto(Long idProduto);
	
	
	//deletar todas as imagens de um produto
	@Transactional
	@Modifying(flushAutomatically = true)
	@Query(nativeQuery = true, value = "delete from imagem_produto where produto_id = ?1")
	void deleteImagem(Long idProduto);
}
