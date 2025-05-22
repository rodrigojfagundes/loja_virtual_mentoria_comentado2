package jdev.mentoria.lojavirtual.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


//criando a class/entidade usuario q tem 1 ou MTAS ROLES/ACESSO
@Entity
@Table(name="usuario")
@SequenceGenerator(name = "seq_usuario", 
sequenceName = "seq_usuario", allocationSize = 1, initialValue = 1)
public class Usuario implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
	generator = "seq_usuario")
	private Long id;
	
	@Column(nullable = false)
	private String login;
	
	@Column(nullable = false)
	private String senha;
	
	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataAtualSenha;
	
	//MTAS USUARIO para 1 PESSOA
	@ManyToOne(targetEntity = Pessoa.class)
	@JoinColumn(name = "pessoa_id", nullable = false,
	foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, 
	name = "pessoa_fk"))
	private Pessoa pessoa;
	
	
	//1 USUARIO tem  MTAS ACESSO/ROLE, ROLE_DEV, ROLE_ADMIN, etc...
	//e 1 ACESSO pode servir para VARIOS USUARIOS, pois criamos
	//a tabela usuario_acesso e nela associamos os USUARIO com os ACESSO
	//
	//tipo acesso id 1 admin, acesso id 2 dev, acesso id 3 compras
	//dai na tabela USUARIO_ACESSO nos fazemos a juncao
	//usuario ID 1 com os acesso de ID 1(admin) e acesso ID 2 (dev), etc...
	//usuario ID 3 com os acesso ID 3 (compras)
	//
	//com a ANNOTAITON @JOINTABLE nos estamos criando uma TABELA de nome
	//USUARIO_ACESSO e nela criamos criamos 2 colunas
	// 1 com o USUARIO_ID e outra com o ACESSO q esse usuario tem
	//
	//com o @JOINCOLUMN nos estamos dizendo q o USUARIO_ID vai fazer
	//referencia com o ID da TABELA USUARIO o UNIQUE = FALSE pq um usuario
	//pode ter VARIOS tipos de ACESSO e a chave estrangeira sera USUARIO_FK
	//
	//e o @INVERSEJOINCOLUMN no name nos vamos apontar para ACESSO_ID
	//q e a ID do tipo de acesso o UNIQUE = FALSE pq MTOS USUARIOS pd
	//ter o mesmo ACESSO(tipo 3 usuarios com acesso tipo ADMIN)...
	//e com o REFERENCEDCOLUMNNAME o ID nos referenciamos para a tabela
	//ACESSO e a ANNOTATION @FOREIGNKEY nos informamos o nome q esse ID vai
	//ter no caso ACESSO_FK
	//
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "usuarios_acesso", 
			uniqueConstraints = @UniqueConstraint(columnNames = {
			"usuario_id", "acesso_id"},
			name = "unique_acesso_user"),
			joinColumns = @JoinColumn(name = "usuario_id",
			referencedColumnName = "id", table = "usuario", unique = false,
			foreignKey = @ForeignKey(name = "usuario_fk",
			value = ConstraintMode.CONSTRAINT)),
			inverseJoinColumns = @JoinColumn(name = "acesso_id",
			unique = false, referencedColumnName = "id", table = "acesso",
			foreignKey = @ForeignKey(name = "acesso_fk",
			value = ConstraintMode.CONSTRAINT)))
	private List<Acesso> acessos;
	
	
	//AUTORIDADE são os ACESSOS/ROLE
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// retornando as permissoes de acesso desse usuario
		return this.acessos;
	}
	
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public Pessoa getPessoa() {
		return pessoa;
	}
	

	@Override
	public String getPassword() {
		// retornando a senha desse usuario
		return this.senha;
	}

	@Override
	public String getUsername() {
		// retornando o login desse usuario q no caso e o username...
		return this.login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
