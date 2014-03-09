package es.vicmonmena.openuax.nfcer.utils;

public class NFCerUtils {

	/**
     * Formatea un array de String para que se muestre como un único String 
     * separado por el caracter indicado en el argumento "separator".
     * 
     * @param array
     * @param separator
     * @return
     */
    public static String formatStringArray(String[] array, String separator) {
    	StringBuilder result = new StringBuilder();
    	
    	if (array.length > 0) {
    		for (String technology : array) {
				result.append(technology.substring(technology.lastIndexOf(".")+1));
				result.append(separator);
			}
    		// Borrar la última coma
    		result.delete(result.length() - 2, result.length() - 1);
    	}
    	return result.toString();
    }
}
