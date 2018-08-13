package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import model.AnormalidadeImovel;
import model.Cliente;
import model.Imovel;
import model.Medidor;
import model.Servicos;
import util.Constantes;
import util.Util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import business.Controlador;

public class ArquivoRetorno {

	private static ArquivoRetorno instancia;
	private static StringBuffer arquivo;

	private static StringBuffer registrosTipoZero = null;
	private static StringBuffer registrosTipoCLiente = null;
	private static StringBuffer registrosTipoImovel = null;
	private static StringBuffer registrosTipoRamoAtividadeImovel = null;
	private static StringBuffer registroTipoServico = null;
	private static StringBuffer registroTipoMedidor = null;
	private static StringBuffer registroTipoAnormalidadeImovel = null;

	private ArquivoRetorno() {
		super();
	}

	public static ArquivoRetorno getInstancia() {
		if (instancia == null) {
			instancia = new ArquivoRetorno();
		}
		return instancia;
	}

	public static StringBuffer gerarDadosImovelSelecionado() {

		arquivo = new StringBuffer();

		gerarRegistroTipoCliente();
		gerarRegistroTipoImovel();
		gerarRegistrosTipoRamosAtividadeImovel();
		gerarRegistroTipoServico();
		gerarRegistroTipoMedidor();
		gerarRegistroTipoAnormalidadeImovel();

		return arquivo;
	}

