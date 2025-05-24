package jdev.mentoria.lojavirtual.service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import jdev.mentoria.lojavirtual.model.AccessTokenJunoAPI;

//basicamente vamos verificar se tem algum token para acesso
//a API da JUNO no banco de dados... e se ele nao te expirado...
//caso nao tenha ou o token para acesso a API da JUNO ja esteja expirado
//vamos pedir para a juno gerar um novo token... +ou- isso
@Service
public class AccessTokenJunoService {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public AccessTokenJunoAPI buscaTokenAtivo() {
		
		try {
		
		   AccessTokenJunoAPI accessTokenJunoAPI = 
				  (AccessTokenJunoAPI) entityManager
				  .createQuery("select a from AccessTokenJunoAPI a ")
		          .setMaxResults(1).getSingleResult();
		   
		   return accessTokenJunoAPI;
		}catch (NoResultException e) {
			return null;
		}
		
		
	}

}
