package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.PessoaJuridica;

@Repository
public interface PessoaRepository extends CrudRepository<PessoaJuridica, Long> {
	
	//pesquisando PESSOAJURIDICA/EMPRESA por nome
	@Query(value = "select pj from PessoaJuridica pj where upper(trim(pj.nome)) like %?1%")
	public List<PessoaJuridica> pesquisaPorNomePJ(String nome);
	

	//metodo q usa uma query para buscar se ja existe pessoa juridica(EMPRESA)
	//com um valor de CNPJ ja cadastrado
	@Query(value = "select pj from PessoaJuridica pj where pj.cnpj = ?1")
	public PessoaJuridica existeCnpjCadastrado(String cnpj);


	//metodo q usa uma query para buscar se ja existe MAIS DE UMA 
	//PESSOAJURIDICA/EMPRESA com um valor de CNPJ ja cadastrado
	//ou seja uma LISTA DE PESSOAJURIDICA/EMPRESA
	@Query(value = "select pj from PessoaJuridica pj where pj.cnpj = ?1")
	public List<PessoaJuridica>  existeCnpjCadastradoList(String cnpj);
	
	
	//metodo q usa uma query para buscar se ja existe UMA PESSOAFISICA
	//com um valor de CPF ja cadastrado
	@Query(value = "select pf from PessoaFisica pf where pf.cpf = ?1")
	public PessoaFisica existeCpfCadastrado(String cpf);
	
	
	//metodo q usa uma query para buscar se ja existe MAIS DE UMA PESSOAFISICA
	//com ESSE VALOR de CPF ja cadastradas, e no caso retorna uma LISTA
	//de PESSOASFISICAS com esse CPF
	@Query(value = "select pf from PessoaFisica pf where pf.cpf = ?1")
	public List<PessoaFisica> existeCpfCadastradoList(String cpf);

	
	
	//metodo q usa uma query para buscar se ja existe pessoa juridica (EMPRESA)
	//com um valor de INSCRICAO ESTADUAL ja cadastrado
	@Query(value = "select pj from PessoaJuridica pj where pj.inscEstadual = ?1")
	public PessoaJuridica existeInsEstadualCadastrado(String inscEstadual);
	

	//metodo q usa uma query para buscar se ja MAIS DE UMA 
	//PESSOAJURIDICA/EMPRESA com essa INSCRICAOESTADUAL... ou seja
	//retorna uma LISTA de PESSOAJURIDICA/EMPRESA com a mesma INSCRICAOESTADUAL
	@Query(value = "select pj from PessoaJuridica pj where pj.inscEstadual = ?1")
	public List<PessoaJuridica>  existeInsEstadualCadastradoList(String inscEstadual);
	
	
	
}
