package background;

import ui.FileManager;
import android.content.Context;
import android.os.Handler;
import business.Controlador;

public class CarregarRotaThread extends Thread {

	public final static int DONE = 0;
	public final static int RUNNING = 1;
	private static Context context;

	Handler handler;
	int state;
	int total;
	private String fileName;

	@SuppressWarnings("static-access")
	public CarregarRotaThread(Handler h, String fileName, Context context) {
		this.handler = h;
		this.context = context;
		this.total = 0;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		state = RUNNING;
		FileManager.getInstancia();

		if (fileName.endsWith(".txt")) {
			Controlador.getInstancia().carregarDadosParaRecordStore(FileManager.readFile(fileName), handler, context);

		} else {
			Controlador.getInstancia().carregarDadosParaRecordStore(FileManager.readCompressedFile(fileName), handler, context);

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