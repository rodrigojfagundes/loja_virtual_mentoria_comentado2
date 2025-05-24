package jdev.mentoria.lojavirtual.enums;

public enum TipoPessoa {
	
	
	JURIDICA("Juridica"),
	JURIDICA_FORNECEDOR("Juridica e Fornecedor"),
	FISICA("Fisica");
	
	private String descricao;

	private TipoPessoa(String descricao) {
		this.descricao = descricao;
	}
	
	
	public String getDescricao() {
		return descricao;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.descricao;
	}
	
}
