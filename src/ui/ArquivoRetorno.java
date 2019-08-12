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

	public static StringBuffer gerarDadosImovel(Imovel imovel) {
		arquivo = new StringBuffer();
		
		Cliente cliente = Controlador.getInstancia().getCadastroDataManipulator().selectClientePorId(imovel.getImovelId());
		Servicos servicos = Controlador.getInstancia().getCadastroDataManipulator().selectServicos(imovel.getImovelId());
		Medidor medidor = Controlador.getInstancia().getCadastroDataManipulator().selectMedidorPorId(imovel.getImovelId());
		AnormalidadeImovel anormalidade = Controlador.getInstancia().getCadastroDataManipulator().selectAnormalidadeImovel(String.valueOf(imovel.getMatricula()));

		gerarLinhaZero();
		gerarRegistroTipoCliente(cliente);
		gerarRegistroTipoImovel(imovel);
		gerarRegistrosTipoRamosAtividadeImovel(imovel);
		gerarRegistroTipoServico(imovel, servicos);
		gerarRegistroTipoMedidor(imovel, medidor);
		gerarRegistroTipoAnormalidadeImovel(imovel, anormalidade);

		return arquivo;
	}

	public static void gerar(Handler handler, Context context, int increment) {
		try {
			File diretorio = new File(Util.getExternalStorageDirectory() + "/external_sd/Cadastro", "Retorno");
			if (!diretorio.exists()) {
				diretorio.mkdirs();
			}

			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				Toast.makeText(context, "Erro ao salvar no cartão de memória!", Toast.LENGTH_SHORT).show();
				return;
			}

			File file = new File(Util.getRetornoRotaDirectory(), Util.getRotaFileName() + ".txt");
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file));

			arquivo = new StringBuffer();
			ArrayList<String> ids = (ArrayList<String>) Controlador.getInstancia().getCadastroDataManipulator().selectIdImoveis(
					"imovel_status != " + Constantes.IMOVEL_A_SALVAR);

			gerarLinhaZero();

			for (int i = 0; i < ids.size(); i++) {
				Controlador.getInstancia().getCadastroDataManipulator().selectCliente(Long.parseLong(ids.get(i)));
				Controlador.getInstancia().getCadastroDataManipulator().selectImovel(Long.parseLong(ids.get(i)));

				if (isCadastroInvalido())
					continue;
				
				Controlador.getInstancia().getCadastroDataManipulator().selectServico(Long.parseLong(ids.get(i)));
				Controlador.getInstancia().getCadastroDataManipulator().selectMedidor(Long.parseLong(ids.get(i)));
				Controlador.getInstancia().getCadastroDataManipulator().selectAnormalidadeImovel(String.valueOf(getImovelSelecionado().getMatricula()));
				
				gerarRegistroTipoCliente(getClienteSelecionado());
				gerarRegistroTipoImovel(getImovelSelecionado());
				gerarRegistrosTipoRamosAtividadeImovel(getImovelSelecionado());
				gerarRegistroTipoServico(getImovelSelecionado(), getServicosSelecionado());
				gerarRegistroTipoMedidor(getImovelSelecionado(), getMedidorSelecionado());
				gerarRegistroTipoAnormalidadeImovel(getImovelSelecionado(), getAnormalidadeImovelSelecionado());

				atualizarProcessamento(handler, increment, i + 1);
			}

			atualizarProcessamento(handler, increment, ids.size());
			
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

	private static void atualizarProcessamento(Handler handler, int increment, int size) {
		Bundle bundle = new Bundle();
		Message msg = handler.obtainMessage();
		bundle.putInt("arquivoCompleto" + increment, size);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	private static boolean isCadastroInvalido() {
		return (getClienteSelecionado().getUsuario().getMatricula() == Constantes.NULO_INT || getClienteSelecionado().getUsuario().getMatricula() == 0) 
				&& getClienteSelecionado().getUsuario().getNome().trim().equals("") 
				&& getImovelSelecionado().getEnderecoImovel().getTipoLogradouro() == 0 
				&& getImovelSelecionado().getImovelStatus() != Constantes.IMOVEL_NOVO_COM_ANORMALIDADE;
	}
	
	public static void gerarArquivoParcial(Handler mHandler, Context context, int increment) {

		try {

			File diretorioRetorno = new File(Util.getExternalStorageDirectory() + "/external_sd/Cadastro", "Retorno");
			if (!diretorioRetorno.exists()) {
				diretorioRetorno.mkdirs();
			}

			File fileArquivoCompleto = new File(Util.getRetornoRotaDirectory(), Util.getRotaFileName() + ".txt");

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

				gerarRegistroTipoCliente(getClienteSelecionado());
				gerarRegistroTipoImovel(getImovelSelecionado());
				gerarRegistrosTipoRamosAtividadeImovel(getImovelSelecionado());
				gerarRegistroTipoServico(getImovelSelecionado(), getServicosSelecionado());
				gerarRegistroTipoMedidor(getImovelSelecionado(), getMedidorSelecionado());
				gerarRegistroTipoAnormalidadeImovel(getImovelSelecionado(), getAnormalidadeImovelSelecionado());

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

			gerarRegistroTipoCliente(getClienteSelecionado());
			gerarRegistroTipoImovel(getImovelSelecionado());
			gerarRegistrosTipoRamosAtividadeImovel(getImovelSelecionado());
			gerarRegistroTipoServico(getImovelSelecionado(), getServicosSelecionado());
			gerarRegistroTipoMedidor(getImovelSelecionado(), getMedidorSelecionado());
			gerarRegistroTipoAnormalidadeImovel(getImovelSelecionado(), getAnormalidadeImovelSelecionado());

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
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(3, Controlador.getInstancia().getDadosGerais().getLocalidade()));
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(3, Controlador.getInstancia().getDadosGerais().getSetor()));
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(2, Controlador.getInstancia().getDadosGerais().getRota()));
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(4, Controlador.getInstancia().getDadosGerais().getIdRota()));
		registrosTipoZero.append(Util.adicionarZerosEsquerdaNumero(10, Controlador.getInstancia().getDadosGerais().getVersaoAplicativo()));
		registrosTipoZero.append(Util.adicionarCharDireita(1, Controlador.getInstancia().getDadosGerais().getTipoArquivo(), ' '));
		
		registrosTipoZero.append("\n");

		arquivo.append(registrosTipoZero);
	}

	private static void gerarRegistroTipoCliente(Cliente cliente) {
		registrosTipoCLiente = new StringBuffer();

		registrosTipoCLiente.append("01");
		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(cliente.getMatricula())));
		registrosTipoCLiente.append(Util.adicionarCharDireita(25, cliente.getNomeGerenciaRegional(), ' '));

		registrosTipoCLiente.append((cliente.getTipoEnderecoProprietario() != Constantes.NULO_INT ? cliente.getTipoEnderecoProprietario() : " "));
		registrosTipoCLiente.append((cliente.getTipoEnderecoResponsavel() != Constantes.NULO_INT ? cliente.getTipoEnderecoResponsavel() : " "));
		registrosTipoCLiente.append(cliente.isUsuarioProprietario());
		registrosTipoCLiente.append(cliente.getTipoResponsavel());

		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(cliente.getUsuario().getMatricula())));
		registrosTipoCLiente.append(Util.adicionarCharDireita(50, Util.substringNome(cliente.getUsuario().getNome()), ' '));
		registrosTipoCLiente.append((cliente.getUsuario().getTipoPessoa() != Constantes.NULO_INT ? cliente.getUsuario().getTipoPessoa() : " "));
		registrosTipoCLiente.append(Util.adicionarCharDireita(14, cliente.getUsuario().getCpfCnpj().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(13, cliente.getUsuario().getRg().replaceAll("[-]", "").replaceAll("[.]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(2, cliente.getUsuario().getUf(), ' '));
		registrosTipoCLiente.append(!cliente.getUsuario().getTipoSexo().equals(Constantes.NULO_STRING) ? cliente.getUsuario().getTipoSexo() : " ");
		registrosTipoCLiente.append(Util.adicionarCharDireita(11, cliente.getUsuario().getTelefone().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(11, cliente.getUsuario().getCelular().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(30, cliente.getUsuario().getEmail(), ' '));

		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(cliente.getProprietario().getMatricula())));
		registrosTipoCLiente.append(Util.adicionarCharDireita(50, Util.substringNome(cliente.getProprietario().getNome()), ' '));
		registrosTipoCLiente.append((cliente.getProprietario().getTipoPessoa() != Constantes.NULO_INT ? cliente.getProprietario().getTipoPessoa() : " "));
		registrosTipoCLiente.append(Util.adicionarCharDireita(14, cliente.getProprietario().getCpfCnpj().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(13, cliente.getProprietario().getRg().replaceAll("[-]", "").replaceAll("[.]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(2, cliente.getProprietario().getUf(), ' '));

		String tipoSexo = "";
		if (cliente.isUsuarioProprietario() == Constantes.SIM) {
			tipoSexo = cliente.getUsuario().getTipoSexo();
		} else {
			tipoSexo = cliente.getProprietario().getTipoSexo();
		}
		
		registrosTipoCLiente.append(Util.adicionarCharDireita(1, tipoSexo, ' '));

		registrosTipoCLiente.append(Util.adicionarCharDireita(11, cliente.getProprietario().getTelefone().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(11, cliente.getProprietario().getCelular().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(30, cliente.getProprietario().getEmail(), ' '));
		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(2, "" + cliente.getEnderecoProprietario().getTipoLogradouro()));
		registrosTipoCLiente.append(Util.adicionarCharDireita(40, cliente.getEnderecoProprietario().getLogradouro(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(5, cliente.getEnderecoProprietario().getNumero(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(25, cliente.getEnderecoProprietario().getComplemento(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(20, cliente.getEnderecoProprietario().getBairro(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(8, cliente.getEnderecoProprietario().getCep().replaceAll("[-]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(15, cliente.getEnderecoProprietario().getMunicipio(), ' '));

		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(cliente.getResponsavel().getMatricula())));
		registrosTipoCLiente.append(Util.adicionarCharDireita(50, Util.substringNome(cliente.getResponsavel().getNome()), ' '));
		registrosTipoCLiente.append((cliente.getResponsavel().getTipoPessoa() != Constantes.NULO_INT ? cliente.getResponsavel().getTipoPessoa() : " "));
		registrosTipoCLiente.append(Util.adicionarCharDireita(14, cliente.getResponsavel().getCpfCnpj().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(13, cliente.getResponsavel().getRg().replaceAll("[-]", "").replaceAll("[.]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(2, cliente.getResponsavel().getUf(), ' '));
		registrosTipoCLiente.append(!cliente.getResponsavel().getTipoSexo().equals(Constantes.NULO_STRING) ? cliente.getResponsavel().getTipoSexo() : " ");
		registrosTipoCLiente.append(Util.adicionarCharDireita(11, cliente.getResponsavel().getTelefone().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(11, cliente.getResponsavel().getCelular().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(30, cliente.getResponsavel().getEmail(), ' '));
		registrosTipoCLiente.append(Util.adicionarZerosEsquerdaNumero(2, "" + cliente.getEnderecoResponsavel().getTipoLogradouro()));
		registrosTipoCLiente.append(Util.adicionarCharDireita(40, cliente.getEnderecoResponsavel().getLogradouro(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(5, cliente.getEnderecoResponsavel().getNumero(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(25, cliente.getEnderecoResponsavel().getComplemento(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(20, cliente.getEnderecoResponsavel().getBairro(), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(8, cliente.getEnderecoResponsavel().getCep().replaceAll("[-]", ""), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(15, cliente.getEnderecoResponsavel().getMunicipio(), ' '));

		registrosTipoCLiente.append(Util.adicionarCharDireita(20, String.valueOf(cliente.getLatitude() != Constantes.NULO_DOUBLE ? cliente.getLatitude() : " "), ' '));
		registrosTipoCLiente.append(Util.adicionarCharDireita(20, String.valueOf(cliente.getLongitude() != Constantes.NULO_DOUBLE ? cliente.getLongitude() : " "), ' '));
		registrosTipoCLiente.append(Util.adicionarCharEsquerda(26, cliente.getData(), ' '));
		registrosTipoCLiente.append("\n");

		arquivo.append(Util.removerCaractereEspecial(registrosTipoCLiente.toString()));
	}

	@SuppressLint("DefaultLocale")
	private static void gerarRegistroTipoImovel(Imovel imovel) {

		registrosTipoImovel = new StringBuffer();

		registrosTipoImovel.append("02");
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(imovel.getMatricula())));
		registrosTipoImovel.append(String.valueOf(imovel.getOperacaoTipo()));
		registrosTipoImovel.append(Util.adicionarCharDireita(30, String.valueOf(imovel.getCodigoCliente()), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(17, imovel.getInscricao(), ' '));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(2, imovel.getRota()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(2, imovel.getFace()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(8, String.valueOf(imovel.getCodigoMunicipio())));
		registrosTipoImovel.append(Util.adicionarCharDireita(31, imovel.getIptu(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(20, imovel.getNumeroCelpa(), ' '));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(5, String.valueOf(imovel.getNumeroPontosUteis())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(5, String.valueOf(imovel.getNumeroOcupantes())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(2, "" + imovel.getEnderecoImovel().getTipoLogradouro()));
		registrosTipoImovel.append(Util.adicionarCharDireita(40, imovel.getEnderecoImovel().getLogradouro(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(5, imovel.getEnderecoImovel().getNumero(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(25, Util.removerCaractereEspecial(imovel.getEnderecoImovel().getComplemento()), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(20, imovel.getEnderecoImovel().getBairro(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(8, imovel.getEnderecoImovel().getCep().replaceAll("[-]", ""), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(15, imovel.getEnderecoImovel().getMunicipio(), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(9, String.valueOf(imovel.getCodigoLogradouro()), ' '));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaResidencial().getEconomiasSubCategoria1())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaResidencial().getEconomiasSubCategoria2())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaResidencial().getEconomiasSubCategoria3())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaResidencial().getEconomiasSubCategoria4())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaComercial().getEconomiasSubCategoria1())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaComercial().getEconomiasSubCategoria2())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaComercial().getEconomiasSubCategoria3())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaComercial().getEconomiasSubCategoria4())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaPublica().getEconomiasSubCategoria1())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaPublica().getEconomiasSubCategoria2())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaPublica().getEconomiasSubCategoria3())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaPublica().getEconomiasSubCategoria4())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaIndustrial().getEconomiasSubCategoria1())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaIndustrial().getEconomiasSubCategoria2())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaIndustrial().getEconomiasSubCategoria3())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getCategoriaIndustrial().getEconomiasSubCategoria4())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(2, "" + imovel.getTipoFonteAbastecimento()));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(10, imovel.getAreaConstruida()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(1, String.valueOf(imovel.getClasseSocial())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getNumeroAnimais())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(7, imovel.getVolumeCisterna()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(7, imovel.getVolumePiscina()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(7, imovel.getVolumeCaixaDagua()));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(1, String.valueOf(imovel.getTipoUso())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(1, String.valueOf(imovel.getAcessoHidrometro())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getCriancas())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getAdultos())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getIdosos())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getEmpregados())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getAlunos())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getCaes())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(imovel.getOcupacaoImovel().getOutros())));
		
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getQuantidadeEconomiasSocial())));
		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getQuantidadeEconomiasOutros())));

		registrosTipoImovel.append(Util.adicionarZerosEsquerdaNumero(3, String.valueOf(imovel.getPercentualAbastecimento())));

		registrosTipoImovel.append(Util.adicionarCharDireita(20, String.valueOf(imovel.getLatitude() != Constantes.NULO_DOUBLE ? imovel.getLatitude() : " "), ' '));
		registrosTipoImovel.append(Util.adicionarCharDireita(20, String.valueOf(imovel.getLongitude() != Constantes.NULO_DOUBLE ? imovel.getLongitude() : " "), ' '));
		registrosTipoImovel.append(Util.adicionarCharEsquerda(26, imovel.getData(), ' '));
		
		if (imovel.getObservacao() != null) {
			registrosTipoImovel.append(Util.adicionarCharDireita(100, (Util.removerCaractereEspecialNovo(imovel.getObservacao().toUpperCase())).replaceAll("\n", " "), ' '));
		}
		
		registrosTipoImovel.append("\n");

		arquivo.append(Util.removerCaractereEspecial(registrosTipoImovel.toString()));
	}

	private static void gerarRegistrosTipoRamosAtividadeImovel(Imovel imovel) {
		for (int i = 0; i < imovel.getListaRamoAtividade().size(); i++) {
			registrosTipoRamoAtividadeImovel = new StringBuffer();

			registrosTipoRamoAtividadeImovel.append("03");
			registrosTipoRamoAtividadeImovel.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(imovel.getMatricula())));
			registrosTipoRamoAtividadeImovel.append(Util.adicionarCharDireita(3, imovel.getListaRamoAtividade().get(i), ' '));
			registrosTipoRamoAtividadeImovel.append("\n");

			arquivo.append(Util.removerCaractereEspecial(registrosTipoRamoAtividadeImovel.toString()));
		}
	}

	private static void gerarRegistroTipoServico(Imovel imovel, Servicos servicos) {
		registroTipoServico = new StringBuffer();
		registroTipoServico.append("04");
		registroTipoServico.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(imovel.getMatricula())));
		registroTipoServico.append(Util.adicionarCharDireita(2, String.valueOf(servicos.getTipoLigacaoAgua()), ' '));
		registroTipoServico.append(Util.adicionarCharDireita(2, String.valueOf(servicos.getTipoLigacaoEsgoto()), ' '));
		registroTipoServico.append(Util.adicionarCharDireita(2, String.valueOf(servicos.getLocalInstalacaoRamal()), ' '));
		registroTipoServico.append(Util.adicionarCharDireita(20, String.valueOf(servicos.getLatitude() != Constantes.NULO_DOUBLE ? servicos.getLatitude() : " "), ' '));
		registroTipoServico.append(Util.adicionarCharDireita(20, String.valueOf(servicos.getLongitude() != Constantes.NULO_DOUBLE ? servicos.getLongitude() : " "), ' '));
		registroTipoServico.append(Util.adicionarCharEsquerda(26, servicos.getData(), ' '));
		registroTipoServico.append("\n");
		arquivo.append(Util.removerCaractereEspecial(registroTipoServico.toString()));
	}

	private static void gerarRegistroTipoMedidor(Imovel imovel, Medidor medidor) {
		registroTipoMedidor = new StringBuffer();
		registroTipoMedidor.append("05");

		registroTipoMedidor.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(imovel.getMatricula())));
		registroTipoMedidor.append(Util.adicionarCharEsquerda(1, "" + medidor.getPossuiMedidor(), ' '));
		registroTipoMedidor.append(Util.adicionarCharDireita(10, medidor.getNumeroHidrometro(), ' '));
		registroTipoMedidor.append(Util.adicionarZerosEsquerdaNumero(2, String.valueOf(medidor.getMarca())));
		registroTipoMedidor.append(Util.adicionarZerosEsquerdaNumero(2, String.valueOf(medidor.getCapacidade() != Constantes.NULO_INT ? medidor.getCapacidade() : ' ')));
		registroTipoMedidor.append(Util.adicionarZerosEsquerdaNumero(2, String.valueOf(medidor.getTipoCaixaProtecao() != Constantes.NULO_INT ? medidor.getTipoCaixaProtecao() : ' ')));
		registroTipoMedidor.append(Util.adicionarCharDireita(20, String.valueOf(medidor.getLatitude() != Constantes.NULO_DOUBLE ? medidor.getLatitude() : " "), ' '));
		registroTipoMedidor.append(Util.adicionarCharDireita(20, String.valueOf(medidor.getLongitude() != Constantes.NULO_DOUBLE ? medidor.getLongitude() : " "), ' '));
		registroTipoMedidor.append(Util.adicionarCharEsquerda(26, medidor.getData(), ' '));
		registroTipoMedidor.append("\n");

		arquivo.append(Util.removerCaractereEspecial(registroTipoMedidor.toString()));
	}

	@SuppressLint("DefaultLocale")
	private static void gerarRegistroTipoAnormalidadeImovel(Imovel imovel, AnormalidadeImovel anormalidade) {
		registroTipoAnormalidadeImovel = new StringBuffer();

		registroTipoAnormalidadeImovel.append("06");
		registroTipoAnormalidadeImovel.append(Util.adicionarZerosEsquerdaNumero(9, String.valueOf(imovel.getMatricula())));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(3, String.valueOf(anormalidade.getCodigoAnormalidade()), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(200, (Util.removerCaractereEspecialNovo(anormalidade.getComentario().toUpperCase())).replaceAll("\n",	" "), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(30, anormalidade.getFoto1(), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(30, anormalidade.getFoto2(), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(20, String.valueOf(anormalidade.getLatitude() != Constantes.NULO_DOUBLE ? anormalidade.getLatitude() : " "), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharDireita(20, String.valueOf(anormalidade.getLongitude() != Constantes.NULO_DOUBLE ? anormalidade.getLongitude() : " "), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharEsquerda(26, anormalidade.getData(), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharEsquerda(20, (Util.removerCaractereEspecialNovo(imovel.getEntrevistado().toUpperCase())), ' '));
		registroTipoAnormalidadeImovel.append(Util.adicionarCharEsquerda(11, anormalidade.getLoginUsuario(), ' '));
		registroTipoAnormalidadeImovel.append("\n");

		arquivo.append(registroTipoAnormalidadeImovel.toString());
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
