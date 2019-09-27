package ui;

import model.AnormalidadeImovel;
import model.Cliente;
import model.Imovel;
import model.Medidor;
import model.Servicos;
import util.Constantes;
import util.LogUtil;
import util.Util;
import android.annotation.SuppressLint;
import android.content.Context;
import business.Controlador;
import business.ControladorAcessoOnline;

import com.AndroidExplorer.R;

import dataBase.DataManipulator;

public class ArquivoRetorno {

	private Context context;
	private DataManipulator manipulator;
	private StringBuffer linha;

	public ArquivoRetorno() {
		super();
	}

	public ArquivoRetorno(Context context) {
		this.context = context;
	}
	
	public void gerarPorImovel(Imovel imovel) {
		this.manipulator = Controlador.getInstancia().getCadastroDataManipulator();

		String condicao = "matricula = " + imovel.getMatricula();
		
		Cliente cliente = manipulator.selectCliente(condicao);
		Servicos servicos = manipulator.selectServicos(condicao);
		Medidor medidor = manipulator.selectMedidor(condicao);
		AnormalidadeImovel anormalidade = manipulator.selectAnormalidadeImovel(condicao);

		StringBuffer conteudo = new StringBuffer(gerarHeader());
		conteudo.append(gerarLinhaCliente(cliente));
		conteudo.append(gerarLinhaImovel(imovel));
		conteudo.append(gerarLinhasRamoAtividade(imovel));
		conteudo.append(gerarLinhaServicos(imovel, servicos));
		conteudo.append(gerarLinhaMedidor(imovel, medidor));
		conteudo.append(gerarLinhaAnormalidade(imovel, anormalidade));

		ControladorAcessoOnline.getInstancia().transmitirImovel(conteudo.toString().getBytes());

		verificarStatus(imovel);
	}
	
	private void verificarStatus(Imovel imovel) {
		if (ControladorAcessoOnline.getInstancia().isImovelTransmitido()) {
			imovel.setImovelTransmitido(String.valueOf(Constantes.SIM));
		} else {
			imovel.setImovelTransmitido(String.valueOf(Constantes.NAO));

			if (MessageDispatcher.isRespostaInconsistencia()) {
				imovel.setImovelStatus(String.valueOf(Constantes.IMOVEL_SALVO_COM_INCONSISTENCIA));
				inserirInconsistencias(imovel);
			}
		}
		
		manipulator.salvarStatusImovel(imovel);
	}

	private void inserirInconsistencias(Imovel imovel) {
		String[] inconsistencias = MessageDispatcher.getInconsistencias().replace("[", "").replace("]", "").split(",");

		for (String inconsistencia : inconsistencias) {
			manipulator.inserirInconsistenciaImovel(imovel.getMatricula(), inconsistencia);
		}
	}
	
