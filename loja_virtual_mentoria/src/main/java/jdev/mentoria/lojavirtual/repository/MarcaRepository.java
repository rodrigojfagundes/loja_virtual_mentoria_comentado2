package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jdev.mentoria.lojavirtual.model.MarcaProduto;

@Repository
@Transactional
public interface MarcaRepository extends JpaRepository<MarcaProduto, Long> {
	
	//buscando as MARCAPRODUTO
	//ALTEREI DE A = ACESSO para MP = MARCAPRODUTO
	@Query("select mp from MarcaProduto mp where upper(trim(mp.nomeDesc)) like %?1%")
	List<MarcaProduto> buscarMarcaDesc(String desc);

}
