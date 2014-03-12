package es.vicmonmena.openuax.nfcer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
        
        // Comprobar que el NFCAdapter está disponible
        nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
        
        if (nfcAdapter == null) {
        	Toast.makeText(MainActivity.this, 
        		getString(R.string.error_nfc_not_available), 
        		Toast.LENGTH_SHORT).show();
        	finish();
        	return;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        	case R.id.action_read:
        		Intent intent = new Intent(MainActivity.this, ReadTagActivity.class);
        		startActivity(intent);
        		return true;
			case R.id.action_info:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.action_info);
				builder.setMessage(Html.fromHtml(getString(R.string.action_info_text)));
				builder.setNeutralButton(R.string.dialog_done, 
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
				builder.create().show();
				
				//Toast.makeText(this, getString(R.string.action_info_text), Toast.LENGTH_LONG).show();
				return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Captura el evento onclick de las opciones de escritura NFC
     * @param view
     */
    public void onOptionSelected(View view) {
    	Log.i(TAG, "onOptionSelected");
    	Intent intent = new Intent(MainActivity.this, WriteTagActivity.class);
    	intent.putExtra("es.vicmonmena.openuax.nfcer.writtingtype",
			(String)view.getTag());
    	startActivity(intent);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Log.i(TAG, "onPause");
    	nfcAdapter.disableForegroundDispatch(this);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.i(TAG, "onResume");
    	
    	Intent i = new Intent(MainActivity.this, ReadTagActivity.class);
    	PendingIntent pIntent = PendingIntent.getActivity(
    		this, 0, i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            
    	nfcAdapter.enableForegroundDispatch(this, pIntent, null, null);
    }
}
