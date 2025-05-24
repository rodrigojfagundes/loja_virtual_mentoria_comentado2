package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.NotaFiscalVenda;


@Repository
public interface NotaFiscalVendaRepository extends JpaRepository<NotaFiscalVenda, Long> {

	
	//Acessando NOTAFISCALVENDA (N) por NOTAFISCALVENDA q estao dentro do
	//VENDACOMPRALOJAVIRTUAL... E retornando uma LISTA com essas NOTAFISCALVENDA
	//	
	@Query(value = "select n from NotaFiscalVenda n where n.vendaCompraLojaVirtual.id = ?1")
	List<NotaFiscalVenda> buscaNotaPorVenda(Long idVenda);
	
	//Acessando NOTAFISCALVENDA (N) por NOTAFISCALVENDA q estao dentro do
	//VENDACOMPRALOJAVIRTUAL... E retornando uma NOTAFISCALVENDA
	//
	@Query(value = "select n from NotaFiscalVenda n where n.vendaCompraLojaVirtual.id = ?1")
	NotaFiscalVenda buscaNotaPorVendaUnica(Long idVenda);
}
