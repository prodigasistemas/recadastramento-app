package model;

import java.util.ArrayList;

import util.Constantes;
import util.Util;

public class Imovel extends Model {

	private long imovelId;
	private Categoria categoriaResidencial;
	private Categoria categoriaComercial;
	private Categoria categoriaPublica;
	private Categoria categoriaIndustrial;
	private Endereco enderecoImovel;
	private Ocupacao ocupacaoImovel;

	private int codigoCliente;
	private int matricula;
	private String localidade;
	private String setor;
	private String quadra;
	private String lote;
	private String subLote;
	private String rota;
	private String face;
	private String inscricao;
	private String observacao;
	private int codigoMunicipio;
	private int codigoLogradouro;
	private String iptu;
	private String numeroCelpa;
	private int numeroPontosUteis;
	private int numeroOcupantes;
	private int tipoFonteAbastecimento;
	private int imovelStatus;
	private int imovelTransmitido;
	private boolean tabSaved;
	private ArrayList<String> listaRamoAtividade;
	private double latitude;
	private double longitude;
	private String data;
	private int tipoOperacao;
	private String entrevistado;

	private String areaConstruida;
	private int classeSocial;
	private int numeroAnimais;
	private String volumeCisterna;
	private String volumePiscina;
	private String volumeCaixaDagua;
	private int tipoUso;
	private int acessoHidrometro;

	private int quantidadeEconomiasSocial;
	private int quantidadeEconomiasOutros;

	private int percentualAbastecimento;

	private String inconsistencias;
	
	public Imovel() {
		tipoOperacao = Constantes.OPERACAO_CADASTRO_ALTERADO;
		imovelId = 0;
		codigoCliente = 0;
		matricula = 0;
		localidade = "";
		setor = "";
		quadra = "";
		lote = "";
		subLote = "";
		rota = "";
		face = "";
		inscricao = "";
		codigoMunicipio = 0;
		codigoLogradouro = 0;
		iptu = "";
		numeroCelpa = "";
		numeroPontosUteis = 0;
		numeroOcupantes = 0;
		tipoFonteAbastecimento = 0;
		imovelStatus = 0;
		imovelTransmitido = 0;
		listaRamoAtividade = new ArrayList<String>();

		enderecoImovel = new Endereco();
		categoriaResidencial = new Categoria();
		categoriaComercial = new Categoria();
		categoriaPublica = new Categoria();
		categoriaIndustrial = new Categoria();
		ocupacaoImovel = new Ocupacao();

		latitude = 0;
		longitude = 0;
		data = "";
		entrevistado = "";

		areaConstruida = "0000000000";

		classeSocial = 0;
		numeroAnimais = 0;
		volumePiscina = "0000000";
		volumeCisterna = "0000000";
		volumeCaixaDagua = "0000000";

		quantidadeEconomiasSocial = 0;
		quantidadeEconomiasOutros = 0;
		
		observacao="";
	}

	public void setTipoOperacao(String tipoOperacao) {
		this.tipoOperacao = Util.verificarNuloInt(tipoOperacao);
	}
		
	public void setEntrevistado(String entrevistado) {
		this.entrevistado = entrevistado;
	}

	public void setImovelId(long id) {
		this.imovelId = id;
	}

	public void setCodigoCliente(String codigoCliente) {
		this.codigoCliente = Util.verificarNuloInt(codigoCliente);
	}

	public void setMatricula(String matricula) {
		this.matricula = Util.verificarNuloInt(matricula);
	}

	public void setInscricao(String inscricao) {
		this.inscricao = Util.verificarNuloString(inscricao);

		String inscricaoToBeParsed = this.inscricao.trim();

		if (inscricaoToBeParsed.length() == 16) {
			setLocalidade(inscricaoToBeParsed.substring(0, 3));
			setSetor(inscricaoToBeParsed.substring(3, 6));
			setQuadra(inscricaoToBeParsed.substring(6, 9));
			setLote(inscricaoToBeParsed.substring(9, 13));
			setSubLote(inscricaoToBeParsed.substring(13, 16));

		} else {
			setLocalidade(inscricaoToBeParsed.substring(0, 3));
			setSetor(inscricaoToBeParsed.substring(3, 6));
			setQuadra(inscricaoToBeParsed.substring(6, 10));
			setLote(inscricaoToBeParsed.substring(10, 14));
			setSubLote(inscricaoToBeParsed.substring(14, 17));
		}
	}

