package com.sparkistic.dynamicgameservices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.games.Games;
import com.sparkistic.dynamicgameservices.GameHelper.GameHelperListener;

public class GoogleGameServicesModel implements GameServicesModel,
		GameHelper.GameHelperListener {

	/*
	 * Copyright (C) 2013 Google Inc.
	 * 
	 * Licensed under the Apache License, Version 2.0 (the "License"); you may
	 * not use this file except in compliance with the License. You may obtain a
	 * copy of the License at
	 * 
	 * http://www.apache.org/licenses/LICENSE-2.0
	 * 
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations
	 * under the License.
	 */

	// The game helper object. This class is mainly a wrapper around this
	// object.
	protected GameHelper mHelper;
	Activity baseActivity;
	// We expose these constants here because we don't want users of this class
	// to have to know about GameHelper at all.
	public static final int CLIENT_GAMES = GameHelper.CLIENT_GAMES;
	public static final int CLIENT_APPSTATE = GameHelper.CLIENT_APPSTATE;
	public static final int CLIENT_PLUS = GameHelper.CLIENT_PLUS;
	public static final int CLIENT_ALL = GameHelper.CLIENT_ALL;

	// Requested clients. By default, that's just the games client.
	protected int mRequestedClients = CLIENT_GAMES;

	protected String mDebugTag = "BaseGameActivity";
	protected boolean mDebugLog = false;
	private AlertDialog alertDialog;

	/** Constructs a BaseGameActivity with default client (GamesClient). */
	protected GoogleGameServicesModel(Activity act) {
		super();
		baseActivity = act;
		mHelper = new GameHelper(act, GameHelper.CLIENT_GAMES);
		if (mHelper == null) {
			return;
		}
		mHelper.setup(this);
	}

	/**
	 * Constructs a BaseGameActivity with the requested clients.
	 * 
	 * @param requestedClients
	 *            The requested clients (a combination of CLIENT_GAMES,
	 *            CLIENT_PLUS and CLIENT_APPSTATE).
	 */
	protected GoogleGameServicesModel(int requestedClients) {
		super();
		setRequestedClients(CLIENT_GAMES);
	}

	/**
	 * Sets the requested clients. The preferred way to set the requested
	 * clients is via the constructor, but this method is available if for some
	 * reason your code cannot do this in the constructor. This must be called
	 * before onCreate in order to have any effect. If called after onCreate,
	 * this method is a no-op.
	 * 
	 * @param requestedClients
	 *            A combination of the flags CLIENT_GAMES, CLIENT_PLUS and
	 *            CLIENT_APPSTATE, or CLIENT_ALL to request all available
	 *            clients.
	 */
	protected void setRequestedClients(int requestedClients) {
		mRequestedClients = requestedClients;
	}

	protected void onStart() {
		if (mHelper == null) {
			return;
		}
		mHelper.onStart(baseActivity);
	}

	protected void onStop() {
		if (mHelper == null) {
			return;
		}
		mHelper.onStop();
	}

	protected void onActivityResult(int request, int response, Intent data) {
		if (mHelper == null) {
			return;
		}
		mHelper.onActivityResult(request, response, data);
	}

	protected boolean isSignedIn() {
		if (mHelper == null) {
			return false;
		}
		return mHelper.isSignedIn();
	}

	protected void beginUserInitiatedSignIn() {
		if (mHelper == null) {
			return;
		}
		mHelper.beginUserInitiatedSignIn();
	}

	protected void signOut() {
		if (mHelper == null) {
			return;
		}
		mHelper.signOut();
	}

	protected String getInvitationId() {
		if (mHelper == null) {
			return "";
		}
		return mHelper.getInvitationId();
	}

	protected boolean hasSignInError() {
		if (mHelper == null) {
			return false;
		}
		return mHelper.hasSignInError();
	}

	protected GameHelper.SignInFailureReason getSignInError() {
		if (mHelper == null) {
			return null;
		}
		return mHelper.getSignInError();
	}

	@Override
	public void initialize(Activity activity) {
		if (mHelper == null) {
			return;
		}
		mHelper.reconnectClient();
	}

	@Override
	public void onSignInFailed() {
		mHelper.disconnect();
		((GameHelperListener) baseActivity).onSignInFailed();
	}

	@Override
	public void onSignInSucceeded() {
		Games.setGravityForPopups(mHelper.getApiClient(), Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL);
		((GameHelperListener) baseActivity).onSignInSucceeded();
	}

	@Override
	public void paused() {
	}

	@Override
	public void exit() {
		onStop();
	}

	@Override
	public void unlockAchievement(String achievementID) {
		if (mHelper == null) {
			return;
		}
		if (mHelper.isSignedIn() && mHelper.getApiClient().isConnected()) {
			Games.Achievements.unlock(mHelper.getApiClient(),
					getStringResourceByName(achievementID));
		}
	}

	@Override
	public void showAchievementOverlay() {
		if (mHelper == null) {
			return;
		}
		if (mHelper.isSignedIn() && mHelper.getApiClient().isConnected()) {
			baseActivity.startActivityForResult(Games.Achievements
					.getAchievementsIntent(mHelper.getApiClient()), 100);
		} else {
			showSignInDialog();
		}
	}

	private void showSignInDialog() {
		try {
			LayoutInflater inflater = baseActivity.getLayoutInflater();
			View dialoglayout = inflater.inflate(R.layout.alert_sign_in_dialog,
					null); // (ViewGroup) baseActivity.getCurrentFocus());
			AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
			builder.setView(dialoglayout);
			com.google.android.gms.common.SignInButton dialogButton = (com.google.android.gms.common.SignInButton) dialoglayout
					.findViewById(R.id.alert_sign_in_button);
			// if button is clicked, close the custom dialog
			alertDialog = builder.create();
			dialogButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					signIn();
					alertDialog.dismiss();
				}
			});
		} catch (Throwable t) {

		}
		alertDialog.show();
	}

	private String getStringResourceByName(String aString) {
		String packageName = baseActivity.getPackageName();
		int resId = baseActivity.getResources().getIdentifier(aString,
				"string", packageName);
		return baseActivity.getString(resId);
	}

	@Override
	public void activityResultChange(int request, int response, Intent data) {
		if (mHelper == null) {
			return;
		}
		mHelper.onActivityResult(request, response, data);
	}

	@Override
	public void startActivity() {
		this.onStart();
	}

	@Override
	public void signIn() {
		if (mHelper == null) {
			mHelper = new GameHelper(baseActivity, mRequestedClients);
			if (mHelper == null) {
				return;
			}
			mHelper.setup(this);
		}
		beginUserInitiatedSignIn();
	}

	@Override
	public boolean isConnected() {
		if (mHelper == null) {
			return false;
		}
		return mHelper.isSignedIn();
		// return mHelper.getGamesClient().isConnected();
	}

	@Override
	public void unlockAchievement(String achievementID, int count,
			int outOfHowMany) {
		if (mHelper == null) {
			return;
		}
		if (mHelper.isSignedIn() && mHelper.getApiClient().isConnected()) {
			Games.Achievements.increment(mHelper.getApiClient(),
					getStringResourceByName(achievementID), count);
		}

	}

	@Override
	public void killConnection() {
		if (mHelper == null) {
			return;
		}
		if (mHelper.hasSignInError()) {
			// mHelper.showFailureDialog();
			mHelper.disconnect();
			mHelper = null;
		}
	}

	@Override
	public void sendScoreToLeaderboard(String leaderboardId, long scoreValue) {
		try {
			if (mHelper.getApiClient().isConnected()) {
				Games.Leaderboards.submitScore(mHelper.getApiClient(),
						getStringResourceByName(leaderboardId), scoreValue);
			}
		} catch (Throwable t) {
		}
	}

	@Override
	public void showLeaderboardOverlay(String leaderboardId) {
		try {
			if (mHelper.getApiClient().isConnected()) {
				baseActivity.startActivityForResult(Games.Leaderboards
						.getLeaderboardIntent(mHelper.getApiClient(),
								getStringResourceByName(leaderboardId)), 100);
			} else {
				showSignInDialog();
			}
		} catch (Throwable t) {
		}
	}
}
