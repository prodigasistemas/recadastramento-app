package model;

import util.Util;

public class Ocupacao {
	
	private int criancas;
	private int adultos;
	private int alunos;
	private int caes;
	private int idosos;
	private int empregados;
	private int outros;
	
	public Ocupacao() {
		criancas = 0;
		adultos = 0;
		alunos = 0;
		caes = 0;
		idosos = 0;
		empregados = 0;
		outros = 0;
	}

	public int getCriancas() {
		return criancas;
	}

	public void setCriancas(String criancas) {
		this.criancas = Util.verificarNuloInt(criancas);
	}

	public int getAdultos() {
		return adultos;
	}

	public void setAdultos(String adultos) {
		this.adultos = Util.verificarNuloInt(adultos);
	}

	public int getAlunos() {
		return alunos;
	}

	public void setAlunos(String alunos) {
		this.alunos = Util.verificarNuloInt(alunos);
	}

	public int getCaes() {
		return caes;
	}

	public void setCaes(String caes) {
		this.caes = Util.verificarNuloInt(caes);
	}

	public int getIdosos() {
		return idosos;
	}

	public void setIdosos(String idosos) {
		this.idosos = Util.verificarNuloInt(idosos);
	}

	public int getEmpregados() {
		return empregados;
	}

	public void setEmpregados(String empregados) {
		this.empregados = Util.verificarNuloInt(empregados);
	}

	public int getOutros() {
		return outros;
	}

	public void setOutros(String outros) {
		this.outros = Util.verificarNuloInt(outros);
	}
}
