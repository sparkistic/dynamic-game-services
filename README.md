# DynamicGameServices Library

The **DGS Library** allows you to easily incorporate leaderboard(s) and achievements in your game. You can deploy one APK to both Google Play and Amazon. The library will detect the user's device type at runtime and use Amazon GameCircle for Kindle devices and Google Play for all other Android devices

## Getting started

There are a couple of external libraries you'll need to get things going.

1\. Download and unzip the Amazon [Apps-SDK.zip](https://developer.amazon.com/public/resources/development-tools/sdk) file. 

2\. Import the GameCircleSDK project found in the Android-&gt;GameCircle folder. Keep this project open as it is used as a library by the **DSG Library**.

3\. Import the google-play-services_lib project by followings steps 1-4 on the [Google Play Services setup page](http://developer.android.com/google/play-services/setup.html#Install). 

4\. Clone this project and open all three in your workspace.

## Setting up Leaderboards and Achievements (Google Play)

1\. Go into your [Google Developer console](https://play.google.com/apps/publish/), click on the joystick, and add a new game. 

2\. Make a note of the app id (a 12-digit number next to the name of your app at the top of the page).

3\. Click on Leaderboards and/or Achievements to add. For each, make a note of the id (usually an 18-character string).

4\. To get things going, add your own email in the Testing Access section of the Testing page.

## Setting up Leaderboards and Achievements (Amazon)

1\. Go into your [Amazon Developer console](https://developer.amazon.com/home.html), click on the name of the app (or add a new one if you haven't already). 

2\. Click on the GameCircle tab. This should take you to the [GameCircle Configuration](https://developer.amazon.com/gc/cfg/index.html) page

3\. Click on Leaderboards and/or Achievements to add. For each, make a note of the id (these can be different than the corresponding ids in Google). **Note**: I make the Amazon ones human-readable, such as _first_level_complete_.

4\. To get things going, add your own GameCircle nickname in the Test Accounts page.

## Extra files needed in your app

There are a couple of extra files that are needed in your game project in order for all this to work.

**ids.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_id">000000000000</string>
    <string name="you_escaped_mordor">CgtheAch1eveM3ntiD</string>
    <string name="main_leaderboard">CgtheLead3erBo4rdiD</string>
</resources>
```
Replace `000000000000` with your Google Play app id; replace `you_escaped_mordor` with your Amazon achievement/leaderboard id; and replace `CgtheAch1eveM3ntiD` with your Google Play achievement/leaderboard id. 

**AndroidManifest.xml**: insert these tags as children of the `<application>` element in your manifest: 
```xml
<meta-data
   android:name="com.google.android.gms.version"
   android:value="4242000" />
<meta-data
   android:name="com.google.android.gms.games.APP_ID"
   android:value="@string/app_id" />
```
Note that the value above (4242000) changes when you upgrade GMS (Google Mobile Services) via the SDK manager. When this happens, you'll get an exception in your Logcat with the new number. Just replace the number and move along.

## Working with the library

To use the library, simply extend the `GameServicesActivity` instead of `Activity` in your activity that first loads. This will automatically do some of the setup work to initialize with Google Play or Amazon game services.

### Game services methods

*   `void unlockAchievement(String achievementId)`: unlocks the achievement with the given id (this should match the _name_ field in the ids.xml file). For example to unlock the achievement above use `unlockAchievement("you_escaped_mordor");` 
*   `void unlockAchievement(String achievementId, int count, int outOfHowMany)`: unlocks an incremental achievement. For Amazon, the percentage complete is needed and thus calculated, but for Google Play only the count field is used.
*   `void showAchievementOverlay()`: displays a modal which pauses your activity and overlays a dialog showing the achievements earned by the player.
*   `void sendScoreToLeaderboard(String leaderboardId, long scoreValue)`: updates the score on the leaderboard with the given id (this should match the _name_ field in the ids.xml file). For example to send a score of 1000 to leaderboard above use `sendScoreToLeaderboard("main_leaderboard", 1000);`
*   `void showLeaderboardOverlay(String leaderboardId)`: displays a modal which pauses your activity and overlays a dialog showing this leaderboard.
*   `void signInToGame()`: initiates the sign-in process for Google Play (should be used with a dialog on first startup).
*   `boolean isConnected()`: returns whether the user is successfully connected to game services (use it to detect auto-sign-in).

### Crypto helper methods

You will likely want to locally store whether the user has unlocked your achievements and high scores for your player. One easy place to store these values is in SharedPreferences. However, a user with an rooted phone can easily edit this file and change their high score. Use these methods to encrypt and decrypt achievements and high scores. These methods use AES to encrypt based on the device's androidId and salted with your uniqueKey. Salting helps ensure values like "true" will be different among your different key/value pairs.

*   `String getEncryptedValue(String uniqueKey, boolean value)`, `String getEncryptedValue(String uniqueKey, int value)`, `String getEncryptedValue(String uniqueKey, String value)`: encrypts the given value using the device's androidId and your uniqueKey. For best results, use a different uniqueKey for each stored value and something slightly different than the SharedPreferences key. For example, for the shared preference `highScore` use something like `String secretScore = getEncryptedValue("highScore" + "myGame", 1000);` you can then save the `secretScore` string as your shared preference for this value.
*   `boolean getDecryptedBoolean(String uniqueKey, String value)`: returns the boolean previously encrypted as `value`. Note that if the value cannot be decrypted into a boolean, false is returned.
*   `int getDecryptedInt(String uniqueKey, String value)`: returns the integer previously encrypted as `value`. Note that if the value cannot be decrypted into a integer, 0 is returned.
*   `String getDecryptedString(String uniqueKey, String value)`: returns the string previously encrypted as `value`.

