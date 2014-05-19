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
</resources>
```
Replace 000000000000 with your Google Play app id; replace you_escaped_mordor with your Amazon achievement/leaderboard id; and replace CgtheAch1eveM3ntiD with your Google Play achievement/leaderboard id. 
