package es.vicmonmena.openuax.nfcer.custom.components;

import android.app.Dialog;
import android.content.Context;
import es.vicmonmena.openuax.nfcer.R;

/**
 * 
 * @author vicmonmena
 *
 */
public class CustomWriteDialog extends Dialog {

	/**
	 * Contexto de la aplicación
	 */
	private Context context;
	
	/**
	 * Constructor por defecto
	 * @param context
	 */
	public CustomWriteDialog(Context context) {
		super(context);
		this.context = context;
		setTitle(R.string.custom_write_dialog_title);
    	setContentView(R.layout.custom_write_dialog);
	}
}
