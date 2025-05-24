package jdev.mentoria.lojavirtual.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import jdev.mentoria.lojavirtual.enums.TipoEndereco;

//classe/entidade pessoa sera ABSTRACT pq nos nao vamos trabalhar
//com a PESSOA em si... Mas sim com as classes FILHAS dela (pessoa)
//tipo a PESSOA_FISICA, e PESSOA_JURIDICA

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SequenceGenerator(name = "seq_pessoa", 
sequenceName = "seq_pessoa", initialValue = 1, allocationSize = 1)
public abstract class Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//os atributos a baixo NAO precisam ser criados novamente 
	//na PESSOA_FISICA e PESSOA_JURIDICA	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
	generator = "seq_pessoa")
	private Long id;
	
	@Size(min = 4, message = "O nome deve ter no minimo 4 letras")
	@NotBlank(message = "Nome deve ser informado")
	@NotNull(message = "Nome deve ser informado")
	@Column(nullable = false)
	private String nome;
	
	@Email
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String telefone;
	
	@Column
	private String tipoPessoa;
	
	//cada pessoa pd ter MAIS de 1 endereco... Ou seja PD ter uma LISTA
	//de ENDERECO
	@OneToMany(mappedBy = "pessoa", orphanRemoval = true,
			cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Endereco> enderecos = new ArrayList<Endereco>();
	
	
	//MTAS PESSOA para 1 EMPRESA
	//(e EMPRESA e uma PESSOA do tipo juridica)... Foi colocado nessa class
	//PESSOA.JAVA pois ela e uma abstracao, dai nao precisamos por no
	//pessoa fisica e pessoa juridica
	@ManyToOne(targetEntity = Pessoa.class)
	@JoinColumn(name = "empresa_id", nullable = true,
	foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, 
	name = "empresa_id_fk"))
	private Pessoa empresa;
	
	//sera usado no VD_CP_LOJA_VIRT_CONTROLLER para percorrer
	//as informacoes sobre o endereco... Para poder enviar
	//a rua, cidade, estado, para a transportadora fazer a entrega
	//do produto...
	public Endereco enderecoEntrega() {
		
		Endereco enderecoReturn = null;
		for(Endereco endereco : enderecos) {
			if(endereco.getTipoEndereco().name()
					.equals(TipoEndereco.ENTREGA.name())) {
				enderecoReturn = endereco;
				break;
			}
		}
		return enderecoReturn;
	}
	
	
	public Pessoa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}
	
	public String getTipoPessoa() {
		return tipoPessoa;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getTelefone() {
		return telefone;
	}
	
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
		
	
	public List<Endereco> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<Endereco> enderecos) {
		this.enderecos = enderecos;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pessoa other = (Pessoa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
