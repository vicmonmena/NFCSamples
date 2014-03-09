package es.vicmonmena.openuax.nfcer;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
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
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import es.vicmonmena.openuax.nfcer.utils.NFCerUtils;

/**
 * 
 * @author vicmonmena
 *
 */
public class ReadTagActivity extends Activity{

	/**
	 * Etiqueta para localización de LOGS correspondientes a esta clase.
	 */
	private static final String TAG = "ReadTagActivity";
	
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
    
    /**
     * Tag content.
     */
    private TextView tagContent;
    
    /**
     * Tag support technologies 
     */
    private TextView tagTechnologies;
    
    /**
     * Tag Forum Type 
     */
    private TextView tagType;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Comprobar que el NFCAdapter está disponible
        nfcAdapter = NfcAdapter.getDefaultAdapter(ReadTagActivity.this);
        
        if (nfcAdapter == null) {
        	Toast.makeText(ReadTagActivity.this, "NFC not available", 
        		Toast.LENGTH_SHORT).show();
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
        
        tagContent = (TextView) findViewById(R.id.nfcTagContentText);
        tagTechnologies = (TextView) findViewById(R.id.nfcTagTechnologyText);
        tagType = (TextView) findViewById(R.id.nfcTagTypeText);
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
				Toast.makeText(this, getString(R.string.read_tag_info), 
					Toast.LENGTH_LONG).show();
				return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	
    	Log.i(TAG, "onNewIntent...");
    	
    	String action = intent.getAction();
    	
    	if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
    		
    		if (intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) != null) {
    			
    			String cadena = read(intent.getParcelableArrayExtra(
    				NfcAdapter.EXTRA_NDEF_MESSAGES));

    			tagContent.setText(cadena);
    		}
    	}
    	
    	// Obtenemos la tecnología de la etiqueta BFC que hemos leído
    	myTag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    	tagTechnologies.setText(NFCerUtils.formatStringArray(
    		myTag.getTechList(), ", "));
    	
    	// Obtenemos información sobre la etiqueta leída
    	Ndef ndefTag = Ndef.get(myTag);
    	tagType.setText(NFCerUtils.getNFCForumType(ndefTag.getType()));
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
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
    	if (!TextUtils.isEmpty(tagContent.getText())) {
    		outState.putString("content",tagContent.getText().toString());
    	}
    	
    	if (!TextUtils.isEmpty(tagTechnologies.getText())) {
    		outState.putString("technologies",tagTechnologies.getText().toString());
    	}
    	
    	if (!TextUtils.isEmpty(tagTechnologies.getText())) {
    		outState.putString("tagtype",tagTechnologies.getText().toString());
    	}
    	
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	
    	if (savedInstanceState  != null) {
    		if (!TextUtils.isEmpty(savedInstanceState.getString("content"))) {
    			tagContent.setText(savedInstanceState.getString("content"));
    		}
    		
    		if (!TextUtils.isEmpty(savedInstanceState.getString("technologies"))) {
    			tagContent.setText(savedInstanceState.getString("technologies"));
    		}
    		
    		if (!TextUtils.isEmpty(savedInstanceState.getString("tagtype"))) {
    			tagContent.setText(savedInstanceState.getString("tagtype"));
    		}
    	}
    	super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Leer una etiqueta NFC
     * @param rawMsgs
     */
    private String read(Parcelable[] rawMsgs) {
    	Log.i(TAG, "Reading NFC tag ...");
    	
    	String content = "";
    	NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
    	for (int i = 0; i < rawMsgs.length; i++) {
    		// Por cada mensaje contenido...
			msgs[i] = (NdefMessage) rawMsgs[i];
	
			// ...lee todos los registros para montar el contenido completo
			NdefRecord[] records = msgs[i].getRecords();
			
	        for (NdefRecord ndefRecord : records) {
	            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
	            	if (Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
		                try {
		                	content = content + readText(ndefRecord);
		                } catch (UnsupportedEncodingException e) {
		                    Log.e(TAG, "Unsupported Encoding", e);
		                }
	            	} else if (Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI)) {
	            		content += readURI(ndefRecord);
	            	}
	            }
	        }
		}
    	return content;
    }
    
    /**
     * Lee el contenido de un registro NDEF de tipo RTD_TEXT
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
     * Lee el contenido de un registro NDEF de tipo RTD_URI
     * @param record
     * @return
     * @throws UnsupportedEncodingException
     */
    private String readURI(NdefRecord record) {
        byte[] payload = record.getPayload();
        /*
         * payload[0] contains the URI Identifier Code, per the
         * NFC Forum "URI Record Type Definition" section 3.2.2.
         *
         * payload[1]...payload[payload.length - 1] contains the rest of
         * the URI.
         */
        int pre = (int)payload[0];
        String prefix = NFCerUtils.URI_PREFIXES[pre];
        String uriStr = new StringBuilder()
            .append(prefix).append(new String(payload, 1, payload.length - 1))
            .toString();
        
        return uriStr;
    }
}
