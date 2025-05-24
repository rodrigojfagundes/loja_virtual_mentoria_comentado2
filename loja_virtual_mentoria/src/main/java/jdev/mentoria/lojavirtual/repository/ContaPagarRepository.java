package jdev.mentoria.lojavirtual.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jdev.mentoria.lojavirtual.model.ContaPagar;

@Repository
@Transactional
public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {
	
		//buscar conta a pagar e trazendo uma LISTA de CONTAPAGAR
		//CP = CONTAPAGAR... o prof deixou A... Mas CP e MAIS adequado...
	@Query("select cp from ContaPagar cp where upper(trim(cp.descricao)) like %?1%")
	List<ContaPagar> buscaContaDesc(String des);
	
		//buscar conta a pagar por PESSOAJURIDICA
	//e trazendo uma LISTA de CONTAPAGAR
	@Query("select cp from ContaPagar cp where cp.pessoa.id = ?1")
	List<ContaPagar> buscaContaPorPessoa(Long idPessoa);
	
		//buscar conta a pagar por FORNECEDOR q e uma PESSOAJURIDICA
	@Query("select cp from ContaPagar cp where cp.pessoa_fornecedor.id = ?1")
	List<ContaPagar> buscaContaPorFornecedor(Long idFornecedor);
	
		
	//buscar conta a pagar por EMPRESA e trazendo uma LISTA de CONTAPAGAR
	@Query("select cp from ContaPagar cp where cp.empresa.id = ?1")
	List<ContaPagar> buscaContaPorEmpresa(Long idEmpresa);


}
