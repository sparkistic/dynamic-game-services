package com.sparkistic.dynamicgameservices;

import java.security.spec.KeySpec;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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
			key = generateSecretKey(androidId, uniqueKey);
		}

		if (key == null) {
			if (isSecretKeySaved()) {
				key = generateSecretKey(getStoredSecretKey(), uniqueKey);
			} else {
				key = generateSecretKey(encryptRandomUUID(), uniqueKey);
			}
		}
		return key;
	}

	@SuppressLint("TrulyRandom")
	private byte[] generateSecretKey(String password, String salt) {
		byte[] key = null;
		byte[] passSalt = salt.getBytes();
		try {
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), passSalt, 1000, 128);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
			SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
			key = secretKey.getEncoded();
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
		System.out.println(new String(secretKey));

		try {
			byte[] decryptedBytes = value.getBytes("UTF-8");
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
		System.out.println(new String(secretKey));
		try {
			byte[] encryptedBytes = Base64.decode(value);
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			decodedValue = new String(cipher.doFinal(encryptedBytes), "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return decodedValue;
	}
}