	public void setLocalidade(String localidade) {
		this.localidade = Util.verificarNuloString(localidade);
	}

	public void setSetor(String setor) {
		this.setor = Util.verificarNuloString(setor);
	}

	public void setQuadra(String quadra) {
		this.quadra = Util.verificarNuloString(quadra);
	}

	public void setLote(String lote) {
		this.lote = Util.verificarNuloString(lote);
	}

	public void setSubLote(String subLote) {
		this.subLote = Util.verificarNuloString(subLote);
	}

	public void setRota(String rota) {
		this.rota = Util.verificarNuloString(rota);
	}

	public void setFace(String face) {
		this.face = Util.verificarNuloString(face);
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = Util.verificarNuloInt(codigoMunicipio);
	}

	public void setCodigoLogradouro(String codigoLogradouro) {
		this.codigoLogradouro = Util.verificarNuloInt(codigoLogradouro);
	}

	public void setIptu(String iptu) {
		this.iptu = Util.verificarNuloString(iptu);
	}

	public void setNumeroCelpa(String numeroCelpa) {
		this.numeroCelpa = Util.verificarNuloString(numeroCelpa);
	}

	public void setNumeroPontosUteis(String numeroPontosUteis) {
		this.numeroPontosUteis = Util.verificarNuloInt(numeroPontosUteis);
	}

	public void setNumeroOcupantes(String numeroOcupantes) {
		this.numeroOcupantes = Util.verificarNuloInt(numeroOcupantes);
	}

	public void setTipoFonteAbastecimento(String tipoFonteAbastecimento) {
		this.tipoFonteAbastecimento = Util.verificarNuloInt(tipoFonteAbastecimento);
	}

	public void setImovelStatus(String imovelStatus) {
		this.imovelStatus = Util.verificarNuloInt(imovelStatus);
	}

