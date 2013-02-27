package model;

import util.Util;

public class Endereco {

	private int tipoLogradouro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String municipio;
    
    public Endereco() {

    	logradouro = "";
    	numero = "";
    	complemento = "";
    	bairro = "";
    	cep = "";
    	municipio = "";
    }
    

	public void setTipoLogradouro(String tipoLogradouro) {
		this.tipoLogradouro = Util.verificarNuloInt(tipoLogradouro);
	}


	public void setLogradouro(String logradouro) {
		this.logradouro = Util.verificarNuloString(logradouro);
	}

	public void setNumero(String numero) {
		this.numero = Util.verificarNuloString(numero);
	}

	public void setComplemento(String complemento) {
		this.complemento = Util.verificarNuloString(complemento);
	}

	public void setBairro(String bairro) {
		this.bairro = Util.verificarNuloString(bairro);
	}

	public void setCep(String cep) {
		this.cep = Util.verificarNuloString(cep);
	}

	public void setMunicipio(String municipio) {
		this.municipio = Util.verificarNuloString(municipio);
	}

	
	public String getLogradouro() {
		return this.logradouro;
	}

	public String getNumero() {
		return this.numero;
	}

	public String getComplemento() {
		return this.complemento;
	}

	public String getBairro() {
		return this.bairro;
	}

	public String getCep() {
		return this.cep;
	}

	public String getMunicipio() {
		return this.municipio;
	}
	
	public int getTipoLogradouro() {
		return tipoLogradouro;
	}
}
