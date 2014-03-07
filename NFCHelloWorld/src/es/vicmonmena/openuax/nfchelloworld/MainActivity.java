	package es.vicmonmena.openuax.nfchelloworld;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author vicmonmena
 *
 */
public class MainActivity extends Activity {
	
	/**
	 * Etiqueta para localización de LOGS correspondientes a esta clase.
	 */
	private static final String TAG = "MainActivity";
	
	/**
	 * 
	 */
	private NfcAdapter nfcAdapter;
	
	/**
	 *  Contendrá la información extraida de la etiqueta NFC
	 */
    private PendingIntent pendingIntent;
    
    /**
     * 
     */
    private IntentFilter intentFiltersArray[];
    
    /**
     * 
     */
    private String[][] techLists;
    
    /**
     * Etiqueta NFC
     */
    private Tag myTag;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Comprobar que el NFCAdapter está disponible
        nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
        
        if (nfcAdapter == null) {
        	Toast.makeText(MainActivity.this, "NFC not available", Toast.LENGTH_SHORT).show();
        	finish();
        	return;
        }
        
        // Rellenamos con los datos de la etiqueta leída
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(
        	this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        
        // Control de todos los tipos MIME para NDEF_DISCOVERED:
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
        	ndef.addDataType("*/*");
        }catch (MalformedMimeTypeException e) {
        	throw new RuntimeException("RuntimeException",e);
        }
        
        /*
         * Obtenemos los className de las tecnologías de etiquetas que 
         * controlará la app.
         * 
         * Si definimos este arraylist a null estaremos indicando que se desean
         * filtrar todas las tecnologías que pasan el intent TAG_DISCOVERED
         */
        techLists = null; 
        		//new String[][] {new String[] {NfcF.class.getName(), 
        		//Ndef.class.getName()}};
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	//super.onNewIntent(intent);
    	
    	Log.i(TAG, "onNewIntent...");
    	
    	String action = intent.getAction();
    	
    	if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
    		
    		if (intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) != null) {
    			String cadena = read(intent.getParcelableArrayExtra(
    				NfcAdapter.EXTRA_NDEF_MESSAGES));
    			
    			((TextView) findViewById(R.id.tagInfo)).setText(cadena);
    		}
    	}
    	
    	myTag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }

    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	Log.i(TAG, "onPause");
    	nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	Log.i(TAG, "onResume");
    	nfcAdapter.enableForegroundDispatch(this, pendingIntent, 
    		intentFiltersArray, techLists);
    }

    /**
     * Escribir en una etiqueta NFC
     * @param text
     * @throws IOException
     * @throws FormatException
     */
    private boolean write(String text) throws IOException, FormatException {
	
    	boolean result = false;
		Log.i(TAG, "Writing NFC tag ...");
		//Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		//if (myTag != null) {
			NdefRecord[] records = {createRecord(text)};
			NdefMessage message = new NdefMessage(records);
			
			// Obtener una instancia de Ndef para la etiqueta
			Ndef ndef = Ndef.get(myTag);
			
			// Activar I/O
			ndef.connect();
			
			// Escribir el mensaje
			ndef.writeNdefMessage(message);
			
			// Cerrar la conexión
			ndef.close();
		//}
		
		return result;
	}
    
    /**
     * Crea un NDEF record para escribir en la etiqueta
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    private NdefRecord createRecord(String text) 
    	throws UnsupportedEncodingException {
    	
    	String lang = "es";
    	
    	byte[] textBytes = text.getBytes();
    	byte[] langBytes = lang.getBytes();
    	
    	int textLength = textBytes.length;
    	int langLength = langBytes.length;
    	
    	byte[] payload = new byte[1 + langLength + textLength];
    	
    	//Byte de estado (Ver especificaciones NDEF)
    	payload[0] = (byte) langLength;
    	
    	//Copiar langbytes y textbytes en el payload
    	System.arraycopy(langBytes, 0, payload, 1, langLength);
    	System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);
    	
    	NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, 
    		NdefRecord.RTD_TEXT, new byte[0], payload);
    	
    	return record;
    }
    
    /**
     * Leer una etiqueta NFC
     * @param rawMsgs
     */
    private String read(Parcelable[] rawMsgs) {
    	Log.i(TAG, "Reading NFC tag ...");
    	
    	String cadena = "";
    	NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
    	for (int i = 0; i < rawMsgs.length; i++) {
    		// Por cada mensaje contenido...
			msgs[i] = (NdefMessage) rawMsgs[i];
	
			// ...lee todos los registros para montar el contenido completo
			NdefRecord[] records = msgs[i].getRecords();
			
	        for (NdefRecord ndefRecord : records) {
	            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
	                try {
	                    cadena = cadena + readText(ndefRecord);
	                } catch (UnsupportedEncodingException e) {
	                    Log.e(TAG, "Unsupported Encoding", e);
	                }
	            }
	        }
		}
    	
    	return cadena;
    }
    
    /**
     * Lee el contenido de un registro NDEF
     * @param record
     * @return
     * @throws UnsupportedEncodingException
     */
    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        
        byte[] payload = record.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
         
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    /**
     * Captura el evento click del botón
     * @param view
     */
    public void onClickButton(View view) {
    	
    	EditText text = (EditText) findViewById(R.id.editText1);
    	if (!TextUtils.isEmpty(text.getText())) {
    		//Toast.makeText(MainActivity.this, text.getText(), Toast.LENGTH_SHORT).show();
    		try {
				if (write(text.getText().toString())) {
					Toast.makeText(MainActivity.this, text.getText() + " was written successfully!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this, "Tag not found!", Toast.LENGTH_SHORT).show();
				}
			} catch (IOException e) {
				Toast.makeText(MainActivity.this, "I/O Exception", Toast.LENGTH_SHORT).show();
			} catch (FormatException e) {
				Toast.makeText(MainActivity.this, "Format Exception", Toast.LENGTH_SHORT).show();
			}
    	}
    }
}