	public String gerarHeader() {
		linha = new StringBuffer("00");
		
		try {
			linha.append(Util.adicionarZerosEsquerdaNumero(3, Controlador.getInstancia().getDadosGerais().getLocalidade()));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, Controlador.getInstancia().getDadosGerais().getSetor()));
			linha.append(Util.adicionarZerosEsquerdaNumero(2, Controlador.getInstancia().getDadosGerais().getRota()));
			linha.append(Util.adicionarZerosEsquerdaNumero(4, Controlador.getInstancia().getDadosGerais().getIdRota()));
			linha.append(Util.adicionarZerosEsquerdaNumero(10, context.getString(R.string.app_versao)));
			linha.append(Util.adicionarCharDireita(1, Controlador.getInstancia().getDadosGerais().getTipoArquivo(), ' '));
			linha.append("\n");
		} catch (Exception e) {
			LogUtil.salvar(getClass(), "Erro ao gerar linha de Header", e);
		}
		
		return linha.toString();
	}

	public String gerarLinhaCliente(Cliente cliente) {
		linha = new StringBuffer("01");
		
		try {
			linha.append(Util.adicionarZerosEsquerdaNumero(9, cliente.getMatricula() == null ? "0" : cliente.getMatricula()));
			linha.append(Util.adicionarCharDireita(25, cliente.getNomeGerenciaRegional(), ' '));
			
			linha.append((cliente.getTipoEnderecoProprietario() != Constantes.NULO_INT ? cliente.getTipoEnderecoProprietario() : " "));
			linha.append((cliente.getTipoEnderecoResponsavel() != Constantes.NULO_INT ? cliente.getTipoEnderecoResponsavel() : " "));
			linha.append(cliente.isUsuarioProprietario());
			linha.append(cliente.getTipoResponsavel());
			
			linha.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(cliente.getUsuario().getMatricula())));
			linha.append(Util.adicionarCharDireita(50, Util.substringNome(cliente.getUsuario().getNome()), ' '));
			linha.append((cliente.getUsuario().getTipoPessoa() != Constantes.NULO_INT ? cliente.getUsuario().getTipoPessoa() : " "));
			linha.append(Util.adicionarCharDireita(14, cliente.getUsuario().getCpfCnpj().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""), ' '));
			linha.append(Util.adicionarCharEsquerda(13, cliente.getUsuario().getRg().replaceAll("[-]", "").replaceAll("[.]", ""), ' '));
			linha.append(Util.adicionarCharEsquerda(2, cliente.getUsuario().getUf(), ' '));
			linha.append(!cliente.getUsuario().getTipoSexo().equals(Constantes.NULO_STRING) ? cliente.getUsuario().getTipoSexo() : " ");
			linha.append(Util.adicionarCharDireita(11, cliente.getUsuario().getTelefone().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
			linha.append(Util.adicionarCharDireita(11, cliente.getUsuario().getCelular().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
			linha.append(Util.adicionarCharDireita(30, cliente.getUsuario().getEmail(), ' '));
			
			linha.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(cliente.getProprietario().getMatricula())));
			linha.append(Util.adicionarCharDireita(50, Util.substringNome(cliente.getProprietario().getNome()), ' '));
			linha.append((cliente.getProprietario().getTipoPessoa() != Constantes.NULO_INT ? cliente.getProprietario().getTipoPessoa() : " "));
			linha.append(Util.adicionarCharDireita(14, cliente.getProprietario().getCpfCnpj().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""), ' '));
			linha.append(Util.adicionarCharEsquerda(13, cliente.getProprietario().getRg().replaceAll("[-]", "").replaceAll("[.]", ""), ' '));
			linha.append(Util.adicionarCharEsquerda(2, cliente.getProprietario().getUf(), ' '));
			
			String tipoSexo = "";
			if (cliente.isUsuarioProprietario() == Constantes.SIM) {
				tipoSexo = cliente.getUsuario().getTipoSexo();
			} else {
				tipoSexo = cliente.getProprietario().getTipoSexo();
			}
			
			linha.append(Util.adicionarCharDireita(1, tipoSexo, ' '));
			
			linha.append(Util.adicionarCharDireita(11, cliente.getProprietario().getTelefone().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
			linha.append(Util.adicionarCharDireita(11, cliente.getProprietario().getCelular().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
			linha.append(Util.adicionarCharDireita(30, cliente.getProprietario().getEmail(), ' '));
			linha.append(Util.adicionarZerosEsquerdaNumero(2, "" + cliente.getEnderecoProprietario().getTipoLogradouro()));
			linha.append(Util.adicionarCharDireita(40, cliente.getEnderecoProprietario().getLogradouro(), ' '));
			linha.append(Util.adicionarCharDireita(5, cliente.getEnderecoProprietario().getNumero(), ' '));
			linha.append(Util.adicionarCharDireita(25, cliente.getEnderecoProprietario().getComplemento(), ' '));
			linha.append(Util.adicionarCharDireita(20, cliente.getEnderecoProprietario().getBairro(), ' '));
			linha.append(Util.adicionarCharDireita(8, cliente.getEnderecoProprietario().getCep().replaceAll("[-]", ""), ' '));
			linha.append(Util.adicionarCharDireita(15, cliente.getEnderecoProprietario().getMunicipio(), ' '));
			
			linha.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(cliente.getResponsavel().getMatricula())));
			linha.append(Util.adicionarCharDireita(50, Util.substringNome(cliente.getResponsavel().getNome()), ' '));
			linha.append((cliente.getResponsavel().getTipoPessoa() != Constantes.NULO_INT ? cliente.getResponsavel().getTipoPessoa() : " "));
			linha.append(Util.adicionarCharDireita(14, cliente.getResponsavel().getCpfCnpj().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""), ' '));
			linha.append(Util.adicionarCharEsquerda(13, cliente.getResponsavel().getRg().replaceAll("[-]", "").replaceAll("[.]", ""), ' '));
			linha.append(Util.adicionarCharEsquerda(2, cliente.getResponsavel().getUf(), ' '));
			linha.append(!cliente.getResponsavel().getTipoSexo().equals(Constantes.NULO_STRING) ? cliente.getResponsavel().getTipoSexo() : " ");
			linha.append(Util.adicionarCharDireita(11, cliente.getResponsavel().getTelefone().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
			linha.append(Util.adicionarCharDireita(11, cliente.getResponsavel().getCelular().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
			linha.append(Util.adicionarCharDireita(30, cliente.getResponsavel().getEmail(), ' '));
			linha.append(Util.adicionarZerosEsquerdaNumero(2, "" + cliente.getEnderecoResponsavel().getTipoLogradouro()));
			linha.append(Util.adicionarCharDireita(40, cliente.getEnderecoResponsavel().getLogradouro(), ' '));
			linha.append(Util.adicionarCharDireita(5, cliente.getEnderecoResponsavel().getNumero(), ' '));
			linha.append(Util.adicionarCharDireita(25, cliente.getEnderecoResponsavel().getComplemento(), ' '));
			linha.append(Util.adicionarCharDireita(20, cliente.getEnderecoResponsavel().getBairro(), ' '));
			linha.append(Util.adicionarCharDireita(8, cliente.getEnderecoResponsavel().getCep().replaceAll("[-]", ""), ' '));
			linha.append(Util.adicionarCharDireita(15, cliente.getEnderecoResponsavel().getMunicipio(), ' '));
			
			linha.append(Util.adicionarCharDireita(20, String.valueOf(cliente.getLatitude() != Constantes.NULO_DOUBLE ? cliente.getLatitude() : " "), ' '));
			linha.append(Util.adicionarCharDireita(20, String.valueOf(cliente.getLongitude() != Constantes.NULO_DOUBLE ? cliente.getLongitude() : " "), ' '));
			linha.append(Util.adicionarCharEsquerda(26, cliente.getData(), ' '));
			
			linha.append("\n");
		} catch (Exception e) {
			LogUtil.salvar(getClass(), "Erro ao gerar linha de Cliente", e);
		}
		
		return Util.removerCaractereEspecial(linha.toString());
	}

	@SuppressLint("DefaultLocale")
	public String gerarLinhaImovel(Imovel imovel) {
		linha = new StringBuffer("02");
		
		try {
			linha.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(imovel.getMatricula())));
			linha.append(String.valueOf(imovel.getTipoOperacao()));
			linha.append(Util.adicionarCharDireita(30, String.valueOf(imovel.getCodigoCliente()), ' '));
			linha.append(Util.adicionarCharDireita(17, imovel.getInscricao(), ' '));
			linha.append(Util.adicionarZerosEsquerdaNumero(2, imovel.getRota()));
			linha.append(Util.adicionarZerosEsquerdaNumero(2, imovel.getFace()));
			linha.append(Util.adicionarZerosEsquerdaNumero(8, String.valueOf(imovel.getCodigoMunicipio())));
			linha.append(Util.adicionarCharDireita(31, imovel.getIptu(), ' '));
			linha.append(Util.adicionarCharDireita(20, imovel.getNumeroCelpa(), ' '));
			linha.append(Util.adicionarZerosEsquerdaNumero(5, String.valueOf(imovel.getNumeroPontosUteis())));
			linha.append(Util.adicionarZerosEsquerdaNumero(5, String.valueOf(imovel.getNumeroOcupantes())));
			linha.append(Util.adicionarZerosEsquerdaNumero(2, "" + imovel.getEnderecoImovel().getTipoLogradouro()));
			linha.append(Util.adicionarCharDireita(40, imovel.getEnderecoImovel().getLogradouro(), ' '));
			linha.append(Util.adicionarCharDireita(5, imovel.getEnderecoImovel().getNumero(), ' '));
			linha.append(Util.adicionarCharDireita(25, Util.removerCaractereEspecial(imovel.getEnderecoImovel().getComplemento()), ' '));
			linha.append(Util.adicionarCharDireita(20, imovel.getEnderecoImovel().getBairro(), ' '));
			linha.append(Util.adicionarCharDireita(8, imovel.getEnderecoImovel().getCep().replaceAll("[-]", ""), ' '));
			linha.append(Util.adicionarCharDireita(15, imovel.getEnderecoImovel().getMunicipio(), ' '));
			linha.append(Util.adicionarCharDireita(9, String.valueOf(imovel.getCodigoLogradouro()), ' '));

			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaResidencial().getEconomiasSubCategoria1())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaResidencial().getEconomiasSubCategoria2())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaResidencial().getEconomiasSubCategoria3())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaResidencial().getEconomiasSubCategoria4())));

			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaComercial().getEconomiasSubCategoria1())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaComercial().getEconomiasSubCategoria2())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaComercial().getEconomiasSubCategoria3())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaComercial().getEconomiasSubCategoria4())));

			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaPublica().getEconomiasSubCategoria1())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaPublica().getEconomiasSubCategoria2())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaPublica().getEconomiasSubCategoria3())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaPublica().getEconomiasSubCategoria4())));

			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaIndustrial().getEconomiasSubCategoria1())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaIndustrial().getEconomiasSubCategoria2())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaIndustrial().getEconomiasSubCategoria3())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaIndustrial().getEconomiasSubCategoria4())));

			linha.append(Util.adicionarZerosEsquerdaNumero(2, "" + imovel.getTipoFonteAbastecimento()));

			linha.append(Util.adicionarZerosEsquerdaNumero(10, imovel.getAreaConstruida()));
			linha.append(Util.adicionarZerosEsquerdaNumero(1, String.valueOf(imovel.getClasseSocial())));
			linha.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getNumeroAnimais())));
			linha.append(Util.adicionarZerosEsquerdaNumero(7, imovel.getVolumeCisterna()));
			linha.append(Util.adicionarZerosEsquerdaNumero(7, imovel.getVolumePiscina()));
			linha.append(Util.adicionarZerosEsquerdaNumero(7, imovel.getVolumeCaixaDagua()));
			linha.append(Util.adicionarZerosEsquerdaNumero(1, String.valueOf(imovel.getTipoUso())));
			linha.append(Util.adicionarZerosEsquerdaNumero(1, String.valueOf(imovel.getAcessoHidrometro())));

			linha.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getCriancas())));
			linha.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getAdultos())));
			linha.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getIdosos())));
			linha.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getEmpregados())));
			linha.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getAlunos())));
			linha.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getCaes())));
			linha.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getOutros())));
			
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getQuantidadeEconomiasSocial())));
			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getQuantidadeEconomiasOutros())));

			linha.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getPercentualAbastecimento())));

			linha.append(Util.adicionarCharDireita(20, String.valueOf(imovel.getLatitude() != Constantes.NULO_DOUBLE ? imovel.getLatitude() : " "), ' '));
			linha.append(Util.adicionarCharDireita(20, String.valueOf(imovel.getLongitude() != Constantes.NULO_DOUBLE ? imovel.getLongitude() : " "), ' '));
			linha.append(Util.adicionarCharEsquerda(26, imovel.getData(), ' '));
			
			linha.append(Util.adicionarCharDireita(100, (Util.removerCaractereEspecialNovo(imovel.getObservacao().toUpperCase())).replaceAll("\n", " "), ' '));
			
			linha.append("\n");
		} catch (Exception e) {
			LogUtil.salvar(getClass(), "Erro ao gerar linha de Imóvel", e);
		}


		return Util.removerCaractereEspecial(linha.toString());
	}

	public String gerarLinhasRamoAtividade(Imovel imovel) {
		StringBuffer ramosAtividade = new StringBuffer();

		try {
			for (int i = 0; i < imovel.getListaRamoAtividade().size(); i++) {
				linha = new StringBuffer("03");
				linha.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(imovel.getMatricula())));
				linha.append(Util.adicionarCharDireita(3, imovel.getListaRamoAtividade().get(i), ' '));
				linha.append("\n");
				ramosAtividade.append(linha);
			}
		} catch (Exception e) {
			LogUtil.salvar(getClass(), "Erro ao gerar linhas de Ramo de Atividade", e);
		}

		return Util.removerCaractereEspecial(ramosAtividade.toString());
	}

	public String gerarLinhaServicos(Imovel imovel, Servicos servicos) {
		linha = new StringBuffer("04");
		
		try {
			linha.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(imovel.getMatricula())));
			linha.append(Util.adicionarCharDireita(2, String.valueOf(servicos.getTipoLigacaoAgua()), ' '));
			linha.append(Util.adicionarCharDireita(2, String.valueOf(servicos.getTipoLigacaoEsgoto()), ' '));
			linha.append(Util.adicionarCharDireita(2, String.valueOf(servicos.getLocalInstalacaoRamal()), ' '));
			linha.append(Util.adicionarCharDireita(20, String.valueOf(servicos.getLatitude() != Constantes.NULO_DOUBLE ? servicos.getLatitude() : " "), ' '));
			linha.append(Util.adicionarCharDireita(20, String.valueOf(servicos.getLongitude() != Constantes.NULO_DOUBLE ? servicos.getLongitude() : " "), ' '));
			linha.append(Util.adicionarCharEsquerda(26, servicos.getData(), ' '));
			linha.append("\n");
		} catch (Exception e) {
			LogUtil.salvar(getClass(), "Erro ao gerar linha de Serviços", e);
		}
		
		return Util.removerCaractereEspecial(linha.toString());
	}

	public String gerarLinhaMedidor(Imovel imovel, Medidor medidor) {
		linha = new StringBuffer("05");

		try {
			linha.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(imovel.getMatricula())));
			linha.append(Util.adicionarCharEsquerda(1, "" + medidor.getPossuiMedidor(), ' '));
			linha.append(Util.adicionarCharDireita(10, medidor.getNumeroHidrometro(), ' '));
			linha.append(Util.adicionarZerosEsquerdaNumero(2, String.valueOf(medidor.getMarca())));
			linha.append(Util.adicionarZerosEsquerdaNumero(2, String.valueOf(medidor.getCapacidade() != Constantes.NULO_INT ? medidor.getCapacidade() : ' ')));
			linha.append(Util.adicionarZerosEsquerdaNumero(2, String.valueOf(medidor.getTipoCaixaProtecao() != Constantes.NULO_INT ? medidor.getTipoCaixaProtecao() : ' ')));
			linha.append(Util.adicionarCharDireita(20, String.valueOf(medidor.getLatitude() != Constantes.NULO_DOUBLE ? medidor.getLatitude() : " "), ' '));
			linha.append(Util.adicionarCharDireita(20, String.valueOf(medidor.getLongitude() != Constantes.NULO_DOUBLE ? medidor.getLongitude() : " "), ' '));
			linha.append(Util.adicionarCharEsquerda(26, medidor.getData(), ' '));
			linha.append("\n");
			
		} catch (Exception e) {
			LogUtil.salvar(getClass(), "Erro ao gerar linha de Medidor", e);
		}

		return Util.removerCaractereEspecial(linha.toString());
	}

	@SuppressLint("DefaultLocale")
	public String gerarLinhaAnormalidade(Imovel imovel, AnormalidadeImovel anormalidade) {
		linha = new StringBuffer("06");

		try {
			linha.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(imovel.getMatricula())));
			linha.append(Util.adicionarCharDireita(3, String.valueOf(anormalidade.getCodigoAnormalidade()), ' '));
			linha.append(Util.adicionarCharDireita(200, (Util.removerCaractereEspecialNovo(anormalidade.getComentario().toUpperCase())).replaceAll("\n", " "), ' '));
			linha.append(Util.adicionarCharDireita(30, anormalidade.getFoto1(), ' '));
			linha.append(Util.adicionarCharDireita(30, anormalidade.getFoto2(), ' '));
			linha.append(Util.adicionarCharDireita(20, String.valueOf(anormalidade.getLatitude() != Constantes.NULO_DOUBLE ? anormalidade.getLatitude() : " "), ' '));
			linha.append(Util.adicionarCharDireita(20, String.valueOf(anormalidade.getLongitude() != Constantes.NULO_DOUBLE ? anormalidade.getLongitude() : " "), ' '));
			linha.append(Util.adicionarCharEsquerda(26, anormalidade.getData(), ' '));
			linha.append(Util.adicionarCharEsquerda(20, (Util.removerCaractereEspecialNovo(imovel.getEntrevistado().toUpperCase())), ' '));
			linha.append(Util.adicionarCharEsquerda(11, anormalidade.getLoginUsuario(), ' '));
			linha.append("\n");
		} catch (Exception e) {
			LogUtil.salvar(getClass(), "Erro ao gerar linha de Anormalidade", e);
		}

		return linha.toString();
	}
}