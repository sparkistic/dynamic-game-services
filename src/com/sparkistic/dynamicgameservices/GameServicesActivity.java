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

	public void unlockAchievement(String achievementID) {
		if (gameCircleModel != null) {
			gameCircleModel.unlockAchievement(achievementID);
		}
	}

	public void unlockAchievement(String achievementID, int count, int outOfHowMany) {
		if (gameCircleModel != null) {
			gameCircleModel.unlockAchievement(achievementID, count, outOfHowMany);
		}
	}

	public void showAchievementOverlay() {
		if (gameCircleModel != null) {
			gameCircleModel.showAchievementOverlay();
		}
	}

	public void signInToGame() {
		if (gameCircleModel != null) {
			gameCircleModel.signIn();
		}
	}

	public boolean isConnected() {
		if (gameCircleModel != null) {
			return gameCircleModel.isConnected();
		}
		return false;
	}

	public String getEncryptedValue(String uniqueKey, boolean value) {
		return gameServicesCrypto.getEncryptedValue(uniqueKey, String.valueOf(value));
	}

	public String getEncryptedValue(String uniqueKey, int value) {
		return gameServicesCrypto.getEncryptedValue(uniqueKey, String.valueOf(value));
	}

	public String getEncryptedValue(String uniqueKey, String value) {
		return gameServicesCrypto.getEncryptedValue(uniqueKey, value);
	}

	public boolean getDecryptedBoolean(String uniqueKey, String value) {
		return gameServicesCrypto.getDecryptedString(uniqueKey, value).equals("true");
	}

	public int getDecryptedInt(String uniqueKey, String value) {
		int decryptedInt = 0;
		try {
			decryptedInt = Integer.parseInt(gameServicesCrypto.getDecryptedString(uniqueKey, value));
		} catch (Throwable t) {
		}
		return decryptedInt;
	}

	public String getDecryptedString(String uniqueKey, String value) {
		return gameServicesCrypto.getDecryptedString(uniqueKey, value);
	}
	
	public void sendScoreToLeaderboard(String leaderboardId, long scoreValue) {
		if (gameCircleModel != null) {
			gameCircleModel.sendScoreToLeaderboard(leaderboardId, scoreValue);
		}
	}
	
	public void showLeaderboardOverlay(String leaderboardId) {
		if (gameCircleModel != null) {
			gameCircleModel.showLeaderboardOverlay(leaderboardId);
		}
	}
	
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
