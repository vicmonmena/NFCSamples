package es.vicmonmena.openuax.nfcer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        		Log.d(TAG, "Go to ead tag");
        		Intent intent = new Intent(MainActivity.this, ReadTagActivity.class);
        		startActivity(intent);
        		return true;
			case R.id.action_info:
				Toast.makeText(this, getString(R.string.action_info_text), Toast.LENGTH_LONG).show();
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
    	
    	Intent intent = null;
    	
    	switch (view.getId()) {
			case R.id.nfcOptionText:
				Log.d(TAG, "Go to write text");
				intent = new Intent(MainActivity.this, WriteTagActivity.class);
				break;
			case R.id.nfcOptionURI:
				Log.d(TAG, "Go to write URI");
				intent = new Intent(MainActivity.this, WriteTagActivity.class);
				break;
			default:
				break;
		} 
    	
    	startActivity(intent);
    }
}
