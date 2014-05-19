package com.sparkistic.dynamicgameservices;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.amazon.ags.api.AGResponseCallback;
import com.amazon.ags.api.AGResponseHandle;
import com.amazon.ags.api.AmazonGamesCallback;
import com.amazon.ags.api.AmazonGamesClient;
import com.amazon.ags.api.AmazonGamesFeature;
import com.amazon.ags.api.AmazonGamesStatus;
import com.amazon.ags.api.achievements.AchievementsClient;
import com.amazon.ags.api.leaderboards.LeaderboardsClient;
import com.amazon.ags.api.leaderboards.SubmitScoreResponse;

public class AmazonGameCircleModel implements GameServicesModel {

	// reference to the agsClient
	AmazonGamesClient agsClient;
	Activity baseActivity;
	List<String> queuedAchievements = new ArrayList<String>();

	AmazonGamesCallback callback = new AmazonGamesCallback() {
		@Override
		public void onServiceNotReady(AmazonGamesStatus status) {
			// unable to use service
		}

		@Override
		public void onServiceReady(AmazonGamesClient amazonGamesClient) {
			agsClient = amazonGamesClient;
			runQueuedAchievements();
			// ready to use GameCircle
		}
	};

	public synchronized void runQueuedAchievements() {
		Iterator<String> it = queuedAchievements.iterator();
		while (it.hasNext()) {
			String t = it.next();
			unlockAchievement(t);
			it.remove();
		}
	}

	// list of features your game uses (in this example, achievements and
	// leaderboards)
	EnumSet<AmazonGamesFeature> myGameFeatures = EnumSet.of(AmazonGamesFeature.Achievements);

	public void initialize(Activity activity) {
		if (activity == null || callback == null || myGameFeatures == null) {
			return;
		}
		baseActivity = activity;
		if (AmazonGamesClient.isInitialized()) {
			return;
		}
		try {
			AmazonGamesClient.initialize(baseActivity, callback, myGameFeatures);

		} catch (Throwable t) {
			Log.e("AmazonGameCircleModel", t.getMessage());
		}
	}

	private void reinitialize() {
		if (baseActivity == null || callback == null || myGameFeatures == null) {
			return;
		}
		if (AmazonGamesClient.isInitialized()) {
			agsClient = (AmazonGamesClient) AmazonGamesClient.getInstance();
			return;
		}
		try {
			AmazonGamesClient.initialize(baseActivity, callback, myGameFeatures);

		} catch (Throwable t) {
			Log.e("AmazonGameCircleModel", t.getMessage());
		}
	}

	@SuppressWarnings("static-access")
	public void paused() {
		if (agsClient == null) {
			if (AmazonGamesClient.isInitialized()) {
				try {
					agsClient = (AmazonGamesClient) AmazonGamesClient.getInstance();
				} catch (Throwable t) {
					Log.e("AmazonGameCircleModel", t.getMessage());
				}
			}
		}
		if (agsClient != null) {
			agsClient.release();
		}
	}

	@SuppressWarnings("static-access")
	public void exit() {
		if (agsClient == null) {
			if (AmazonGamesClient.isInitialized()) {
				try {
					agsClient = (AmazonGamesClient) AmazonGamesClient.getInstance();
				} catch (Throwable t) {
					Log.e("AmazonGameCircleModel", t.getMessage());
				}
			}
		}
		if (agsClient != null) {
			agsClient.shutdown();
		}
	}

	public void unlockAchievement(String achievementID) {
		if (agsClient == null) {
			if (AmazonGamesClient.isInitialized()) {
				try {
					agsClient = (AmazonGamesClient) AmazonGamesClient.getInstance();
				} catch (Throwable t) {
					Log.e("AmazonGameCircleModel", t.getMessage());
				}
			} else {
				reinitialize();
			}
		}
		if (agsClient == null) {
			queuedAchievements.add(achievementID);
		} else if (!AmazonGamesClient.isInitialized()) {
			reinitialize();
		} else {
			try {
				reinitialize();
				AchievementsClient client = agsClient.getAchievementsClient();
				if (client != null) {
					client.updateProgress(achievementID, 100.0f);
				}
			} catch (Throwable t) {
				Log.e("AmazonGameCircleModel", t.getMessage());
			}
		}
	}

	public void showAchievementOverlay() {
		if (agsClient == null) {
			if (AmazonGamesClient.isInitialized()) {
				try {
					agsClient = (AmazonGamesClient) AmazonGamesClient.getInstance();
				} catch (Throwable t) {
					Log.e("AmazonGameCircleModel", t.getMessage());
				}
			} else {
				reinitialize();
			}
		}
		if (agsClient != null) {
			if (!AmazonGamesClient.isInitialized()) {
				reinitialize();
			}
			try {
				AchievementsClient client = agsClient.getAchievementsClient();
				if (client != null) {
					client.showAchievementsOverlay();
				}
			} catch (Throwable t) {
				Log.e("AmazonGameCircleModel", t.getMessage());
			}
		}
	}

	@Override
	public void activityResultChange(int request, int response, Intent data) {

	}

	@Override
	public void startActivity() {
	}

	@Override
	public void signIn() {
	}

	@Override
	public boolean isConnected() {
		return (agsClient != null);
	}

	@Override
	public void unlockAchievement(String achievementID, int count, int outOfHowMany) {
		if (agsClient == null || outOfHowMany == 0) {
			// queuedAchievements.add(achievementID);
		} else if (!AmazonGamesClient.isInitialized()) {
			reinitialize();
		} else {
			try {
				AchievementsClient client = agsClient.getAchievementsClient();
				if (client != null) {
					float percentComplete = count / outOfHowMany;
					client.updateProgress(achievementID, percentComplete);
				}
			} catch (Throwable t) {
				Log.e("AmazonGameCircleModel", t.getMessage());
			}
		}
	}

	@Override
	public void killConnection() {
	}

	@Override
	public void sendScoreToLeaderboard(String leaderboardId, long scoreValue) {
		if (agsClient != null) {
			LeaderboardsClient lbClient = agsClient.getLeaderboardsClient();
			AGResponseHandle<SubmitScoreResponse> handle = lbClient.submitScore(leaderboardId, scoreValue);

			// Optional callback to receive notification of success/failure.
			handle.setCallback(new AGResponseCallback<SubmitScoreResponse>() {

				@Override
				public void onComplete(SubmitScoreResponse result) {
					if (result.isError()) {
						// Add optional error handling here. Not strictly
						// required
						// since retries and on-device request caching are
						// automatic.
					} else {
						// Continue game flow.
					}
				}
			});
		}
		
	}

	@Override
	public void showLeaderboardOverlay(String leaderboardId) {
		if (agsClient != null) {
			LeaderboardsClient lbClient = agsClient.getLeaderboardsClient();
			lbClient.showLeaderboardsOverlay(leaderboardId);
		}
	}
}
