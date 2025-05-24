package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.StatusRastreio;

@Repository
public interface StatusRastreioRepository extends JpaRepository<StatusRastreio, Long>{
	
	
	//cada vendacompralojavirtual vai ter MUITOS STATUSRASTREIO
	//ex: 1 STATUSRASTREIO disse q saiu de sp
	//  dps outro STATUSRASTREIO disse q ja ta na transportadora
	//	dps outro STATUSRASTREIO disse q ja ta chegando no deposito de joinville
	//  dps outro STATUSRASTREIO disse q ja ta em Tijucas-SC
	//ou seja uma VENDACOMPRALOJAVIRTUAL tera uma LISTA de STATUSRASTREIO
	//
	//para retornar essa LISTA com os STATUSRASTREIO vamos ter q receber
	//o ID da VENDACOMPRALOJAVIRTUAL
	//
	//vamos chamar STATUSRASTREIO de S
	//selecionando os S(STATUSRASTREIO) na TABELA STATUSRASTREIO onde o
	//VENDACOMPRALOJAVIRTUAL tem o ID q foi passado para var long IDVENDA
	//
	@Query(value = "select s from StatusRastreio s where s.vendaCompraLojaVirtual.id = ?1 order by s.id")
	public List<StatusRastreio> listaRastreioVenda(Long idVenda);
	
	
	
}
