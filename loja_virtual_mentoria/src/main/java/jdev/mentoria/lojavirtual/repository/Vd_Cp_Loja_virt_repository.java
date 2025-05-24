package jdev.mentoria.lojavirtual.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
	@Query(value="select i.vendaCompraLojaVirtual from ItemVendaLoja i where "
			+ " i.vendaCompraLojaVirtual.excluido = false and i.produto.id = ?1")
	List<VendaCompraLojaVirtual> vendaPorProduto(Long idProduto);

	//metodo q recebe o NOME de um PRODUTO e com o NOME desse PRODUTO nos buscamos
	//dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL em q o PRODUTO
	//tenha o NOME q foi PASSADO PARA String VALOR
	//
	@Query(value="select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false and upper(trim(i.produto.nome)) like %?1%")
	List<VendaCompraLojaVirtual> vendaPorNomeProduto(String valor);
	
	//metodo q recebe o ID de um CLIENTE e com o ID desse CLIENTE nos buscamos
	//dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL em q o CLIENTE
	//tenha o ID q foi PASSADO PARA Long IDCLIENTE
	//
	@Query(value="select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false and i.vendaCompraLojaVirtual.pessoa.id = ?1")
	List<VendaCompraLojaVirtual> vendaPorCliente(Long idCliente);


	//metodo q recebe o NOME de um CLIENTE/PESSOA e com o NOME desse CLIENTE/PESSOA nos buscamos
	//dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL em q o CLIENTE/PESSOA
	//tenha o NOME q foi PASSADO PARA String NOMEPESSOA
	//
	@Query(value="select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false and upper(trim(i.vendaCompraLojaVirtual.pessoa.nome)) like %?1%")
	List<VendaCompraLojaVirtual> vendaPorNomeCliente(String nomepessoa);
	
	//metodo q recebe o NOME e CPF de um CLIENTE/PESSOA e com o NOME e CPF desse CLIENTE/PESSOA nos buscamos
	//dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL em q o CLIENTE/PESSOA
	//tenha o NOME e CPF q foi PASSADO PARA String NOMEPESSOA e STRING CPF
	//
	@Query(value="select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false "
			+ " and upper(trim(i.vendaCompraLojaVirtual.pessoa.nome)) like %?1% "
			+ " and i.vendaCompraLojaVirtual.pessoa.cpf = ?2")
	List<VendaCompraLojaVirtual> vendaPorNomeCliente(String nomepessoa, String cpf);
	
	//metodo q recebe o CPF de um CLIENTE/PESSOA e com o CPF desse CLIENTE/PESSOA nos buscamos
	//dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL em q o CLIENTE/PESSOA
	//tenha um CPF PARECIDO((LIKE)) q foi PASSADO PARA String CPF 
	//
	@Query(value="select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false and upper(trim(i.vendaCompraLojaVirtual.pessoa.cpf)) like %?1%")
	List<VendaCompraLojaVirtual> vendaPorCpfClienteLike(String cpf);
	
	//metodo q recebe o CPF de um CLIENTE/PESSOA e com o CPF desse CLIENTE/PESSOA nos buscamos
	//dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL em q o CLIENTE/PESSOA
	//tenha o CPF q foi PASSADO PARA String CPF 
	//
	@Query(value="select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false and upper(trim(i.vendaCompraLojaVirtual.pessoa.cpf)) = ?1")
	List<VendaCompraLojaVirtual> vendaPorCpfClienteIgual(String cpf);

	@Query(value="select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false and upper(trim(i.vendaCompraLojaVirtual.enderecoCobranca.ruaLogra)) "
			+ " like %?1%")
	List<VendaCompraLojaVirtual> vendaPorEndereCobranca(String enderecocobranca);

	
	@Query(value="select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false and upper(trim(i.vendaCompraLojaVirtual.enderecoEntrega.ruaLogra)) "
			+ " like %?1%")
	List<VendaCompraLojaVirtual> vendaPorEnderecoEntrega(String enderecoentrega);

	//
	//OBS O METODO A BAIXO O PROF TENTOU MAS NAO DEU CERTO...
	//
	//metodo q recebe 2 datas e pesquisa as VENDACOMPRALOJAVIRTUAL
	//q aconteceram entre essas datas...
	//
	//recebendo 2 datas e pesquisamos dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL
	//em q a DATA da COMPRA é MAIOR q a DATA1 e MENOR q a DATA2...
	@Query(value="select distinct(i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false "
			+ " and i.vendaCompraLojaVirtual.dataVenda >= ?1 "
			+ " and i.vendaCompraLojaVirtual.dataVenda <= ?2 ")
	List<VendaCompraLojaVirtual> consultaVendaFaixaData(Date data1, Date data2);

	//metodo q recebe o ID da ETIQUETA q foi gerado pela MELHORENVIO
	//e recebe o ID da VENDACOMPRALOJAVIRTUAL... Ou seja o ID da VENDA
	//para podermos associar dizer q tipo a VENDA ID 77 tem a IDETIQUETA
	//777...
	@Modifying(flushAutomatically = true)
	@Query(nativeQuery = true, value = "update vd_cp_loja_virt set codigo_etiqueta = ?1 where id = ?2")
	void updateEtiqueta(String idEtiqueta, Long idVenda);

	//metodo q recebe o URL de uma ETIQUETA (GERADO PELA API do MELHORENVIO)
	//e recebe o ID de uma COMPRAVENDALOJAVIRTUAL...
	//dai nos podemos associar no banco de dados, colocando q
	//a COMPRAVENDALOJAVIRTUAL ex ID 28 tem a URLETIQUETA http://...
	@Modifying(flushAutomatically = true)
	@Query(nativeQuery = true, value = "update vd_cp_loja_virt set url_imprime_etiqueta = ?1 where id = ?2")
	void updateURLEtiqueta(String urlEtiqueta, Long id);
	
	//aqui atualiza a venda dps q o pagamento ficou ok
	@Modifying(flushAutomatically = true)
	@Query(nativeQuery = true, value = "update vd_cp_loja_virt set status_venda_loja_virtual = 'FINALIZADA' where id = ?1")
	void updateFinalizaVenda(Long id);
    	
	

}
