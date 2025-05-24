package jdev.mentoria.lojavirtual.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class VendaService {
	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	
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

	
	
}
