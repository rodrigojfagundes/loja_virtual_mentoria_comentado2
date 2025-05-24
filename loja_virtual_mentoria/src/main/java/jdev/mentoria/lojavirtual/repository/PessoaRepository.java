package jdev.mentoria.lojavirtual.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.PessoaJuridica;

@Repository
public interface PessoaRepository extends CrudRepository<PessoaJuridica, Long> {

	//metodo q usa uma query para buscar se ja existe pessoa juridica(EMPRESA)
	//com um valor de CNPJ ja cadastrado
	@Query(value = "select pj from PessoaJuridica pj where pj.cnpj = ?1")
	public PessoaJuridica existeCnpjCadastrado(String cnpj);

	
	//metodo q usa uma query para buscar se ja existe PESSOAFISICA
	//com um valor de CPF ja cadastrado
	@Query(value = "select pf from PessoaFisica pf where pf.cpf = ?1")
	public PessoaFisica existeCpfCadastrado(String cpf);
	
	
	//metodo q usa uma query para buscar se ja existe pessoa juridica (EMPRESA)
	//com um valor de INSCRICAO ESTADUAL ja cadastrado
	@Query(value = "select pj from PessoaJuridica pj where pj.inscEstadual = ?1")
	public PessoaJuridica existeInsEstadualCadastrado(String inscEstadual);
	

	
	
}
