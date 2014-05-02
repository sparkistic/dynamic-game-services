package com.sparkistic.dynamicgameservices;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class GameServicesActivity extends Activity {

	private GameServicesModel gameCircleModel = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		String manufacturer = Build.MANUFACTURER;
		if (manufacturer.toUpperCase().equals("AMAZON")) {
			gameCircleModel = new AmazonGameCircleModel();
		} else {
			gameCircleModel = new GoogleGameServicesModel(this);
		}
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
}
