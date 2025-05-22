package jdev.mentoria.lojavirtual.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//classe/entidade PESSOAFISICA é FILHA(extends) da classe(abstrata) PESSOA
//e pega seus atributos e metodos...

@Entity
@Table(name = "pessoa_fisica")
//informando q o ID de PESSOAFISICA vai ser o ID de PESSOA (class MAE)
@PrimaryKeyJoinColumn(name = "id")
public class PessoaFisica extends Pessoa {
	private static final long serialVersionUID = 1L;
	
	@Column(nullable = false)
	private String cpf;
	
	@Temporal(TemporalType.DATE)
	private Date dataNasimento;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getDataNasimento() {
		return dataNasimento;
	}

	public void setDataNasimento(Date dataNasimento) {
		this.dataNasimento = dataNasimento;
	}
		
	
}
