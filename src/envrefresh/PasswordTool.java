package envrefresh;

import java.security.GeneralSecurityException;

import com.salesforce.dataloader.security.EncryptionUtil;

// Although the source is checked in, this is only to provide the overall
// form of the code.  If the code changes, be sure to wipe the key before
// submitting it to source control!
public class PasswordTool {
	private static String hiddenCipherKey = "Workstation Ergonomics";
	public static void main(String[] args) throws GeneralSecurityException {
		hiddenCipherKey = "12345";
		EncryptionUtil eu = new EncryptionUtil();
		eu.resetCryptoKey();
		String cipherKey = "12345";
		cipherKey = (args.length > 1) ? args[1] : EncryptionUtil.generateKey(hiddenCipherKey);
		eu.setCipherKey(cipherKey);
		String encrypted = eu.encryptString("salesforce1");
		//String encrypted = eu.encryptString(args[0]);
		System.out.printf("cipherKey: %s\nencrypted: %s\n", cipherKey, encrypted);		
		System.out.println(getDecodedString(cipherKey, encrypted));
	}
	
	protected static String getDecodedString(String key, String encoded) throws GeneralSecurityException {
		EncryptionUtil eu = new EncryptionUtil();
		eu.resetCryptoKey();
		eu.setCipherKey(key);
		return eu.decryptString(encoded);
	}
	
	public static String getEncrpted(String unEncripted)  throws GeneralSecurityException {
		hiddenCipherKey = "12345";
		EncryptionUtil eu = new EncryptionUtil();
		eu.resetCryptoKey();
		String cipherKey = "12345";
		cipherKey =  EncryptionUtil.generateKey(hiddenCipherKey);
		eu.setCipherKey(cipherKey);
		String encrypted = eu.encryptString(unEncripted);
		//String encrypted = eu.encryptString(args[0]);
		System.out.printf("cipherKey: %s\nencrypted: %s\n", cipherKey, encrypted);
		return encrypted;
	}

}
