package es.vicmonmena.openuax.nfcer.utils;

import android.nfc.tech.Ndef;

public class NFCerUtils {

	/**
     * 
     */
    public static final String[] URI_PREFIXES = new String[] {
        "",
        "http://www.",
        "https://www.",
        "http://",
        "https://",
        "tel:",
        "mailto:",
        "ftp://anonymous:anonymous@",
        "ftp://ftp.",
        "ftps://",
        "sftp://",
        "smb://",
        "nfs://",
        "ftp://",
        "dav://",
        "news:",
        "telnet://",
        "imap:",
        "rtsp://",
        "urn:",
        "pop:",
        "sip:",
        "sips:",
        "tftp:",
        "btspp://",
        "btl2cap://",
        "btgoep://",
        "tcpobex://",
        "irdaobex://",
        "file://",
        "urn:epc:id:",
        "urn:epc:tag:",
        "urn:epc:pat:",
        "urn:epc:raw:",
        "urn:epc:",
        "urn:nfc:",
    };
    
    /**
     * 
     */
    public static final String getNFCForumType(String source) {
    	String result = "";
    	if (source.equals(Ndef.NFC_FORUM_TYPE_1)) {
    		result = "NFC Forum Type 1";
    	} else if (source.equals(Ndef.NFC_FORUM_TYPE_2)) {
    		result = "NFC Forum Type 2";
    	}  else if (source.equals(Ndef.NFC_FORUM_TYPE_3)) {
    		result = "NFC Forum Type 3";
    	}  else if (source.equals(Ndef.NFC_FORUM_TYPE_4)) {
    		result = "NFC Forum Type 4";
    	}
        
        return result;
    };
    
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
