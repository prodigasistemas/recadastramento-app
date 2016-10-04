package business;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import model.AnormalidadeImovel;
import model.Cliente;
import model.DadosGerais;
import model.Imovel;
import model.Medidor;
import model.Registro;
import model.Servicos;
import util.Constantes;
import util.Util;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import dataBase.DataManipulator;

public class Controlador {

	public static Controlador instancia;
	private boolean permissionGranted = false;
	private int qtdRegistros = 0;
	private static int linhasLidas = 0;

	private static Cliente clienteSelecionado = new Cliente();
	private static Imovel imovelSelecionado = new Imovel();
	private static Medidor medidorSelecionado = new Medidor();
	private static Servicos servicosSelecionado = new Servicos();
	private static DadosGerais dadosGerais = new DadosGerais();
	private static Registro anormalidades = new Registro();
	private static AnormalidadeImovel anormalidadeImovelSelecionado = new AnormalidadeImovel();
	private static Registro ramosAtividade = new Registro();

	private static long idCadastroSelecionado = 0;
	private static int cadastroListPosition = -1;

	private static int isRotaCarregadaOk = Constantes.NAO;

	DataManipulator manipulator;

	public static Controlador getInstancia() {
		if (Controlador.instancia == null) {
			Controlador.instancia = new Controlador();
		}
		
		return Controlador.instancia;
	}

	public Cliente getClienteSelecionado() {
		return Controlador.clienteSelecionado;
	}

	public Imovel getImovelSelecionado() {
		return Controlador.imovelSelecionado;
	}

	public Medidor getMedidorSelecionado() {
		return Controlador.medidorSelecionado;
	}

	public Servicos getServicosSelecionado() {
		return Controlador.servicosSelecionado;
	}

	public DadosGerais getDadosGerais() {
		return Controlador.dadosGerais;
	}

	public Registro getAnormalidades() {
		return Controlador.anormalidades;
	}

	public AnormalidadeImovel getAnormalidadeImovelSelecionado() {
		return Controlador.anormalidadeImovelSelecionado;
	}

	public Registro getRamosAtividade() {
		return Controlador.ramosAtividade;
	}

	public void setClienteSelecionado(Cliente clienteSelecionado) {
		Controlador.clienteSelecionado = clienteSelecionado;
	}

	public void setImovelSelecionado(Imovel imovelSelecionado) {
		Controlador.imovelSelecionado = imovelSelecionado;
	}

	public void setMedidorSelecionado(Medidor medidorSelecionado) {
		Controlador.medidorSelecionado = medidorSelecionado;
	}

	public void setServicosSelecionado(Servicos servicosSelecionado) {
		Controlador.servicosSelecionado = servicosSelecionado;
	}

	public void setAnormalidadeImovelSelecionado(AnormalidadeImovel anormalidadeImovelSelecionado) {
		Controlador.anormalidadeImovelSelecionado = anormalidadeImovelSelecionado;
	}

	public void setDadosGerais(DadosGerais dadosGerais) {
		Controlador.dadosGerais = dadosGerais;
	}

	public void setAnormalidades(Registro anormalidades) {
		Controlador.anormalidades = anormalidades;
	}

	public void setRamosAtividade(Registro ramosAtividade) {
		Controlador.ramosAtividade = ramosAtividade;
	}

	public void setCadastroSelecionadoByListPosition(int listPosition) {
		initCadastroTabs();
		setCadastroListPosition(listPosition);
		idCadastroSelecionado = getIdCadastroSelecionado(listPosition, null);
		manipulator.selectCliente(idCadastroSelecionado);
		manipulator.selectImovel(idCadastroSelecionado);
		manipulator.selectServico(idCadastroSelecionado);
		manipulator.selectMedidor(idCadastroSelecionado);
		manipulator.selectAnormalidadeImovel(idCadastroSelecionado);
	}

