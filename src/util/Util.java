package util;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import business.Controlador;

import com.AndroidExplorer.ClienteTab;

@SuppressLint({ "SdCardPath", "SimpleDateFormat", "DefaultLocale" })
public class Util {

	static boolean isUpdatingCep;
	static boolean isUpdatingIptu;
	static boolean isUpdatingPhone;
	static boolean isUpdatingCpfCnpj;
	static boolean isCpfProprietarioOk = false;
	static boolean isCnpjProprietarioOk = false;
	static boolean isCpfUsuarioOk = false;
	static boolean isCnpjUsuarioOk = false;
	static boolean isCpfResponsavelOk = false;
	static boolean isCnpjResponsavelOk = false;
	static boolean isUpdatingConsulta = false;
	static TextWatcher consultaTextWatcher = null;
	
	public static boolean getCpfResponsavelOk(){
		return isCpfResponsavelOk;
	}

	public static boolean getCnpjResponsavelOk(){
		return isCnpjResponsavelOk;
	}

	public static boolean isValidText(final String textName){
		boolean result = false;
		if (!textName.matches("[a-zA-Z &/]*")) {
			result = true;
		}
		return result;
	}
	
	// Define a variavel editText para tratar os eventos de textChanged considerando mascara para CEP.
	public static void addTextChangedListenerCepMask(final EditText edt){
    	edt.addTextChangedListener(new TextWatcher() {  
    	    
    		
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
    	    }  
    	      
    		
    	    public void onTextChanged(CharSequence s, int start, int before, int after) {  
    	      
    			// Quando o texto é alterado o onTextChange é chamado. Essa flag evita a chamada infinita desse método  
    			if (isUpdatingCep){
    				isUpdatingCep = false;  
    				return;  
    			}  
    	      
    			boolean hasMask = s.toString().indexOf('-') > -1;  
    	      
    			// Remove o '-' da String  
    			String str = s.toString().replaceAll("[-]", "");  
    	      
    			if (after > before) {  

    				// Se tem mais de 5 caracteres (sem máscara) coloca o '-'  
    				if (str.length() > 5) {  
    					str = str.substring(0,5) + '-' + str.substring(5);  
    				}  
    				
    				// Seta a flag pra evitar chamada infinita  
    				isUpdatingCep = true;  
    				
    				// seta o novo texto  
    				edt.setText(str);  
    				
    				// seta a posição do cursor  
    				if(start == 5){
        				edt.setSelection(start + 2);  
    				}else{
        				edt.setSelection(start + 1);  
    				}
    	      
    			} else {  
    				isUpdatingCep = true;  
    				
    				if (str.length() > 5){
    					str = str.substring(0,5) + '-' + str.substring(5);
    				}else{
        				edt.setText(str);  
    				}
    				
    				// Se estiver apagando posiciona o cursor no local correto. Isso trata a deleção dos caracteres da máscara.  
    				edt.setSelection(Math.max(0, Math.min(hasMask ? start + 1 - before : start, str.length() ) ) );  
    			}  
    		}  
    	         
    		
    	    public void afterTextChanged(Editable s) {  
    	    }  
        });
		
	}
	
	
	// Define a variavel editText para tratar os eventos de textChanged considerando mascara para CEP.
	public static void addTextChangedListenerIPTUMask(final EditText edt){
    	edt.addTextChangedListener(new TextWatcher() {  
    	    
    		
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}  
    		
    	    public void onTextChanged(CharSequence s, int start, int before, int after) {  
    	      
    			// Quando o texto é alterado o onTextChange é chamado. Essa flag evita a chamada infinita desse método  
    			if (isUpdatingIptu){
    				isUpdatingIptu = false;  
    				return;  
    			}  
    	      
    			boolean hasMask = s.toString().indexOf('-') > -1;  
    	      
    			// Remove o '-' da String  
    			String str = s.toString().replaceAll("[-]", "").replaceAll("[/]", "");  
    	      
    			if (after > before) {  

    				if (str.length() > 3) {  
    					str = str.substring(0,3) + '/' + str.substring(3);  
    				}  
    				if (str.length() > 9) {  
    					str = str.substring(0,9) + '/' + str.substring(9);  
    				}  
    				if (str.length() > 12) {  
    					str = str.substring(0,12) + '/' + str.substring(12);  
    				}  
    				if (str.length() > 15) {  
    					str = str.substring(0,15) + '/' + str.substring(15);  
    				}  
    				if (str.length() > 20) {  
    					str = str.substring(0,20) + '/' + str.substring(20);  
    				}  
    				// Se tem mais de 3 caracteres (sem máscara) coloca o '/'  
    				if (str.length() > 24) {  
    					str = str.substring(0,24) + '/' + str.substring(24);  
    				}  
    				// Se tem mais de 28 caracteres (sem máscara) coloca o '-'  
    				if (str.length() > 28) {  
    					str = str.substring(0,28) + '-' + str.substring(28);  
    				}  
    				
     				// Seta a flag pra evitar chamada infinita  
    				isUpdatingIptu = true;  
    				
    				// seta o novo texto  
    				edt.setText(str);  
    				
    				// seta a posição do cursor  
    				if(start == 3 || start == 9 || start == 12 || start == 15 || start == 20 || start == 24 || start == 28){
        				edt.setSelection(start + 2);  
    				}else{
        				edt.setSelection(start + 1);  
    				}
    	      
    			} else {  
    				isUpdatingIptu = true;  

    				if (str.length() > 3) {  
    					str = str.substring(0,3) + '/' + str.substring(3);  
    				}  
    				if (str.length() > 9) {  
    					str = str.substring(0,9) + '/' + str.substring(9);  
    				}  
    				if (str.length() > 12) {  
    					str = str.substring(0,12) + '/' + str.substring(12);  
    				}  
    				if (str.length() > 15) {  
    					str = str.substring(0,15) + '/' + str.substring(15);  
    				}  
    				if (str.length() > 20) {  
    					str = str.substring(0,20) + '/' + str.substring(20);  
    				}  
    				// Se tem mais de 3 caracteres (sem máscara) coloca o '/'  
    				if (str.length() > 24) {  
    					str = str.substring(0,24) + '/' + str.substring(24);  
    				}  
    				// Se tem mais de 28 caracteres (sem máscara) coloca o '-'  
    				if (str.length() > 28) {  
    					str = str.substring(0,28) + '-' + str.substring(28);  
    				}  

    				edt.setText(str);  
    				
    				// Se estiver apagando posiciona o cursor no local correto. Isso trata a deleção dos caracteres da máscara.  
    				edt.setSelection(Math.max(0, Math.min(hasMask ? start + 1 - before : start, str.length() ) ) );  
    			}  
    		}  
    		
    	    public void afterTextChanged(Editable s) {}  
        });
		
	}

	// Define a variavel editText para tratar os eventos de textChanged considerando mascara para Telefone.
	public static void addTextChangedListenerPhoneMask(final EditText edt){
    	edt.addTextChangedListener(new TextWatcher() {  
    	    
    		
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
    	    }  
    	      
    		
    	    public void onTextChanged(CharSequence s, int start, int before, int after) {  
    	      
    			// Quando o texto é alterado o onTextChange é chamado. Essa flag evita a chamada infinita desse método  
    			if (isUpdatingPhone){
    				isUpdatingPhone = false;  
    				return;  
    			}  
    	      
    			// Remove o '-' da String  
    			String str = s.toString().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", "");  
    	      
    			if (after > before) {  

    				str = '(' + str;  
    				
    				if (str.length() > 3) {  
    					str = str.substring(0,3) + ')' + str.substring(3);  
    				}  
    				
    				if (str.length() > 8) {  
    					str = str.substring(0,8) + '-' + str.substring(8);  
    				}  
    				
    				if (str.length() > 13) {
    					str = str.toString().replaceAll("[-]", "");
    					str = str.substring(0,9) + '-' + str.substring(9);  
    				}  
    				
    				// Seta a flag pra evitar chamada infinita  
    				isUpdatingPhone = true;  
    				
    				// seta o novo texto  
    				edt.setText(str);  
    				
    				// seta a posição do cursor  
    				if(start == 0 || start == 3 || start == 8 ){
        				edt.setSelection(start + 2);  
    				}else{
        				edt.setSelection(start + 1);  
    				}
    	      
    			} else {  
    				isUpdatingPhone = true;  
    				
    				if(str.length() > 0){
        				str = '(' + str;
    				}
    				
    				if (str.length() > 3) {  
    					str = str.substring(0,3) + ')' + str.substring(3);  
    				}

    				if (str.length() > 8) {  
    					str = str.substring(0,8) + '-' + str.substring(8);  
    				}

    				edt.setText(str);  
    				
    				// Se estiver apagando posiciona o cursor no local correto. Isso trata a deleção dos caracteres da máscara.  
    				edt.setSelection(Math.max(0, Math.min(start + 1 - before, str.length() ) ) ); 
    			}  
    		}  
    		
    	    public void afterTextChanged(Editable s) {  
    	    }  
        });
	}
	
	// Define a variável editText para tratar os eventos de textChanged de Consulta.
	public static void addTextChangedListenerConsultaVerifierAndMask(final EditText edt, final int metodoBusca){
		
		if (consultaTextWatcher != null){
			edt.removeTextChangedListener(consultaTextWatcher);
		}
		
		consultaTextWatcher = new TextWatcher(){  
    	    
    		
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}  
    	      
    		
    	    public void onTextChanged(CharSequence s, int start, int before, int after) {  
    	         
    			// Quando o texto é alterado o onTextChange é chamado. Essa flag evita a chamada infinita desse método  
    			if (isUpdatingConsulta){
    				isUpdatingConsulta = false;  
    				return;  
    			}  
    	      
				// Se for CPF
    			if (metodoBusca == Constantes.METODO_BUSCA_CPF){
	    			
					// Remove o '-'  e o '.'da String  
	    			String str = s.toString().replaceAll("[-]", "").replaceAll("[.]", "");  
	    	      
	    			if (after > before && before < 12) {  

	    				if (str.length() > 3) {  
	    					str = str.substring(0,3) + '.' + str.substring(3);  
	    				}  
	    				
	    				if (str.length() > 7) {  
	    					str = str.substring(0,7) + '.' + str.substring(7);  
	    				}  
	    				
	    				if (str.length() > 11) {  
	    					str = str.substring(0,11) + '-' + str.substring(11);  
	    				}  
	    				
	    				if (str.length() > 14) {  
	    					str = str.substring(0,14);  
	    				}
	    				
	    				// Seta a flag pra evitar chamada infinita  
	    				isUpdatingConsulta = true;  
	    				
	    				// seta o novo texto  
	    				edt.setText(str);  
	    				
	    				// seta a posição do cursor  
	    				if(start == 3 || start == 7 || start == 11 ){
	        				edt.setSelection(start + 2);  
	    				}else if (start == 14){
	        				edt.setSelection(start);  
	    				}else{
	        				edt.setSelection(start + 1);  
	    				}
	    	      
	    			} else {  
	    				isUpdatingConsulta = true;  
	    				
	    				if (str.length() > 3) {  
	    					str = str.substring(0,3) + '.' + str.substring(3);  
	    				}

	    				if (str.length() > 7) {  
	    					str = str.substring(0,7) + '.' + str.substring(7);  
	    				}

	    				if (str.length() > 11) {  
	    					str = str.substring(0,11) + '-' + str.substring(11);  
	    				}

	    				if (str.length() > 18) {  
	    					str = str.substring(0,18);  
	    				}
	    				
	    				edt.setText(str);  
	    				
	    				// Se estiver apagando posiciona o cursor no local correto. Isso trata a deleção dos caracteres da máscara.  
	    				edt.setSelection(Math.max(0, Math.min(start + 1 - before, str.length() ) ) ); 
	    			}  
				// Se for CNPJ
    			}else if (metodoBusca == Constantes.METODO_BUSCA_CNPJ){
					
	    			// Remove o '-'  e '.'da String  
	    			String str = s.toString().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", "");  

	    			if (after > before && before < 18) {  

	    				if (str.length() > 2) {  
	    					str = str.substring(0,2) + '.' + str.substring(2);  
	    				}  
	    				
	    				if (str.length() > 6) {  
	    					str = str.substring(0,6) + '.' + str.substring(6);  
	    				}  
	    				
	    				if (str.length() > 10) {  
	    					str = str.substring(0,10) + '/' + str.substring(10);  
	    				}  
	    				
	    				if (str.length() > 15) {  
	    					str = str.substring(0,15) + '-' + str.substring(15);  
	    				}  
	    				
	    				// Seta a flag pra evitar chamada infinita  
	    				isUpdatingConsulta = true;  
	    				
	    				// seta o novo texto  
	    				edt.setText(str);  
	    				
	    				// seta a posição do cursor  
	    				if(start == 2 || start == 6 || start == 10 || start == 15 ){
	        				edt.setSelection(start + 2);  
	    				}else{
	        				edt.setSelection(start + 1);  
	    				}
	    	      
	    			} else {  
	    				isUpdatingConsulta = true;  
	    				
	    				if (str.length() > 2) {  
	    					str = str.substring(0,2) + '.' + str.substring(2);  
	    				}

	    				if (str.length() > 6) {  
	    					str = str.substring(0,6) + '.' + str.substring(6);  
	    				}

	    				if (str.length() > 10) {  
	    					str = str.substring(0,10) + '/' + str.substring(10);  
	    				}

	    				if (str.length() > 15) {  
	    					str = str.substring(0,15) + '-' + str.substring(15);  
	    				}

	    				edt.setText(str);  
	    				
	    				// Se estiver apagando posiciona o cursor no local correto. Isso trata a deleção dos caracteres da máscara.  
	    				edt.setSelection(Math.max(0, Math.min(start + 1 - before, str.length() ) ) ); 
	    			}  
				}
    		}

			
    	    public void afterTextChanged(Editable s) {}  
        };
		
		edt.addTextChangedListener(consultaTextWatcher);
	}
	
	// Define a variavel editText para tratar os eventos de textChanged para verificar o numero de CPF ou CNPJ.
	public static void addTextChangedListenerCpfCnpjVerifierAndMask(final EditText edt, final int pessoa){
		
		edt.addTextChangedListener(new TextWatcher() {  
    	    
    		
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}  
    	      
    		
    	    public void onTextChanged(CharSequence s, int start, int before, int after) {  
    	         
    			// Quando o texto é alterado o onTextChange é chamado. Essa flag evita a chamada infinita desse método  
    			if (isUpdatingCpfCnpj){
    				isUpdatingCpfCnpj = false;  
    				return;  
    			}  
    	      
				// Se for CPF
    			if (ClienteTab.isTipoCpf(pessoa)){
	    			
					// Remove o '-'  e o '.'da String  
	    			String str = s.toString().replaceAll("[-]", "").replaceAll("[.]", "");  
	    	      
	    			if (after > before && before < 12) {  

	    				if (str.length() > 3) {  
	    					str = str.substring(0,3) + '.' + str.substring(3);  
	    				}  
	    				
	    				if (str.length() > 7) {  
	    					str = str.substring(0,7) + '.' + str.substring(7);  
	    				}  
	    				
	    				if (str.length() > 11) {  
	    					str = str.substring(0,11) + '-' + str.substring(11);  
	    				}  
	    				
	    				if (str.length() > 14) {  
	    					str = str.substring(0,14);  
	    				}
	    				
	    				// Seta a flag pra evitar chamada infinita  
	    				isUpdatingCpfCnpj = true;  
	    				
	    				// seta o novo texto  
	    				edt.setText(str);  
	    				
	    				// seta a posição do cursor  
	    				if(start == 3 || start == 7 || start == 11 ){
	        				edt.setSelection(start + 2);  
	    				}else if (start == 14){
	        				edt.setSelection(start);  
	    				}else{
	        				edt.setSelection(start + 1);  
	    				}
	    	      
	    			} else {  
	    				isUpdatingCpfCnpj = true;  
	    				
	    				if (str.length() > 3) {  
	    					str = str.substring(0,3) + '.' + str.substring(3);  
	    				}

	    				if (str.length() > 7) {  
	    					str = str.substring(0,7) + '.' + str.substring(7);  
	    				}

	    				if (str.length() > 11) {  
	    					str = str.substring(0,11) + '-' + str.substring(11);  
	    				}

	    				if (str.length() > 18) {  
	    					str = str.substring(0,18);  
	    				}
	    				
	    				edt.setText(str);  
	    				
	    				// Se estiver apagando posiciona o cursor no local correto. Isso trata a deleção dos caracteres da máscara.  
	    				edt.setSelection(Math.max(0, Math.min(start + 1 - before, str.length() ) ) ); 
	    			}  
				// Se for CNPJ
    			}else{
					
	    			// Remove o '-'  e '.'da String  
	    			String str = s.toString().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", "");  

	    			if (after > before && before < 18) {  

	    				if (str.length() > 2) {  
	    					str = str.substring(0,2) + '.' + str.substring(2);  
	    				}  
	    				
	    				if (str.length() > 6) {  
	    					str = str.substring(0,6) + '.' + str.substring(6);  
	    				}  
	    				
	    				if (str.length() > 10) {  
	    					str = str.substring(0,10) + '/' + str.substring(10);  
	    				}  
	    				
	    				if (str.length() > 15) {  
	    					str = str.substring(0,15) + '-' + str.substring(15);  
	    				}  
	    				
	    				// Seta a flag pra evitar chamada infinita  
	    				isUpdatingCpfCnpj = true;  
	    				
	    				// seta o novo texto  
	    				edt.setText(str);  
	    				
	    				// seta a posição do cursor  
	    				if(start == 2 || start == 6 || start == 10 || start == 15 ){
	        				edt.setSelection(start + 2);  
	    				}else{
	        				edt.setSelection(start + 1);  
	    				}
	    	      
	    			} else {  
	    				isUpdatingCpfCnpj = true;  
	    				
	    				if (str.length() > 2) {  
	    					str = str.substring(0,2) + '.' + str.substring(2);  
	    				}

	    				if (str.length() > 6) {  
	    					str = str.substring(0,6) + '.' + str.substring(6);  
	    				}

	    				if (str.length() > 10) {  
	    					str = str.substring(0,10) + '/' + str.substring(10);  
	    				}

	    				if (str.length() > 15) {  
	    					str = str.substring(0,15) + '-' + str.substring(15);  
	    				}

	    				edt.setText(str);  
	    				
	    				// Se estiver apagando posiciona o cursor no local correto. Isso trata a deleção dos caracteres da máscara.  
	    				edt.setSelection(Math.max(0, Math.min(start + 1 - before, str.length() ) ) ); 
	    			}  
				}
    		}
			
    	    public void afterTextChanged(Editable s) {  
    	    
    			if (ClienteTab.isTipoCpf(pessoa)){
    					
					switch (pessoa){
					case Constantes.PESSOA_PROPRIETARIO:
	    				if (s.length() == 14){
	    					isCpfProprietarioOk = validateCpf(s.toString().replaceAll("[-]", "").replaceAll("[.]", ""));
	    				}else {
	    					isCpfProprietarioOk = false;
	    				}
						break;
						
					case Constantes.PESSOA_USUARIO:
	    				if (s.length() == 14){
	    					isCpfUsuarioOk = validateCpf(s.toString().replaceAll("[-]", "").replaceAll("[.]", ""));
	    				}else {
	    					isCpfUsuarioOk = false;
	    				}
						break;
						
					case Constantes.PESSOA_RESPONSAVEL:
	    				if (s.length() == 14){
	    					isCpfResponsavelOk = validateCpf(s.toString().replaceAll("[-]", "").replaceAll("[.]", ""));
	    				}else {
	    					isCpfResponsavelOk = false;
	    				}
						break;
					}
    			}else{

					switch (pessoa){
					case Constantes.PESSOA_PROPRIETARIO:
	    				if (s.length() == 18){
	    					isCnpjProprietarioOk = validateCnpj(s.toString().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""));
	    				}else {
	    					isCnpjProprietarioOk = false;
	    				}
						break;
						
					case Constantes.PESSOA_USUARIO:
	    				if (s.length() == 18){
	    					isCnpjUsuarioOk = validateCnpj(s.toString().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""));
	    				}else {
	    					isCnpjUsuarioOk = false;
	    				}
						break;
						
					case Constantes.PESSOA_RESPONSAVEL:
	    				if (s.length() == 18){
	    					isCnpjResponsavelOk = validateCnpj(s.toString().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""));
	    				}else {
	    					isCnpjResponsavelOk = false;
	    				}
    					break;
					}
    			}
    		}  
        });
	}
	
	public static boolean validateCpf(String cpf) {  
        int     d1, d2;  
        int     digito1, digito2, resto;  
        int     digitoCPF;  
        String  nDigResult;  
        
        cpf = cpf.replaceAll("[-]", "").replaceAll("[.]", "");

        d1 = d2 = 0;  
        digito1 = digito2 = resto = 0;  

        if (cpf.length() != 11 || cpf.contentEquals("00000000000") ||
					        	  cpf.contentEquals("11111111111") ||
					        	  cpf.contentEquals("22222222222") ||
					        	  cpf.contentEquals("33333333333") ||
					        	  cpf.contentEquals("44444444444") ||
					        	  cpf.contentEquals("55555555555") ||
					        	  cpf.contentEquals("66666666666") || 
					        	  cpf.contentEquals("77777777777") || 
					        	  cpf.contentEquals("88888888888") || 
					        	  cpf.contentEquals("99999999999")){
        	return false;
        }
        
        for (int nCount = 1; nCount < cpf.length() -1; nCount++)  
        {  
           digitoCPF = Integer.valueOf (cpf.substring(nCount -1, nCount)).intValue();  

           //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.  
           d1 = d1 + ( 11 - nCount ) * digitoCPF;  

           //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.  
           d2 = d2 + ( 12 - nCount ) * digitoCPF;  
        };  

        //Primeiro resto da divisão por 11.  
        resto = (d1 % 11);  

        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
        if (resto < 2)  
           digito1 = 0;  
        else  
           digito1 = 11 - resto;  

        d2 += 2 * digito1;  

        //Segundo resto da divisão por 11.  
        resto = (d2 % 11);  

        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
        if (resto < 2)  
           digito2 = 0;  
        else  
           digito2 = 11 - resto;  

        //Digito verificador do CPF que está sendo validado.  
        String nDigVerific = cpf.substring (cpf.length()-2, cpf.length());  

        //Concatenando o primeiro resto com o segundo.  
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);  

        //comparar o digito verificador do cpf com o primeiro resto + o segundo resto.  
        return nDigVerific.equals(nDigResult);  
     }
	
    public static boolean validateCnpj( String str_cnpj ) {  
        if (! str_cnpj.substring(0,1).equals("")){  
            try{
                str_cnpj=str_cnpj.replace('.',' ');
                str_cnpj=str_cnpj.replace('/',' ');
                str_cnpj=str_cnpj.replace('-',' ');
                str_cnpj=str_cnpj.replaceAll(" ","");
            	
                if(str_cnpj.length() != 14 || str_cnpj.contentEquals("00000000000000")){
            		return false;
            	}
            	
                int soma = 0, dig;  
                String cnpj_calc = str_cnpj.substring(0,12);  
                  
                if ( str_cnpj.length() != 14 )  
                    return false;  
                char[] chr_cnpj = str_cnpj.toCharArray();  
                /* Primeira parte */  
                for( int i = 0; i < 4; i++ )  
                    if ( chr_cnpj[i]-48 >=0 && chr_cnpj[i]-48 <=9 )  
                        soma += (chr_cnpj[i] - 48 ) * (6 - (i + 1)) ;  
                for( int i = 0; i < 8; i++ )  
                    if ( chr_cnpj[i+4]-48 >=0 && chr_cnpj[i+4]-48 <=9 )  
                        soma += (chr_cnpj[i+4] - 48 ) * (10 - (i + 1)) ;  
                dig = 11 - (soma % 11);  
                cnpj_calc += ( dig == 10 || dig == 11 ) ?  
                    "0" : Integer.toString(dig);  
                /* Segunda parte */  
                soma = 0;  
                for ( int i = 0; i < 5; i++ )  
                    if ( chr_cnpj[i]-48 >=0 && chr_cnpj[i]-48 <=9 )  
                        soma += (chr_cnpj[i] - 48 ) * (7 - (i + 1)) ;  
                for ( int i = 0; i < 8; i++ )  
                    if ( chr_cnpj[i+5]-48 >=0 && chr_cnpj[i+5]-48 <=9 )  
                        soma += (chr_cnpj[i+5] - 48 ) * (10 - (i + 1)) ;  
                dig = 11 - (soma % 11);  
                cnpj_calc += ( dig == 10 || dig == 11 ) ?  
                    "0" : Integer.toString(dig);  
                return str_cnpj.equals(cnpj_calc);  
            }catch (Exception e){  
                return false;  
            }  
        }else return false;  
          
    }
    
    /**
     * Verifica se o valor da String.trim() veio como null ou como
     * Constantes.NULO_DOUBLE, setando como Constantes.NULO_DOUBLE caso
     * verdadeiro
     * 
     * @param valor
     * @return
     */
    public static double verificarNuloDouble(String valor) {
		if (valor == null || valor.trim().equals(Constantes.NULO_STRING)) {
		    return Constantes.NULO_DOUBLE;
		} else {
		    return Double.parseDouble(valor.trim());
		}
    }

    /**
     * Verifica se o valor da String.trim() veio como null ou como
     * Constantes.NULO_STRING, setando como Constantes.NULO_STRING caso
     * verdadadeiro
     * 
     * @param valor
     * @return
     */
    public static String verificarNuloString(String valor) {
		if (valor == null || valor.trim().equals(Constantes.NULO_STRING) || valor.trim().equals("null")) {
		    return Constantes.NULO_STRING;
		} else {
		    return valor.trim();
		}
    }

    /**
     * Verifica se o valor da String.trim() veio como null ou como
     * Constantes.NULO_INT, setando como Constantes.NULO_INT caso
     * verdadadeiro
     * 
     * @param valor
     * @return
     */
    public static int verificarNuloInt(String valor) {
		if (valor == null || valor.trim().equals(Constantes.NULO_STRING)) {
		    return Constantes.NULO_INT;
		} else {
		    return Integer.parseInt(valor.trim());
		}
    }
    
    public static int verificarNuloIntParaZero(String valor) {
		if (valor == null || valor.trim().equals(Constantes.NULO_STRING)) {
		    return Integer.valueOf(0);
		} else {
		    return Integer.parseInt(valor.trim());
		}
    }

    /**
     * < <Descrição do método>>
     * 
     * @param data
     *            Descrição do parâmetro
     * @return Descrição do retorno
     */
    public static String formatarData(Date data) {
		StringBuffer dataBD = new StringBuffer();
	
		if (data != null) {
		    Calendar dataCalendar = Calendar.getInstance();
		    dataCalendar.setTime(data);
	
		    dataBD.append(dataCalendar.get(Calendar.YEAR) + "-");
	
		    // Obs.: Janeiro no Calendar é mês zero
		    if ((dataCalendar.get(Calendar.MONTH) + 1) > 9) {
				dataBD.append(dataCalendar.get(Calendar.MONTH) + 1 + "-");
		    } else {
		    	dataBD.append("0" + (dataCalendar.get(Calendar.MONTH) + 1) + "-");
		    }
	
		    if (dataCalendar.get(Calendar.DAY_OF_MONTH) > 9) {
		    	dataBD.append(dataCalendar.get(Calendar.DAY_OF_MONTH));
		    } else {
		    	dataBD.append("0" + dataCalendar.get(Calendar.DAY_OF_MONTH));
		    }
	
		    dataBD.append(" ");
	
		    if (dataCalendar.get(Calendar.HOUR_OF_DAY) > 9) {
		    	dataBD.append(dataCalendar.get(Calendar.HOUR_OF_DAY));
		    } else {
		    	dataBD.append("0" + dataCalendar.get(Calendar.HOUR_OF_DAY));
		    }
	
		    dataBD.append(":");
	
		    if (dataCalendar.get(Calendar.MINUTE) > 9) {
		    	dataBD.append(dataCalendar.get(Calendar.MINUTE));
		    } else {
		    	dataBD.append("0" + dataCalendar.get(Calendar.MINUTE));
		    }
	
		    dataBD.append(":");
	
		    if (dataCalendar.get(Calendar.SECOND) > 9) {
		    	dataBD.append(dataCalendar.get(Calendar.SECOND));
		    } else {
		    	dataBD.append("0" + dataCalendar.get(Calendar.SECOND));
		    }
	
		    dataBD.append(".");
	
		    dataBD.append(Util.adicionarZerosEsquerdaNumero(6, dataCalendar.get(Calendar.MILLISECOND) + ""));
		}
	
		return dataBD.toString();
    }

    /**
     * Adiciona zeros a esqueda do número informado tamamho máximo campo 6
     * Número 16 retorna 000016
     * 
     * @param tamanhoMaximoCampo
     *            Descrição do parâmetro
     * @param numero
     *            Descrição do parâmetro
     * @return Descrição do retorno
     */
    public static String adicionarZerosEsquerdaNumero(int tamanhoMaximoCampo, String numero) {
		String zeros = "";
		String retorno = null;
	
		boolean ehNegativo = numero != null && !numero.equals(Constantes.NULO_STRING) && !numero.equals(Constantes.NULO_DOUBLE + "") && !numero.equals(Constantes.NULO_INT + "") && !numero.equals(Constantes.NULO_SHORT + "") && numero.charAt(0) == '-';
	
		if (ehNegativo) {
		    numero = numero.substring(1);
		}
	
		if (numero != null && !numero.equals("") && !numero.equals(Constantes.NULO_INT + "")) {
		    for (int a = 0; a < (tamanhoMaximoCampo - numero.length()); a++) {
		    	zeros = zeros.concat("0");
		    }
		    // concatena os zeros ao numero
		    // caso o numero seja diferente de nulo
		    retorno = zeros.concat(numero);
		} else {
		    for (int a = 0; a < tamanhoMaximoCampo; a++) {
		    	zeros = zeros.concat("0");
		    }
		    // retorna os zeros
		    // caso o numero seja nulo
		    retorno = zeros;
		}
	
		if (ehNegativo) {
		    retorno = "-" + retorno.substring(1);
		}
		return retorno;
    }

    /**
     * Adiciona char " " a esqueda da String informada número 16 retorna 000016
     * 
     * @param tamanhoMaximoCampo
     *            Descrição do parâmetro
     * @param numero
     *            Descrição do parâmetro
     * @return Descrição do retorno
     */
    public static String adicionarCharEsquerda(int tamanhoMaximoCampo, String string, char c) {
		String repetido = "";
		String retorno = null;
	
		if (string != null && !string.equals("") && !string.equals(Constantes.NULO_INT + "")) {
		    for (int a = 0; a < (tamanhoMaximoCampo - string.length()); a++) {
		    	repetido = repetido.concat(c + "");
		    }
		    // concatena os zeros ao numero
		    // caso o numero seja diferente de nulo
		    retorno = repetido.concat(string);
		} else {
		    for (int a = 0; a < tamanhoMaximoCampo; a++) {
		    	repetido = repetido.concat(c + "");
		    }
		    // retorna os zeros caso o numero seja nulo
		    retorno = repetido;
		}
		return retorno;
    }

    /**
     * Adiciona caracteres a direita da string informada tamamho máximo campo 1 = 15
     * Palavra  "apagar" retorna "apagar         "
     * 
     * @param tamanhoMaximoCampo
     *            Descrição do parâmetro
     * @param numero
     *            Descrição do parâmetro
     * @return Descrição do retorno
     */
    public static String adicionarCharDireita(int tamanhoMaximoCampo, String string, char c) {
		String retorno = new String();
	
		if (string != null && !string.equals("") && !string.equals(Constantes.NULO_INT + "")) {
			retorno = string;
			for (int a = 0; a < (tamanhoMaximoCampo - string.length()); a++) {
				retorno = retorno.concat(c + "");
		    }
		} else {
		    for (int a = 0; a < tamanhoMaximoCampo; a++) {
		    	retorno = retorno.concat(c + "");
		    }
		}
		return retorno;
    }

    public static String getRetornoRotaDirectory() {
    	Controlador.getInstancia().getCadastroDataManipulator().selectGeral();
    	String nomeArquivo = Controlador.getInstancia().getDadosGerais().getNomeArquivo() + "_" + getData();

        File diretorio = new File(getExternalStorageDirectory() + Constantes.DIRETORIO_RETORNO, nomeArquivo);
        if(!diretorio.exists()) {
        	diretorio.mkdirs();
        }

    	return diretorio.getAbsolutePath();
    }
 
    public static String getRotaFileName(){
    	Controlador.getInstancia().getCadastroDataManipulator().selectGeral();
    	return Controlador.getInstancia().getDadosGerais().getNomeArquivo() + "_" + getData();
    }
    
	public static String capitalizarString(String string) {
    	string = string.toLowerCase();
    	String[] palavras = string.split(" ");
    	String novaString = "";
    	
    	for (String palavra : palavras) {
    		if (palavra.equals("")){
    			continue;
    		}
			palavra = Character.toUpperCase(palavra.charAt(0)) + palavra.substring(1);
			novaString += palavra + " ";
		}
    	
    	return novaString;
    }

    @SuppressLint("SdCardPath") 
	public static File getExternalStorageDirectory() {
		return new File("/mnt/sdcard/");

	}
    
	public static boolean allowPopulateDados() {
		boolean result = false;
		if ((Controlador.getInstancia().getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_SALVO || Controlador.getInstancia()
				.getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_SALVO_COM_ANORMALIDADE)
				|| (Controlador.getInstancia().getImovelSelecionado().isTabSaved())) {

			result = true;
		}
		return result;
	}

	public static void gerarZipRetorno() throws IOException {
		ArrayList<String> filesToZip = new ArrayList<String>();
		File diretorio = new File(getRetornoRotaDirectory());

		String nome = diretorio.getPath() + "/" + getRotaFileName() + ".zip";

		File[] files = diretorio.listFiles();

		if (files != null) {

			for (int i = 0; i < files.length; i++) {

				File file = files[i];
				if (!file.isDirectory()) {
					if (!file.getName().endsWith(".zip") && !file.getName().startsWith("._")) {
						filesToZip.add(file.getPath());
					}
				}
			}
		}

		Compress arquivo = new Compress(filesToZip, nome);
		arquivo.zip();
	}
	
	public static String removeDecimalChar(String value){
		return value.replace(".","");
	}
	
	@SuppressLint("NewApi") 
	public static String removerCaractereEspecial(String valor) {
		String temp = Normalizer.normalize(valor, java.text.Normalizer.Form.NFD);
		return temp.replaceAll("[^\\p{ASCII}]", " ");
	}
	
	@SuppressLint("NewApi")
	public static String removerCaractereEspecialNovo(String valor) {
		String temp = Normalizer.normalize(valor, java.text.Normalizer.Form.NFD);
		return temp.replaceAll("[^\\p{ASCII}]", "");
	}
	
	public static String substringNome(String nome) {
		return nome.length() > 50 ? nome.substring(0, 50) : nome;
	}
	
	public static String getData() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
	
	public static String removerZerosAEsquerda(String valor) {
		return valor.trim().replaceFirst("^0+(?!$)", "");
	}
	
	public static AlertDialog criarDialog(Context context, View layout, String title, String message, int iconId, 
			OnClickListener positiveListener, OnClickListener negativeListener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		if (title != null && !title.equals("")) {
			builder.setTitle(title);
		}
		
		if (message != null && !message.equals("")) {
			builder.setMessage(message);
		}
		
		if (iconId != -1) {
			builder.setIcon(iconId);
		}
		
		if (layout != null) {
			builder.setView(layout);
		}
		
		builder.setPositiveButton(android.R.string.ok, positiveListener);
		
		if (negativeListener == null) {
			builder.setCancelable(false);
		} else {
			builder.setNegativeButton(android.R.string.cancel, negativeListener);
		}

		return builder.create();
	}
}
