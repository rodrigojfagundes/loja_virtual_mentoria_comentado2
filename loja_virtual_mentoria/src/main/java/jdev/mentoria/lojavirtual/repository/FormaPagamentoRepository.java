package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.FormaPagamento;

@Repository
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {

	
	//reescrevendo o METODO FINDALL do JPA mas nesse ele recebe o ID
	//de uma EMPRESA(IDEMPRESA) para buscar as FORMAGAMENTO de uma EMPRESA
	@Query(value = "select f from FormaPagamento f where f.empresa.id =?1")
	List<FormaPagamento> findAll(Long idEmpresa);
	
	
	
	
	
}