	public void setCadastroSelecionadoByListPositionInConsulta(int listPositionInConsulta, String condition) {
		initCadastroTabs();
		idCadastroSelecionado = getIdCadastroSelecionado(listPositionInConsulta, condition);
		setCadastroListPosition(getCadastroListPositionById(idCadastroSelecionado));

		manipulator.selectCliente(idCadastroSelecionado);
		manipulator.selectImovel(idCadastroSelecionado);
		manipulator.selectServico(idCadastroSelecionado);
		manipulator.selectMedidor(idCadastroSelecionado);
		manipulator.selectAnormalidadeImovel(idCadastroSelecionado);
	}

	public void setCadastroSelecionado(long id) {
		initCadastroTabs();
		idCadastroSelecionado = id;
		manipulator.selectCliente(idCadastroSelecionado);
		manipulator.selectImovel(idCadastroSelecionado);
		manipulator.selectServico(idCadastroSelecionado);
		manipulator.selectMedidor(idCadastroSelecionado);
		manipulator.selectAnormalidadeImovel(idCadastroSelecionado);
	}

	public void setCadastroSelecionadoNovoImovel() {
		initCadastroTabs();
		idCadastroSelecionado = -1;
	}

	public void initCadastroTabs() {
		clienteSelecionado = new Cliente();
		imovelSelecionado = new Imovel();
		medidorSelecionado = new Medidor();
		servicosSelecionado = new Servicos();
		anormalidadeImovelSelecionado = new AnormalidadeImovel();
	}

	public int getIdCadastroSelecionado(int listPosition, String condition) {
		if (listPosition == -1) {
			return 0;

		} else {
			return Integer.parseInt(Controlador.getInstancia().getCadastroDataManipulator().selectIdImoveis(condition).get(listPosition));
		}
	}

	public int getCadastroListPositionById(long id) {
		int position = 0;
		ArrayList<String> listIds = (ArrayList<String>) Controlador.getInstancia().getCadastroDataManipulator().selectIdImoveis(null);

		for (int i = 0; i < listIds.size(); i++) {
			if (id == Long.parseLong(listIds.get(i))) {
				position = i;
				break;
			}
		}
		
		return position;
	}

	public boolean isCadastroAlterado() {
		boolean result = false;

		Cliente clienteEditado = clienteSelecionado;
		Imovel imovelEditado = imovelSelecionado;
		Servicos servicoEditado = servicosSelecionado;
		Medidor medidorEditado = medidorSelecionado;
		AnormalidadeImovel anormalidadeImovelEditado = anormalidadeImovelSelecionado;

		manipulator.selectCliente(idCadastroSelecionado);
		manipulator.selectImovel(idCadastroSelecionado);
		manipulator.selectServico(idCadastroSelecionado);
		manipulator.selectMedidor(idCadastroSelecionado);
		manipulator.selectAnormalidadeImovel(idCadastroSelecionado);

		if (clienteEditado != clienteSelecionado) {
			result = true;
		} else if (imovelEditado != imovelSelecionado) {
			result = true;
		} else if (servicoEditado != servicosSelecionado) {
			result = true;
		} else if (medidorEditado != medidorSelecionado) {
			result = true;
		} else if (anormalidadeImovelEditado != anormalidadeImovelSelecionado) {
			result = true;
		}

		if (result) {
			clienteSelecionado = clienteEditado;
			imovelSelecionado = imovelEditado;
			servicosSelecionado = servicoEditado;
			medidorSelecionado = medidorEditado;
			anormalidadeImovelSelecionado = anormalidadeImovelEditado;
		}
		
		return result;
	}

