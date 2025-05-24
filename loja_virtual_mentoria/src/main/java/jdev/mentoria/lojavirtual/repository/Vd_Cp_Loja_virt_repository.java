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
	//dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL em q o PRODUTO
	//tenha o ID q foi PASSADO PARA Long IDPRODUTO
	//
	@Query(value = "select i.vendaCompraLojaVirtual from ItemVendaLoja i where i.vendaCompraLojaVirtual.excluido = false and i.produto.id = ?1")
	List<VendaCompraLojaVirtual> vendaPorProduto(Long idProduto);


	//metodo q recebe o NOME de um PRODUTO e com o NOME desse PRODUTO nos buscamos
	//dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL em q o PRODUTO
	//tenha o NOME q foi PASSADO PARA String VALOR
	//
	@Query(value = "select distinct (i.vendaCompraLojaVirtual) from ItemVendaLoja i where "
			+ " i.vendaCompraLojaVirtual.excluido = false and upper(trim(i.produto.nome)) like %?1%")
	List<VendaCompraLojaVirtual> vendaPorNomeProduto(String valor);


	//metodo q recebe o NOME de um CLIENTE/PESSOA e com o NOME desse CLIENTE/PESSOA nos buscamos
	//dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL em q o CLIENTE/PESSOA
	//tenha o NOME q foi PASSADO PARA String NOMEPESSOA
	//
	@Query(value = "select distinct (i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false and upper(trim(i.vendaCompraLojaVirtual.pessoa.nome)) like %?1%")
	List<VendaCompraLojaVirtual> vendaPorNomeCliente(String nomepessoa);


	@Query(value = "select distinct (i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false and upper(trim(i.vendaCompraLojaVirtual.enderecoCobranca.ruaLogra)) "
			+ " like %?1%")	
	List<VendaCompraLojaVirtual> vendaPorEnderecoCobranca(String enderecocobranca);


	@Query(value = "select distinct (i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false and upper(trim(i.vendaCompraLojaVirtual.enderecoEntrega.ruaLogra)) "
			+ " like %?1%")
	List<VendaCompraLojaVirtual> vendaPorEnderecoEntrega(String enderecoentrega);
	
	
	
}
