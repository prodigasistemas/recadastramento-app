package com.AndroidExplorer;

import java.io.File;
import java.util.ArrayList;

import util.Constantes;
import util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import background.CarregarRotaTask;
import business.Controlador;

public class ListaRotas extends ListActivity {

	private MySimpleArrayAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.getListView().setCacheColorHint(Color.TRANSPARENT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		int[] colors = { 0x12121212, 0xFFFFFFFF, 0x12121212 };
		this.getListView().setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
		this.getListView().setDividerHeight(1);

		instanciate();
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		instanciate();
	}

	public void instanciate() {

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {

			// We can read and write the media
			File path = Util.getExternalStorageDirectory();
			path.getAbsolutePath();
			Log.i("ExternalStorage", "ExternalStorage :" + path.getAbsolutePath());
			String root = path.getAbsolutePath() + Constantes.DIRETORIO_ROTAS;
			getDir(root);

			// Display a messagebox.
			Toast.makeText(getBaseContext(), "Por favor, escolha a rota a ser carregada.", Toast.LENGTH_LONG).show();
		}
	}

	private void getDir(String root) {

		ArrayList<String> item = new ArrayList<String>();
		ArrayList<String> path = new ArrayList<String>();
		File f = new File(root);
		File[] files = f.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				path.add(file.getPath());

				if (!file.isDirectory()) {
					if (((file.getName().endsWith(".txt")) || (file.getName().endsWith(".gz"))) && !file.getName().startsWith("._")) {
						item.add(file.getName());
					}
				}
			}
		}
		adapter = new MySimpleArrayAdapter(this, item);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View view, int position, long id) {
		adapter.setSelectedPosition(position);
		String nomeArquivo = adapter.getListElementName(position);
		Controlador.getInstancia().initiateDataManipulator(getBaseContext());
		new CarregarRotaTask(this, nomeArquivo).execute();
	}

	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
		private final Activity context;
		private final ArrayList<String> names;

		// used to keep selected position in ListView
		private int selectedPos = -1;

		public MySimpleArrayAdapter(Activity context, ArrayList<String> names) {
			super(context, R.layout.rowroteiro, names);
			this.context = context;
			this.names = names;
		}

		public void setSelectedPosition(int pos) {
			selectedPos = pos;
			notifyDataSetChanged();
		}

		public int getSelectedPosition() {
			return selectedPos;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.rowroteiro, null, true);
			TextView textView = (TextView) rowView.findViewById(R.id.nomerota);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
			textView.setText(names.get(position));

			if (names.get(position).endsWith(".txt")) {
				imageView.setImageResource(R.drawable.text);
			} else {
				imageView.setImageResource(R.drawable.compressed);
			}

			// change the row color based on selected state
			if (selectedPos == position) {
				rowView.setBackgroundColor(Color.argb(70, 255, 255, 255));
			} else {
				rowView.setBackgroundColor(Color.TRANSPARENT);
			}

			return rowView;
		}

		public String getListElementName(int element) {
			return names.get(element);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;

		} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			return true;

		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			return true;

		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_importar_banco, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.importarBanco:
			Controlador.getInstancia().importarBanco(ListaRotas.this);
			startActivity(new Intent(ListaRotas.this, MenuPrincipal.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}