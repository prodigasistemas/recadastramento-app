package background;

import java.io.IOException;

import ui.ArquivoRetorno;
import ui.FileManager;
import util.Util;
import android.content.Context;
import android.os.Handler;

public class GerarArquivoParcialThread extends Thread{
	
	public final static int DONE = 2;
	public final static int RUNNING = 1;
	private static Context context;

	Handler handler;
	int state;
	int total;
	int increment;

	public GerarArquivoParcialThread(Handler handler, Context context, int increment) {
		this.handler = handler;
		GerarArquivoParcialThread.context = context;
		this.total = 0;
		this.increment = increment;
	}

	@Override
	public void run() {
		state = RUNNING;
		FileManager.getInstancia();
		
		ArquivoRetorno.gerarArquivoParcial(handler, context, increment);

		try {
			Util.zipArquivoCompleto();
		} catch (IOException e) {
			e.printStackTrace();
		}
		state = DONE;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getCustomizedState() {
		return state;
	}

}
