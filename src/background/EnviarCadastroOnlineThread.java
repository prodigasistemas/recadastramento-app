package background;

import java.io.IOException;

import model.Imovel;
import ui.ArquivoRetorno;
import ui.MessageDispatcher;
import util.Constantes;
import android.util.Log;
import business.Controlador;
import business.ControladorAcessoOnline;

public class EnviarCadastroOnlineThread extends Thread {

	private Imovel imovel;

	public EnviarCadastroOnlineThread(Imovel imovel) {
		this.imovel = imovel;
	}

	@Override
	public void run() {
		try {
			StringBuffer arquivo = ArquivoRetorno.gerarDadosImovel(imovel);

			ControladorAcessoOnline.getInstancia().atualizarCadastro(arquivo.toString().getBytes());

			verificarStatus();
			
			Controlador.getInstancia().getCadastroDataManipulator().salvarStatusImovel(imovel);

		} catch (IOException e) {
			Log.e("[EnviarCadastroOnlineThread]", "Erro ao enviar cadastro online para o imóvel " + imovel.getMatricula());
		}
	}

	private void verificarStatus() {
		if (ControladorAcessoOnline.getInstancia().isRequestOK()) {
			imovel.setImovelEnviado(String.valueOf(Constantes.SIM));
		} else {
			imovel.setImovelEnviado(String.valueOf(Constantes.NAO));

			if (MessageDispatcher.isRespostaInconsistencia()) {
				imovel.setImovelStatus(String.valueOf(Constantes.IMOVEL_SALVO_COM_INCONSISTENCIA));
				inserirInconsistencias();
			}
		}
	}
	
	private void inserirInconsistencias() {
		String[] inconsistencias = MessageDispatcher.getInconsistencias().replace("[", "").replace("]", "").split(",");
		
		for (String inconsistencia : inconsistencias) {
			Controlador.getInstancia().getCadastroDataManipulator().inserirInconsistenciaImovel(imovel.getMatricula(), inconsistencia);
		}
	}
}