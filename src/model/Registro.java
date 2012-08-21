package model;

import util.Util;

public class Registro {

	private int codigo;
	private int descricao;
	
	public Registro(){
		codigo = 0;
		descricao = 0;
		
	}
	
	public void setCodigo(String codigo) {
		this.codigo = Util.verificarNuloInt(codigo);
	}

	public void setDescricao(String descricao) {
		this.descricao = Util.verificarNuloInt(descricao);
	}

	public int getCodigo() {
		return this.codigo;
	}

	public int getDescricao() {
		return this.descricao;
	}



}
