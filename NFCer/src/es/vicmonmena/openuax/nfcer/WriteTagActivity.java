package es.vicmonmena.openuax.nfcer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
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
     * Etiqueta NFC
     */
    private Tag myTag;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
							Toast.makeText(WriteTagActivity.this, text.getText() + " was written successfully!", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(WriteTagActivity.this, "Tag not found!", Toast.LENGTH_SHORT).show();
						}
					} catch (IOException e) {
						Toast.makeText(WriteTagActivity.this, "I/O Exception", Toast.LENGTH_SHORT).show();
					} catch (FormatException e) {
						Toast.makeText(WriteTagActivity.this, "Format Exception", Toast.LENGTH_SHORT).show();
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
}
