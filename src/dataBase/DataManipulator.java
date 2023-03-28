package dataBase;

import java.util.ArrayList;
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
import util.ParserUtil;
import util.Util;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import business.Controlador;

public class DataManipulator
{
    private static Context context;
    private DbHelper openHelper;
    static SQLiteDatabase db;

    public DataManipulator(Context context) {
        DataManipulator.context = context;
        openHelper = new DbHelper(DataManipulator.context);
    }

    public DataManipulator open() throws SQLException {
        db = openHelper.getWritableDatabase();
        return this;
    }

    public void close() {
    	openHelper.close();
    }

    public int getNumeroImoveis() {
        return (int)DatabaseUtils.queryNumEntries(db,Constantes.TABLE_IMOVEL);
    }

    public Cliente getClienteSelecionado(){
    	return Controlador.getInstancia().getClienteSelecionado();
    }
    
    public Imovel getImovelSelecionado(){
    	return Controlador.getInstancia().getImovelSelecionado();
    }
    
    public Medidor getMedidorSelecionado(){
    	return Controlador.getInstancia().getMedidorSelecionado();
    }
    
    public Servicos getServicosSelecionado(){
    	return Controlador.getInstancia().getServicosSelecionado();
    }
    
    public AnormalidadeImovel getAnormalidadeImovelSelecionado(){
    	return Controlador.getInstancia().getAnormalidadeImovelSelecionado();
    }
    
    public DadosGerais getDadosGerais(){
    	return Controlador.getInstancia().getDadosGerais();
    }
    
    public Registro getAnormalidades(){
    	return Controlador.getInstancia().getAnormalidades();
    }
    
    public Registro getRamosAtividade(){
    	return Controlador.getInstancia().getRamosAtividade();
    }
    
    public Usuario getUsuario(){
    	return Controlador.getInstancia().getUsuario();
    }
    
    public void deleteTable(String tableName) {
        db.delete(tableName, null, null);
    }
    
    public void deleteElementId(int rowId, String tableName) {
        db.delete(tableName, null, null);
   }
    
