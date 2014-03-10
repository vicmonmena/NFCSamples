package es.vicmonmena.openuax.nfcer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import es.vicmonmena.openuax.nfcer.utils.NFCerUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author vicmonmena
 *
 */
public class WriteTagActivity extends Activity{

	/**
	 * Etiqueta para localización de LOGS correspondientes a esta clase.
	 */
	private static final String TAG = "WriteTagActivity";
	
	/**
	 * 
	 */
	private NfcAdapter nfcAdapter;
	
	/**
	 *  Contendrá la información extraida de la etiqueta NFC
	 */
    private PendingIntent pendingIntent;
    
    /**
     * Tipo de escritura que vamos a realizar (RTD_TEXT, RTD_URI ...).
     */
    private String writtingType;
    
	/**
     * Etiqueta NFC
     */
    private Tag myTag;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Comprobar que el NFCAdapter está disponible
        nfcAdapter = NfcAdapter.getDefaultAdapter(WriteTagActivity.this);
        
        if (nfcAdapter == null) {
        	Toast.makeText(WriteTagActivity.this, 
        		getString(R.string.error_nfc_not_available), 
        		Toast.LENGTH_SHORT).show();
        	finish();
        	return;
        }
        
        // Rellenamos con los datos de la etiqueta leída
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(
        	this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        writtingType = getIntent()
        	.getStringExtra("es.vicmonmena.openuax.nfcer.writtingtype");
	}
	
	@Override
    protected void onPause() {
    	super.onPause();
    	Log.d(TAG, "onPause");
    	nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	Log.d(TAG, "onResume");
    	nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
			case R.id.action_info:
				Toast.makeText(this, getString(R.string.write_tag_info), Toast.LENGTH_LONG).show();
				return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	
    	Log.d(TAG, "onNewIntent...");
    	// Obtenemos la tecnología de la etiqueta BFC que hemos leído
    	myTag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }
    
    /**
     * Captura el evento click de los botones que tengan definido este método.
     * @param view
     */
    public void onClickButton(View view) {
    	
    	switch (view.getId()) {
		
			case R.id.toWriteBtn:
	    		
		    	EditText text = (EditText) findViewById(R.id.textToWrite);
		    	if (!TextUtils.isEmpty(text.getText())) {
		    		try {
		    			
						if (write(text.getText().toString())) {
							Toast.makeText(WriteTagActivity.this, 
								text.getText().toString() + " " + 
								getString(R.string.msg_tag_written), 
								Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(WriteTagActivity.this, getString(R.string.error_tag), Toast.LENGTH_SHORT).show();
						}
					} catch (IOException e) {
						Log.e(TAG, "I/O Exception");
					} catch (FormatException e) {
						Log.e(TAG, "Format Exception");
					}
		    	} else {
		    		Toast.makeText(WriteTagActivity.this, getString(R.string.write_tag_validation), Toast.LENGTH_SHORT).show();
		    	}
		    	break;
			default:
				break;
		}
    }
    
    /**
     * Escribir en una etiqueta NFC
     * @param text
     * @throws IOException
     * @throws FormatException
     */
    private boolean write(String text) throws IOException, FormatException {
	
    	boolean result = false;
		Log.d(TAG, "Writing NFC tag ...");
		
		if (myTag != null) {
			
			NdefRecord[] records = null;
			
			if(writtingType.equals(NFCerUtils.RTD_TEXT)) {
				records = new NdefRecord[]{createTextRecord(text)};
			}
			else if (writtingType.equals(NFCerUtils.RTD_URI)) {
				records = new NdefRecord[]{createUriRecord(text)};
			}
			
			NdefMessage message = new NdefMessage(records);
			
			// Obtener una instancia de Ndef para la etiqueta
			Ndef ndef = Ndef.get(myTag);
			
			// Activar I/O
			ndef.connect();
			
			// Escribir el mensaje
			ndef.writeNdefMessage(message);
			
			// Cerrar la conexión
			ndef.close();
			
			result = true;
		}
		return result;
	}
    
    /**
     * Crea un NDEF TEXT Record para escribir en la etiqueta
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    private NdefRecord createTextRecord(String text) 
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
     * Crea un NDEF URI Record para escribir en la etiqueta
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    private NdefRecord createUriRecord(String text) 
    	throws UnsupportedEncodingException {
    	
    	byte[] uriBytes = text.getBytes(Charset.forName("US-ASCII"));
    	
    	// Se añade un byte para el prefijo de la URI, el primero del payload
    	byte[] payload = new byte[uriBytes.length + 1];
    	payload[0] = 0x01;
    	
    	// Añadimos el resto de la URI al payload
    	System.arraycopy(uriBytes, 0, payload, 1, uriBytes.length);
    	
    	NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, 
        		NdefRecord.RTD_URI, new byte[0], payload);
    	
    	return record;
	}
}
