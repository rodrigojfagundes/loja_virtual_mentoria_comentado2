package jdev.mentoria.lojavirtual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jdev.mentoria.lojavirtual.model.VendaCompraLojaVirtual;



@Repository
@Transactional
public interface Vd_Cp_Loja_virt_repository extends JpaRepository<VendaCompraLojaVirtual, Long> {
	
	//O PROF COLOCOU A, mas eu ACHEI MAIS CORRETO COLOCAR VCLV
	//VENDACOMPRALOJAVIRTUAL
	//
	//aqui estamos buscando TODAS AS VENDACOMPRALOJAVIRTUAL em q o VALOR
	//da COLUNA EXCLUIDO seja FALSE... Pois se o valor da coluna EXCLUIDO
	//for TRUE dai nao vamos trazer...
	@Query(value="select vclv from VendaCompraLojaVirtual vclv where vclv.id = ?1 and vclv.excluido = false")
	VendaCompraLojaVirtual findByIdExclusao(Long id);
	
	
	
	//metodo q recebe o ID de um PRODUTO e com o ID desse PRODUTO nos buscamos
	//os ITEMVENDA q tem esse PRODUTO e e retorna uma LISTA com o ID das 
	//VENDACOMPRALOJAVIRTUAL q tiveram esse PRODUTO/
	//
	//carregando as VENDA Ligadas com o ITEMDEVENDA onde o EXCLUIDO e FALSE
	@Query(value = "select i.vendaCompraLojaVirtual from ItemVendaLoja i where i.vendaCompraLojaVirtual.excluido = false and i.produto.id = ?1")
	List<VendaCompraLojaVirtual> vendaPorProduto(Long idProduto);
}
