package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jdev.mentoria.lojavirtual.model.NotaFiscalCompra;
import jdev.mentoria.lojavirtual.model.NotaItemProduto;

@Repository
@Transactional
public interface NotaItemProdutoRepository extends JpaRepository<NotaItemProduto, Long> {
	
	//buscar os itens de uma nota
	//
	//o prof usou A eu preferi usar NIP por causa q e NOTAITEMPRODUTO
	//
	//verificar se para o ID produto ja tem NOTAITEMCOMPRA cadastrada ou se
	//para essa NOTAITEMPRODUTO ja existe esse produto
	@Query("select nip from NotaItemProduto nip where nip.produto.id = ?1 and nip.notaFiscalCompra.id = ?2")
	List<NotaItemProduto> buscaNotaItemPorProdutoNota(Long idProduto, Long idNotaFiscal);
	
	
	//buscar notaitemproduto de um determinado produto
	@Query("select nip from NotaItemProduto nip where nip.produto.id = ?1")
	List<NotaItemProduto> buscaNotaItemProduto(Long idProduto);
	
	//buscar os ITENS de uma NOTAITEMPRODUTO
	@Query("select nip from NotaItemProduto nip where nip.notaFiscalCompra.id = ?2")
	List<NotaItemProduto> buscaNotaItemPorNotaFiscal(Long idNotaFiscal);
	
	//buscar NOTAITEMPRODUTO por EMPRESA
	@Query("select nip from NotaItemProduto nip where nip.empresa.id = ?2")
	List<NotaFiscalCompra> buscaNotaItemPorEmpresa(Long idEmpresa);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "delete from nota_item_produto where id = ?1")
	void deleteByIdNotaItem(Long id);

}
