package model;

import util.Constantes;
import util.Util;

public class Cliente {

	private Pessoa proprietario;
	private Pessoa usuario;
	private Pessoa responsavel;
	
	private int tipoEnderecoProprietario;
	private int tipoEnderecoResponsavel;
	private int tipoResponsavel;
	private int usuarioEProprietario;
	private boolean tabSaved;
	
	private String matricula;
	private String nomeGerenciaRegional;
	
	private Endereco enderecoProprietario;
	private Endereco enderecoResponsavel;

	private double latitude;
	private double longitude;
	private String data;

    public Cliente() {
 
    	tabSaved = false;
    	tipoEnderecoProprietario = 0;
    	tipoEnderecoResponsavel = 0;
    	tipoResponsavel = 0;
    	usuarioEProprietario = Constantes.SIM;
    	matricula = "";
    	nomeGerenciaRegional = "";
    	
    	enderecoProprietario = new Endereco();
    	enderecoResponsavel = new Endereco();
    	proprietario = new Pessoa();
    	usuario = new Pessoa();
    	responsavel = new Pessoa();
		latitude = 0;
		longitude = 0;
		data = "";
   }
    
	public void setMatricula(String matricula) {
		this.matricula = Util.verificarNuloString(matricula);
    }

	public void setNomeGerenciaRegional(String nomeGerenciaRegional) {
		this.nomeGerenciaRegional = Util.verificarNuloString(nomeGerenciaRegional);
    }
	
	public void setUsuarioEProprietario(String usuarioEProprietario) {
		this.usuarioEProprietario = Util.verificarNuloInt(usuarioEProprietario);
    }
	
	public void setTipoResponsavel(String tipoResponsavel) {
		this.tipoResponsavel = Util.verificarNuloInt(tipoResponsavel);
    }
	
	public void setTipoEnderecoProprietario(String tipoEndereco) {
		this.tipoEnderecoProprietario = Util.verificarNuloInt(tipoEndereco);
    }
	
	public void setTipoEnderecoResponsavel(String tipoEndereco) {
		this.tipoEnderecoResponsavel = Util.verificarNuloInt(tipoEndereco);
    }
	
	public void setLatitude(String latitude) {
		this.latitude = Util.verificarNuloDouble(latitude);
	}

	public void setLongitude(String longitude) {
		this.longitude = Util.verificarNuloDouble(longitude);
	}

	public void setData(String data) {
		this.data = Util.verificarNuloString(data);
	}

	public void setTabSaved(boolean tabSaved) {
		this.tabSaved = tabSaved;
    }
	
	public String getMatricula() {
		return this.matricula;
    }

	public String getNomeGerenciaRegional() {
		return this.nomeGerenciaRegional;
    }
	
	public int isUsuarioProprietario() {
		return this.usuarioEProprietario;
    }
	
	public int getTipoResponsavel() {
		return this.tipoResponsavel;
    }
	
	public int getTipoEnderecoProprietario() {
		return this.tipoEnderecoProprietario;
    }
	
	public int getTipoEnderecoResponsavel() {
		return this.tipoEnderecoResponsavel;
    }
	
	public Endereco getEnderecoProprietario() {
		return this.enderecoProprietario;
    }
	
	public Endereco getEnderecoResponsavel() {
		return this.enderecoResponsavel;
    }
	
	public Pessoa getProprietario() {
		return this.proprietario;
    }
	
	public Pessoa getUsuario() {
		return this.usuario;
    }
	
	public Pessoa getResponsavel() {
		return this.responsavel;
    }
	
	public double getLatitude() {
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public String getData() {
		return this.data;
	}

	public boolean isTabSaved(){
		return tabSaved;
	}
}
