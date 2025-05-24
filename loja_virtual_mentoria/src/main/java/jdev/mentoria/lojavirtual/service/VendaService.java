package jdev.mentoria.lojavirtual.service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jdev.mentoria.lojavirtual.model.VendaCompraLojaVirtual;
import jdev.mentoria.lojavirtual.repository.Vd_Cp_Loja_virt_repository;


@Service
public class VendaService {
	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private Vd_Cp_Loja_virt_repository vd_Cp_Loja_virt_repository;
	
	
	
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

	
	//metodo q recebe 2 datas e e passa para o VD_CP_LOJA_VIRT_REPOSITORY
	//pesquisa as VENDACOMPRALOJAVIRTUAL
	//q aconteceram entre essas datas...
	//
	public List<VendaCompraLojaVirtual> consultaVendaFaixaData(String data1, String data2) throws ParseException{
		
		//convertendo as DATA1 e DATA2 de STRING para DATE
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date date1 = dateFormat.parse(data1);
		Date date2 = dateFormat.parse(data2);
		
		
	return vd_Cp_Loja_virt_repository.consultaVendaFaixaData(date1, date2);
	}
}
