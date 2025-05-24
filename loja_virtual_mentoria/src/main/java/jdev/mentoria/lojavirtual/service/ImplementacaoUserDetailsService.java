package jdev.mentoria.lojavirtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jdev.mentoria.lojavirtual.model.Usuario;
import jdev.mentoria.lojavirtual.repository.UsuarioRepository;

//implementacao do userdetailsservice para solicitar coisas ao
//UsuarioRepository... Implementacao de detalhes do usuario
//para fazer criar conta e fazer login
@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// o username e o login para consulta
		Usuario usuario = usuarioRepository.findUserByLogin(username);/* Recebe o login pra consulta */

		if (usuario == null) {
			throw new UsernameNotFoundException("Usuário não foi encontrado");
		}

		// se o usuario existir no banco vamos retornar o LOGIN/USERNAME
		// a senha e as roles/acessos
		return new User(usuario.getLogin(), usuario.getPassword(), usuario.getAuthorities());
	}

}
