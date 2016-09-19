package model;

import util.Util;

public class Servicos extends Model{

	private int tipoLigacaoAgua;
	private int tipoLigacaoEsgoto;
	private int localInstalacaoRamal;
	private double latitude;
	private double longitude;
	private String data;
	private boolean tabSaved;

	public Servicos() {
		tipoLigacaoAgua = 0;
		tipoLigacaoEsgoto = 0;
		localInstalacaoRamal = 0;
		latitude = 0;
		longitude = 0;
		data = "";
	}

	public void setTipoLigacaoAgua(String tipoLigacaoAgua) {
		this.tipoLigacaoAgua = Util.verificarNuloInt(tipoLigacaoAgua);
	}

	public void setTipoLigacaoEsgoto(String tipoLigacaoEsgoto) {
		this.tipoLigacaoEsgoto = Util.verificarNuloInt(tipoLigacaoEsgoto);
	}

	public void setLocalInstalacaoRamal(String localInstalacaoRamal) {
		this.localInstalacaoRamal = Util.verificarNuloInt(localInstalacaoRamal);
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

	public int getTipoLigacaoAgua() {
		return this.tipoLigacaoAgua;
	}

	public int getTipoLigacaoEsgoto() {
		return this.tipoLigacaoEsgoto;
	}

	public int getLocalInstalacaoRamal() {
		return this.localInstalacaoRamal;
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

	public boolean isTabSaved() {
		return tabSaved;
	}
}
