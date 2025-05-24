package jdev.mentoria.lojavirtual.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jdev.mentoria.lojavirtual.model.dto.ObejtoRequisicaoRelatorioProdCompraNotaFiscalDto;
import jdev.mentoria.lojavirtual.model.dto.ObejtoRequisicaoRelatorioProdutoAlertaEstoque;

@Service
public class NotaFiscalCompraService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * Title: Historio de compras de produtos para a loja
	 * Este relatório permite saber os produtos  comprados e 
	 * para serem vendidos pela loja virtual, todos os produtos tem a 
	 * relacao com a nota fiscal de compra/venda
	 * @param obejtoRequisicaoRelatorioProdCompraNotaFiscalDto
	 * @param dataInicio e dataFinal são parametros obrigatorios
	 * @return List<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDto>
	 * 
	 * @author Rodrigo
	 */
	public List<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDto> gerarRelatorioProdCompraNota(
			ObejtoRequisicaoRelatorioProdCompraNotaFiscalDto obejtoRequisicaoRelatorioProdCompraNotaFiscalDto) {
		// TODO Auto-generated method stub
		
		
		List<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDto> retorno =
				new ArrayList<ObejtoRequisicaoRelatorioProdCompraNotaFiscalDto>();
		
		
		// aparentemente estamos ligando o ID da NOTAFISCALDECOMPRA com o ID 
		//dos NOTAITEMSPRODUTO dessa 
		//NOTAFISCALCOMPRA e BUSCANDO os PRODUTOS q fazem parte
		//
		//fazendo um SELECT na tabela de NOTAFISCALCOMPRA(cfc) 
		//UNINDO/INNERJOIN a onde na tabela de NOTAITEMPRODUTO
		//o ID da NOTAFISCALCOMPRA é IGUAL o ID q ta na tabela é
		//IGUAL ao ID q ta na TABELA NOTAFISCALCOMPRA(cfc)
		//dps UNINDO/INNERJOIN o PRODUTO(p) com o NOTAITEMPRODUTO(NTP) 
		//ID de NOTAFISCALCOMPRA q ta no WHERE CFC.ID = 4
		//
		//dai dessa forma vamos trazer os ID ITEMSPRODUTOS dessa
		//NOTAFISCALCOMPRA... ou seja os produto q foram 
		//comprados e estao associados a NOTAFISCALCOMPRA
		//
		//
		// Q AQUI SO ESTAMOS TRAZENDO O CODIGO DO PRODUTO (ID)
		//NOMEPRODUTO, VALORVENDAPRODUTO, e a QUANTIDADE
		//
		//e tbm estamos trazendo o codigo e nome e etc do fornecedor...
		//
		//e TBM tem uma UNIAO/INNERJOIN para juntar 
		//PESSOA_JURIDICA(fornecedor) com a (cfc)NOTAFISCALCOMPRA.PESSOA_ID
		//
		String sql = "select p.id as codigoProduto, p.nome as nomeProduto, "
				+ " p.valor_venda as valorVendaProduto, ntp.quantidade as quantidadeComprada, "
				+ " pj.id as codigoFornecedor, pj.nome as nomeFornecedor,cfc.data_compra as dataCompra "
				+ " from nota_fiscal_compra as cfc "
				+ " inner join nota_item_produto as ntp on  cfc.id = nota_fiscal_compra_id "
				+ " inner join produto as p on p.id = ntp.produto_id "
				+ " inner join pessoa_juridica as pj on pj.id = cfc.pessoa_id where ";
		

		//se nao informar os FILTROS A BAIXO ali dos IF/else, vai trazer
		//por uma faixa de data...
		sql += " cfc.data_compra >='"+obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getDataInicial()+"' and ";
		sql += " cfc.data_compra <= '" + obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getDataFinal() +"' ";

		
		//SE o GETCODIGONOTA do OBJETOREQUISICAORELATORIOPRODCOMPRANOTAFISCALDTO
		//NAO for NULL... Dai o valor q ta vai ser passado para o ID do 
		//CFC(NOTAFISCALCOMPRA)
		//
		//aqui tbm pelo o q eu entendi, se informar o CODIGO da NOTA
		//ele vai trazer pelo CODIGO da NOTA... Se informar o ID/CODIGO
		//do PRODUTO vai trazer pelo o ID/CODIGO do PRODUTO... etc...
		if (!obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getCodigoNota().isEmpty()) {
		 sql += " and cfc.id = " + obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getCodigoNota() + " ";
		}

		if (!obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getCodigoProduto().isEmpty()) {
			sql += " and p.id = " + obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getCodigoProduto() + " ";
		}

		if (!obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getNomeProduto().isEmpty()) {
			sql += " upper(p.nome) like upper('%"+obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getNomeProduto()+"')";
		}
		
		if (!obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getNomeFornecedor().isEmpty()) {
			sql += " upper(pj.nome) like upper('%"+obejtoRequisicaoRelatorioProdCompraNotaFiscalDto.getNomeFornecedor()+"')";
		}

		
		retorno = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ObejtoRequisicaoRelatorioProdCompraNotaFiscalDto.class));
		
		
		
		return retorno;
	}
	
	
	
	/**
	 * Este relatório retorna os produtos que estão com o estoque menos
	 * ou igual a quantidade definida no campo qtde_alerta_estoque
	 * 
	 * @param alertaEstoque ObejtoRequisicaoRelatorioProdutoAlertaEstoque
	 * @return List<ObejtoRequisicaoRelatorioProdutoAlertaEstoque> Lista de
	 * Objetos ObejtoRequisicaoRelatorioProdutoAlertaEstoque
	 */
	public List<ObejtoRequisicaoRelatorioProdutoAlertaEstoque>
	gerarRelatorioAlertaEstoque(
			ObejtoRequisicaoRelatorioProdutoAlertaEstoque alertaEstoque){
		
		
		List<ObejtoRequisicaoRelatorioProdutoAlertaEstoque> retorno =
				new ArrayList<ObejtoRequisicaoRelatorioProdutoAlertaEstoque>();
		
		
	
		String sql = "select p.id as codigoProduto, p.nome as nomeProduto, "
				+ " p.valor_venda as valorVendaProduto, ntp.quantidade as quantidadeComprada, "
				+ " pj.id as codigoFornecedor, pj.nome as nomeFornecedor,cfc.data_compra as dataCompra, "
				+ " p.qtd_estoque as qtdEstoque, p.qtde_alerta_estoque as qtdAlertaEstoque "
				+ " from nota_fiscal_compra as cfc "
				+ " inner join nota_item_produto as ntp on  cfc.id = nota_fiscal_compra_id "
				+ " inner join produto as p on p.id = ntp.produto_id "
				+ " inner join pessoa_juridica as pj on pj.id = cfc.pessoa_id where ";
		

		sql += " cfc.data_compra >='"+alertaEstoque.getDataInicial()+"' and ";
		sql += " cfc.data_compra <= '" + alertaEstoque.getDataFinal() +"' ";
		sql += " and p.alerta_qtde_estoque = true and p.qtd_estoque <= p.qtde_alerta_estoque ";
		

		if (!alertaEstoque.getCodigoNota().isEmpty()) {
		 sql += " and cfc.id = " + alertaEstoque.getCodigoNota() + " ";
		}

		if (!alertaEstoque.getCodigoProduto().isEmpty()) {
			sql += " and p.id = " + alertaEstoque.getCodigoProduto() + " ";
		}

		if (!alertaEstoque.getNomeProduto().isEmpty()) {
			sql += " upper(p.nome) like upper('%"+alertaEstoque.getNomeProduto()+"')";
		}
		
		if (!alertaEstoque.getNomeFornecedor().isEmpty()) {
			sql += " upper(pj.nome) like upper('%"+alertaEstoque.getNomeFornecedor()+"')";
		}

		
		retorno = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ObejtoRequisicaoRelatorioProdutoAlertaEstoque.class));
				
		
		return retorno;
		
		
		
		
	}
	
	
	
}
