package jdev.mentoria.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jdev.mentoria.lojavirtual.model.AccessTokenJunoAPI;


//basicamente esse repository nos vamos usar para
//salvar e deletar token (bearer) da API JUNO
@Repository
@Transactional
public interface AccesTokenJunoRepository extends JpaRepository<AccessTokenJunoAPI, Long> {

}
