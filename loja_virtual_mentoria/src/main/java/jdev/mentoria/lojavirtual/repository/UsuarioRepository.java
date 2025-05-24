package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
	//buscar os usuarios q estao a mais de (110 dias agora hehe) com a mesma senha
	//para a TarefaAutomatiza enviar uma notificacao para eles
	@Query(value = "select u from Usuario u where u.dataAtualSenha <= current_date - 110")
	List<Usuario> usuarioSenhaVencida();
	
	
	//selecionando usuario na class/tabela usuario, onde u.login
	//e o parametro passado q sera o q estamos procurando na class/tabela
	//usuario
	@Query(value = "select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);

	//metodo para verificar se uma pessoa tem usuario
	//pesquisando pelo ID e pelo e-mail/login
	@Query(value = "select u from Usuario u where u.pessoa.id = ?1 or u.login =?2")
	Usuario findUserByPessoa(Long id, String email);

	@Query(value = "select constraint_name from information_schema.constraint_column_usage where table_name = 'usuarios_acesso' and column_name = 'acesso_id' and constraint_name <> 'unique_acesso_user';", nativeQuery = true)
	String consultaConstraintAcesso();

	//fazendo um insert na tabela usuarios_acesso passando o ID do usuario
	//e passando os acessos q esse usuario tem, atraves dos IDs dos acesso
	//por padrao o usuario vai ter ACESSO a ROLE_USER q sera uma role padrao
	//dai vamos pesquisar no banco o ID da ROLE_USER e associar esse ID ao
	//usuario, sendo assim o usuario vai ter acesso a role_user
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "insert into usuarios_acesso(usuario_id, acesso_id) values (?1, (select id from acesso where descricao = 'ROLE_USER'))")
	void insereAcessoUser(Long idUser);


	//fazendo um insert na tabela usuarios_acesso passando o ID do usuario
	//e passando os acessos q esse usuario tem, atraves dos IDs dos acesso
	//SEMELHANTE AO METODO ACIMA, mas so q com ACESSO DINAMICO
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "insert into usuarios_acesso(usuario_id, acesso_id) values (?1, (select id from acesso where descricao = ?2 limit 1))")
	void insereAcessoUserPj(Long idUser, String acesso);
	
	
}