	public static void gerarArquivoCompleto(Handler mHandler, Context context, int increment) {

		try {

			File diretorioRetorno = new File(Util.getExternalStorageDirectory() + "/external_sd/Cadastro", "Retorno");
			if (!diretorioRetorno.exists()) {
				diretorioRetorno.mkdirs();
			}

			File fileArquivoCompleto = new File(Util.getRetornoRotaDirectory(), Util.getRotaFileName());

			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				Toast.makeText(context, "Erro ao salvar no cartão de memória!", Toast.LENGTH_SHORT).show();
				return;
			}

			FileOutputStream os = new FileOutputStream(fileArquivoCompleto);
			OutputStreamWriter out = new OutputStreamWriter(os);

			arquivo = new StringBuffer();

			ArrayList<String> listIdImoveis = (ArrayList<String>) Controlador.getInstancia().getCadastroDataManipulator().selectIdImoveis("imovel_status != " + Constantes.IMOVEL_A_SALVAR);

			gerarLinhaZero();

			for (int i = 0; i < listIdImoveis.size(); i++) {
				Controlador.getInstancia().getCadastroDataManipulator().selectCliente(Long.parseLong(listIdImoveis.get(i)));
				Controlador.getInstancia().getCadastroDataManipulator().selectImovel(Long.parseLong(listIdImoveis.get(i)));
				Controlador.getInstancia().getCadastroDataManipulator().selectServico(Long.parseLong(listIdImoveis.get(i)));
				Controlador.getInstancia().getCadastroDataManipulator().selectMedidor(Long.parseLong(listIdImoveis.get(i)));
				Controlador.getInstancia().getCadastroDataManipulator().selectAnormalidadeImovel(String.valueOf(getImovelSelecionado().getMatricula()));
				
				gerarRegistroTipoCliente();
				gerarRegistroTipoImovel();
				gerarRegistrosTipoRamosAtividadeImovel();
				gerarRegistroTipoServico();
				gerarRegistroTipoMedidor();
				gerarRegistroTipoAnormalidadeImovel();

				Bundle b = new Bundle();
				Message msg = mHandler.obtainMessage();
				b.putInt("arquivoCompleto" + String.valueOf(increment), (i + 1));
				msg.setData(b);
				mHandler.sendMessage(msg);
			}

			out.write(arquivo.toString());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void gerarArquivoParcial(Handler mHandler, Context context, int increment) {

		try {

			File diretorioRetorno = new File(Util.getExternalStorageDirectory() + "/external_sd/Cadastro", "Retorno");
			if (!diretorioRetorno.exists()) {
				diretorioRetorno.mkdirs();
			}

			File fileArquivoCompleto = new File(Util.getRetornoRotaDirectory(), Util.getRotaFileName());

			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				Toast.makeText(context, "Erro ao salvar no cartão de memória!", Toast.LENGTH_SHORT).show();
				return;
			}

			FileOutputStream os = new FileOutputStream(fileArquivoCompleto);
			OutputStreamWriter out = new OutputStreamWriter(os);

			arquivo = new StringBuffer();

			ArrayList<String> listIdImoveis = (ArrayList<String>) Controlador.getInstancia().getCadastroDataManipulator().selectIdImoveis(null);

			gerarLinhaZero();

			for (int i = 0; i < listIdImoveis.size(); i++) {
				try{
					Controlador.getInstancia().getCadastroDataManipulator().selectCliente(Long.parseLong(listIdImoveis.get(i)));
					Controlador.getInstancia().getCadastroDataManipulator().selectImovel(Long.parseLong(listIdImoveis.get(i)));
					
					if (getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_A_SALVAR)
						continue;
					
					Controlador.getInstancia().getCadastroDataManipulator().selectServico(Long.parseLong(listIdImoveis.get(i)));
					Controlador.getInstancia().getCadastroDataManipulator().selectMedidor(Long.parseLong(listIdImoveis.get(i)));
					Controlador.getInstancia().getCadastroDataManipulator().selectAnormalidadeImovel(String.valueOf(getImovelSelecionado().getMatricula()));
					
				}catch(Exception e){
					continue;
				}

				gerarRegistroTipoCliente();
				gerarRegistroTipoImovel();
				gerarRegistrosTipoRamosAtividadeImovel();
				gerarRegistroTipoServico();
				gerarRegistroTipoMedidor();
				gerarRegistroTipoAnormalidadeImovel();

				Bundle b = new Bundle();
				Message msg = mHandler.obtainMessage();
				b.putInt("arquivoParcial" + String.valueOf(increment), (i + 1));
				msg.setData(b);
				mHandler.sendMessage(msg);
			}

			out.write(arquivo.toString());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static StringBuffer gerarDadosFinalizacaoRotaOnline(Handler mHandler, Context context, int increment) {
		arquivo = new StringBuffer();

		String filterCondition = "(imovel_enviado = " + Constantes.NAO + ")";

		ArrayList<String> listIdImoveis = (ArrayList<String>) Controlador.getInstancia().getCadastroDataManipulator().selectIdImoveis(filterCondition);

		for (int i = 0; i < listIdImoveis.size(); i++) {

			Controlador.getInstancia().getCadastroDataManipulator().selectCliente(Long.parseLong(listIdImoveis.get(i)));
			Controlador.getInstancia().getCadastroDataManipulator().selectImovel(Long.parseLong(listIdImoveis.get(i)));
			Controlador.getInstancia().getCadastroDataManipulator().selectServico(Long.parseLong(listIdImoveis.get(i)));
			Controlador.getInstancia().getCadastroDataManipulator().selectMedidor(Long.parseLong(listIdImoveis.get(i)));
			Controlador.getInstancia().getCadastroDataManipulator().selectAnormalidadeImovel(Long.parseLong(listIdImoveis.get(i)));

			gerarRegistroTipoCliente();
			gerarRegistroTipoImovel();
			gerarRegistrosTipoRamosAtividadeImovel();
			gerarRegistroTipoServico();
			gerarRegistroTipoMedidor();
			gerarRegistroTipoAnormalidadeImovel();

			Bundle b = new Bundle();
			Message msg = mHandler.obtainMessage();
			b.putInt("finalizacao" + String.valueOf(increment), (i + 1));
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

		return arquivo;
	}

	private static void gerarLinhaZero() {
		registrosTipoZero = new StringBuffer();

		registrosTipoZero.append("00");
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(3, Controlador.getInstancia().getDadosGerais().getGrupoFaturamento()));
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(3, Controlador.getInstancia().getDadosGerais().getLocalidade()));
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(3, Controlador.getInstancia().getDadosGerais().getSetor()));
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(2, Controlador.getInstancia().getDadosGerais().getRota()));
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(6, Controlador.getInstancia().getDadosGerais().getAnoMesFaturamento()));
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(4, "" + Controlador.getInstancia().getDadosGerais().getIdRota()));
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(10, Controlador.getInstancia().getDadosGerais().getVersaoCelular()));

		registrosTipoZero.append("\n");

		arquivo.append(registrosTipoZero);
	}

	private static void gerarRegistroTipoCliente() {
		registrosTipoCLiente = new StringBuffer();

		registrosTipoCLiente.append("01");
		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(getClienteSelecionado().getMatricula())));
		registrosTipoCLiente.append(Util.adicionarCharDireita(25, getClienteSelecionado().getNomeGerenciaRegional(), ' '));

		registrosTipoCLiente.append((getClienteSelecionado().getTipoEnderecoProprietario() != Constantes.NULO_INT ? getClienteSelecionado().getTipoEnderecoProprietario() : " "));
		registrosTipoCLiente.append((getClienteSelecionado().getTipoEnderecoResponsavel() != Constantes.NULO_INT ? getClienteSelecionado().getTipoEnderecoResponsavel() : " "));
		registrosTipoCLiente.append(getClienteSelecionado().isUsuarioProprietario());
		registrosTipoCLiente.append(getClienteSelecionado().getTipoResponsavel());

		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(getClienteSelecionado().getUsuario().getMatricula())));
		registrosTipoCLiente.append(Util.adicionarCharDireita(50, Util.substringNome(getClienteSelecionado().getUsuario().getNome()), ' '));
		registrosTipoCLiente.append((getClienteSelecionado().getUsuario().getTipoPessoa() != Constantes.NULO_INT ? getClienteSelecionado().getUsuario().getTipoPessoa() : " "));
		registrosTipoCLiente.append(Util.adicionarCharDireita(14, getClienteSelecionado().getUsuario().getCpfCnpj().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(9, getClienteSelecionado().getUsuario().getRg().replaceAll("[-]", "").replaceAll("[.]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(2, getClienteSelecionado().getUsuario().getUf(), ' '));
		registrosTipoCLiente.append(!getClienteSelecionado().getUsuario().getTipoSexo().equals(Constantes.NULO_STRING) ? getClienteSelecionado().getUsuario().getTipoSexo() : " ");
		registrosTipoCLiente.append(Util.adicionarCharDireita(11, getClienteSelecionado().getUsuario().getTelefone().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(11, getClienteSelecionado().getUsuario().getCelular().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(30, getClienteSelecionado().getUsuario().getEmail(), ' '));

		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(getClienteSelecionado().getProprietario().getMatricula())));
		registrosTipoCLiente.append(Util.adicionarCharDireita(50, Util.substringNome(getClienteSelecionado().getProprietario().getNome()), ' '));
		registrosTipoCLiente.append((getClienteSelecionado().getProprietario().getTipoPessoa() != Constantes.NULO_INT ? getClienteSelecionado().getProprietario().getTipoPessoa() : " "));
		registrosTipoCLiente.append(Util.adicionarCharDireita(14, getClienteSelecionado().getProprietario().getCpfCnpj().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(9, getClienteSelecionado().getProprietario().getRg().replaceAll("[-]", "").replaceAll("[.]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(2, getClienteSelecionado().getProprietario().getUf(), ' '));

		String tipoSexo = "";
		if (getClienteSelecionado().isUsuarioProprietario() == Constantes.SIM) {
			tipoSexo = getClienteSelecionado().getUsuario().getTipoSexo();
		} else {
			tipoSexo = getClienteSelecionado().getProprietario().getTipoSexo();
		}
		
		registrosTipoCLiente.append(Util.adicionarCharDireita(1, tipoSexo, ' '));

		registrosTipoCLiente.append(Util.adicionarCharDireita(11, getClienteSelecionado().getProprietario().getTelefone().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(11, getClienteSelecionado().getProprietario().getCelular().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(30, getClienteSelecionado().getProprietario().getEmail(), ' '));
		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(2, "" + getClienteSelecionado().getEnderecoProprietario().getTipoLogradouro()));
		registrosTipoCLiente.append(Util.adicionarCharDireita(40, getClienteSelecionado().getEnderecoProprietario().getLogradouro(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(5, getClienteSelecionado().getEnderecoProprietario().getNumero(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(25, getClienteSelecionado().getEnderecoProprietario().getComplemento(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(20, getClienteSelecionado().getEnderecoProprietario().getBairro(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(8, getClienteSelecionado().getEnderecoProprietario().getCep().replaceAll("[-]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(15, getClienteSelecionado().getEnderecoProprietario().getMunicipio(), ' '));

		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(getClienteSelecionado().getResponsavel().getMatricula())));
		registrosTipoCLiente.append(Util.adicionarCharDireita(50, Util.substringNome(getClienteSelecionado().getResponsavel().getNome()), ' '));
		registrosTipoCLiente.append((getClienteSelecionado().getResponsavel().getTipoPessoa() != Constantes.NULO_INT ? getClienteSelecionado().getResponsavel().getTipoPessoa() : " "));
		registrosTipoCLiente.append(Util.adicionarCharDireita(14, getClienteSelecionado().getResponsavel().getCpfCnpj().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(9, getClienteSelecionado().getResponsavel().getRg().replaceAll("[-]", "").replaceAll("[.]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(2, getClienteSelecionado().getResponsavel().getUf(), ' '));
		registrosTipoCLiente.append(!getClienteSelecionado().getResponsavel().getTipoSexo().equals(Constantes.NULO_STRING) ? getClienteSelecionado().getResponsavel().getTipoSexo() : " ");
		registrosTipoCLiente.append(Util.adicionarCharDireita(11, getClienteSelecionado().getResponsavel().getTelefone().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(11, getClienteSelecionado().getResponsavel().getCelular().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(30, getClienteSelecionado().getResponsavel().getEmail(), ' '));
		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(2, "" + getClienteSelecionado().getEnderecoResponsavel().getTipoLogradouro()));
		registrosTipoCLiente.append(Util.adicionarCharDireita(40, getClienteSelecionado().getEnderecoResponsavel().getLogradouro(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(5, getClienteSelecionado().getEnderecoResponsavel().getNumero(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(25, getClienteSelecionado().getEnderecoResponsavel().getComplemento(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(20, getClienteSelecionado().getEnderecoResponsavel().getBairro(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(8, getClienteSelecionado().getEnderecoResponsavel().getCep().replaceAll("[-]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(15, getClienteSelecionado().getEnderecoResponsavel().getMunicipio(), ' '));

		registrosTipoCLiente.append(Util.adicionarCharDireita(20, String.valueOf(getClienteSelecionado().getLatitude() != Constantes.NULO_DOUBLE ? getClienteSelecionado().getLatitude() : " "), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(20, String.valueOf(getClienteSelecionado().getLongitude() != Constantes.NULO_DOUBLE ? getClienteSelecionado().getLongitude() : " "), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(26, getClienteSelecionado().getData(), ' '));
		registrosTipoCLiente.append("\n");

		arquivo.append(Util.removerCaractereEspecial(registrosTipoCLiente.toString()));
	}

	private static void gerarRegistroTipoImovel() {

		registrosTipoImovel = new StringBuffer();

		registrosTipoImovel.append("02");
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(getImovelSelecionado().getMatricula())));
		registrosTipoImovel.append(String.valueOf(getImovelSelecionado().getOperacaoTipo()));
		registrosTipoImovel.append(Util.adicionarCharDireita(30, String.valueOf(getImovelSelecionado().getCodigoCliente()), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(17, getImovelSelecionado().getInscricao(), ' '));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(2, getImovelSelecionado().getRota()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(2, getImovelSelecionado().getFace()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(8, String.valueOf(getImovelSelecionado().getCodigoMunicipio())));
		registrosTipoImovel.append(Util.adicionarCharDireita(31, getImovelSelecionado().getIptu(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(20, getImovelSelecionado().getNumeroCelpa(), ' '));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(5, String.valueOf(getImovelSelecionado().getNumeroPontosUteis())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(5, String.valueOf(getImovelSelecionado().getNumeroOcupantes())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(2, "" + getImovelSelecionado().getEnderecoImovel().getTipoLogradouro()));
		registrosTipoImovel.append(Util.adicionarCharDireita(40, getImovelSelecionado().getEnderecoImovel().getLogradouro(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(5, getImovelSelecionado().getEnderecoImovel().getNumero(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(25, getImovelSelecionado().getEnderecoImovel().getComplemento(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(20, getImovelSelecionado().getEnderecoImovel().getBairro(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(8, getImovelSelecionado().getEnderecoImovel().getCep().replaceAll("[-]", ""), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(15, getImovelSelecionado().getEnderecoImovel().getMunicipio(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(9, String.valueOf(getImovelSelecionado().getCodigoLogradouro()), ' '));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria1())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria2())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria3())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria4())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria1())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria2())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria3())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria4())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria1())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria2())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria3())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria4())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria1())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria2())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria3())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria4())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(2, "" + getImovelSelecionado().getTipoFonteAbastecimento()));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(10, getImovelSelecionado().getAreaConstruida()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(1, String.valueOf(getImovelSelecionado().getClasseSocial())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(getImovelSelecionado().getNumeroAnimais())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(7, getImovelSelecionado().getVolumeCisterna()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(7, getImovelSelecionado().getVolumePiscina()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(7, getImovelSelecionado().getVolumeCaixaDagua()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(1, String.valueOf(getImovelSelecionado().getTipoUso())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(1, String.valueOf(getImovelSelecionado().getAcessoHidrometro())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(getImovelSelecionado().getOcupacaoImovel().getCriancas())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(getImovelSelecionado().getOcupacaoImovel().getAdultos())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(getImovelSelecionado().getOcupacaoImovel().getIdosos())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(getImovelSelecionado().getOcupacaoImovel().getEmpregados())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(getImovelSelecionado().getOcupacaoImovel().getAlunos())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(getImovelSelecionado().getOcupacaoImovel().getCaes())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(getImovelSelecionado().getOcupacaoImovel().getOutros())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getQuantidadeEconomiasSocial())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getQuantidadeEconomiasOutros())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(getImovelSelecionado().getPercentualAbastecimento())));

		registrosTipoImovel.append(Util.adicionarCharDireita(20, String.valueOf(getImovelSelecionado().getLatitude() != Constantes.NULO_DOUBLE ? getImovelSelecionado().getLatitude() : " "), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(20, String.valueOf(getImovelSelecionado().getLongitude() != Constantes.NULO_DOUBLE ? getImovelSelecionado().getLongitude() : " "), ' '));
		registrosTipoImovel.append(Util.adicionarCharEsquerda(26, getImovelSelecionado().getData(), ' '));
		registrosTipoImovel.append("\n");

		arquivo.append(Util.removerCaractereEspecial(registrosTipoImovel.toString()));
	}

	private static void gerarRegistrosTipoRamosAtividadeImovel() {
		for (int i = 0; i < getImovelSelecionado().getListaRamoAtividade().size(); i++) {
			registrosTipoRamoAtividadeImovel = new StringBuffer();

			registrosTipoRamoAtividadeImovel.append("03");
			registrosTipoRamoAtividadeImovel.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(getImovelSelecionado().getMatricula())));
			registrosTipoRamoAtividadeImovel.append(Util.adicionarCharDireita(3, getImovelSelecionado().getListaRamoAtividade().get(i), ' '));
			registrosTipoRamoAtividadeImovel.append("\n");

			arquivo.append(Util.removerCaractereEspecial(registrosTipoRamoAtividadeImovel.toString()));
		}
	}

	private static void gerarRegistroTipoServico() {
		registroTipoServico = new StringBuffer();
		registroTipoServico.append("04");
		registroTipoServico.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(getImovelSelecionado().getMatricula())));
		registroTipoServico.append(Util.adicionarCharDireita(2, String.valueOf(getServicosSelecionado().getTipoLigacaoAgua()), ' '));
		registroTipoServico.append(Util.adicionarCharDireita(2, String.valueOf(getServicosSelecionado().getTipoLigacaoEsgoto()), ' '));
		registroTipoServico.append(Util.adicionarCharDireita(2, String.valueOf(getServicosSelecionado().getLocalInstalacaoRamal()), ' '));
		registroTipoServico.append(Util.adicionarCharDireita(20, String.valueOf(getServicosSelecionado().getLatitude() != Constantes.NULO_DOUBLE ? getServicosSelecionado().getLatitude() : " "), ' '));
		registroTipoServico.append(Util.adicionarCharDireita(20, String.valueOf(getServicosSelecionado().getLongitude() != Constantes.NULO_DOUBLE ? getServicosSelecionado().getLongitude() : " "), ' '));
		registroTipoServico.append(Util.adicionarCharEsquerda(26, getServicosSelecionado().getData(), ' '));
		registroTipoServico.append("\n");
		arquivo.append(Util.removerCaractereEspecial(registroTipoServico.toString()));
	}

	private static void gerarRegistroTipoMedidor() {
		registroTipoMedidor = new StringBuffer();
		registroTipoMedidor.append("05");

		registroTipoMedidor.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(getImovelSelecionado().getMatricula())));
		registroTipoMedidor.append(Util.adicionarCharEsquerda(1, "" + getMedidorSelecionado().getPossuiMedidor(), ' '));
		registroTipoMedidor.append(Util.adicionarCharDireita(10, getMedidorSelecionado().getNumeroHidrometro(), ' '));
		registroTipoMedidor.append(Util.adicionarZerosEsquerdaNumero(2, String.valueOf(getMedidorSelecionado().getMarca())));
		registroTipoMedidor.append(Util.adicionarZerosEsquerdaNumero(2, String.valueOf(getMedidorSelecionado().getCapacidade() != Constantes.NULO_INT ? getMedidorSelecionado().getCapacidade() : ' ')));
		registroTipoMedidor.append(Util.adicionarZerosEsquerdaNumero(2, String.valueOf(getMedidorSelecionado().getTipoCaixaProtecao() != Constantes.NULO_INT ? getMedidorSelecionado().getTipoCaixaProtecao() : ' ')));
		registroTipoMedidor.append(Util.adicionarCharDireita(20, String.valueOf(getMedidorSelecionado().getLatitude() != Constantes.NULO_DOUBLE ? getMedidorSelecionado().getLatitude() : " "), ' '));
		registroTipoMedidor.append(Util.adicionarCharDireita(20, String.valueOf(getMedidorSelecionado().getLongitude() != Constantes.NULO_DOUBLE ? getMedidorSelecionado().getLongitude() : " "), ' '));
		registroTipoMedidor.append(Util.adicionarCharEsquerda(26, getMedidorSelecionado().getData(), ' '));
		registroTipoMedidor.append("\n");

		arquivo.append(Util.removerCaractereEspecial(registroTipoMedidor.toString()));
	}

	@SuppressLint("DefaultLocale")
	private static void gerarRegistroTipoAnormalidadeImovel() {
		registroTipoAnormalidadeImovel = new StringBuffer();

		registroTipoAnormalidadeImovel.append("06");
		registroTipoAnormalidadeImovel.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(getImovelSelecionado().getMatricula())));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(3, String.valueOf(getAnormalidadeImovelSelecionado().getCodigoAnormalidade()), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(200, getAnormalidadeImovelSelecionado().getComentario().toUpperCase().replaceAll("\n",	" "), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(30, getAnormalidadeImovelSelecionado().getFoto1(), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(30, getAnormalidadeImovelSelecionado().getFoto2(), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(20, String.valueOf(getAnormalidadeImovelSelecionado().getLatitude() != Constantes.NULO_DOUBLE ? getAnormalidadeImovelSelecionado().getLatitude() : " "), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(20, String.valueOf(getAnormalidadeImovelSelecionado().getLongitude() != Constantes.NULO_DOUBLE ? getAnormalidadeImovelSelecionado().getLongitude() : " "), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharEsquerda(26, getAnormalidadeImovelSelecionado().getData(), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharEsquerda(20, getImovelSelecionado().getEntrevistado().toUpperCase(), ' '));
		registroTipoAnormalidadeImovel.append("\n");

		arquivo.append(Util.removerCaractereEspecial(registroTipoAnormalidadeImovel.toString()));
	}

	public static Cliente getClienteSelecionado() {
		return Controlador.getInstancia().getClienteSelecionado();
	}

	public static Imovel getImovelSelecionado() {
		return Controlador.getInstancia().getImovelSelecionado();
	}

	public static Medidor getMedidorSelecionado() {
		return Controlador.getInstancia().getMedidorSelecionado();
	}

	public static Servicos getServicosSelecionado() {
		return Controlador.getInstancia().getServicosSelecionado();
	}

	public static AnormalidadeImovel getAnormalidadeImovelSelecionado() {
		return Controlador.getInstancia().getAnormalidadeImovelSelecionado();
	}

}
