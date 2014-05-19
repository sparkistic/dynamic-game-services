package com.sparkistic.dynamicgameservices;

import android.app.Activity;
import android.content.Intent;

public interface GameServicesModel {
	public void initialize(Activity activity);
	public void paused();
	public void exit();
	public void unlockAchievement(String achievementId);
	public void showAchievementOverlay();
	public void activityResultChange(int request, int response, Intent data);
	public void startActivity();
	public void signIn();
	public boolean isConnected();
	public void unlockAchievement(String achievementId, int count, int outOfHowMany);
	public void killConnection();
	public void sendScoreToLeaderboard(String leadboarderId, long scoreValue);
	public void showLeaderboardOverlay(String leadboarderId);
}
