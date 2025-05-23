package jdev.mentoria.lojavirtual;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jdev.mentoria.lojavirtual.controller.AcessoController;
import jdev.mentoria.lojavirtual.model.Acesso;
import jdev.mentoria.lojavirtual.repository.AcessoRepository;
import jdev.mentoria.lojavirtual.service.AcessoService;
import junit.framework.TestCase;

@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class LojaVirtualMentoriaApplicationTests extends TestCase {


	@Autowired
	private AcessoController acessoController;
	
	@Autowired
	private AcessoRepository acessoRepository;
	
	
	@Test
	public void testCadastraAcesso() {
		
		Acesso acesso = new Acesso();
		
		acesso.setDescricao("ROLE_ADMIN");
		
		assertEquals(true, acesso.getId() == null);
		
		acesso = acessoController.salvarAcesso(acesso).getBody();
		
		assertEquals(true, acesso.getId() > 0);
		
		//validar dados salvos da forma correta
		assertEquals("ROLE_ADMIN", acesso.getDescricao());
		
		
		//teste carregamento
		Acesso acesso2 = acessoRepository.findById(acesso.getId()).get();
		
		assertEquals(acesso.getId(), acesso2.getId());
		
		
		//teste de delete
		acessoRepository.deleteById(acesso2.getId());
		//o flush e para rodar o SQL de delete no BANCO
		acessoRepository.flush();
		
		Acesso acesso3 = acessoRepository.findById(
				acesso2.getId()).orElse(null);
		
		
		assertEquals(true, acesso3 == null);
	
		//teste de query
		
		
		acesso = new Acesso();
		acesso.setDescricao("ROLE_ALUNO");
		acesso = acessoController.salvarAcesso(acesso).getBody();
		List<Acesso> acessos = acessoRepository.
				buscarAcessoDesc("ALUNO".trim().toUpperCase());
		
		assertEquals(6, acessos.size());
		
		acessoRepository.deleteById(acesso.getId());
		

	}

}
