package jdev.mentoria.lojavirtual.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.PessoaJuridica;
import jdev.mentoria.lojavirtual.repository.PessoaRepository;
import jdev.mentoria.lojavirtual.service.PessoaUserService;
import jdev.mentoria.lojavirtual.util.ValidaCNPJ;
import jdev.mentoria.lojavirtual.util.ValidaCPF;

@RestController
public class PessoaController {
	
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private PessoaUserService pessoaUserService;
	
	//salvar PESSOAJURIDICA/EMPRESA
	@ResponseBody
	@PostMapping(value = "**/salvarPj")
	public ResponseEntity<PessoaJuridica> salvarPj(
			@RequestBody @Valid PessoaJuridica pessoaJuridica) throws ExceptionMentoriaJava {
		
		if(pessoaJuridica == null) {
			throw new ExceptionMentoriaJava("Pessoa juridica nao pode ser NULL");
		}
		//verificando se ja tem PESSOAJURIDICA com esse CNPJ
		if (pessoaJuridica.getId() == null && pessoaRepository
				.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
			
			throw new ExceptionMentoriaJava(""
					+ "Ja existe CNPJ cadastrado com o numero: " + pessoaJuridica.getCnpj());
		}
		
		//verificando se ja tem PESSOAJURIDICA(EMPRESA) com essa INSCRICAO ESTADUAL
		if (pessoaJuridica.getId() == null && pessoaRepository
				.existeInsEstadualCadastrado(pessoaJuridica.getInscEstadual()) != null) {
			
			throw new ExceptionMentoriaJava(""
					+ "Ja existe Inscricao Estadual cadastrado com o numero: " + pessoaJuridica.getInscEstadual());
		}
		
		//verificando se o CNPJ e valido, se nao for TRUE dai e barrado
		if (!ValidaCNPJ.isCNPJ(pessoaJuridica.getCnpj())) {
			throw new ExceptionMentoriaJava("CNPJ : " + pessoaJuridica
					.getCnpj() + " esta invalido");
		}
		
		
		//chamando o service para salvar a pessoa juridica
		 pessoaJuridica = pessoaUserService
				 .salvarPessoaJuridica(pessoaJuridica);
		
		return new ResponseEntity<PessoaJuridica>(
				pessoaJuridica, HttpStatus.OK);
		
	}
	
	//salvar PESSOAFISICA
	@ResponseBody
	@PostMapping(value = "**/salvarPf")
	public ResponseEntity<PessoaFisica> salvarPf(
			@RequestBody PessoaFisica pessoaFisica) throws ExceptionMentoriaJava {
		
		if(pessoaFisica == null) {
			throw new ExceptionMentoriaJava("Pessoa fisica nao pode ser NULL");
		}
		//verificando se ja tem PESSOAFISICA com esse CPF
		if (pessoaFisica.getId() == null && pessoaRepository
				.existeCpfCadastrado(pessoaFisica.getCpf()) != null) {
			
			throw new ExceptionMentoriaJava(""
					+ "Ja existe CPF cadastrado com o numero: " + pessoaFisica.getCpf());
		}
				
		//verificando se o CPF e valido, se nao for TRUE dai e barrado
		if (!ValidaCPF.isCPF(pessoaFisica.getCpf())) {
			throw new ExceptionMentoriaJava("CPF : " + pessoaFisica
					.getCpf() + " esta invalido");
		}
		
		
		//chamando o service para salvar a PESSOAFISICA
		 pessoaFisica = pessoaUserService
				 .salvarPessoaFisica(pessoaFisica);
		
		return new ResponseEntity<PessoaFisica>(
				pessoaFisica, HttpStatus.OK);
		
	}

}
