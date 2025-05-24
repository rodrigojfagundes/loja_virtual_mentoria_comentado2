package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
	
	@Query(nativeQuery = true, value = "select count(1) > 0 from produto where upper(trim(nome)) = upper(trim(?1))")
	public boolean existeProduto(String nomeCategoria);
	

	@Query(nativeQuery = true, value = "select count(1) > 0 from produto where upper(trim(nome)) = upper(trim(?1)) and empresa_id = ?2")
	public boolean existeProduto(String nomeCategoria, Long idEmpresa);


	//o prof deixou A, mas eu mudei para P pq o A é de ACESSO e P e de PRODUTO
	@Query("select p from Produto p where upper(trim(p.nome)) like %?1%")
	public List<Produto> buscarProdutoNome(String nome);
	
	
	@Query("select p from Produto p where upper(trim(p.nome)) like %?1% and p.empresa.id = ?2")
	public List<Produto> buscarProdutoNome(String nome, Long idEmpresa);

}
