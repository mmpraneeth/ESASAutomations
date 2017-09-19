package com.salesforce.esas.salesgtm;

import java.security.GeneralSecurityException;

public class PasswordTool {

	private static String hiddenCipherKey = "Workstation Ergonomics";
	public static void main(String[] args) throws GeneralSecurityException {
		hiddenCipherKey = "12345";
		EncryptionUtil eu = new EncryptionUtil();
		eu.resetCryptoKey();
		String cipherKey = "12345";
		cipherKey = (args.length > 1) ? args[1] : EncryptionUtil.generateKey(hiddenCipherKey);
		eu.setCipherKey(cipherKey);
		String encrypted = eu.encryptString("testjerry123");
		//String encrypted = eu.encryptString(args[0]);
		System.out.printf("cipherKey: %s\nencrypted: %s\n", cipherKey, "612442096bee34c56a2151c23152a77a");		
		System.out.println(getDecodedString(cipherKey, "612442096bee34c56a2151c23152a77a"));
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
