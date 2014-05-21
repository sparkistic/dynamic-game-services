package com.sparkistic.dynamicgameservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class GameServicesActivity extends Activity {

	private GameServicesModel gameCircleModel = null;
	private GameServicesCrypto gameServicesCrypto = null;

	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String manufacturer = Build.MANUFACTURER;
		if (manufacturer.toUpperCase().equals("AMAZON")) {
			gameCircleModel = new AmazonGameCircleModel();
		} else {
			gameCircleModel = new GoogleGameServicesModel(this);
		}
		gameServicesCrypto = new GameServicesCrypto(this);
		super.onCreate(savedInstanceState);
	}

	public void killConnection() {
		gameCircleModel.killConnection();
	}

	@Override
	public void onStart() {
		super.onStart();
		gameCircleModel.startActivity();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		gameCircleModel.activityResultChange(request, response, data);
	}

	@Override
	public void onDestroy() {
		if (gameCircleModel != null) {
			gameCircleModel.exit();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (gameCircleModel != null) {
			try {
				gameCircleModel.initialize(this);
			} catch (Throwable t) {
			}
		}
	}

	@Override
	public void onPause() {
		if (gameCircleModel != null) {
			gameCircleModel.paused();
		}
		super.onPause();
	}

	/**
	 * Unlocks the achievement with the given ID.
	 * 
	 * @param achievementId  the name of the achievement in the ids.xml file (this should correspond to the Amazon ID)
	 *  
	 */
	public void unlockAchievement(String achievementId) {
		if (gameCircleModel != null) {
			gameCircleModel.unlockAchievement(achievementId);
		}
	}
	/**
	 * Updates an incremental achievements with the given ID.
	 * 
	 * @param achievementId  the name of the achievement in the ids.xml file (this should correspond to the Amazon ID)
	 * @param count  the number of steps to increment this achievement
	 * @param outOfHowMany  the total number of steps required to unlock this achievement (unused to Google)
	 *  
	 */
	public void unlockAchievement(String achievementId, int count, int outOfHowMany) {
		if (gameCircleModel != null) {
			gameCircleModel.unlockAchievement(achievementId, count, outOfHowMany);
		}
	}

	/**
	 * Shows the achievement modal overlay.
	 */
	public void showAchievementOverlay() {
		if (gameCircleModel != null) {
			gameCircleModel.showAchievementOverlay();
		}
	}

	/** 
	 * Signs into the game services engine.
	 */
	public void signInToGame() {
		if (gameCircleModel != null) {
			gameCircleModel.signIn();
		}
	}

	/**
	 * Returns whether the system is registered with the online game services engine.
	 * 
	 * @return true if connected
	 */
	public boolean isConnected() {
		if (gameCircleModel != null) {
			return gameCircleModel.isConnected();
		}
		return false;
	}

	/** 
	 * Encrypts the given value with the given key used as salt along with the user's unique device ID.
	 * 
	 * @param uniqueKey  salt used with this encryption to ensure each encryption is unique
	 * @param value  the value to encrypt
	 * @return a string with the encrypted value
	 */
	public String getEncryptedValue(String uniqueKey, boolean value) {
		return gameServicesCrypto.getEncryptedValue(uniqueKey, String.valueOf(value));
	}

	/** 
	 * Encrypts the given value with the given key used as salt along with the user's unique device ID.
	 * 
	 * @param uniqueKey  salt used with this encryption to ensure each encryption is unique
	 * @param value  the value to encrypt
	 * @return a string with the encrypted value
	 */
	public String getEncryptedValue(String uniqueKey, int value) {
		return gameServicesCrypto.getEncryptedValue(uniqueKey, String.valueOf(value));
	}

	/** 
	 * Encrypts the given value with the given key used as salt along with the user's unique device ID.
	 * 
	 * @param uniqueKey  salt used with this encryption to ensure each encryption is unique
	 * @param value  the value to encrypt
	 * @return a string with the encrypted value
	 */
	public String getEncryptedValue(String uniqueKey, String value) {
		return gameServicesCrypto.getEncryptedValue(uniqueKey, value);
	}

	/**
	 * Decrypts the given value using the given key as salt along with the user's unique device ID.
	 * 
	 * @param uniqueKey  salt used with this encryption to ensure each encryption is unique
	 * @param value  the value to decrypt
	 * @return a boolean with the decrypted value (note that if the decrypted value cannot be converted to a boolean, false is returned)
	 */
	public boolean getDecryptedBoolean(String uniqueKey,  String value) {
		return gameServicesCrypto.getDecryptedString(uniqueKey, value).equals("true");
	}

	/**
	 * Decrypts the given value using the given key as salt along with the user's unique device ID.
	 * 
	 * @param uniqueKey  salt used with this encryption to ensure each encryption is unique
	 * @param value  the value to decrypt
	 * @return a boolean with the decrypted value (note that if the decrypted value cannot be converted to an int, 0 is returned)
	 */
	public int getDecryptedInt(String uniqueKey, String value) {
		int decryptedInt = 0;
		try {
			decryptedInt = Integer.parseInt(gameServicesCrypto.getDecryptedString(uniqueKey, value));
		} catch (Throwable t) {
		}
		return decryptedInt;
	}

	/**
	 * Decrypts the given value using the given key as salt along with the user's unique device ID.
	 * 
	 * @param uniqueKey  salt used with this encryption to ensure each encryption is unique
	 * @param value  the value to decrypt
	 * @return a boolean with the decrypted value
	 */
	public String getDecryptedString(String uniqueKey, String value) {
		return gameServicesCrypto.getDecryptedString(uniqueKey, value);
	}
	
	/**
	 * Sends the score to the game services leaderboard for posting.
	 * 
	 * @param leaderboardId  the name of the leaderboard in the ids.xml file
	 * @param scoreValue  the score to send to the leaderboard
	 */
	public void sendScoreToLeaderboard(String leaderboardId, long scoreValue) {
		if (gameCircleModel != null) {
			gameCircleModel.sendScoreToLeaderboard(leaderboardId, scoreValue);
		}
	}
	
	/**
	 * Shows the leaderboard modal overlay.
	 */
	public void showLeaderboardOverlay(String leaderboardId) {
		if (gameCircleModel != null) {
			gameCircleModel.showLeaderboardOverlay(leaderboardId);
		}
	}
	
	/**
	 * Shows a sign-in dialog for Google game services (not required for Amazon).
	 */
	@SuppressLint("DefaultLocale")
	public void showSignInDialog() {
		String manufacturer = Build.MANUFACTURER;
		if (isConnected() || (manufacturer.toUpperCase().equals("AMAZON"))) {
			return;
		}
		LayoutInflater inflater = getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.sign_in_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(dialoglayout);
		com.google.android.gms.common.SignInButton dialogButton = (com.google.android.gms.common.SignInButton) dialoglayout.findViewById(R.id.sign_in_button);
		final AlertDialog alertDialog = builder.create();
		// if button is clicked, close the custom dialog`
		dialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				signInToGame();
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}
}
