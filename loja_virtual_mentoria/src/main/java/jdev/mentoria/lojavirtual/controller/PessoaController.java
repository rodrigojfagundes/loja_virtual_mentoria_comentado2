package jdev.mentoria.lojavirtual.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.model.Endereco;
import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.PessoaJuridica;
import jdev.mentoria.lojavirtual.model.dto.CepDTO;
import jdev.mentoria.lojavirtual.repository.EnderecoRepository;
import jdev.mentoria.lojavirtual.repository.PessoaFisicaRepository;
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
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private PessoaFisicaRepository pessoaFisicaRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	//EndPoint/metodo para buscar uma LISTA de PESSOAFISICA pelo nome
	@ResponseBody
	@GetMapping(value = "**/consultaPfNome/{nome}")
	public ResponseEntity<List<PessoaFisica>> consultaPfNome(
			@PathVariable("nome") String nome){
		
		List<PessoaFisica> fisicas = pessoaFisicaRepository
				.pesquisaPorNomePF(nome.trim().toUpperCase());
		
		//para contar quantas pessoas chamam o metodo/funcao/endpoint CONSULTAPFNOME
		jdbcTemplate.execute("begin; update tabela_acesso_end_potin set qtde_acesso_end_point = qtde_acesso_end_point + 1 where nome_end_point = 'END-POINT-NOME-PESSOA-FISICA'; commit");
		
		return new ResponseEntity<
				List<PessoaFisica>>(fisicas, HttpStatus.OK);
	}

	
	//EndPoint/metodo para buscar UMA PESSOAFISICA CPF
	@ResponseBody
	@GetMapping(value = "**/consultaPfCpf/{cpf}")
	public ResponseEntity<List<PessoaFisica>> consultaPfCpf(
			@PathVariable("cpf") String cpf){
		
		List<PessoaFisica> fisicas = pessoaFisicaRepository
				.pesquisaPorCpfPF(cpf);
		
		return new ResponseEntity<
				List<PessoaFisica>>(fisicas, HttpStatus.OK);
	}
	
	
	//EndPoint/metodo para buscar UMA PESSOAJURIDICA/EMPRESA por NOME
	@ResponseBody
	@GetMapping(value = "**/consultaNomePJ/{nome}")
	public ResponseEntity<List<PessoaJuridica>> consultaNomePJ(
			@PathVariable("nome") String nome){
		
		//O OBJ O PROF DEIXOU COM O NOME DE FISICAS, MAS COMO E 
		//PESSOAJURIDICA/EMPRESA EU ACHEI MELHOR DEIXAR COMO PESSOASJURIDICAS
		List<PessoaJuridica> pessoasJuridicas = pessoaRepository
				.pesquisaPorNomePJ(nome.trim().toUpperCase());
		
		return new ResponseEntity<
				List<PessoaJuridica>>(pessoasJuridicas, HttpStatus.OK);
	}
	
	
	//EndPoint/metodo para buscar UMA PESSOAJURIDICA/EMPRESA por CNPJ
	@ResponseBody
	@GetMapping(value = "**/consultaCnpjPJ/{cnpj}")
	public ResponseEntity<List<PessoaJuridica>> consultaCnpjPJ(
			@PathVariable("cnpj") String cnpj){
		
		//O OBJ O PROF DEIXOU COM O NOME DE FISICAS, MAS COMO E 
		//PESSOAJURIDICA/EMPRESA EU ACHEI MELHOR DEIXAR COMO PESSOASJURIDICAS
		List<PessoaJuridica> pessoasJuridicas = pessoaRepository
				.existeCnpjCadastradoList(cnpj.trim().toUpperCase());
		
		return new ResponseEntity<
				List<PessoaJuridica>>(pessoasJuridicas, HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaCep/{cep}")
	public ResponseEntity<CepDTO> consultaCep(@PathVariable("cep") String cep) {
				
		return new ResponseEntity<CepDTO>(pessoaUserService.consultaCep(cep), HttpStatus.OK);
	}
	
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
		
		//se tiver cadastrando uma PESSOAJURIDICA/EMPRESA
		//vamos pegar a lista de enderecos e verificar o CEP
		//e enviar para a API do VIACEP para validar se o CEP e valido
		if (pessoaJuridica.getId() == null || pessoaJuridica.getId() <= 0) {
			for (int p = 0; p < pessoaJuridica.getEnderecos().size(); p++) {
				CepDTO cepDTO = pessoaUserService
						.consultaCep(pessoaJuridica.getEnderecos()
								.get(p).getCep());
				
				//pegando as INFORMACOES q foram retornadas da API VIACEP
				//e SUBSTITUINDO pela as q o usuario colocou na hora de
				//cadastrar PESSOAJURIDICA/EMPRESA...
				pessoaJuridica.getEnderecos().get(p).setBairro(cepDTO.getBairro());
				pessoaJuridica.getEnderecos().get(p).setCidade(cepDTO.getLocalidade());
				pessoaJuridica.getEnderecos().get(p).setComplemento(cepDTO.getComplemento());
				pessoaJuridica.getEnderecos().get(p).setRuaLogra(cepDTO.getLogradouro());
				pessoaJuridica.getEnderecos().get(p).setUf(cepDTO.getUf());
			} 
			
		}
			
			//caso a PESSOAJURIDICA/EMPRESA atualize o CEP, nos vamos
			//verificar a validade do novo CEP informado...
			//e pegar a CIDADE, BAIRRO, COMPLEMENTO, etc... informado pelo
			//VIACEP
			else {
				
				for (int p = 0; p < pessoaJuridica.getEnderecos().size(); p++) {
					Endereco enderecoTemp = enderecoRepository
							.findById(pessoaJuridica.getEnderecos().get(p).getId()).get();
				
					
					if (!enderecoTemp.getCep().equals(pessoaJuridica.getEnderecos().get(p).getCep())) {
						
						CepDTO cepDTO = pessoaUserService
								.consultaCep(pessoaJuridica.getEnderecos()
										.get(p).getCep());
						
						//pegando as INFORMACOES q foram retornadas da API VIACEP
						//e SUBSTITUINDO pela as q o usuario colocou na hora de
						//EDITAR PESSOAJURIDICA/EMPRESA...
						pessoaJuridica.getEnderecos().get(p).setBairro(cepDTO.getBairro());
						pessoaJuridica.getEnderecos().get(p).setCidade(cepDTO.getLocalidade());
						pessoaJuridica.getEnderecos().get(p).setComplemento(cepDTO.getComplemento());
						pessoaJuridica.getEnderecos().get(p).setRuaLogra(cepDTO.getLogradouro());
						pessoaJuridica.getEnderecos().get(p).setUf(cepDTO.getUf());
						
						
					}
					
			}
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