	public void setImovelTransmitido(String imovelTransmitido) {
		this.imovelTransmitido = Util.verificarNuloInt(imovelTransmitido);
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

	public void setListaRamoAtividade(ArrayList<String> listaRamoAtividade) {
		this.listaRamoAtividade = listaRamoAtividade;
	}

	public int getTipoOperacao() {
		return this.tipoOperacao;
	}

	public String getEntrevistado() {
		return this.entrevistado;
	}

	public long getImovelId() {
		return this.imovelId;
	}

	public int getCodigoCliente() {
		return this.codigoCliente;
	}

	public int getMatricula() {
		return this.matricula;
	}

	public String getInscricao() {
		inscricao = String.valueOf(getLocalidade()) + String.valueOf(getSetor()) + String.valueOf(getQuadra()) + String.valueOf(getLote()) + String.valueOf(getSubLote());
		return inscricao;
	}

	public String getLocalidade() {
		return this.localidade;
	}

	public String getSetor() {
		return this.setor;
	}

	public String getQuadra() {
		return this.quadra;
	}

	public String getLote() {
		return this.lote;
	}

	public String getSubLote() {
		return this.subLote;
	}

	public String getRota() {
		return this.rota;
	}

	public String getFace() {
		return this.face;
	}

	public int getCodigoMunicipio() {
		return this.codigoMunicipio;
	}

	public int getCodigoLogradouro() {
		return this.codigoLogradouro;
	}

	public ArrayList<String> getListaRamoAtividade() {
		return this.listaRamoAtividade;
	}

	public String getIptu() {
		return this.iptu;
	}

	public String getNumeroCelpa() {
		return this.numeroCelpa;
	}

	public int getNumeroPontosUteis() {
		return this.numeroPontosUteis;
	}

	public int getNumeroOcupantes() {
		return this.numeroOcupantes;
	}

	public int getTipoFonteAbastecimento() {
		return this.tipoFonteAbastecimento;
	}

	public int getImovelStatus() {
		return this.imovelStatus;
	}

	public int getImovelTransmitido() {
		return this.imovelTransmitido;
	}

	public Endereco getEnderecoImovel() {
		return this.enderecoImovel;
	}

	public void setEnderecoImovel(Endereco enderecoImovel) {
		this.enderecoImovel = enderecoImovel;
	}

	public Categoria getCategoriaResidencial() {
		return this.categoriaResidencial;
	}

	public Categoria getCategoriaComercial() {
		return this.categoriaComercial;
	}

	public Categoria getCategoriaPublica() {
		return this.categoriaPublica;
	}

	public Categoria getCategoriaIndustrial() {
		return this.categoriaIndustrial;
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

	public Ocupacao getOcupacaoImovel() {
		return this.ocupacaoImovel;
	}

	public int getNumeroAnimais() {
		return numeroAnimais;
	}

	public void setNumeroAnimais(String numeroAnimais) {
		this.numeroAnimais = Util.verificarNuloInt(numeroAnimais);
	}

	public String getVolumeCisterna() {
		return volumeCisterna;
	}

	public void setVolumeCisterna(String volumeCisterna) {
		this.volumeCisterna = volumeCisterna;
	}

	public String getVolumePiscina() {
		return volumePiscina;
	}

	public void setVolumePiscina(String volumePiscina) {
		this.volumePiscina = volumePiscina;
	}

	public String getVolumeCaixaDagua() {
		return volumeCaixaDagua;
	}

	public void setVolumeCaixaDagua(String volumeCaixaDagua) {
		this.volumeCaixaDagua = volumeCaixaDagua;
	}

	public int getClasseSocial() {
		return classeSocial;
	}

	public void setClasseSocial(String classeSocial) {
		this.classeSocial = Util.verificarNuloInt(classeSocial);
	}

	public int getTipoUso() {
		return tipoUso;
	}

	public void setTipoUso(String tipoUso) {
		this.tipoUso = Util.verificarNuloInt(tipoUso);
	}

	public int getAcessoHidrometro() {
		return acessoHidrometro;
	}

	public void setAcessoHidrometro(String acessoHidrometro) {
		this.acessoHidrometro = Util.verificarNuloInt(acessoHidrometro);
	}

	public int getQuantidadeEconomiasSocial() {
		return quantidadeEconomiasSocial;
	}

	public void setQuantidadeEconomiasSocial(String quantidadeEconomiasSocial) {
		this.quantidadeEconomiasSocial = Util.verificarNuloIntParaZero(quantidadeEconomiasSocial);
	}

	public int getQuantidadeEconomiasOutros() {
		return quantidadeEconomiasOutros;
	}

	public void setQuantidadeEconomiasOutros(String quantidadeEconomiasOutros) {
		this.quantidadeEconomiasOutros = Util.verificarNuloIntParaZero(quantidadeEconomiasOutros);
	}

	public String getAreaConstruida() {
		return areaConstruida;
	}

	public void setAreaConstruida(String areaConstruida) {
		this.areaConstruida = areaConstruida;
	}

	public int getPercentualAbastecimento() {
		return percentualAbastecimento;
	}

	public void setPercentualAbastecimento(String percentualAbastecimento) {
		this.percentualAbastecimento = Util.verificarNuloInt(percentualAbastecimento);
	}

	public boolean hasCategoria(Categoria categoria) {
		boolean result = false;
		if ((categoria.getEconomiasSubCategoria1() != Constantes.NULO_INT && categoria.getEconomiasSubCategoria1() > 0)
				|| (categoria.getEconomiasSubCategoria2() != Constantes.NULO_INT && categoria.getEconomiasSubCategoria2() > 0)
				|| (categoria.getEconomiasSubCategoria3() != Constantes.NULO_INT && categoria.getEconomiasSubCategoria3() > 0)
				|| (categoria.getEconomiasSubCategoria4() != Constantes.NULO_INT && categoria.getEconomiasSubCategoria4() > 0)) {

			result = true;
		}

		return result;
	}
	
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public String getObservacao(){
		return this.observacao;
	}
	
	public boolean isTransmitido() {
		return imovelTransmitido == Constantes.SIM;
	}
	
	public boolean isImovelNovo() {
		return imovelStatus == Constantes.IMOVEL_NOVO || imovelStatus == Constantes.IMOVEL_NOVO_COM_ANORMALIDADE;
	}

	public String getInconsistencias() {
		return inconsistencias;
	}

	public void setInconsistencias(String inconsistencias) {
		this.inconsistencias = inconsistencias;
	}
	
	public boolean isExcluido() {
		return imovelStatus == Constantes.IMOVEL_EXCLUIDO;
	}
	
	public boolean isInformativo() {
		return imovelStatus == Constantes.IMOVEL_INFORMATIVO;
	}
	
	public boolean possueRamoAtividade() {
		return listaRamoAtividade != null && listaRamoAtividade.size() > 0;
	}
}
