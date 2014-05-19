package com.sparkistic.dynamicgameservices;

import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;

public class GameServicesCrypto {

	private Activity activity;

	public GameServicesCrypto(Activity activity) {
		this.activity = activity;
	}

	@SuppressLint("TrulyRandom")
	private byte[] getSecretKey(String uniqueKey) {
		String androidId = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
		byte[] key = null;

		if (androidId != null && androidId.length() > 0) {
			androidId += uniqueKey;
			key = generateSecretKey(androidId);
		}

		if (key == null) {
			if (isSecretKeySaved()) {
				key = generateSecretKey(getStoredSecretKey() + uniqueKey);
			} else {
				key = generateSecretKey(encryptRandomUUID() + uniqueKey);
			}
		}
		return key;
	}

	@SuppressLint("TrulyRandom")
	private byte[] generateSecretKey(String randomSeed) {
		byte[] key = null;
		byte[] keyStart = randomSeed.getBytes();
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(keyStart);
			kgen.init(128, sr); // 192 and 256 bits may not be available
			SecretKey skey = kgen.generateKey();
			key = skey.getEncoded();
		} catch (Exception ex) {

		}
		return key;
	}

	private boolean isSecretKeySaved() {
		SharedPreferences sharedPref = activity.getSharedPreferences("gameServices", Context.MODE_PRIVATE);
		String temp = sharedPref.getString("androidId", "null");
		return !temp.equals("null");
	}

	private String encryptRandomUUID() {
		String stringKey = UUID.randomUUID().toString();
		storeRandomKey(stringKey);
		return stringKey;
	}

	private String getStoredSecretKey() {
		SharedPreferences sharedPref = activity.getSharedPreferences("gameServices", Context.MODE_PRIVATE);
		String stringKey = sharedPref.getString("androidId", "null");
		return stringKey;
	}

	private void storeRandomKey(String key) {
		SharedPreferences sharedPref = activity.getSharedPreferences("gameServices", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("androidId", key);
		editor.commit();
	}

	@SuppressLint("TrulyRandom")
	public String getEncryptedValue(String uniqueKey, String value) {
		String encryptedValue = "";
		byte[] secretKey = getSecretKey(uniqueKey);
		try {
			byte[] decryptedBytes = value.getBytes("UTF8");
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			encryptedValue = Base64.encode(cipher.doFinal(decryptedBytes));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return encryptedValue;
	}

	public String getDecryptedString(String uniqueKey, String value) {
		String decodedValue = "";
		byte[] secretKey = getSecretKey(uniqueKey);
		try {
			byte[] encryptedBytes = Base64.decode(value);
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			decodedValue = new String(cipher.doFinal(encryptedBytes), "UTF8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return decodedValue;
	}

}
