package background;

import java.io.IOException;

import business.ControladorAcessoOnline;
import ui.ArquivoRetorno;
import ui.FileManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class EnviarCadastroOnlineThread extends Thread {

	public final static int DONE_OK = 2;
	public final static int DONE_ERROR = 3;
	public final static int RUNNING = 1;

	Handler handler;
	int state;
	int total;
	int increment;

	public EnviarCadastroOnlineThread(Handler h, Context context, int increment) {
		this.handler = h;
		this.total = 0;
		this.increment = increment;
	}

	@Override
	public void run() {
		state = RUNNING;
		FileManager.getInstancia();

		StringBuffer mensagem = ArquivoRetorno.gerarDadosImovelSelecionado();

		try {

			ControladorAcessoOnline.getInstancia().enviarCadastro(mensagem.toString().getBytes());

			if (ControladorAcessoOnline.getInstancia().isRequestOK()) {
				state = DONE_OK;

			} else {
				state = DONE_ERROR;
			}

		} catch (IOException e) {
			state = DONE_ERROR;
		}
		total = 100;

		Bundle bundle = new Bundle();
		Message msg = handler.obtainMessage();
		bundle.putInt("envioCadastroOnline" + String.valueOf(increment), 100);
		msg.setData(bundle);
		handler.sendMessage(msg);

	}

	public void setState(int state) {
		this.state = state;
	}

	public int getCustomizedState() {
		return state;
	}
}