package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.Acesso;
import jdev.mentoria.lojavirtual.model.CategoriaProduto;

@Repository
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long>{

	//metodo para verificar se a DESCRICAO/NOME da categoriaproduto a ser cadastrada
	//ja nao e repetida na EMPRESA... Tipo EMPRESA ABC nao pode ter 2 ou mais
	//CATEGORIAPRODUTO com o nome TECNOLOGIA cadastrada na empresa/loja...
	@Query(nativeQuery = true, value = "select count(1) > 0 from categoria_produto where upper(trim(nome_desc)) = upper(trim(?1))")
	public boolean existeCategoria(String nomeCategoria);

	//buscar CATEGORIAPRODUTO pela descricao/nome
	@Query("select cp from CategoriaProduto cp where upper(trim(cp.nomeDesc)) like %?1%")
	public List<CategoriaProduto> buscarCategoriaDes(String nomeDesc);
	
}
