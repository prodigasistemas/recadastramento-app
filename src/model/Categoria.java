package model;

import util.Util;

public class Categoria {

	private int economiasSubCategoria1;
	private int economiasSubCategoria2;
	private int economiasSubCategoria3;
	private int economiasSubCategoria4;

	public Categoria() {
	}

	public void setEconomiasSubCategoria1(String economiasSubCategoria1) {
		this.economiasSubCategoria1 = Util.verificarNuloInt(economiasSubCategoria1);
	}

	public void setEconomiasSubCategoria2(String economiasSubCategoria2) {
		this.economiasSubCategoria2 = Util.verificarNuloInt(economiasSubCategoria2);
	}

	public void setEconomiasSubCategoria3(String economiasSubCategoria3) {
		this.economiasSubCategoria3 = Util.verificarNuloInt(economiasSubCategoria3);
	}

	public void setEconomiasSubCategoria4(String economiasSubCategoria4) {
		this.economiasSubCategoria4 = Util.verificarNuloInt(economiasSubCategoria4);
	}

	public int getEconomiasSubCategoria1() {
		return this.economiasSubCategoria1;
	}

	public int getEconomiasSubCategoria2() {
		return this.economiasSubCategoria2;
	}

	public int getEconomiasSubCategoria3() {
		return this.economiasSubCategoria3;
	}

	public int getEconomiasSubCategoria4() {
		return this.economiasSubCategoria4;
	}
}
