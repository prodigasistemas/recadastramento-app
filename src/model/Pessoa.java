package model;

import util.Util;

public class Pessoa {

	private int matricula;
	private String nome;
	private int tipoPessoa;

	private String CpfCnpj;
	private String rg;
	private String uf;
	private String tipoSexo;
	private String telefone;
	private String celular;
	private String eMail;

	public Pessoa() {
		nome = "";
		tipoPessoa = 0;
		CpfCnpj = "";
		rg = "";
		uf = "";
		tipoSexo = "";
		telefone = "";
		celular = "";
		eMail = "";
	}

	public int getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = Util.verificarNuloInt(matricula);
	}

	public void setNome(String nome) {
		this.nome = Util.verificarNuloString(nome);
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = Util.verificarNuloInt(tipoPessoa);
	}

	public void setCpfCnpj(String CpfCnpj) {
		this.CpfCnpj = Util.verificarNuloString(CpfCnpj);
	}

	public void setRg(String rg) {
		this.rg = Util.verificarNuloString(rg);
	}

	public void setUf(String uf) {
		this.uf = Util.verificarNuloString(uf);
	}

	public void setTipoSexo(String tipoSexo) {
		this.tipoSexo = Util.verificarNuloString(tipoSexo);
	}

	public void setTelefone(String telefone) {
		this.telefone = Util.verificarNuloString(telefone);
	}

	public void setCelular(String celular) {
		this.celular = Util.verificarNuloString(celular);
	}

	public void setEmail(String eMail) {
		this.eMail = Util.verificarNuloString(eMail);
	}

	public String getNome() {
		return this.nome;
	}

	public int getTipoPessoa() {
		return this.tipoPessoa;
	}

	public String getCpfCnpj() {
		return this.CpfCnpj;
	}

	public String getRg() {
		return this.rg;
	}

	public String getUf() {
		return this.uf;
	}

	public String getTipoSexo() {
		return this.tipoSexo;
	}

	public String getTelefone() {
		return this.telefone;
	}

	public String getCelular() {
		return this.celular;
	}

	public String getEmail() {
		return this.eMail;
	}
}
