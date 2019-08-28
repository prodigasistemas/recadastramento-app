package background;

import java.io.IOException;

import ui.ArquivoRetorno;
import util.Util;
import android.content.Context;
import android.os.Handler;

public class GerarArquivoRetornoThread extends Thread {

	Handler handler;
	Context context;

	public GerarArquivoRetornoThread(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void run() {
		ArquivoRetorno.gerar(handler, context);

		try {
			Util.gerarZip();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}