    public int getPosicaoImovelLista(Imovel imovel) {
    	Cursor cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "matricula" }, null, null, null, null,  "inscricao asc");
    	int i = 0;
    	 if (cursor.moveToFirst()) {
    		 do {
    			 i++;
    			 if (imovel.getMatricula() == Integer.parseInt(cursor.getString(0))) {
    				 return i;
    			 }
    		 } while (cursor.moveToNext());
    	 }
    	 
    	 return i;
    }
    
    
	public List<String> selectEnderecoImoveis(String condition) {

		List<String> enderecos = new ArrayList<String>();

		Cursor cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { 
				"matricula", 
				"logradouro_imovel", 
				"numero_imovel", 
				"complemento_imovel",
				"bairro_imovel", 
				"cep_imovel", 
				"municipio_imovel" }, 
				condition, null, null, null, "inscricao asc");

		int contador = 0;
		if (cursor.moveToFirst()) {
			do {
				int matricula = Integer.parseInt(cursor.getString(0));
				
				String endereco = "(" + (++contador) + ") " +
								  (matricula > 0 ? matricula : "NOVO") + " - " + 
								  cursor.getString(1).trim() + ", n°" + 
								  cursor.getString(2).trim() + " " + 
								  cursor.getString(3).trim() + " " + 
								  cursor.getString(4).trim() + " " + 
								  cursor.getString(5).trim() + " " + 
								  cursor.getString(6).trim();
				
				enderecos.add(endereco);
			} while (cursor.moveToNext());
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return enderecos;
	}

	public List<String> selectSubLotesImovel(String localidadeSetorQuadraLote) {
	
		ArrayList<String> list = new ArrayList<String>();
	
		if (localidadeSetorQuadraLote != Constantes.NULO_STRING || localidadeSetorQuadraLote != null) {
			Cursor cursor;
			
			cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "inscricao" }, " inscricao LIKE '"+localidadeSetorQuadraLote+"%'",
					null, null, null, "inscricao asc");
			
			if (cursor.moveToFirst()) {
				do {
					list.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
	        if (cursor != null && !cursor.isClosed()) {
	            cursor.close();
	         }
		}

		return list;
	}

	public List<Imovel> selectEnderecoImovel() {

		List<Imovel> imoveis = null;

		Cursor cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { 
				"inscricao", 
				"rota", 
				"face", 
				"codigo_municipio", 
				"municipio_imovel", 
				"tipo_logradouro_imovel",
				"codigo_logradouro_imovel", 
				"logradouro_imovel", 
				"bairro_imovel",
				"cep_imovel", 
				"numero_imovel", 
				"complemento_imovel" }, 
				null, null, null, null, "inscricao asc");

		if (cursor.moveToFirst()) {
			imoveis = new ArrayList<Imovel>();
			
			do {
				Imovel imovel = new Imovel();
				imovel.setInscricao(cursor.getString(0));
				imovel.setRota(cursor.getString(1));
				imovel.setFace(cursor.getString(2));
				imovel.setCodigoMunicipio(cursor.getString(3));
				imovel.getEnderecoImovel().setMunicipio(cursor.getString(4));
				imovel.getEnderecoImovel().setTipoLogradouro(cursor.getString(5));
				imovel.setCodigoLogradouro(cursor.getString(6));
				imovel.getEnderecoImovel().setLogradouro(cursor.getString(7));
				imovel.getEnderecoImovel().setBairro(cursor.getString(8));
				imovel.getEnderecoImovel().setCep(cursor.getString(9));
				imovel.getEnderecoImovel().setNumero(cursor.getString(10));
				imovel.getEnderecoImovel().setComplemento(cursor.getString(11));

				imoveis.add(imovel);
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return imoveis;
	}
    
	public List<String> selectIdImoveis(String condition) {

		List<String> ids = new ArrayList<String>();
		
		Cursor cursor;
		if (condition == Constantes.NULO_STRING || condition == null) {
			cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "id" }, null, null, null, null, "inscricao asc");
		} else {
			cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "id" }, condition, null, null, null, "inscricao asc");
		}

		if (cursor.moveToFirst()) {
			do {
				ids.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return ids;
	}
    
    public List<String> selectIdClientes(String condition){
    	
    	ArrayList<String> list = new ArrayList<String>();
    	Cursor cursor;
    	String table = Constantes.TABLE_CLIENTE;
    	
    	if(condition.substring(1,7).equals("imovel")){
    		table += " INNER JOIN "+ Constantes.TABLE_IMOVEL +" ON cliente.id=imovel.codigo_cliente ";
		}
    	
    	if (condition == Constantes.NULO_STRING  || condition == null){

    		cursor = db.query(table, new String[] { "cliente.id"}, null, null, null, null, "cliente.id asc");

    	}else{
    		
    		cursor = db.query(table, new String[] { "cliente.id"}, condition, null, null, null, "cliente.id asc");
    	}
    	
        if (cursor.moveToFirst()) {
           do {
                String b1= cursor.getString(0);
                list.add(b1);
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}
    
	public List<Imovel> selectStatusImoveis(String condicao) {

		List<Imovel> imoveis = new ArrayList<Imovel>();

		Cursor cursor;
		if (condicao == null) {
			cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "imovel_status, imovel_transmitido, tipo_operacao" }, null, null, null, null, "inscricao asc");
		} else {
			cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "imovel_status", "imovel_transmitido, tipo_operacao" }, condicao, null, null, null, "inscricao asc");
		}

		if (cursor.moveToFirst()) {
			do {
				Imovel imovel = new Imovel();
				imovel.setImovelStatus(cursor.getString(0));
				imovel.setImovelTransmitido(cursor.getString(1));
				imovel.setTipoOperacao(cursor.getString(2));
				imoveis.add(imovel);
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return imoveis;
	}

	public List<Integer> obterDadosRelatorio() {
		List<Integer> lista = new ArrayList<Integer>();

		int total = 0;
		int informativos = 0;
		int pendentes = 0;

		int totalFinalizados = 0;
		int finalizados = 0;
		int finalizadosAnormalidade = 0;
		int novos = 0;
		int excluidos = 0;

		int naoTransmitidos = 0;
		int transmitidos = 0;
		int transmitidosInconsistencia = 0;

		Cursor cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "imovel_status", "imovel_transmitido" }, null, null, null, null, "inscricao asc");

		if (cursor.moveToFirst()) {

			total = cursor.getCount();

			do {
				int status = Integer.parseInt(cursor.getString(0));
				int enviado = Integer.parseInt(cursor.getString(1));

				if (status == Constantes.IMOVEL_A_SALVAR) {
					pendentes++;
				}

				if (status == Constantes.IMOVEL_SALVO) {
					totalFinalizados++;
					finalizados++;
				}

				if (status == Constantes.IMOVEL_SALVO_COM_ANORMALIDADE) {
					totalFinalizados++;
					finalizadosAnormalidade++;
				}

				if (status == Constantes.IMOVEL_NOVO || status == Constantes.IMOVEL_NOVO_COM_ANORMALIDADE) {
					totalFinalizados++;
					novos++;
					total--;
				}

				if (status == Constantes.IMOVEL_EXCLUIDO) {
					totalFinalizados++;
					excluidos++;
				}
				
				if (status == Constantes.IMOVEL_INFORMATIVO) {
					informativos++;
				} else {
					if (enviado == Constantes.SIM) {
						transmitidos++;
					} else {
						if (status == Constantes.IMOVEL_SALVO_COM_INCONSISTENCIA) {
							transmitidosInconsistencia++;
						} else {
							if (status != Constantes.IMOVEL_A_SALVAR) {
								naoTransmitidos++;
							}
						}
					}
				}

			} while (cursor.moveToNext());

			lista.add(total);
			lista.add(informativos);
			lista.add(pendentes);

			lista.add(totalFinalizados);
			lista.add(finalizados);
			lista.add(finalizadosAnormalidade);
			lista.add(novos);
			lista.add(excluidos);

			lista.add(naoTransmitidos);
			lista.add(transmitidos);
			lista.add(transmitidosInconsistencia);
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return lista;
	}

	public void selectCliente(long id) {
		Cursor cursor = getCursorCliente("id = " + id);

		if (cursor.moveToFirst()) {
			montarCliente(cursor, getClienteSelecionado());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}
    
	public Cliente selectCliente(String condicao) {
		Cursor cursor = getCursorCliente(condicao);

		Cliente cliente = null;
		if (cursor.moveToFirst()) {
			cliente = new Cliente();
			montarCliente(cursor, cliente);
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return cliente;
	}

	private Cursor getCursorCliente(String condicao) {
		Cursor cursor = db.query(Constantes.TABLE_CLIENTE, new String[] { 
		        "matricula", 
		        "gerencia", 
		        "tipo_endereco_proprietario", 
		        "tipo_endereco_responsavel",
		        "usuario_proprietario", 
		        "tipo_responsavel",
		        "matricula_usuario",
		        "nome_usuario",
		        "tipo_pessoa_usuario",
		        "cpf_cnpj_usuario",
		        "rg_usuario",
		        "uf_usuario",
		        "tipo_sexo_usuario",
		        "telefone_usuario",
		        "celular_usuario",
		        "email_usuario",
				"numero_nis_usuario",
		        "matricula_proprietario",
		        "nome_proprietario",
		        "tipo_pessoa_proprietario",
		        "cpf_cnpj_proprietario",
		        "rg_proprietario",
		        "uf_proprietario",
		        "tipo_sexo_proprietario",
		        "telefone_proprietario",
		        "celular_proprietario",
		        "email_proprietario",
		        "tipo_logradouro_proprietario",
		        "logradouro_proprietario",
		        "numero_proprietario",
		        "complemento_proprietario",
		        "bairro_proprietario",
		        "cep_proprietario",
		        "municipio_proprietario",
				"numero_nis_proprietario",
		        "matricula_responsavel",
		        "nome_responsavel",
		        "tipo_pessoa_responsavel",
		        "cpf_cnpj_responsavel",
		        "rg_responsavel",
		        "uf_responsavel",
		        "tipo_sexo_responsavel",
		        "telefone_responsavel",
		        "celular_responsavel",
		        "email_responsavel",
		        "tipo_logradouro_responsavel",
		        "logradouro_responsavel",
		        "numero_responsavel",
		        "complemento_responsavel",
		        "bairro_responsavel",
		        "cep_responsavel",
		        "municipio_responsavel",
				"numero_nis_responsavel",
		        "latitude",
		        "longitude",
		        "data",
		        "matricula_usuario",
		        "matricula_responsavel",
		        "matricula_proprietario" },
		        
		        condicao,
		        null, null, null,
		        "id asc");
		
		return cursor;
	}
    
	private void montarCliente(Cursor cursor, Cliente cliente) {
		cliente.setMatricula(cursor.getString(0));
		cliente.setNomeGerenciaRegional(cursor.getString(1));
		cliente.setTipoEnderecoProprietario(cursor.getString(2));
		cliente.setTipoEnderecoResponsavel(cursor.getString(3));
		cliente.setUsuarioEProprietario(cursor.getString(4));
		cliente.setTipoResponsavel(cursor.getString(5));

		cliente.getUsuario().setMatricula(cursor.getString(6));
		cliente.getUsuario().setNome(cursor.getString(7));
		cliente.getUsuario().setTipoPessoa(cursor.getString(8));
		cliente.getUsuario().setCpfCnpj(cursor.getString(9));
		cliente.getUsuario().setRg(cursor.getString(10));
		cliente.getUsuario().setUf(cursor.getString(11));
		cliente.getUsuario().setTipoSexo(cursor.getString(12));
		cliente.getUsuario().setTelefone(cursor.getString(13));
		cliente.getUsuario().setCelular(cursor.getString(14));
		cliente.getUsuario().setEmail(cursor.getString(15));
		cliente.getUsuario().setNumeroNIS(cursor.getString(16));

		cliente.getProprietario().setMatricula(cursor.getString(17));
		cliente.getProprietario().setNome(cursor.getString(18));
		cliente.getProprietario().setTipoPessoa(cursor.getString(19));
		cliente.getProprietario().setCpfCnpj(cursor.getString(20));
		cliente.getProprietario().setRg(cursor.getString(21));
		cliente.getProprietario().setUf(cursor.getString(22));
		cliente.getProprietario().setTipoSexo(cursor.getString(23));
		cliente.getProprietario().setTelefone(cursor.getString(24));
		cliente.getProprietario().setCelular(cursor.getString(25));
		cliente.getProprietario().setEmail(cursor.getString(26));
		cliente.getEnderecoProprietario().setTipoLogradouro(cursor.getString(27));
		cliente.getEnderecoProprietario().setLogradouro(cursor.getString(28));
		cliente.getEnderecoProprietario().setNumero(cursor.getString(29));
		cliente.getEnderecoProprietario().setComplemento(cursor.getString(30));
		cliente.getEnderecoProprietario().setBairro(cursor.getString(31));
		cliente.getEnderecoProprietario().setCep(cursor.getString(32));
		cliente.getEnderecoProprietario().setMunicipio(cursor.getString(33));
		cliente.getProprietario().setNumeroNIS(cursor.getString(34));

		cliente.getResponsavel().setMatricula(cursor.getString(35));
		cliente.getResponsavel().setNome(cursor.getString(36));
		cliente.getResponsavel().setTipoPessoa(cursor.getString(37));
		cliente.getResponsavel().setCpfCnpj(cursor.getString(38));
		cliente.getResponsavel().setRg(cursor.getString(39));
		cliente.getResponsavel().setUf(cursor.getString(40));
		cliente.getResponsavel().setTipoSexo(cursor.getString(41));
		cliente.getResponsavel().setTelefone(cursor.getString(42));
		cliente.getResponsavel().setCelular(cursor.getString(43));
		cliente.getResponsavel().setEmail(cursor.getString(44));

		cliente.getEnderecoResponsavel().setTipoLogradouro(cursor.getString(45));
		cliente.getEnderecoResponsavel().setLogradouro(cursor.getString(46));
		cliente.getEnderecoResponsavel().setNumero(cursor.getString(47));
		cliente.getEnderecoResponsavel().setComplemento(cursor.getString(48));
		cliente.getEnderecoResponsavel().setBairro(cursor.getString(49));
		cliente.getEnderecoResponsavel().setCep(cursor.getString(50));
		cliente.getEnderecoResponsavel().setMunicipio(cursor.getString(51));
		cliente.getProprietario().setNumeroNIS(cursor.getString(52));

		cliente.setLatitude(cursor.getString(53));
		cliente.setLongitude(cursor.getString(54));
		cliente.setData(cursor.getString(55));
	}
	
	public void selectImovel(long id) {
		Cursor cursor = getCursorImovel("id = " + id);

		if (cursor.moveToFirst()) {
			montarImovel(cursor, id, getImovelSelecionado());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}
   
	public void selectServicos(long id) {
		Cursor cursor = getCursorServicos("id = " + id);

		if (cursor.moveToFirst()) {
			montarServicos(cursor, getServicosSelecionado());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

	public Servicos selectServicos(String condicao) {
		Cursor cursor = getCursorServicos(condicao);

		Servicos servicos = null;

		if (cursor.moveToFirst()) {
			servicos = new Servicos();
			servicos.setTipoLigacaoAgua(cursor.getString(0));
			servicos.setTipoLigacaoEsgoto(cursor.getString(1));
			servicos.setLocalInstalacaoRamal(cursor.getString(2));
			servicos.setLatitude(cursor.getString(3));
			servicos.setLongitude(cursor.getString(4));
			servicos.setData(cursor.getString(5));
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		cursor.close();

		return servicos;
	}
	
	private Cursor getCursorServicos(String condicao) {
		Cursor cursor = db.query(Constantes.TABLE_SERVICO, new String[] { 
				"tipo_ligacao_agua", 
				"tipo_ligacao_esgoto", 
				"local_instalacao_ramal", 
				"latitude",
				"longitude", "data" }, 
				
				condicao, 
				
				null, null, null, "id asc");
		
		return cursor;
	}
	
	private void montarServicos(Cursor cursor, Servicos servicos) {
		servicos.setTipoLigacaoAgua(cursor.getString(0));
		servicos.setTipoLigacaoEsgoto(cursor.getString(1));
		servicos.setLocalInstalacaoRamal(cursor.getString(2));
		servicos.setLatitude(cursor.getString(3));
		servicos.setLongitude(cursor.getString(4));
		servicos.setData(cursor.getString(5));
	}
            
	public void selectMedidor(long id) {
		Cursor cursor = getCursorMedidor("id = " + id);
		
		if (cursor.moveToFirst()) {
			montarDadosMedidor(cursor, getMedidorSelecionado());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}
	
	public Medidor selectMedidor(String condicao) {
		Cursor cursor = getCursorMedidor(condicao);

		Medidor medidor = null;
		if (cursor.moveToFirst()) {
			medidor = new Medidor();
			montarDadosMedidor(cursor, medidor);
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return medidor;
	}

	private Cursor getCursorMedidor(String condicao) {
		Cursor cursor = db.query(Constantes.TABLE_MEDIDOR, new String[] { 
				"possui_medidor", 
				"numero_hidrometro", 
				"marca", "capacidade", 
				"tipo_caixa_protecao",
				"latitude", 
				"longitude", 
				"data" }, 
				
				condicao, 
				
				null, null, null, "id asc");
		
		return cursor;
	}

	private void montarDadosMedidor(Cursor cursor, Medidor medidor) {
		medidor.setPossuiMedidor(cursor.getString(0));
		medidor.setNumeroHidrometro(cursor.getString(1));
		medidor.setMarca(cursor.getString(2));
		medidor.setCapacidade(cursor.getString(3));
		medidor.setTipoCaixaProtecao(cursor.getString(4));
		medidor.setLatitude(cursor.getString(5));
		medidor.setLongitude(cursor.getString(6));
		medidor.setData(cursor.getString(7));
	}
                
	public void selectAnormalidadeImovel(long id) {
		Cursor cursor = getCursorAnormalidadeImovel("id = " + id);

		if (cursor != null && cursor.moveToFirst()) {
			montarAnormalidadeImovel(cursor, getAnormalidadeImovelSelecionado());
		} else {
			getAnormalidadeImovelSelecionado().setCodigoAnormalidade(0);
			getAnormalidadeImovelSelecionado().setComentario("");
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}
	
	public AnormalidadeImovel selectAnormalidadeImovel(String condicao) {
		Cursor cursor = getCursorAnormalidadeImovel(condicao);

		AnormalidadeImovel anormalidade = null;
		if (cursor != null && cursor.moveToFirst()) {
			anormalidade = new AnormalidadeImovel();
			montarAnormalidadeImovel(cursor, anormalidade);
			Controlador.getInstancia().setAnormalidadeImovelSelecionado(anormalidade);
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return anormalidade;
	}

	private Cursor getCursorAnormalidadeImovel(String condicao) {
		Cursor cursor = db.query(Constantes.TABLE_ANORMALIDADE_IMOVEL, new String[] { 
				"latitude", 
				"longitude", 
				"codigo_anormalidade", 
				"comentario",
				"path_image_1", 
				"path_image_2", 
				"data", 
				"matricula", 
				"login_usuario" }, 
				
				condicao, 
				
				null, null, null, "codigo_anormalidade asc");
		return cursor;
	}

	private void montarAnormalidadeImovel(Cursor cursor, AnormalidadeImovel anormalidade) {
		anormalidade.setLatitude(cursor.getString(0));
		anormalidade.setLongitude(cursor.getString(1));
		anormalidade.setCodigoAnormalidade(Integer.parseInt(cursor.getString(2)));
		anormalidade.setComentario(cursor.getString(3));
		anormalidade.setFoto1(cursor.getString(4));
		anormalidade.setFoto2(cursor.getString(5));
		anormalidade.setData(cursor.getString(6));
		anormalidade.setMatricula(Integer.parseInt(cursor.getString(7)));
		anormalidade.setLoginUsuario(cursor.getString(8));
	}
                
	public void selectGeral() {

		Cursor cursor = db.query(Constantes.TABLE_GERAL, new String[] { 
				"id_rota",
				"localidade",
				"setor",
				"rota",
				"versao_arquivo",
				"nome_arquivo",
				"tipo_arquivo",
				"id_arquivo"},
				null, null, null, null, "id asc");

		if (cursor.moveToFirst()) {
			getDadosGerais().setIdRota(cursor.getString(0));
			getDadosGerais().setLocalidade(cursor.getString(1));
			getDadosGerais().setSetor(cursor.getString(2));
			getDadosGerais().setRota(cursor.getString(3));
			getDadosGerais().setVersaoArquivo(cursor.getString(4));
			getDadosGerais().setNomeArquivo(cursor.getString(5));
			getDadosGerais().setTipoArquivo(cursor.getString(6));
			getDadosGerais().setIdArquivo(cursor.getString(7));
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		cursor.close();
	}
	
	public int selectConfiguracaoElement(String element){
		   
		int elementValue = 0;
		
		Cursor cursor = db.query(Constantes.TABLE_CONFIGURACAO, new String[] {element}, null, null, null, null,  "id asc");

		if (cursor.moveToFirst()) {
			elementValue = Integer.parseInt(cursor.getString(0));
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		cursor.close();

		return elementValue;
	}
   

	public List<String> selectInformacoesRota() {
		ArrayList<String> lista = new ArrayList<String>();
		Cursor cursor = db.query(Constantes.TABLE_GERAL, new String[] { 
				"localidade", 
				"setor", 
				"rota", 
				"nome_arquivo", 
				"tipo_arquivo" },
				null, null, null, null, "localidade asc");

		if (cursor.moveToFirst()) {
			lista.add(cursor.getString(0));
			lista.add(cursor.getString(1));
			lista.add(cursor.getString(2));
			lista.add(cursor.getString(3));
			lista.add(cursor.getString(4));
		}
		
		cursor.close();

		return lista;
	}

	public List<String> selectAnormalidades(){
    	
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = db.query(Constantes.TABLE_ANORMALIDADE, new String[] {"codigo", "descricao"}, null, null, null, null,  "codigo asc");
		int x=0;
		if (cursor.moveToFirst()) {
			do {
                String b1= cursor.getString(1);
                list.add(b1);
                x=x+1;
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}

	public ArrayList<String> selectRamoAtividadeImovel(long id){
    	
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = db.query(Constantes.TABLE_RAMO_ATIVIDADE_IMOVEL, new String[] {"codigo"}, "id_imovel = " + id, null, null, null,  "codigo asc");
		int x=0;
		if (cursor.moveToFirst()) {
			do {
                list.add(cursor.getString(0));
                x=x+1;
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}

	public List<String> selectDescricoesFromTable(String table){
    	
		ArrayList<String> list = new ArrayList<String>();
		String asc = null;
		if(!table.equals(Constantes.TABLE_TIPO_USO) && 
				!table.equals(Constantes.TABLE_CLASSE_SOCIAL) &&
				!table.equals(Constantes.TABLE_ACESSO_HIDROMETRO)) asc = "descricao asc";
		
		Cursor cursor = db.query(table, new String[] {"codigo", "descricao"}, null, null, null, null, asc);
		int x=0;
		if (cursor.moveToFirst()) {
			do {
                String b1= cursor.getString(1);
                list.add(b1);
                x=x+1;
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}
	
    public String selectCodigoByDescricaoFromTable(String table, String descricao){
    	
    	String codigo = new String();
        Cursor cursor = db.query(table, new String[] {"codigo"}, "descricao = " + "\""  + descricao + "\"" , null, null, null,  "descricao asc");
        if (cursor.moveToFirst()) {
        	codigo = cursor.getString(0);
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return codigo;
   }

    public String selectDescricaoByCodigoFromTable(String table, String codigo){
    	
    	String descricao = new String();
        Cursor cursor = db.query(table, new String[] {"descricao"}, "codigo = " + "\""  + codigo + "\"" , null, null, null,  "codigo asc");
        if (cursor != null ){
	        if (cursor.moveToFirst()) {
	        	descricao = cursor.getString(0);
	        }
	        if (cursor != null && !cursor.isClosed()) {
	           cursor.close();
	        }
	        cursor.close();
	        return descricao;

        }else{
        	return null;
        }
   }
	
	public Usuario selectUsuario(String login) {
		Cursor cursor = db.query(Constantes.TABLE_USUARIO, new String[] { "nome", "login", "senha" }, "login = '" + login + "'", null, null, null, "nome ASC");
		Usuario usuario = null;
			
		if (cursor.moveToFirst()) {
			usuario = new Usuario();
					
			usuario.setNome(cursor.getString(0));
			usuario.setLogin(cursor.getString(1));
			usuario.setSenha(cursor.getString(2));
			
			Controlador.getInstancia().setUsuario(usuario);
		} else {
			Controlador.getInstancia().setUsuario(null);
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		cursor.close();
		return usuario;
	}

	public long insertDadosGerais(String linhaArquivo, String nomeArquivo) {
		ParserUtil parser = new ParserUtil(linhaArquivo);
		ContentValues initialValues = new ContentValues();

		parser.obterDadoParser(2);
		parser.obterDadoParser(4);
		parser.obterDadoParser(6);
		parser.obterDadoParser(12);
		parser.obterDadoParser(14);
		parser.obterDadoParser(20);
		parser.obterDadoParser(11);
		parser.obterDadoParser(40);
		parser.obterDadoParser(1);
		initialValues.put("versao_arquivo", Util.removerZerosAEsquerda(parser.obterDadoParser(10)));
		parser.obterDadoParser(8);
		parser.obterDadoParser(8);
		initialValues.put("id_rota", parser.obterDadoParser(4));
		initialValues.put("localidade", parser.obterDadoParser(3));
		initialValues.put("setor", parser.obterDadoParser(3));
		initialValues.put("rota", parser.obterDadoParser(2));
		parser.obterDadoParser(3);
		initialValues.put("nome_arquivo", nomeArquivo.replace(".txt", "").replace(".zip", ""));
		initialValues.put("tipo_arquivo", parser.obterDadoParser(1));
		initialValues.put("id_arquivo", Util.removerZerosAEsquerda(parser.obterDadoParser(11)));

		return db.insert(Constantes.TABLE_GERAL, null, initialValues);
	}
   
	public void updateConfiguracao(String parametroName, int value){
		   
		ContentValues initialValues = new ContentValues();
		initialValues.put(parametroName, value);

		if (DatabaseUtils.queryNumEntries(db,Constantes.TABLE_CONFIGURACAO) > 0){
			db.update(Constantes.TABLE_CONFIGURACAO, initialValues, "id=?", new String []{String.valueOf(1)});
			
		}else{
			db.insert(Constantes.TABLE_CONFIGURACAO, null, initialValues);
		}
	}

	public long insertCliente(String linhaArquivo) {
		int tamanhoLinha = linhaArquivo.length();

		ParserUtil parser = new ParserUtil(linhaArquivo);
		parser.obterDadoParser(2);
		
		ContentValues values = new ContentValues();

		values.put("matricula", String.valueOf(Integer.parseInt(parser.obterDadoParser(9))));
		values.put("gerencia", parser.obterDadoParser(25));
		values.put("tipo_endereco_proprietario", parser.obterDadoParser(1));
		values.put("tipo_endereco_responsavel", parser.obterDadoParser(1));
		values.put("usuario_proprietario", parser.obterDadoParser(1));
		values.put("tipo_responsavel", parser.obterDadoParser(1));

		int tamanhoTelefoneUsuario = getTamanhoTelefone(tamanhoLinha);
		int tamanhoCelularUsuario = getTamanhoCelularUsuario(tamanhoLinha);

		values.put("matricula_usuario", parser.obterDadoParser(9));
		values.put("nome_usuario", Util.removerCaractereEspecial(parser.obterDadoParser(50)));
		values.put("tipo_pessoa_usuario", parser.obterDadoParser(1));
		values.put("cpf_cnpj_usuario", parser.obterDadoParser(14).trim());
		values.put("rg_usuario", parser.obterDadoParser(13).trim());
		values.put("uf_usuario", parser.obterDadoParser(2));
		values.put("tipo_sexo_usuario", parser.obterDadoParser(1));
		values.put("telefone_usuario", parser.obterDadoParser(tamanhoTelefoneUsuario));
		values.put("celular_usuario", parser.obterDadoParser(tamanhoCelularUsuario));
		values.put("email_usuario", Util.removerCaractereEspecial(parser.obterDadoParser(30)));
		values.put("numero_nis_usuario", parser.obterDadoParser(14).trim());

		String matriculaProprietario = parser.obterDadoParser(9);

		int tamanhoTelefoneProprietario = getTamanhoTelefone(tamanhoLinha);
		int tamanhoCelularProprietario = getTamanhoCelularProprietarioOuResponsavel(tamanhoLinha, matriculaProprietario);

		values.put("matricula_proprietario", matriculaProprietario);
		values.put("nome_proprietario", Util.removerCaractereEspecial(parser.obterDadoParser(50)));
		values.put("tipo_pessoa_proprietario", parser.obterDadoParser(1));
		values.put("cpf_cnpj_proprietario", parser.obterDadoParser(14).trim());
		values.put("rg_proprietario", parser.obterDadoParser(13).trim());
		values.put("uf_proprietario", parser.obterDadoParser(2));
		values.put("tipo_sexo_proprietario", parser.obterDadoParser(1));
		values.put("telefone_proprietario", parser.obterDadoParser(tamanhoTelefoneProprietario));
		values.put("celular_proprietario", parser.obterDadoParser(tamanhoCelularProprietario));
		values.put("email_proprietario", Util.removerCaractereEspecial(parser.obterDadoParser(30)));
		values.put("tipo_logradouro_proprietario", parser.obterDadoParser(2));
		values.put("logradouro_proprietario", Util.removerCaractereEspecial(parser.obterDadoParser(40)));
		values.put("numero_proprietario", Util.removerCaractereEspecial(parser.obterDadoParser(5)));
		values.put("complemento_proprietario", Util.removerCaractereEspecial(parser.obterDadoParser(25)));
		values.put("bairro_proprietario", Util.removerCaractereEspecial(parser.obterDadoParser(20)));
		values.put("cep_proprietario", parser.obterDadoParser(8));
		values.put("municipio_proprietario", Util.removerCaractereEspecial(parser.obterDadoParser(15)));
		values.put("numero_nis_proprietario", parser.obterDadoParser(14).trim());

		String matriculaResponsavel = parser.obterDadoParser(9);

		int tamanhoTelefoneResponsavel = getTamanhoTelefone(tamanhoLinha);
		int tamanhoCelularResponsavel = getTamanhoCelularProprietarioOuResponsavel(tamanhoLinha, matriculaResponsavel);

		values.put("matricula_responsavel", matriculaResponsavel);
		values.put("nome_responsavel", Util.removerCaractereEspecial(parser.obterDadoParser(50)));
		values.put("tipo_pessoa_responsavel", parser.obterDadoParser(1));
		values.put("cpf_cnpj_responsavel", parser.obterDadoParser(14).trim());
		values.put("rg_responsavel", parser.obterDadoParser(13).trim());
		values.put("uf_responsavel", parser.obterDadoParser(2));
		values.put("tipo_sexo_responsavel", parser.obterDadoParser(1));
		values.put("telefone_responsavel", parser.obterDadoParser(tamanhoTelefoneResponsavel));
		values.put("celular_responsavel", parser.obterDadoParser(tamanhoCelularResponsavel));
		values.put("email_responsavel", Util.removerCaractereEspecial(parser.obterDadoParser(30)));
		values.put("tipo_logradouro_responsavel", parser.obterDadoParser(2));
		values.put("logradouro_responsavel", Util.removerCaractereEspecial(parser.obterDadoParser(40)));
		values.put("numero_responsavel", Util.removerCaractereEspecial(parser.obterDadoParser(5)));
		values.put("complemento_responsavel", Util.removerCaractereEspecial(parser.obterDadoParser(25)));
		values.put("bairro_responsavel", Util.removerCaractereEspecial(parser.obterDadoParser(20)));
		values.put("cep_responsavel", parser.obterDadoParser(8));
		values.put("municipio_responsavel", Util.removerCaractereEspecial(parser.obterDadoParser(15)));
		values.put("numero_nis_responsavel", parser.obterDadoParser(14).trim());
		values.put("latitude", String.valueOf(Constantes.NULO_DOUBLE));
		values.put("longitude", String.valueOf(Constantes.NULO_DOUBLE));
		values.put("data", "");

		return db.insert(Constantes.TABLE_CLIENTE, null, values);
	}

	private int getTamanhoTelefone(int tamanhoLinha) {
		if (tamanhoLinha == 696 || tamanhoLinha == 695) {
			return 11;
		} else {
			return 10;
		}
	}

	private int getTamanhoCelularUsuario(int tamanhoLinha) {
		if (tamanhoLinha == 690) {
			return 10;
		} else {
			return 11;
		}
	}

	private int getTamanhoCelularProprietarioOuResponsavel(int tamanhoLinha, String matricula) {
		int tamanho = 0;

		if (tamanhoLinha == 690) {
			tamanho = 10;
		} else if (tamanhoLinha == 696) {
			return 11;
		} else {
			if (matricula.trim().length() == 0) {
				tamanho = 10;
			} else {
				tamanho = 11;
			}
		}

		return tamanho;
	}

	public long insertImovel(String linhaArquivo) {
		ParserUtil parser = new ParserUtil(linhaArquivo);
		parser.obterDadoParser(2);

		int matricula = Integer.parseInt(parser.obterDadoParser(9));
		ContentValues initialValues = new ContentValues();
		initialValues.put("matricula", String.valueOf(matricula));
		initialValues.put("codigo_cliente", parser.obterDadoParser(30));
		initialValues.put("inscricao", parser.obterDadoParser(17));
		initialValues.put("rota", parser.obterDadoParser(2));
		initialValues.put("face", parser.obterDadoParser(2));
		initialValues.put("codigo_municipio", parser.obterDadoParser(8));
		initialValues.put("numero_iptu", parser.obterDadoParser(31));
		initialValues.put("numero_celpa", parser.obterDadoParser(20));
		initialValues.put("numero_pontos_uteis", parser.obterDadoParser(5));
		initialValues.put("numero_ocupantes", parser.obterDadoParser(5));

		if (matricula == 8081581) {
			Log.i("DEBUG", "ENTROU NO IMOVEL");
		}
		
		initialValues.put("tipo_logradouro_imovel", parser.obterDadoParser(2));
		initialValues.put("logradouro_imovel", Util.removerCaractereEspecial(parser.obterDadoParser(40)));
		initialValues.put("numero_imovel", Util.removerCaractereEspecial(parser.obterDadoParser(5)));
		initialValues.put("complemento_imovel", Util.removerCaractereEspecial(parser.obterDadoParser(25)));
		initialValues.put("bairro_imovel", Util.removerCaractereEspecial(parser.obterDadoParser(20)));
		initialValues.put("cep_imovel", parser.obterDadoParser(8));
		initialValues.put("municipio_imovel", Util.removerCaractereEspecial(parser.obterDadoParser(15)));
		initialValues.put("codigo_logradouro_imovel", parser.obterDadoParser(9));

		initialValues.put("sub_categoria_residencial_1", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_residencial_2", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_residencial_3", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_residencial_4", parser.obterDadoParser(3));

		initialValues.put("sub_categoria_comercial_1", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_comercial_2", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_comercial_3", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_comercial_4", parser.obterDadoParser(3));

		initialValues.put("sub_categoria_publica_1", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_publica_2", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_publica_3", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_publica_4", parser.obterDadoParser(3));

		initialValues.put("sub_categoria_industrial_1", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_industrial_2", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_industrial_3", parser.obterDadoParser(3));
		initialValues.put("sub_categoria_industrial_4", parser.obterDadoParser(3));
		initialValues.put("tipo_fonte_abastecimento", parser.obterDadoParser(2));

		initialValues.put("area_construida", parser.obterDadoParser(10));
		initialValues.put("classe_social", parser.obterDadoParser(1));
		initialValues.put("numero_animais", parser.obterDadoParser(4));

		initialValues.put("volume_cisterna", parser.obterDadoParser(7));
		initialValues.put("volume_piscina", parser.obterDadoParser(7));
		initialValues.put("volume_caixa_dagua", parser.obterDadoParser(7));

		initialValues.put("tipo_uso", parser.obterDadoParser(1));
		initialValues.put("acesso_hidrometro", parser.obterDadoParser(1));

		initialValues.put("numero_criancas", parser.obterDadoParser(4));
		initialValues.put("numero_adultos", parser.obterDadoParser(4));
		initialValues.put("numero_idosos", parser.obterDadoParser(4));
		initialValues.put("numero_empregados", parser.obterDadoParser(4));
		initialValues.put("numero_alunos", parser.obterDadoParser(4));
		initialValues.put("numero_caes", parser.obterDadoParser(4));
		initialValues.put("numero_outros", parser.obterDadoParser(4));

		initialValues.put("quantidade_economias_social", parser.obterDadoParser(3));
		initialValues.put("quantidade_economias_outros", parser.obterDadoParser(3));

		initialValues.put("percentual_abastecimento", parser.obterDadoParser(3) != null ? parser.getConteudo() : "000");

	   initialValues.put("quantidade_nos_fundos", parser.obterDadoParser(3));
	   initialValues.put("quantidade_nos_altos", parser.obterDadoParser(3));
	   initialValues.put("individualizacao", parser.obterDadoParser(3));

		String informativo = parser.obterDadoParser(1);
		String status = null;
		if (informativo.length() > 0 && Integer.valueOf(informativo) == Constantes.SIM) {
			status = String.valueOf(Constantes.IMOVEL_INFORMATIVO);
		} else {
			status = String.valueOf(Constantes.IMOVEL_A_SALVAR);
		}

		initialValues.put("imovel_status", status);

		initialValues.put("imovel_transmitido", String.valueOf(Constantes.NAO));
		initialValues.put("latitude", String.valueOf(Constantes.NULO_DOUBLE));
		initialValues.put("longitude", String.valueOf(Constantes.NULO_DOUBLE));
		initialValues.put("data", "");
		initialValues.put("entrevistado", "");
		initialValues.put("observacao", "");
		initialValues.put("tipo_operacao", Constantes.OPERACAO_CADASTRO_ALTERADO);

		return db.insert(Constantes.TABLE_IMOVEL, null, initialValues);

	}

	public long insertAnormalidadeImovel(String linhaArquivo) {
		ParserUtil parser = new ParserUtil(linhaArquivo);
		parser.obterDadoParser(2);

		ContentValues initialValues = new ContentValues();

		initialValues.put("matricula", String.valueOf(Integer.parseInt(parser.obterDadoParser(9))));
		initialValues.put("latitude", String.valueOf(Integer.parseInt(parser.obterDadoParser(15))));
		initialValues.put("longitude", String.valueOf(Integer.parseInt(parser.obterDadoParser(15))));
		initialValues.put("codigo_anormalidade", "0");
		initialValues.put("comentario", "");
		initialValues.put("path_image_1", "");
		initialValues.put("path_image_2", "");
		initialValues.put("data", "");

		
		return db.insert(Constantes.TABLE_ANORMALIDADE_IMOVEL, null, initialValues);
	}

	public long insertRamosAtividadeImovel(String linhaArquivo) {

		ParserUtil parser = new ParserUtil(linhaArquivo);
		parser.obterDadoParser(2);
		
		ContentValues values = new ContentValues();

		values.put("matricula", String.valueOf(Integer.parseInt(parser.obterDadoParser(9))));
		values.put("id_imovel", selectIdImoveis(null).size());
		values.put("codigo", parser.obterDadoParser(3));

		return db.insert(Constantes.TABLE_RAMO_ATIVIDADE_IMOVEL, null, values);
	}

	private long inserirRamoAtividadeImovel(String codigo) {
		ContentValues values = new ContentValues();

		values.put("matricula", getImovelSelecionado().getMatricula());
		values.put("id_imovel", getImovelSelecionado().getImovelId());
		values.put("codigo", codigo);

		return db.insert(Constantes.TABLE_RAMO_ATIVIDADE_IMOVEL, null, values);
	}

	public long insertServico(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("matricula", String.valueOf(Integer.parseInt(parser.obterDadoParser(9))));
		   initialValues.put("tipo_ligacao_agua", parser.obterDadoParser(2));
		   initialValues.put("tipo_ligacao_esgoto", parser.obterDadoParser(2));
		   initialValues.put("local_instalacao_ramal", parser.obterDadoParser(2));
		   initialValues.put("latitude",  String.valueOf(Constantes.NULO_DOUBLE));
		   initialValues.put("longitude", String.valueOf(Constantes.NULO_DOUBLE));
	   	   initialValues.put("data", "");

		   return db.insert(Constantes.TABLE_SERVICO, null, initialValues);
		}
	    
	public long insertAnormalidade(String linhaArquivo){

	   ParserUtil parser = new ParserUtil(linhaArquivo);
	   parser.obterDadoParser(2);
	   ContentValues initialValues = new ContentValues();
	   
	   initialValues.put("codigo", parser.obterDadoParser(3));
	   initialValues.put("descricao", parser.obterDadoParser(25));

	   return db.insert(Constantes.TABLE_ANORMALIDADE, null, initialValues);
	}
    
	public long insertRamoAtividade(String linhaArquivo){

	   ParserUtil parser = new ParserUtil(linhaArquivo);
	   parser.obterDadoParser(2);
	   ContentValues initialValues = new ContentValues();
	   
	   initialValues.put("codigo", parser.obterDadoParser(3));
	   initialValues.put("descricao", parser.obterDadoParser(20));

	   return db.insert(Constantes.TABLE_RAMO_ATIVIDADE, null, initialValues);
	}
	
	public long insertSituacaoLigacaoAgua(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_SITUACAO_LIGACAO_AGUA, null, initialValues);
	}
		
	public long insertSituacaoLigacaoEsgoto(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_SITUACAO_LIGACAO_ESGOTO, null, initialValues);
	}
		
	public long insertProtecaoHidrometro(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_PROTECAO_HIDROMETRO, null, initialValues);
	}
		
	public long insertFonteAbastecimento(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_FONTE_ABASTECIMENTO, null, initialValues);
	}
			
	public long insertMarcaHidrometro(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(30));

		   return db.insert(Constantes.TABLE_MARCA_HIDROMETRO, null, initialValues);
	}
		
	public long insertLocalInstalacaoRamal(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(30));

		   return db.insert(Constantes.TABLE_LOCAL_INSTALACAO_RAMAL, null, initialValues);
	}
		
	public long insertMedidor(String linhaArquivo){

	   ParserUtil parser = new ParserUtil(linhaArquivo);
	   parser.obterDadoParser(2);
	   ContentValues initialValues = new ContentValues();

	   initialValues.put("matricula", String.valueOf(Integer.parseInt(parser.obterDadoParser(9))));
	   initialValues.put("possui_medidor", parser.obterDadoParser(1));
	   initialValues.put("numero_hidrometro", parser.obterDadoParser(10));
	   initialValues.put("marca", parser.obterDadoParser(2));
	   initialValues.put("capacidade", parser.obterDadoParser(2));
	   initialValues.put("tipo_caixa_protecao", parser.obterDadoParser(2));
	   initialValues.put("latitude", String.valueOf(Constantes.NULO_DOUBLE));
	   initialValues.put("longitude", String.valueOf(Constantes.NULO_DOUBLE));
   	   initialValues.put("data", "");

	   return db.insert(Constantes.TABLE_MEDIDOR, null, initialValues);
   }
	
	public long insertCapacidadeHidrometro(String linhaArquivo){
	   ParserUtil parser = new ParserUtil(linhaArquivo);
	   parser.obterDadoParser(2);
	   ContentValues initialValues = new ContentValues();
	   
	   initialValues.put("codigo", parser.obterDadoParser(2));
	   initialValues.put("descricao", parser.obterDadoParser(20));

	   return db.insert(Constantes.TABLE_CAPACIDADE_HIDROMETRO, null, initialValues);
	}
	
	public long insertLogradouro(String linhaArquivo){
		ParserUtil parser = new ParserUtil(linhaArquivo);
		parser.obterDadoParser(2);
		ContentValues initialValues = new ContentValues();
		
		initialValues.put("codigo", parser.obterDadoParser(2));
		initialValues.put("descricao", parser.obterDadoParser(20));
		
		return db.insert(Constantes.TABLE_TIPO_LOGRADOURO, null, initialValues);
	}
	
	public long insertClasseSocial(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_CLASSE_SOCIAL, null, initialValues);
	}
	
	public long insertTipoUso(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_TIPO_USO, null, initialValues);
	}
	
	public long insertAcessoHidrometro(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_ACESSO_HIDROMETRO, null, initialValues);
	}
	
	public long insertUsuario(String linhaArquivo) {
		ParserUtil parser = new ParserUtil(linhaArquivo);
		ContentValues initialValues = new ContentValues();

		parser.obterDadoParser(2);
		initialValues.put("nome", parser.obterDadoParser(50).trim());
		initialValues.put("login", parser.obterDadoParser(11).trim());
		initialValues.put("senha", parser.obterDadoParser(40).trim());

		return db.insert(Constantes.TABLE_USUARIO, null, initialValues);
	}
	
	public void salvarCliente() {

		ContentValues values = new ContentValues();
		values.put("matricula", getImovelSelecionado().getMatricula());
		values.put("gerencia", getClienteSelecionado().getNomeGerenciaRegional());
		values.put("tipo_endereco_proprietario", String.valueOf(getClienteSelecionado().getTipoEnderecoProprietario()));
		values.put("tipo_endereco_responsavel", String.valueOf(getClienteSelecionado().getTipoEnderecoResponsavel()));
		values.put("usuario_proprietario", String.valueOf(getClienteSelecionado().isUsuarioProprietario()));
		values.put("tipo_responsavel", String.valueOf(getClienteSelecionado().getTipoResponsavel()));

		if (getClienteSelecionado().isNovoUsuario())
			values.put("matricula_usuario", "0");

		values.put("nome_usuario", getClienteSelecionado().getUsuario().getNome());
		values.put("tipo_pessoa_usuario", String.valueOf(getClienteSelecionado().getUsuario().getTipoPessoa()));
		values.put("cpf_cnpj_usuario", getClienteSelecionado().getUsuario().getCpfCnpj());
		values.put("rg_usuario", getClienteSelecionado().getUsuario().getRg());
		values.put("uf_usuario", getClienteSelecionado().getUsuario().getUf());
		values.put("tipo_sexo_usuario", getClienteSelecionado().getUsuario().getTipoSexo());
		values.put("telefone_usuario", getClienteSelecionado().getUsuario().getTelefone());
		values.put("celular_usuario", getClienteSelecionado().getUsuario().getCelular());
		values.put("email_usuario", getClienteSelecionado().getUsuario().getEmail());
		values.put("numero_nis_usuario", getClienteSelecionado().getUsuario().getNumeroNIS());

		if (getClienteSelecionado().isNovoProprietario())
			values.put("matricula_proprietario", "0");

		values.put("nome_proprietario", getClienteSelecionado().getProprietario().getNome());
		values.put("tipo_pessoa_proprietario", String.valueOf(getClienteSelecionado().getProprietario().getTipoPessoa()));
		values.put("cpf_cnpj_proprietario", getClienteSelecionado().getProprietario().getCpfCnpj());
		values.put("rg_proprietario", getClienteSelecionado().getProprietario().getRg());
		values.put("uf_proprietario", getClienteSelecionado().getProprietario().getUf());
		values.put("tipo_sexo_proprietario", getClienteSelecionado().getProprietario().getTipoSexo());
		values.put("telefone_proprietario", getClienteSelecionado().getProprietario().getTelefone());
		values.put("celular_proprietario", getClienteSelecionado().getProprietario().getCelular());
		values.put("email_proprietario", getClienteSelecionado().getProprietario().getEmail());
		values.put("tipo_logradouro_proprietario", getClienteSelecionado().getEnderecoProprietario().getTipoLogradouro());
		values.put("logradouro_proprietario", getClienteSelecionado().getEnderecoProprietario().getLogradouro());
		values.put("numero_proprietario", getClienteSelecionado().getEnderecoProprietario().getNumero());
		values.put("complemento_proprietario", getClienteSelecionado().getEnderecoProprietario().getComplemento());
		values.put("bairro_proprietario", getClienteSelecionado().getEnderecoProprietario().getBairro());
		values.put("cep_proprietario", getClienteSelecionado().getEnderecoProprietario().getCep());
		values.put("municipio_proprietario", getClienteSelecionado().getEnderecoProprietario().getMunicipio());
		values.put("numero_nis_proprietario", getClienteSelecionado().getProprietario().getNumeroNIS());

		if (getClienteSelecionado().isNovoResponsavel())
			values.put("matricula_responsavel", "0");

		values.put("nome_responsavel", getClienteSelecionado().getResponsavel().getNome());
		values.put("tipo_pessoa_responsavel", String.valueOf(getClienteSelecionado().getResponsavel().getTipoPessoa()));
		values.put("cpf_cnpj_responsavel", getClienteSelecionado().getResponsavel().getCpfCnpj());
		values.put("rg_responsavel", getClienteSelecionado().getResponsavel().getRg());
		values.put("uf_responsavel", getClienteSelecionado().getResponsavel().getUf());
		values.put("tipo_sexo_responsavel", getClienteSelecionado().getResponsavel().getTipoSexo());
		values.put("telefone_responsavel", getClienteSelecionado().getResponsavel().getTelefone());
		values.put("celular_responsavel", getClienteSelecionado().getResponsavel().getCelular());
		values.put("email_responsavel", getClienteSelecionado().getResponsavel().getEmail());
		values.put("tipo_logradouro_responsavel", getClienteSelecionado().getEnderecoResponsavel().getTipoLogradouro());
		values.put("logradouro_responsavel", getClienteSelecionado().getEnderecoResponsavel().getLogradouro());
		values.put("numero_responsavel", getClienteSelecionado().getEnderecoResponsavel().getNumero());
		values.put("complemento_responsavel", getClienteSelecionado().getEnderecoResponsavel().getComplemento());
		values.put("bairro_responsavel", getClienteSelecionado().getEnderecoResponsavel().getBairro());
		values.put("cep_responsavel", getClienteSelecionado().getEnderecoResponsavel().getCep());
		values.put("municipio_responsavel", getClienteSelecionado().getEnderecoResponsavel().getMunicipio());
		values.put("numero_nis_responsavel", getClienteSelecionado().getResponsavel().getNumeroNIS());

		values.put("latitude", getClienteSelecionado().getLatitude());
		values.put("longitude", getClienteSelecionado().getLongitude());
		values.put("data", getClienteSelecionado().getData());

		if (Controlador.getInstancia().getClienteSelecionado().isNovoRegistro()) {
			db.insert(Constantes.TABLE_CLIENTE, null, values);
			Controlador.getInstancia().getClienteSelecionado().setNovoRegistro(false);
		} else {
			db.update(Constantes.TABLE_CLIENTE, values, "id=?", new String[] { String.valueOf(getImovelSelecionado().getImovelId()) });
		}
	}

	public void salvarImovel() {
		ContentValues values = new ContentValues();
		
		values.put("matricula", getImovelSelecionado().getMatricula());
		values.put("inscricao", getImovelSelecionado().getInscricao());
		values.put("rota", getImovelSelecionado().getRota());
		values.put("face", getImovelSelecionado().getFace());
		values.put("codigo_municipio", getImovelSelecionado().getCodigoMunicipio());
		values.put("numero_iptu", getImovelSelecionado().getIptu());
		values.put("numero_celpa", getImovelSelecionado().getNumeroCelpa());
		values.put("numero_pontos_uteis", getImovelSelecionado().getNumeroPontosUteis());
		values.put("numero_ocupantes", getImovelSelecionado().getNumeroOcupantes());
		values.put("tipo_logradouro_imovel", getImovelSelecionado().getEnderecoImovel().getTipoLogradouro());
		values.put("logradouro_imovel", getImovelSelecionado().getEnderecoImovel().getLogradouro());
		values.put("numero_imovel", getImovelSelecionado().getEnderecoImovel().getNumero());
		values.put("complemento_imovel", getImovelSelecionado().getEnderecoImovel().getComplemento());

		values.put("bairro_imovel", getImovelSelecionado().getEnderecoImovel().getBairro());
		values.put("cep_imovel", getImovelSelecionado().getEnderecoImovel().getCep());
		values.put("municipio_imovel", getImovelSelecionado().getEnderecoImovel().getMunicipio());
		values.put("codigo_logradouro_imovel", getImovelSelecionado().getCodigoLogradouro());
		values.put("sub_categoria_residencial_1", String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria1()));
		values.put("sub_categoria_residencial_2", String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria2()));
		values.put("sub_categoria_residencial_3", String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria3()));
		values.put("sub_categoria_residencial_4", String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria4()));

		values.put("sub_categoria_comercial_1", String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria1()));
		values.put("sub_categoria_comercial_2", String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria2()));
		values.put("sub_categoria_comercial_3", String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria3()));
		values.put("sub_categoria_comercial_4", String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria4()));

		values.put("sub_categoria_publica_1", String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria1()));
		values.put("sub_categoria_publica_2", String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria2()));
		values.put("sub_categoria_publica_3", String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria3()));
		values.put("sub_categoria_publica_4", String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria4()));

		values.put("sub_categoria_industrial_1", String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria1()));
		values.put("sub_categoria_industrial_2", String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria2()));
		values.put("sub_categoria_industrial_3", String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria3()));
		values.put("sub_categoria_industrial_4", String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria4()));
		values.put("tipo_fonte_abastecimento", String.valueOf(getImovelSelecionado().getTipoFonteAbastecimento()));
		values.put("imovel_status", String.valueOf(getImovelSelecionado().getImovelStatus()));
		values.put("imovel_transmitido", String.valueOf(getImovelSelecionado().getImovelTransmitido()));

		values.put("latitude", getImovelSelecionado().getLatitude());
		values.put("longitude", getImovelSelecionado().getLongitude());
		values.put("data", getImovelSelecionado().getData());
		values.put("entrevistado", getImovelSelecionado().getEntrevistado());
		values.put("tipo_operacao", getImovelSelecionado().getTipoOperacao());

		values.put("area_construida", getImovelSelecionado().getAreaConstruida());
		values.put("classe_social", getImovelSelecionado().getClasseSocial());
		values.put("numero_animais", getImovelSelecionado().getNumeroAnimais());
		values.put("volume_piscina", getImovelSelecionado().getVolumePiscina());
		values.put("volume_cisterna", getImovelSelecionado().getVolumeCisterna());
		values.put("volume_caixa_dagua", getImovelSelecionado().getVolumeCaixaDagua());
		values.put("tipo_uso", getImovelSelecionado().getTipoUso());
		values.put("acesso_hidrometro", getImovelSelecionado().getAcessoHidrometro());

		values.put("numero_criancas", getImovelSelecionado().getOcupacaoImovel().getCriancas());
		values.put("numero_adultos", getImovelSelecionado().getOcupacaoImovel().getAdultos());
		values.put("numero_alunos", getImovelSelecionado().getOcupacaoImovel().getAlunos());
		values.put("numero_caes", getImovelSelecionado().getOcupacaoImovel().getCaes());
		values.put("numero_idosos", getImovelSelecionado().getOcupacaoImovel().getIdosos());
		values.put("numero_empregados", getImovelSelecionado().getOcupacaoImovel().getEmpregados());
		values.put("numero_outros", getImovelSelecionado().getOcupacaoImovel().getOutros());

		values.put("quantidade_economias_social", getImovelSelecionado().getQuantidadeEconomiasSocial());
		values.put("quantidade_economias_outros", getImovelSelecionado().getQuantidadeEconomiasOutros());

		values.put("observacao", getImovelSelecionado().getObservacao());

		values.put("percentual_abastecimento", getImovelSelecionado().getPercentualAbastecimento());
		
		values.put("quantidade_nos_fundos", getImovelSelecionado().getQuantidadeNosFundos());
		values.put("quantidade_nos_altos", getImovelSelecionado().getQuantidadeNosAltos());
		values.put("individualizacao", getImovelSelecionado().getIndividualizacao());

		if (Controlador.getInstancia().getImovelSelecionado().isNovoRegistro()) {
			db.insert(Constantes.TABLE_IMOVEL, null, values);
			Controlador.getInstancia().getImovelSelecionado().setNovoRegistro(false);
		} else {
			db.update(Constantes.TABLE_IMOVEL, values, "id=?", new String[] { String.valueOf(getImovelSelecionado().getImovelId()) });
			db.delete(Constantes.TABLE_RAMO_ATIVIDADE_IMOVEL, "id_imovel=?", new String[] { String.valueOf(getImovelSelecionado().getImovelId()) });
		}
		
		if (getImovelSelecionado().possueRamoAtividade()) {
			for (int i = 0; i < getImovelSelecionado().getListaRamoAtividade().size(); i++) {
				inserirRamoAtividadeImovel(getImovelSelecionado().getListaRamoAtividade().get(i));
			}
		}
	}
	
	public void salvarStatusImovel(Imovel imovel) {
		ContentValues values = new ContentValues();
		values.put("imovel_status", imovel.getImovelStatus());
		values.put("imovel_transmitido", imovel.getImovelTransmitido());

		db.update(Constantes.TABLE_IMOVEL, values, "matricula=?", new String[] { String.valueOf(imovel.getMatricula()) });
	}

	public void salvarServico() {

		ContentValues values = new ContentValues();
		values.put("matricula", getImovelSelecionado().getMatricula());
		values.put("tipo_ligacao_agua", String.valueOf(getServicosSelecionado().getTipoLigacaoAgua()));
		values.put("tipo_ligacao_esgoto", String.valueOf(getServicosSelecionado().getTipoLigacaoEsgoto()));
		values.put("local_instalacao_ramal", String.valueOf(getServicosSelecionado().getLocalInstalacaoRamal()));
		values.put("latitude", String.valueOf(getServicosSelecionado().getLatitude()));
		values.put("longitude", String.valueOf(getServicosSelecionado().getLongitude()));
		values.put("data", getServicosSelecionado().getData());

		if (Controlador.getInstancia().getServicosSelecionado().isNovoRegistro()) {
			db.insert(Constantes.TABLE_SERVICO, null, values);
			Controlador.getInstancia().getServicosSelecionado().setNovoRegistro(false);
		} else {
			db.update(Constantes.TABLE_SERVICO, values, "id=?", new String[] { String.valueOf(getImovelSelecionado().getImovelId()) });
		}
	}

	public void salvarMedidor() {

		ContentValues values = new ContentValues();
		values.put("matricula", getImovelSelecionado().getMatricula());
		values.put("possui_medidor", String.valueOf(getMedidorSelecionado().getPossuiMedidor()));
		values.put("numero_hidrometro", getMedidorSelecionado().getNumeroHidrometro());
		values.put("marca", getMedidorSelecionado().getMarca());
		values.put("capacidade", getMedidorSelecionado().getCapacidade());
		values.put("tipo_caixa_protecao", String.valueOf(getMedidorSelecionado().getTipoCaixaProtecao()));
		values.put("latitude", getMedidorSelecionado().getLatitude());
		values.put("longitude", getMedidorSelecionado().getLongitude());
		values.put("data", getMedidorSelecionado().getData());

		if (Controlador.getInstancia().getMedidorSelecionado().isNovoRegistro()) {
			db.insert(Constantes.TABLE_MEDIDOR, null, values);
			Controlador.getInstancia().getMedidorSelecionado().setNovoRegistro(false);
		} else {
			db.update(Constantes.TABLE_MEDIDOR, values, "id=?", new String[] { String.valueOf(getImovelSelecionado().getImovelId()) });
		}
	}

	public void salvarAnormalidadeImovel() {
		ContentValues values = new ContentValues();

		values.put("matricula", getImovelSelecionado().getMatricula());
		values.put("codigo_anormalidade", String.valueOf(getAnormalidadeImovelSelecionado().getCodigoAnormalidade()));
		values.put("comentario", getAnormalidadeImovelSelecionado().getComentario());
		values.put("path_image_1", getAnormalidadeImovelSelecionado().getFoto1());
		values.put("path_image_2", getAnormalidadeImovelSelecionado().getFoto2());
		values.put("latitude", getAnormalidadeImovelSelecionado().getLatitude());
		values.put("longitude", getAnormalidadeImovelSelecionado().getLongitude());
		values.put("data", getAnormalidadeImovelSelecionado().getData());
		values.put("login_usuario", getAnormalidadeImovelSelecionado().getLoginUsuario());

		if (Controlador.getInstancia().getAnormalidadeImovelSelecionado().isNovoRegistro()) {
			db.insert(Constantes.TABLE_ANORMALIDADE_IMOVEL, null, values);
			Controlador.getInstancia().getAnormalidadeImovelSelecionado().setNovoRegistro(false);
		} else {
			db.update(Constantes.TABLE_ANORMALIDADE_IMOVEL, values, "id=?", new String[] { String.valueOf(getImovelSelecionado().getImovelId()) });
		}
	}
	
	public int getQtdImoveisNovo() {
		Cursor cursor = db.query(Constantes.TABLE_GERAL, new String[] { "qtd_imoveis_novos" }, null, null, null, null, null);
		int qtd = 0;
		if (cursor.moveToFirst()) {
			qtd = cursor.getInt(0);
		}

		return qtd;
	}
	
	public void salvarQtdImoveisNovos() {
		ContentValues values = new ContentValues();
		values.put("qtd_imoveis_novos", getQtdImoveisNovo() + 1);

		db.update(Constantes.TABLE_GERAL, values, "id=?", new String[] { String.valueOf(1) });
	}
	
	public void apagarInconsistenciaImovel(int matricula) {
		db.delete(Constantes.TABLE_INCONSISTENCIA_IMOVEL, "matricula=?", new String[] { String.valueOf(matricula) });
	}
	
	public void inserirInconsistenciaImovel(int matricula, String inconsistencia) {
		ContentValues valores = new ContentValues();
		valores.put("matricula", matricula);
		valores.put("inconsistencia", inconsistencia);

		db.insert(Constantes.TABLE_INCONSISTENCIA_IMOVEL, null, valores);
	}
	
	public String[] pesquisarInconsistencias(int matricula) {
		Cursor cursor = db.query(Constantes.TABLE_INCONSISTENCIA_IMOVEL, new String[] { "inconsistencia" }, "matricula = " + matricula, null, null, null, "id asc");

		String[] inconsistencias = new String[cursor.getCount()];

		int count = 0;
		if (cursor.moveToFirst()) {
			do {
				inconsistencias[count] = cursor.getString(0);
				count++;
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return inconsistencias;
	}
	
	public List<Imovel> pesquisarImoveisFinalizados(int transmitido) {

		Cursor cursor = getCursorImovel("imovel_transmitido = " + transmitido + 
				" AND imovel_status NOT IN (" + Constantes.IMOVEL_A_SALVAR + "," + Constantes.IMOVEL_INFORMATIVO + ")");

		List<Imovel> imoveis = new ArrayList<Imovel>();

		if (cursor.moveToFirst()) {
			do {
				long id = Long.valueOf(getValorColuna(cursor, "id"));

				Imovel imovel = new Imovel();
				montarImovel(cursor, id, imovel);
				imoveis.add(imovel);
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return imoveis;
	}

	private Cursor getCursorImovel(String condicao) {
		
		Cursor cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { 
				"id", 
				"inscricao", 
				"rota", 
				"face", 
				"codigo_municipio",
				"numero_iptu", 
				"numero_celpa", 
				"numero_pontos_uteis", 
				"numero_ocupantes", 
				"tipo_logradouro_imovel", 
				"logradouro_imovel", 
				"numero_imovel",
				"complemento_imovel", 
				"bairro_imovel", 
				"cep_imovel", 
				"municipio_imovel", 
				"codigo_logradouro_imovel", 
				"sub_categoria_residencial_1",
				"sub_categoria_residencial_2", 
				"sub_categoria_residencial_3", 
				"sub_categoria_residencial_4", 
				"sub_categoria_comercial_1",
				"sub_categoria_comercial_2", 
				"sub_categoria_comercial_3", 
				"sub_categoria_comercial_4", 
				"sub_categoria_publica_1", 
				"sub_categoria_publica_2",
				"sub_categoria_publica_3", 
				"sub_categoria_publica_4", 
				"sub_categoria_industrial_1", 
				"sub_categoria_industrial_2", 
				"sub_categoria_industrial_3",
				"sub_categoria_industrial_4", 
				"tipo_fonte_abastecimento", 
				"matricula", 
				"imovel_status", 
				"imovel_transmitido", 
				"latitude", 
				"longitude", 
				"data",
				"entrevistado", 
				"tipo_operacao", 
				"area_construida", 
				"classe_social", 
				"numero_animais", 
				"volume_piscina", 
				"volume_cisterna",
				"volume_caixa_dagua", 
				"tipo_uso", 
				"acesso_hidrometro", 
				"numero_criancas", 
				"numero_adultos", 
				"numero_alunos", 
				"numero_caes", 
				"numero_idosos",
				"numero_empregados", 
				"numero_outros", 
				"quantidade_economias_social", 
				"quantidade_economias_outros", 
				"observacao", 
				"percentual_abastecimento",
				"quantidade_nos_fundos",
				"quantidade_nos_altos",
				"individualizacao"},
				condicao, null, null, null, "inscricao asc");
		
		return cursor;
	}
	
	private void montarImovel(Cursor cursor, long id, Imovel imovel) {
		imovel.setImovelId(id);
		imovel.setInscricao(getValorColuna(cursor, "inscricao"));
		imovel.setRota(getValorColuna(cursor, "rota"));
		imovel.setFace(getValorColuna(cursor, "face"));
		imovel.setCodigoMunicipio(getValorColuna(cursor, "codigo_municipio"));
		imovel.setIptu(getValorColuna(cursor, "numero_iptu"));
		imovel.setNumeroCelpa(getValorColuna(cursor, "numero_celpa"));
		imovel.setNumeroPontosUteis(getValorColuna(cursor, "numero_pontos_uteis"));
		imovel.setNumeroOcupantes(getValorColuna(cursor, "numero_ocupantes"));

		imovel.getEnderecoImovel().setTipoLogradouro(getValorColuna(cursor, "tipo_logradouro_imovel"));
		imovel.getEnderecoImovel().setLogradouro(getValorColuna(cursor, "logradouro_imovel"));
		imovel.getEnderecoImovel().setNumero(getValorColuna(cursor, "numero_imovel"));
		imovel.getEnderecoImovel().setComplemento(getValorColuna(cursor, "complemento_imovel"));
		imovel.getEnderecoImovel().setBairro(getValorColuna(cursor, "bairro_imovel"));
		imovel.getEnderecoImovel().setCep(getValorColuna(cursor, "cep_imovel"));
		imovel.getEnderecoImovel().setMunicipio(getValorColuna(cursor, "municipio_imovel"));
		imovel.setCodigoLogradouro(getValorColuna(cursor, "codigo_logradouro_imovel"));

		imovel.getCategoriaResidencial().setEconomiasSubCategoria1(getValorColuna(cursor, "sub_categoria_residencial_1"));
		imovel.getCategoriaResidencial().setEconomiasSubCategoria2(getValorColuna(cursor, "sub_categoria_residencial_2"));
		imovel.getCategoriaResidencial().setEconomiasSubCategoria3(getValorColuna(cursor, "sub_categoria_residencial_3"));
		imovel.getCategoriaResidencial().setEconomiasSubCategoria4(getValorColuna(cursor, "sub_categoria_residencial_4"));
		imovel.getCategoriaComercial().setEconomiasSubCategoria1(getValorColuna(cursor, "sub_categoria_comercial_1"));
		imovel.getCategoriaComercial().setEconomiasSubCategoria2(getValorColuna(cursor, "sub_categoria_comercial_2"));
		imovel.getCategoriaComercial().setEconomiasSubCategoria3(getValorColuna(cursor, "sub_categoria_comercial_3"));
		imovel.getCategoriaComercial().setEconomiasSubCategoria4(getValorColuna(cursor, "sub_categoria_comercial_4"));
		imovel.getCategoriaPublica().setEconomiasSubCategoria1(getValorColuna(cursor, "sub_categoria_publica_1"));
		imovel.getCategoriaPublica().setEconomiasSubCategoria2(getValorColuna(cursor, "sub_categoria_publica_2"));
		imovel.getCategoriaPublica().setEconomiasSubCategoria3(getValorColuna(cursor, "sub_categoria_publica_3"));
		imovel.getCategoriaPublica().setEconomiasSubCategoria4(getValorColuna(cursor, "sub_categoria_publica_4"));
		imovel.getCategoriaIndustrial().setEconomiasSubCategoria1(getValorColuna(cursor, "sub_categoria_industrial_1"));
		imovel.getCategoriaIndustrial().setEconomiasSubCategoria2(getValorColuna(cursor, "sub_categoria_industrial_2"));
		imovel.getCategoriaIndustrial().setEconomiasSubCategoria3(getValorColuna(cursor, "sub_categoria_industrial_3"));
		imovel.getCategoriaIndustrial().setEconomiasSubCategoria4(getValorColuna(cursor, "sub_categoria_industrial_4"));

		imovel.setTipoFonteAbastecimento(getValorColuna(cursor, "tipo_fonte_abastecimento"));
		imovel.setMatricula(getValorColuna(cursor, "matricula"));
		imovel.setImovelStatus(getValorColuna(cursor, "imovel_status"));
		imovel.setImovelTransmitido(getValorColuna(cursor, "imovel_transmitido"));
		imovel.setLatitude(getValorColuna(cursor, "latitude"));
		imovel.setLongitude(getValorColuna(cursor, "longitude"));
		imovel.setData(getValorColuna(cursor, "data"));
		imovel.setEntrevistado(getValorColuna(cursor, "entrevistado"));
		imovel.setTipoOperacao(getValorColuna(cursor, "tipo_operacao"));

		imovel.setListaRamoAtividade(selectRamoAtividadeImovel(id));

		imovel.setAreaConstruida(getValorColuna(cursor, "area_construida"));
		imovel.setClasseSocial(getValorColuna(cursor, "classe_social"));
		imovel.setNumeroAnimais(getValorColuna(cursor, "numero_animais"));
		imovel.setVolumePiscina(getValorColuna(cursor, "volume_piscina"));
		imovel.setVolumeCisterna(getValorColuna(cursor, "volume_cisterna"));
		imovel.setVolumeCaixaDagua(getValorColuna(cursor, "volume_caixa_dagua"));
		imovel.setTipoUso(getValorColuna(cursor, "tipo_uso"));
		imovel.setAcessoHidrometro(getValorColuna(cursor, "acesso_hidrometro"));

		imovel.getOcupacaoImovel().setCriancas(getValorColuna(cursor, "numero_criancas"));
		imovel.getOcupacaoImovel().setAdultos(getValorColuna(cursor, "numero_adultos"));
		imovel.getOcupacaoImovel().setAlunos(getValorColuna(cursor, "numero_alunos"));
		imovel.getOcupacaoImovel().setCaes(getValorColuna(cursor, "numero_caes"));
		imovel.getOcupacaoImovel().setIdosos(getValorColuna(cursor, "numero_idosos"));
		imovel.getOcupacaoImovel().setEmpregados(getValorColuna(cursor, "numero_empregados"));
		imovel.getOcupacaoImovel().setOutros(getValorColuna(cursor, "numero_outros"));

		imovel.setQuantidadeEconomiasSocial(getValorColuna(cursor, "quantidade_economias_social"));
		imovel.setQuantidadeEconomiasOutros(getValorColuna(cursor, "quantidade_economias_outros"));
		imovel.setObservacao(getValorColuna(cursor, "observacao"));
		imovel.setPercentualAbastecimento(getValorColuna(cursor, "percentual_abastecimento"));
		
		imovel.setQuantidadeNosFundos(getValorColuna(cursor, "quantidade_nos_fundos"));
		imovel.setQuantidadeNosAltos(getValorColuna(cursor, "quantidade_nos_altos"));
		imovel.setIndividualizacao(getValorColuna(cursor, "individualizacao"));
		
	}
	
	private String getValorColuna(Cursor cursor, String coluna) {
		return cursor.getString(cursor.getColumnIndexOrThrow(coluna));		
	}
}