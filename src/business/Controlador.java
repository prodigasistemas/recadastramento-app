package business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import model.AnormalidadeImovel;
import model.Cliente;
import model.DadosGerais;
import model.Imovel;
import model.Medidor;
import model.Registro;
import model.Servicos;
import model.Usuario;
import util.Constantes;
import util.LogUtil;
import util.Util;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import dataBase.DataManipulator;

public class Controlador {

	public static Controlador instancia;
	
	private boolean logado = false;

	private static Cliente clienteSelecionado = new Cliente();
	private static Imovel imovelSelecionado = new Imovel();
	private static Medidor medidorSelecionado = new Medidor();
	private static Servicos servicosSelecionado = new Servicos();
	private static DadosGerais dadosGerais = new DadosGerais();
	private static Registro anormalidades = new Registro();
	private static AnormalidadeImovel anormalidadeImovelSelecionado = new AnormalidadeImovel();
	private static Registro ramosAtividade = new Registro();
	private static Usuario usuario = new Usuario();

	private static long idSelecionado = 0;
	private static int posicaoListaImoveis = -1;
	private static int menuSelecionado = -1;

	private DataManipulator manipulator;
	
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

	public Usuario getUsuario() {
		return usuario;
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

	public void setUsuario(Usuario usuario) {
		Controlador.usuario = usuario;
	}

	public void setSelecionadoPorPosicao(int posicao) {
		iniciarTabs();
		setPosicaoListaImoveis(posicao);
		idSelecionado = getIdSelecionado(posicao, null);
		manipulator.selectCliente(idSelecionado);
		manipulator.selectImovel(idSelecionado);
		manipulator.selectServicos(idSelecionado);
		manipulator.selectMedidor(idSelecionado);
		manipulator.selectAnormalidadeImovel(idSelecionado);
	}

	public void setSelecionadoPorPosicao(int posicao, String condicao) {
		iniciarTabs();
		idSelecionado = getIdSelecionado(posicao, condicao);
		setPosicaoListaImoveis(getPosicaoPorId(idSelecionado));

		manipulator.selectCliente(idSelecionado);
		manipulator.selectImovel(idSelecionado);
		manipulator.selectServicos(idSelecionado);
		manipulator.selectMedidor(idSelecionado);
		manipulator.selectAnormalidadeImovel(idSelecionado);
	}

	public void setNovoCadastro() {
		iniciarTabs();
		idSelecionado = -1;
	}

	public void iniciarTabs() {
		clienteSelecionado = new Cliente();
		imovelSelecionado = new Imovel();
		medidorSelecionado = new Medidor();
		servicosSelecionado = new Servicos();
		anormalidadeImovelSelecionado = new AnormalidadeImovel();
	}

	public int getIdSelecionado(int posicao, String condicao) {
		if (posicao == -1) {
			return 0;
		} else {
			return Integer.parseInt(Controlador.getInstancia().getCadastroDataManipulator().selectIdImoveis(condicao).get(posicao));
		}
	}

	public int getPosicaoPorId(long id) {
		int position = 0;
		List<String> ids = (List<String>) Controlador.getInstancia().getCadastroDataManipulator().selectIdImoveis(null);

		for (int i = 0; i < ids.size(); i++) {
			if (id == Long.parseLong(ids.get(i))) {
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

		manipulator.selectCliente(idSelecionado);
		manipulator.selectImovel(idSelecionado);
		manipulator.selectServicos(idSelecionado);
		manipulator.selectMedidor(idSelecionado);
		manipulator.selectAnormalidadeImovel(idSelecionado);

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

	public void setLogado(boolean logado) {
		this.logado = logado;
	}

	public boolean isLogado() {
		return logado;
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

	public int getPosicaoListaImoveis() {
		return posicaoListaImoveis;
	}
	
	@SuppressWarnings("static-access")
	public void setPosicaoListaImoveis(int posicao) {
		this.posicaoListaImoveis = posicao;
		manipulator.updateConfiguracao("posicao_cadastro_selecionado", posicao);
	}

	public int getMenuSelecionado() {
		return menuSelecionado;
	}

	public void setMenuSelecionado(int menuSelecionado) {
		Controlador.menuSelecionado = menuSelecionado;
	}

	public boolean databaseExists() {
		File db = new File(Constantes.DATABASE_PATH + Constantes.DATABASE_NAME);
		return db.exists();
	}

	public boolean rotaCarregada() {
		if (manipulator.selectConfiguracaoElement("rota_carregada") == Constantes.SIM) {
			return true;
		} else {
			return false;
		}
	}

	public void apagarBancoDeDados() {
		finalizeDataManipulator();
		setLogado(false);
		
		String path = Constantes.DATABASE_PATH + Constantes.DATABASE_NAME;
		File file = new File(path);
		file.delete();
	}

	@SuppressWarnings("resource")
	public void exportarBanco(Context context) {
		try {
			File sd = Environment.getExternalStorageDirectory();

			if (sd.canWrite()) {
				String caminho = Constantes.DATABASE_PATH + Constantes.DATABASE_NAME;

				File original = new File(caminho);
				File diretorio = new File(sd + Constantes.DIRETORIO_EXPORTACAO_BANCO);

				if (!diretorio.exists()) {
					diretorio.mkdirs();
				}

				File backup = getBackup(diretorio);

				FileChannel origem = new FileInputStream(original).getChannel();
				FileChannel destino = new FileOutputStream(backup).getChannel();

				destino.transferFrom(origem, 0, origem.size());

				origem.close();
				destino.close();

				Toast.makeText(context, "Banco de Dados exportado para " + Constantes.DIRETORIO_EXPORTACAO_BANCO, Toast.LENGTH_LONG).show();
			}
		} catch (IOException e) {
			LogUtil.salvar(Controlador.class, "Erro ao exportar banco de dados", e);
		}
	}

	@SuppressWarnings("resource")
	public void importarBanco(Context context) {
		try {
			File sd = Environment.getExternalStorageDirectory();

			if (sd.canWrite()) {
				File diretorio = new File(sd + Constantes.DIRETORIO_EXPORTACAO_BANCO);
				File original = new File(diretorio, "cadastro.db");

				FileChannel origem = new FileInputStream(original).getChannel();
				FileChannel destino = new FileOutputStream(new File(Constantes.DATABASE_PATH + Constantes.DATABASE_NAME)).getChannel();

				destino.transferFrom(origem, 0, origem.size());

				origem.close();
				destino.close();

				Toast.makeText(context, "Banco de Dados importado com sucesso.", Toast.LENGTH_LONG).show();
			}
		} catch (IOException e) {
			LogUtil.salvar(Controlador.class, "Erro ao importar banco de dados", e);
		}
	}

	private File getBackup(File diretorio) {
		return new File(diretorio, "cadastro_" + Util.getRotaFileName() + ".db");
	}
}