	public void carregarDadosParaRecordStore(BufferedReader input, Handler handler, Context context) {
		String linha = "";
		linhasLidas = 0;

		if (input != null) {
			try {
				Bundle bundle = new Bundle();

				while ((linha = input.readLine()) != null) {

					if (linhasLidas == 0) {
						qtdRegistros = Integer.parseInt(linha);
						linhasLidas++;
						continue;
					}

					linhasLidas++;

					linha = Util.removerCaractereEspecial(linha);

					int tipoRegistro = Integer.parseInt(linha.substring(0, 2));

					if (tipoRegistro == Constantes.REGISTRO_TIPO_CLIENTE) {
						manipulator.insertCliente(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_IMOVEL) {
						manipulator.insertImovel(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_RAMOS_ATIVIDADE_IMOVEL) {
						manipulator.insertRamosAtividadeImovel(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_SERVICO) {
						manipulator.insertServico(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_HIDROMETRO) {
						manipulator.insertMedidor(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_ANORMALIDADE_IMOVEL) {
						manipulator.insertAnormalidadeImovel(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_GERAL) {
						manipulator.insertDadosGerais(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_ANORMALIDADE) {
						manipulator.insertAnormalidade(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_RAMO_ATIVIDADE) {
						manipulator.insertRamoAtividade(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_SITUACAO_AGUA) {
						manipulator.insertSituacaoLigacaoAgua(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_SITUACAO_ESGOTO) {
						manipulator.insertSituacaoLigacaoEsgoto(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_PROTECAO_HIDROMETRO) {
						manipulator.insertProtecaoHidrometro(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_FONTE_ABASTECIMENTO) {
						manipulator.insertFonteAbastecimento(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_MARCA_HIDROMETRO) {
						manipulator.insertMarcaHidrometro(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_LOCAl_INSTALACAO_RAMAL) {
						manipulator.insertLocalInstalacaoRamal(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_CAPACIDADE_HIDROMETRO) {
						manipulator.insertCapacidadeHidrometro(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_LOGRADOURO) {
						manipulator.insertLogradouro(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_CLASSE_SOCIAL) {
						manipulator.insertClasseSocial(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_USO) {
						manipulator.insertTipoUso(linha);
						
					} else if (tipoRegistro == Constantes.REGISTRO_TIPO_ACESSO_HIDROMETRO) {
						manipulator.insertAcessoHidrometro(linha);
					}

					if (linhasLidas < qtdRegistros) {
						Message msg = handler.obtainMessage();
						bundle.putInt("total", linhasLidas);
						msg.setData(bundle);
						handler.sendMessage(msg);
					}
				}

				setRotaCarregamentoOk(Constantes.SIM);

				Message msg = handler.obtainMessage();
				bundle.putInt("total", linhasLidas);
				msg.setData(bundle);
				handler.sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getQtdRegistros() {
		return qtdRegistros;
	}

	public void setPermissionGranted(boolean state) {
		this.permissionGranted = state;
	}

	public boolean isPermissionGranted() {
		return this.permissionGranted;
	}

	public void initiateDataManipulator(Context context) {
		if (manipulator == null) {
			manipulator = new DataManipulator(context);
			manipulator.open();
		}
	}

	public void finalizeDataManipulator() {
		if (manipulator != null) {
			manipulator.close();
			manipulator = null;
		}
	}

	public DataManipulator getCadastroDataManipulator() {
		return manipulator;
	}

	public long getIdCadastroSelecionado() {
		return idCadastroSelecionado;
	}

	public int getCadastroListPosition() {
		return cadastroListPosition;
	}

	@SuppressWarnings("static-access")
	public void setCadastroListPosition(int position) {
		this.cadastroListPosition = position;
		manipulator.updateConfiguracao("posicao_cadastro_selecionado", position);
	}

	public boolean databaseExists(Context context) {
		File dbFile = new File(Constantes.DATABASE_PATH + Constantes.DATABASE_NAME);

		initiateDataManipulator(context);

		return (dbFile.exists() && manipulator.selectAnormalidades().size() > 0);
	}

	@SuppressWarnings("static-access")
	public int isDatabaseRotaCarregadaOk() {

		if (manipulator.selectConfiguracaoElement("rota_carregada") == Constantes.SIM) {
			this.isRotaCarregadaOk = Constantes.SIM;
		}

		return this.isRotaCarregadaOk;
	}

	public void setRotaCarregamentoOk(int isRotaCarregadaOk) {
		manipulator.updateConfiguracao("rota_carregada", Constantes.SIM);
	}

	public void deleteDatabase() {
		String strDBFilePath = Constantes.DATABASE_PATH + Constantes.DATABASE_NAME;
		File file = new File(strDBFilePath);
		file.delete();
	}
}
