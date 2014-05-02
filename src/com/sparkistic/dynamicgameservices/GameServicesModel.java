package com.sparkistic.dynamicgameservices;

import android.app.Activity;
import android.content.Intent;

public interface GameServicesModel {
	public void initialize(Activity activity);
	public void paused();
	public void exit();
	public void unlockAchievement(String achievementID);
	public void showAchievementOverlay();
	public void activityResultChange(int request, int response, Intent data);
	public void startActivity();
	public void signIn();
	public boolean isConnected();
	public void unlockAchievement(String achievementID, int count, int outOfHowMany);
	public void killConnection();
	public void sendScoreToLeaderboard(String leadboardID, long scoreValue);
	public void showLeaderboardOverlay(String leadboardID);
	
}
