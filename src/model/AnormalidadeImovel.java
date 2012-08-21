package model;

import util.Util;

public class AnormalidadeImovel {

	private int codigoAnormalidade;
	private String descricaoAnormalidade;
	private String comentario;
	private String pathFoto1;
	private String pathFoto2;
	private boolean tabSaved;
	private double latitude;
	private double longitude;
	private String data;
	private int imovel_status;
	
	public AnormalidadeImovel(){
		descricaoAnormalidade = "";
		comentario = "";
		pathFoto1 = "";
		pathFoto2 = "";
		codigoAnormalidade = 0;
		latitude = 0;
		longitude = 0;
		data = "";
	}
	
	public int getCodigoAnormalidade(){
		return codigoAnormalidade;
	}
	
	public String getDescricaoAnormalidade(){
		return descricaoAnormalidade;
	}
	
	public String getComentario(){
		return comentario;
	}
	
	public String getPathFoto1(){
		return pathFoto1;
	}
	
	public String getPathFoto2(){
		return pathFoto2;
	}
	
	public void setCodigoAnormalidade(int codigoAnormalidade){
		this.codigoAnormalidade = codigoAnormalidade;
	}
	
	public void setDescricaoAnormalidade(String descricaoAnormalidade){
		this.descricaoAnormalidade = descricaoAnormalidade;
	}
	
	public void setComentario(String comentario){
		this.comentario = comentario;
	}
	
	public void setPathFoto1(String pathFoto1){
		this.pathFoto1 = pathFoto1;
	}
	
	public void setPathFoto2(String pathFoto2){
		this.pathFoto2 = pathFoto2;
	}
		
	public void setTabSaved(boolean tabSaved) {
		this.tabSaved = tabSaved;
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
