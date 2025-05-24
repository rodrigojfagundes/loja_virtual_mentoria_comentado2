package jdev.mentoria.lojavirtual.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.PessoaJuridica;

@Repository
public interface PessoaRepository extends CrudRepository<PessoaJuridica, Long> {

	//metodo q usa uma query para buscar se ja existe pessoa juridica
	//com um valor de CNPJ ja cadastrado
	@Query(value = "select pj from PessoaJuridica pj where pj.cnpj = ?1")
	public PessoaJuridica existeCnpjCadastrado(String cnpj);
	
	
}
