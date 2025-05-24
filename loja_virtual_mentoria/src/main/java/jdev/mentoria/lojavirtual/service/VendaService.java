package jdev.mentoria.lojavirtual.service;


import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jdev.mentoria.lojavirtual.model.VendaCompraLojaVirtual;


@Service
public class VendaService {
	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	public void exclusaoTotalVendaBanco2(Long idVenda) {
		String sql = "begin; update vd_cp_loja_virt set excluido = true where id = " + idVenda +"; commit;";
		jdbcTemplate.execute(sql);
	}
	

	public void exclusaoTotalVendaBanco(Long idVenda) {
		
		
		//no SQL a baixo na 
//1 - estamos passando a COLUNA NOTAFISCALVENDA q esta ASSOCIADA ao IDVENDA para NULL
//2 - estamos DELETANDO A COLUNA NOTAFISCALVENDA q ta associada ao IDVENDA
//3 - estamos deletando os dados q estao na COLUNA ITEMVENDALOJA q ta associada ao IDVENDA
//4 - estamos deletando a COLUNA STATUSRASTREIO q ta ASSOCIADA AO IDVENDA
//5 - estamos DELETANDO TODA TABELA VDCDPLOJAVIRT q faz parte do IDVENDA
//
//O IDVENDA e o USUARIO q passa atraves do metodo de deletar q ta no
//VD_CP_LOJA_VIRT_CONTROLLER.JAVA... Ou seja vem do FRONTEND
		//
		//codigo a baixo e em SQL
		String value = 
		                  " begin;"
		      			+ " UPDATE nota_fiscal_venda set venda_compra_loja_virt_id = null where venda_compra_loja_virt_id = "+idVenda+"; "
		      			+ " delete from nota_fiscal_venda where venda_compra_loja_virt_id = "+idVenda+"; "
		      			+ " delete from item_venda_loja where venda_compra_loja_virtu_id = "+idVenda+"; "
		      			+ " delete from status_rastreio where venda_compra_loja_virt_id = "+idVenda+"; "
		      			+ " delete from vd_cp_loja_virt where id = "+idVenda+"; "
		      			+ " commit; ";
		
		jdbcTemplate.execute(value);
	}


	//para ativar novamente uma vendacompralojavirtual apos ela ter sido
	//deletada logicamente... Ou seja passando o campo excluido para false
	
	public void ativaRegistroVendaBanco(Long idVenda) {
		// TODO Auto-generated method stub
		String sql = "begin; update vd_cp_loja_virt set excluido = false where id = " + idVenda +"; commit;";
		jdbcTemplate.execute(sql);
		
	}

	
	//metodo q recebe 2 datas e pesquisa as VENDACOMPRALOJAVIRTUAL
	//q aconteceram entre essas datas...
	//
	//recebendo 2 datas e pesquisamos dentro do ITEMVENDALOJA as VENDACOMPRALOJAVIRTUAL
	//em q a DATA da COMPRA é MAIOR q a DATA1 e MENOR q a DATA2...
	//
	//meio q estamos selecionando a COLUNA/var/obj VENDACOMPRALOJAVIRTUAL
	//q ta dentro da TABELA/CLASS ITEMVENDALOJA... Aonde o EXCLUIDO  e FALSE
	//e a DATAVENDA dessa VENDACOMPRALOJAVIRTUAL da entre o valor da
	//DATA1 e DATA2...
	//
	//codigo em JPQL...
	@SuppressWarnings("unchecked")
	public List<VendaCompraLojaVirtual> consultaVendaFaixaData(String data1, String data2){
	String sql = "select distinct (i.vendaCompraLojaVirtual) from ItemVendaLoja i "
			+ " where i.vendaCompraLojaVirtual.excluido = false "
			+ " and i.vendaCompraLojaVirtual.dataVenda >= '" + data1 + "'"
			+ " and i.vendaCompraLojaVirtual.dataVenda <= '" + data2 + "' ";
		
	return entityManager.createQuery(sql).getResultList();
	}
}
