package model;

public class DadosGerais {

	private String idRota;
	private String localidade;
	private String setor;
	private String rota;
	private String nomeArquivo;
	private String tipoArquivo;
	private String versaoAplicativo;

	private static DadosGerais instancia;

	public DadosGerais() {
	}

	public void adicionaDadosGerais(DadosGerais dadosGerais) {
		DadosGerais.instancia = dadosGerais;
	}

	public DadosGerais getDadosGerais() {
		return DadosGerais.instancia;
	}

	public static DadosGerais getInstancia() {
		return DadosGerais.instancia;
	}

	public String getIdRota() {
		return idRota;
	}

	public void setIdRota(String idRota) {
		this.idRota = idRota;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getSetor() {
		return setor;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}

	public String getRota() {
		return rota;
	}

	public void setRota(String rota) {
		this.rota = rota;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(String tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public String getVersaoAplicativo() {
		return versaoAplicativo;
	}

	public void setVersaoAplicativo(String versaoAplicativo) {
		this.versaoAplicativo = versaoAplicativo;
	}
}