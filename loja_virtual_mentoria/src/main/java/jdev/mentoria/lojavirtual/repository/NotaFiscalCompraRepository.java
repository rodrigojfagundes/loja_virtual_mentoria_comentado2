package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jdev.mentoria.lojavirtual.model.NotaFiscalCompra;

@Repository
@Transactional
public interface NotaFiscalCompraRepository extends JpaRepository<NotaFiscalCompra, Long> {
	
	//NFC = nota fiscal compra
	
	
	//buscando notafiscalcompra pela DESCRICAO/NOME
	@Query("select nfc from NotaFiscalCompra nfc where upper(trim(nfc.descricaoObs)) like %?1%")
	List<NotaFiscalCompra> buscarNotaDesc(String desc);
	
	@Query(nativeQuery = true, value = "select count(1) > 0 from nota_fiscal_compra where upper(descricao_obs) like %?1%")
	boolean existeNotaComDescricao(String desc);
	
	//buscando as notasfiscalcompra associadas a uma pessoa
	@Query("select nfc from NotaFiscalCompra nfc where nfc.pessoa.id = ?1")
	List<NotaFiscalCompra> buscaNotaPorPessoa(Long idPessoa);
	
	//buscar as notafiscalcompra associadas a uma contapagar
	@Query("select nfc from NotaFiscalCompra nfc where nfc.contaPagar.id = ?1")
	List<NotaFiscalCompra> buscaNotaContaPagar(Long idContaPagar);
	
	//buscar as notafiscalcompra associadas a uma empresa
	@Query("select nfc from NotaFiscalCompra nfc where nfc.empresa.id = ?1")
	List<NotaFiscalCompra> buscaNotaPorEmpresa(Long idEmpresa);
	
	//uma NOTAFISCALCOMPRAS tem uma LISTA DE NOTAITEMPRODUTOS (ou seja
	//produtos q foram comprados e fazem parte da NOTAFISCALCOMPRAS)
	//dai antes de DELETAR A NOTAFISCALCOMPRA temos q deletar esses
	//produtos q estao no NOTAITEMPRODUTOS
	//
	@Transactional
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query(nativeQuery = true, value = "delete from nota_item_produto where nota_fiscal_compra_id = ?1")
	void deleteItemNotaFiscalCompra(Long idNotaFiscalCompra);
	
	
}
